package com.springboot.integration.filepooling.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.springboot.integration.filepooling.configuration.FilePoolerProps;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.kafka.common.errors.TimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {FileEventService.class, FilePoolerProps.class})
@ExtendWith(SpringExtension.class)
class FileEventServiceDiffblueTest {
    @Autowired
    private FileEventService fileEventService;

    @Autowired
    private FilePoolerProps filePoolerProps;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Method under test:
     * {@link FileEventService#moveFileAndGetKafkaJsonMessage(MessageHeaders)}
     */
    @Test
    void testMoveFileAndGetKafkaJsonMessage() {
        MessageHeaders headers = mock(MessageHeaders.class);
        when(headers.isEmpty()).thenReturn(true);
        fileEventService.moveFileAndGetKafkaJsonMessage(headers);
        verify(headers).isEmpty();
    }

    /**
     * Method under test:
     * {@link FileEventService#moveFileAndGetKafkaJsonMessage(MessageHeaders)}
     */
    @Test
    void testMoveFileAndGetKafkaJsonMessage2() {
        MessageHeaders headers = mock(MessageHeaders.class);
        when(headers.isEmpty()).thenThrow(new TimeoutException("An error occurred"));
        fileEventService.moveFileAndGetKafkaJsonMessage(headers);
        verify(headers).isEmpty();
    }

    /**
     * Method under test:
     * {@link FileEventService#moveFileAndGetKafkaJsonMessage(MessageHeaders)}
     */
    @Test
    void testMoveFileAndGetKafkaJsonMessage3() {
        HashSet<Map.Entry<String, Object>> entrySet = new HashSet<>();
        entrySet.add(new AbstractMap.SimpleEntry<>("=== File Polling Event Started ===", "42"));
        MessageHeaders headers = mock(MessageHeaders.class);
        when(headers.isEmpty()).thenReturn(false);
        when(headers.entrySet()).thenReturn(entrySet);
        fileEventService.moveFileAndGetKafkaJsonMessage(headers);
        verify(headers).entrySet();
        verify(headers).isEmpty();
    }

    /**
     * Method under test:
     * {@link FileEventService#moveFileAndGetKafkaJsonMessage(MessageHeaders)}
     */
    @Test
    void testMoveFileAndGetKafkaJsonMessage4() {
        HashSet<Map.Entry<String, Object>> entrySet = new HashSet<>();
        entrySet.add(null);
        MessageHeaders headers = mock(MessageHeaders.class);
        when(headers.isEmpty()).thenReturn(false);
        when(headers.entrySet()).thenReturn(entrySet);
        fileEventService.moveFileAndGetKafkaJsonMessage(headers);
        verify(headers).entrySet();
        verify(headers).isEmpty();
    }
}
