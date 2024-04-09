package com.springboot.integration.filepooling.temp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestClass {

    private final Map<String, String> instanceMap;

    public void serValues() {
        if (instanceMap.containsKey("key")) {
            displayMethod();
        } else {
            instanceMap.put("key", "value");
        }
    }

    private void displayMethod() {
        log.info("display method called");
    }

}
