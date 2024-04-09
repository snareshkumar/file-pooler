package com.springboot.integration.filepooling.temp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) {
        //String jsonString = "{\"header\":{\"file_originalFile\":\"/home/nareshkumars/MySpace/temp/source1/testdemo.txt\",\"id\":\"9164c8de-dbcb-7e07-19de-64ce0f8b0052\",\"file_name\":\"testdemo.txt\",\"file_relativePath\":\"testdemo.txt\",\"timestamp\":1702534536017,\"serviceName\":\"mt-event-router\",\"source\":\"CALM-FILE POLLING\",\"destinationDirectory\":\"/home/nareshkumars/MySpace/temp/destination1/\",\"topic\":\"testtopic\"},\"data\":{},\"error\":{}}";
        String jsonString = """
                {"isbn": "123-456-222", \s
                 "author":\s
                    {
                      "lastname": "Doe",
                      "firstname": "Jane"
                    },
                "editor":\s
                    {
                      "lastname": "Smith",
                      "firstname": "Jane",
                      "books":{
                        "name":"naresh"
                      }
                    },
                  "title": "The Ultimate Database Study Guide", \s
                  "category": ["Non-Fiction", "Technology"]
                 }
                """;
        String dynamicPath = "editor.books.name"; // Example dynamic path

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(jsonString);

            String[] pathElements = dynamicPath.split("\\.");

            Optional<JsonNode> result = Stream.of(pathElements)
                    .reduce(Optional.of(jsonNode), (node, pathElement) ->
                            node.flatMap(n -> Optional.ofNullable(n.get(pathElement))), (a, b) -> b);

            String value = result.map(JsonNode::asText)
                    .orElseThrow(() -> new RuntimeException("Path not found in the JSON: " + dynamicPath));

            System.out.println("Value at dynamic path '" + dynamicPath + "': " + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
