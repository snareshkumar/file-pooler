package com.springboot.integration.filepooling.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BusinessLogicService {

    public void executeBusinessLogic(String taskName) {
        // Implement business logic corresponding to the taskName
        // Example: communicate via Kafka or execute some service logic
        if (taskName.equalsIgnoreCase("task1")) {
            log.info("1======task name executed {}", taskName);
        }
        if (taskName.equalsIgnoreCase("task2")) {
            log.info("2=======task name executed {}", taskName);

        }
    }


}
