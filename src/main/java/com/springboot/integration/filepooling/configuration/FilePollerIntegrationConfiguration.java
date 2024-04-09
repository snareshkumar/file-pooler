package com.springboot.integration.filepooling.configuration;

import com.springboot.integration.filepooling.service.FileEventService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.file.dsl.Files;

import java.io.File;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FilePollerIntegrationConfiguration {

    private final IntegrationFlowContext integrationFlowContext;
    private final FileEventService fileEventService;
    private final FilePoolerProps filePoolerProps;


    @PostConstruct
    public void registerFileIntegrationFlows() {
        log.info("Integration flow registration starting for the directories {}", filePoolerProps.getDirectories());
        for (var directory : filePoolerProps.getDirectories()) {
            IntegrationFlow flow = IntegrationFlow.from(Files.inboundAdapter(new File(directory))
                            .autoCreateDirectory(true)
                            .preventDuplicates(true)
                            .scanEachPoll(true)
                            .getObject(), e -> e.poller(Pollers.fixedDelay(10000)))
                    .handle((message) -> fileEventService.moveFileAndGetKafkaJsonMessage(message.getHeaders()))
                    .get();
            integrationFlowContext.registration(flow)
                    .register();
            log.info("Integration Flow registration completed for the directory {}", directory);
        }
        log.info("Integration Flow registration completed for the directories {}", filePoolerProps.getDirectories());

    }


}
