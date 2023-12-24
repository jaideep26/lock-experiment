package com.sidhucodes.lockexperiment.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.amazonaws.services.dynamodbv2.LockItem;

public class LockItemUtil {

    public static String getData(LockItem lockItem) {
        ByteBuffer dataByteBuffer = lockItem.getData().orElse(null);
        return dataByteBuffer != null ? StandardCharsets.UTF_8.decode(dataByteBuffer).toString() : null;
    }

}
