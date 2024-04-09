package com.springboot.integration.filepooling.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.integration.leader.event.OnGrantedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LeaderSelection {
    @EventListener
    public void onGranted(OnGrantedEvent event) {
        log.info("Leader Initiator event called {}", event.getContext().isLeader());
    }
}
