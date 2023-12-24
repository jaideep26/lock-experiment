package com.sidhucodes.lockexperiment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.dynamodbv2.LockItem;
import com.sidhucodes.lockexperiment.lock.LockClient;
import com.sidhucodes.lockexperiment.models.GetLockInfoResponse;
import com.sidhucodes.lockexperiment.models.NewLockOwnerRequest;
import com.sidhucodes.lockexperiment.utils.ConnectivityUtil;
import com.sidhucodes.lockexperiment.utils.LockItemUtil;

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
            response.setData(LockItemUtil.getData(lockItem));
        }

        return response;
    }

    @PostMapping("/switch")
    public Boolean switchLockOwner(@RequestBody NewLockOwnerRequest request) {

        Boolean success = false;
        System.err.println("Attempting to change LockOwner of " + request.getPartitionKey() //
                + " to " + request.getAddress() + " | " + request.getRegion());

        System.err.println("Checking availability of new lock owner");
        if (ConnectivityUtil.checkConnectivity(request.getAddress())) {
            System.err.println("Able to connect");
        } else {
            System.err.println("Unable to connect");
        }

        return success;
    }

}
