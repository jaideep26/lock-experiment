package com.sidhucodes.lockexperiment.utils;

import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectivityUtil {

    public static boolean checkConnectivity(String address) {
        Boolean isReachable = false;
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set timeout to 5 seconds (adjust as needed)
            connection.setConnectTimeout(5000);

            int responseCode = connection.getResponseCode();
            isReachable = (responseCode >= 200 && responseCode < 300);

        } catch (Exception e) {
            isReachable = false;
        }

        return isReachable;
    }

}
