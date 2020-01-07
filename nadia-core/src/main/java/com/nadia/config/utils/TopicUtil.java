package com.nadia.config.utils;

/**
 * @author xiang.shi
 * @date 2019-12-09 10:39
 */
public class TopicUtil {
    private static final String PREFIX = "config";
    private static final String SPLIT = ":";
    private static final String TOPIC = PREFIX + SPLIT + "topic";
    private static final String TOPIC_APPLICATION = TOPIC + SPLIT + "application:";
    private static final String TOPIC_CLIENT_MESSAGE = TOPIC + SPLIT + "clientMessage";


    public static String getTopicApplication(String application){
        return TOPIC_APPLICATION + application;
    }

    public static String getTopicClientMessage(){
        return TOPIC_CLIENT_MESSAGE;
    }
}
