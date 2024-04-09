package com.springboot.integration.filepooling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.springboot.integration.filepooling.configuration.FilePoolerProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static com.springboot.integration.filepooling.constants.AppConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileEventService {

    private final FilePoolerProps filePoolerProps;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void moveFileAndGetKafkaJsonMessage(MessageHeaders headers) {
        try {
            log.info("=== File Polling Event Started ===");
            String jsonMessage = enrichAndBuildJsonMessage(headers);
            log.info("Json payload generation successful {}", jsonMessage);
            ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            Object json = mapper.readValue(jsonMessage, Object.class);
            String prettyJson = mapper.writeValueAsString(json);
            log.info("Pretty Json value is {}", prettyJson);
            // Persist first event - eventName -> JSON Generation successful
            fileMovementForDownstream(prettyJson);
            // Persist second event - eventName -> File Movement success
            publishToKafkaTopic(prettyJson);
            // Persist third event -> eventName -> Service success
        } catch (SpelEvaluationException see) {
            log.error("Expression is invalid, please check the application yaml configurations {}", see.getMessage());
        } catch (ParseException parseException) {
            log.error("Expression evaluation failed {}", parseException.getMessage());

        } catch (JsonProcessingException jsonProcessingException) {
            log.error("Unable to convert json message from the received payload {}", jsonProcessingException.getMessage());
        } catch (IOException exception) {
            log.error("Unable to move file from source to destination location {}", exception.getMessage());
        } catch (Exception e) {
            log.error("Unable to process the event {}", e.fillInStackTrace());
        }

    }

    private String enrichAndBuildJsonMessage(MessageHeaders headers) throws JsonProcessingException {
        var payload = Map.of(
                HEADER, enrichHeaders(headers),
                DATA, Map.of(),
                ERROR, Map.of()
        );
        return new ObjectMapper().writeValueAsString(payload);
    }

    private Map<String, Object> enrichHeaders(MessageHeaders headers) {
        Map<String, Object> headerMap = new ObjectMapper().convertValue(headers, new TypeReference<>() {
        });
        headerMap.put(SERVICE_NAME, SERVICE_NAME_VALUE);
        headerMap.put(SOURCE, SOURCE_VALUE);
        StandardEvaluationContext context = new StandardEvaluationContext(headerMap);
        ExpressionParser parser = new SpelExpressionParser();
        filePoolerProps.getDestinationdirectories().stream()
                .peek(expressionMap -> log.info("Expression value is {} and result value is {}", expressionMap.get(EXPRESSION), expressionMap.get(RESULT_VALUE)))
                .filter(expressionMap -> Boolean.TRUE.equals(parser.parseExpression(expressionMap.get(EXPRESSION)).getValue(context, Boolean.class)))
                .findFirst()
                .ifPresentOrElse(
                        matchedExpression -> {
                            headerMap.put(DESTINATION_DIRECTORY, matchedExpression.get(RESULT_VALUE));
                            headerMap.put(TOPIC, matchedExpression.get(TOPIC));
                        },
                        () -> {
                            throw new RuntimeException("Unable to enrich destinationDirectory for the request " + headers.get("file_name"));
                        }
                );
        return headerMap;
    }

    private void fileMovementForDownstream(String jsonMessage) throws IOException {
        String fileName = getValueByKeyIfExistsInJsonMessage(jsonMessage, FILE_NAME_PATH);
        String destinationDirectory = Objects.requireNonNull(getValueByKeyIfExistsInJsonMessage(jsonMessage, DESTINATION_PATH)).replaceAll("^\"|\"$", "");
        String sourceLocation = getValueByKeyIfExistsInJsonMessage(jsonMessage, SOURCE_LOCATION_PATH);
        assert sourceLocation != null;
        Path source = Paths.get(sourceLocation);
        Path destination = Paths.get(destinationDirectory, fileName);
        Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
        log.info("File moved successfully from the source {} to destination location {}", source, destination);
    }

    private void publishToKafkaTopic(String jsonMessage) {
        var topic = getValueByKeyIfExistsInJsonMessage(jsonMessage, TOPIC_PATH);
        assert topic != null;
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, jsonMessage);
        future.whenComplete((result, exception) -> {
            if (exception == null) {
                log.info("Message sent to the topic {} and message is {} " +
                        "and kafka offset value is {}", topic, jsonMessage, result.getRecordMetadata().offset());
            }
            if (exception instanceof TimeoutException k) {
                throw new TimeoutException("Failed to publish the message " + k.getMessage());
            }
        });
    }

    private String getValueByKeyIfExistsInJsonMessage(String jsonMessage, String path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(jsonMessage);

            String[] pathElements = path.split("\\.");
            Optional<JsonNode> result = Stream.of(pathElements).reduce(
                    Optional.of(jsonNode),
                    (node, pathElement) -> node.flatMap(n -> Optional.ofNullable(n.get(pathElement))),
                    (a, b) -> b);
            return result.map(JsonNode::asText)
                    .orElseThrow(() -> new RuntimeException("Path not found:" + path));


        } catch (JsonProcessingException jpe) {
            throw new RuntimeException("Unable to get the value for the path parsed from json message " + path);
        }

    }


}
