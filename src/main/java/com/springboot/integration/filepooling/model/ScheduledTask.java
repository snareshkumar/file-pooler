package com.springboot.integration.filepooling.model;

public record ScheduledTask(Long id, String taskName, String cronExpression) {
}
