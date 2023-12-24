package com.sidhucodes.lockexperiment.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.LockItem;
import com.sidhucodes.lockexperiment.lock.LockClient;

@Component
public class ScheduledTasks {

    @Autowired
    private LockClient lockClient;

    @Scheduled(cron = "*/10 * * * * *")
    public void executeScheduledTasks() {

        LockItem lock = lockClient.getLock("Scheduler");

        if (lock != null && !lock.isExpired()) {
            System.err.println("Executing job.");
        } else {
            System.err.println("Skipping");
        }
    }
}
