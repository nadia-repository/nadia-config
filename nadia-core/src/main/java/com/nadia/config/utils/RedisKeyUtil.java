package com.nadia.config.utils;

public class RedisKeyUtil {
    private static final String PREFIX = "config";
    private static final String SPLIT = ":";
    private static final String GROUP = "group";
    private static final String INSTANCE = "instance";
    private static final String CONFIG = "configs";
    private static final String APPLICATION = PREFIX + SPLIT + "application";
    private static final String SERVER_INFO = PREFIX + SPLIT + "serverInfo";
    private static final String SERVER = "server";
    private static final String CLIENT = "client";
    private static final String TOKEN = "token";


    /**
     * 命名空间
     *
     * @return
     */
    public static String getApplication() {
        return APPLICATION;
    }

    public static String getGroup(String application) {
        return PREFIX + SPLIT + application + SPLIT + GROUP;
    }

    public static String getInstance(String application, String group) {
        return PREFIX + SPLIT + application + SPLIT + group + SPLIT + INSTANCE;
    }

    public static String getGroupConfig(String application, String group) {
        return PREFIX + SPLIT + application + SPLIT + group + SPLIT + CONFIG;
    }

    public static String getInstanceConfig(String application, String group, String instance) {
        return PREFIX + SPLIT + application + SPLIT + group + SPLIT + instance + SPLIT + CONFIG;
    }

    public static String getInstanceName(String nameOrIP, String port) {
        return nameOrIP + SPLIT + port;
    }

    public static String getServers(){
        return SERVER_INFO + SPLIT + SERVER;
    }

    public static String getClints(){
        return SERVER_INFO + SPLIT + CLIENT;
    }

    public static String getClientRetryTimes(String hashCode){
        return SERVER_INFO + SPLIT + CLIENT + SPLIT + hashCode;
    }

    public static String getToken(String token){
        return PREFIX + SPLIT + TOKEN + SPLIT + token;
    }

    public static void main(String[] args) {
        String instance = RedisKeyUtil.getInstanceConfig("Trade", "Default", "10.0.0.1");
        System.out.println(instance);
        String group = RedisKeyUtil.getGroupConfig("Trade", "Default");
        System.out.println(group);
    }
}
