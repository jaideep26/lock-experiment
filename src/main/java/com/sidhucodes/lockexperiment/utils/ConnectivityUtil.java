package com.sidhucodes.lockexperiment.utils;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ConnectivityUtil {

    public static boolean checkConnectivity(String address) {
        RestTemplate restTemplate = new RestTemplate();
        String addressUrl = "http://" + address + "/lock";
        ResponseEntity<String> response = restTemplate.getForEntity(addressUrl, String.class);
        HttpStatusCode statusCode = response.getStatusCode();
        return HttpStatusCode.valueOf(200).equals(statusCode);
    }

}
