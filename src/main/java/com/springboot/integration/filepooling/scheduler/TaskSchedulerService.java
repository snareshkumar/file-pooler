package com.springboot.integration.filepooling.scheduler;

import com.springboot.integration.filepooling.model.ScheduledTask;
import com.springboot.integration.filepooling.scheduler.BusinessLogicService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskSchedulerService {


    private final TaskScheduler taskScheduler;
    private final BusinessLogicService businessLogicService;

    // Inject business logic service for execution


    @PostConstruct
    public void initializeTasks() {
        ScheduledTask task1 = new ScheduledTask(1L, "task1", "0 */2 * ? * *");
        ScheduledTask task2 = new ScheduledTask(2L, "task2", "0 */3 * ? * *");
        List<ScheduledTask> tasks = List.of(task1, task2);
        log.info("Size of scheduled tasks are {}", tasks.size());
        for (ScheduledTask task : tasks) {
            scheduleTask(task);
        }
    }

    public void scheduleTask(ScheduledTask task) {
        Runnable runnableTask = () -> businessLogicService.executeBusinessLogic(task.taskName());
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(runnableTask, new CronTrigger(task.cronExpression()));
    }

    private void cancelTask(ScheduledTask task) {
        // Implement task cancellation logic here
    }
}
