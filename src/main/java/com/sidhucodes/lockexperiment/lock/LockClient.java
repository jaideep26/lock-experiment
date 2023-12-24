package com.sidhucodes.lockexperiment.lock;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.amazonaws.services.dynamodbv2.AcquireLockOptions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBLockClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBLockClientOptions;
import com.amazonaws.services.dynamodbv2.LockItem;
import com.amazonaws.services.dynamodbv2.SendHeartbeatOptions;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class LockClient {

    private AmazonDynamoDBLockClient awsLockClient;

    public LockClient(DynamoDbClient dynamoDB, String tableName, Long leaseDuration, Long heartbeatPeriod) {
        this.awsLockClient = new AmazonDynamoDBLockClient(
                AmazonDynamoDBLockClientOptions.builder(dynamoDB, "LOCK") //
                        .withTimeUnit(TimeUnit.SECONDS) //
                        .withLeaseDuration(leaseDuration) //
                        .withHeartbeatPeriod(heartbeatPeriod)//
                        .withCreateHeartbeatBackgroundThread(false) //
                        .build());
    }

    public LockItem acquireLock(AcquireLockOptions acquireLockOptions) throws Exception {
        System.out.println("Acquiring lock");
        return this.awsLockClient.acquireLock(acquireLockOptions);
    }

    public LockItem getLock(String partitionKey) {
        return this.awsLockClient.getLock(partitionKey, Optional.empty()).orElse(null);
    }

    public Boolean releaseLock(LockItem lockItem) {
        System.out.println("Releasing lock");
        return this.awsLockClient.releaseLock(lockItem);
    }

    public void sendHeartbeat(LockItem lock) {
        String time = LocalDateTime.now().toString();
        this.awsLockClient.sendHeartbeat(SendHeartbeatOptions.builder(lock)//
                .withData(ByteBuffer.wrap(time.getBytes())).build());
    }
}