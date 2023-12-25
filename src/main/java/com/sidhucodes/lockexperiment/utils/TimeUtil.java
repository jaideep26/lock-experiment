package com.sidhucodes.lockexperiment.utils;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtil {

    public static LocalDateTime getLocalDateTime(String data) {
        return LocalDateTime.parse(data);
    }

    public static Duration getDurationSinceNow(LocalDateTime localDateTime) {
        return Duration.between(localDateTime, LocalDateTime.now());
    }

    public static String now() {
        return LocalDateTime.now().toString();
    }
}
