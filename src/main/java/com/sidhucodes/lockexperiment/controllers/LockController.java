package com.sidhucodes.lockexperiment.controllers;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.services.dynamodbv2.AcquireLockOptions;
import com.amazonaws.services.dynamodbv2.LockItem;
import com.sidhucodes.lockexperiment.lock.LockClient;
import com.sidhucodes.lockexperiment.models.GetLockInfoResponse;
import com.sidhucodes.lockexperiment.models.NewLockOwnerRequest;
import com.sidhucodes.lockexperiment.utils.ConnectivityUtil;
import com.sidhucodes.lockexperiment.utils.LockItemUtil;
import com.sidhucodes.lockexperiment.utils.TimeUtil;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@RestController
@RequestMapping("/lock")
public class LockController {

    @Autowired
    private LockClient lockClient;

    @Autowired
    Map<String, AttributeValue> additionalAttributes;

    private RestTemplate restTemplate = new RestTemplate();

    @GetMapping()
    public String base() {
        return "Healthy";
    }

    @GetMapping("/info")
    public GetLockInfoResponse getCurrentLockHolderInfo(@RequestParam String partitionKey) {
        GetLockInfoResponse response = new GetLockInfoResponse();

        LockItem lockItem = lockClient.getLock(partitionKey);

        if (lockItem != null) {
            response.setKey(lockItem.getPartitionKey());
            response.setAddress(lockItem.getAdditionalAttributes().get("address").s());
            response.setOwnerName(lockItem.getOwnerName());
            response.setRegion(lockItem.getAdditionalAttributes().get("region").s());
            response.setData(LockItemUtil.getData(lockItem));
        }

        return response;
    }

    @PostMapping("/switch")
    public Boolean switchLockOwner(@RequestBody NewLockOwnerRequest request) {

        Boolean success = false;

        System.err.print("Checking availability of new lock owner... ");
        if (ConnectivityUtil.checkConnectivity(request.getAddress())) {
            System.err.println("Able to connect");

            LockItem lock = lockClient.getLock(request.getPartitionKey());
            if (lock != null && !lock.isExpired()) {
                lockClient.releaseLock(lock);

                String url = "http://" + request.getAddress() + "/lock/acquire?partitionKey="
                        + request.getPartitionKey();
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                success = response.getStatusCode().is2xxSuccessful();
            }

        }

        return success;
    }

    @GetMapping("/acquire")
    public String acquireLock(@RequestParam String partitionKey) {

        String response = null;
        try {
            lockClient.acquireLock(AcquireLockOptions.builder("Scheduler")//
                    .withAdditionalAttributes(additionalAttributes)//
                    .withData(ByteBuffer.wrap(TimeUtil.now().getBytes()))//
                    .withReplaceData(true)//
                    .withTimeUnit(TimeUnit.SECONDS)//
                    .build());
            response = "Success";
        } catch (Exception e) {
            e.printStackTrace();
            response = "Fail";
        }

        return response;
    }

}
