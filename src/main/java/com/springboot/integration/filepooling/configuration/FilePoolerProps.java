package com.springboot.integration.filepooling.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "file-pooler")
@Data
public class FilePoolerProps {

    private List<String> directories;

    private List<Map<String, String>> destinationdirectories;


}
