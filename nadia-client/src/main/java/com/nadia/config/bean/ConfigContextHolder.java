package com.nadia.config.bean;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigContextHolder {

    private static Map<String, List<Config>> configHolder = new ConcurrentHashMap<>();

    private static Map<String, List<Config>> configExistsCallbackHolder = new ConcurrentHashMap<>();

    private static Map<String, String> clientConfigsHolder = new ConcurrentHashMap<>();

    private static final String CALLBACK_SUFFIX = "_hasCallback";

    public static Object getOldValue(String key) {
        List<Config> configs = configHolder.get(key);
        if (CollectionUtils.isEmpty(configs)) {
            return null;
        }
        return configs.get(0).getOldValue();
    }

    public static void setConfigHolder(String key, Config config) {
        if(config == null){
            return;
        }
        putIntoConfigHolder(key, config, configHolder);
        if (config.getCallback() != null) {
            putIntoConfigHolder(key + CALLBACK_SUFFIX, config, configExistsCallbackHolder);
        }
    }

    private static void putIntoConfigHolder(String key, Config config, Map<String, List<Config>> configHolderMap) {
        List<Config> configs;
        if (configHolderMap.containsKey(key)) {
            configs = configHolderMap.get(key);
        } else {
            configs = new ArrayList<>();
            configHolderMap.put(key, configs);
        }
        configs.add(config);
    }

    public static List<Config> getConfigs(String key) {
        return configHolder.get(key);
    }

    public static Map<String, List<Config>> getAllConfigs(){
        return configHolder;
    }

    public static List<Config> getExistsCallbackConfigs(String key) {
        return configExistsCallbackHolder.get(key + CALLBACK_SUFFIX);
    }

    public static void setClientConfigsHolder(String key ,Object oldValue, Object newValue){
        ClientValueBody body = new ClientValueBody();
        body.setNewValue(newValue);
        body.setOldValue(oldValue);
        clientConfigsHolder.put(key, JSONObject.toJSONString(body));
    }
    public static Map<String, String> getClientConfigs(){
        return clientConfigsHolder;
    }
}
