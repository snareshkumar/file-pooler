package com.springboot.integration.filepooling.configuration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.springboot.integration.filepooling.service.FileEventService;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

class FilePollerIntegrationConfigurationDiffblueTest {
    /**
     * Method under test:
     * {@link FilePollerIntegrationConfiguration#registerFileIntegrationFlows()}
     */
    @Test
    void testRegisterFileIntegrationFlows() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Diffblue AI was unable to find a test

        ProducerFactory<String, Object> producerFactory = mock(ProducerFactory.class);
        when(producerFactory.transactionCapable()).thenReturn(true);
        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        FileEventService fileEventService = new FileEventService(new FilePoolerProps(), kafkaTemplate);

        FilePoolerProps filePoolerProps = new FilePoolerProps();
        filePoolerProps.setDirectories(new ArrayList<>());
        (new FilePollerIntegrationConfiguration(null, fileEventService, filePoolerProps)).registerFileIntegrationFlows();
        verify(producerFactory).transactionCapable();
    }
}
