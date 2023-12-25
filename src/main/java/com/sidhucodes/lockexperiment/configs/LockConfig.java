package com.sidhucodes.lockexperiment.configs;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.amazonaws.services.dynamodbv2.AcquireLockOptions;
import com.amazonaws.services.dynamodbv2.LockItem;
import com.google.common.collect.Maps;
import com.sidhucodes.lockexperiment.lock.LockClient;
import com.sidhucodes.lockexperiment.utils.LockItemUtil;
import com.sidhucodes.lockexperiment.utils.TimeUtil;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Configuration
public class LockConfig {

    @Value("${lock.region}")
    private String lockRegion;

    @Value("${server.port}")
    private Integer port;

    @Value("${lock.leaseDuration}")
    private Long leaseDuration;

    @Value("${lock.heartbeatPeriod}")
    private Long heartbeatPeriod;

    @Autowired
    private DynamoDbClient dynamoDB;

    private LockClient lockClient = null;

    @Bean
    LockClient lockClient() throws Exception {
        if (this.lockClient == null) {
            this.lockClient = new LockClient(dynamoDB, "LOCK", leaseDuration, heartbeatPeriod);
        }
        return this.lockClient;
    }

    @Scheduled(cron = "*/3 * * * * *")
    public void sendHeartbeat() throws Exception {

        LockItem lock = lockClient().getLock("Scheduler");

        if (lock == null) {
            lock = lockClient().acquireLock(acquireLockOptions());
        } else if (lock.isExpired()) {
            lock.close();

            String data = LockItemUtil.getData(lock);

            if (data == null) {
                System.err.println("ERROR: Data was null, but should not be");
            } else {
                Duration duration = TimeUtil.getDurationSinceNow(TimeUtil.getLocalDateTime(data));
                if (duration.getSeconds() > leaseDuration * 2) {
                    System.err.println("Need to force re-election");
                    lock = lockClient().acquireLock(forceAcquireLockOptions(LocalDateTime.now().toString()));
                }
            }

        } else {
            lockClient().sendHeartbeat(lock);
        }

    }

    AcquireLockOptions acquireLockOptions() throws Exception {
        return AcquireLockOptions.builder("Scheduler")//
                .withAdditionalAttributes(additionalAttributes())//
                .withShouldSkipBlockingWait(true)//
                .withTimeUnit(TimeUnit.SECONDS)//
                .build();
    }

    AcquireLockOptions forceAcquireLockOptions(String data) throws Exception {
        return AcquireLockOptions.builder("Scheduler")//
                .withAdditionalAttributes(additionalAttributes())//
                .withData(ByteBuffer.wrap(data.getBytes()))//
                .withReplaceData(true)//
                .withTimeUnit(TimeUnit.SECONDS)//
                .build();

    }

    @Bean
    Map<String, AttributeValue> additionalAttributes() throws Exception {
        Map<String, AttributeValue> additionalAttributes = Maps.newHashMap();
        additionalAttributes.put("region", AttributeValue.builder().s(lockRegion).build());
        additionalAttributes.put("createdOn", AttributeValue.builder().s(LocalDateTime.now().toString()).build());
        additionalAttributes.put("address",
                AttributeValue.builder().s(InetAddress.getLocalHost().getHostAddress() + ":" + String.valueOf(port))
                        .build());
        return additionalAttributes;
    }
}
