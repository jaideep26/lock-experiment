package com.sidhucodes.lockexperiment.controllers;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.dynamodbv2.LockItem;
import com.sidhucodes.lockexperiment.lock.LockClient;
import com.sidhucodes.lockexperiment.models.GetLockInfoResponse;

@RestController
@RequestMapping("/lock")
public class LockController {

    @Autowired
    private LockClient lockClient;

    @GetMapping("/info")
    public GetLockInfoResponse getCurrentLockHolderInfo(@RequestParam String partitionKey) {
        GetLockInfoResponse response = new GetLockInfoResponse();

        LockItem lockItem = lockClient.getLock(partitionKey);

        if (lockItem != null) {
            response.setKey(lockItem.getPartitionKey());
            response.setAddress(lockItem.getAdditionalAttributes().get("address").s());
            response.setOwnerName(lockItem.getOwnerName());
            response.setRegion(lockItem.getAdditionalAttributes().get("region").s());

            ByteBuffer dataByteBuffer = lockItem.getData().get();
            response.setData(dataByteBuffer != null ? StandardCharsets.UTF_8.decode(dataByteBuffer).toString() : null);
        }

        return response;
    }

}
