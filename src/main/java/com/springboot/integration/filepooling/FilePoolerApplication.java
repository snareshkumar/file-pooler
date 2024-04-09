package com.springboot.integration.filepooling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@EnableIntegration
public class FilePoolerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilePoolerApplication.class, args);
	}

}
