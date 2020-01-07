package com.nadia.config.bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RemoteContextHolder {

    private static Map<String, Map<String, String>> remoteHolder = new ConcurrentHashMap<>();

    public static void setRemoteHolder(String application, Map<String, String> values) {
        remoteHolder.put(application, values);
    }

    public static Map<String, String> getRemoteHolder(String application) {
        return remoteHolder.get(application);
    }
}
