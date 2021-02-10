package com.nadia.config.bean;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigContextHolder {

    private static Map<String, Map<String, List<Config>>> agConfigHolder = new ConcurrentHashMap<>();

    private static Map<String, Map<String, List<Config>>> agConfigExistsCallbackHolder = new ConcurrentHashMap<>();

    private static Map<String, Map<String, String>> agClientConfigsHolder = new ConcurrentHashMap<>();

    private static Map<String, String> agMap = new ConcurrentHashMap<>();

    private static final String CALLBACK_SUFFIX = "_hasCallback";

    private static String mergeAG(String application, String group) {
        return application + ":" + group;
    }

    public static Object getOldValue(String application, String group, String key) {
        String ag = ConfigContextHolder.mergeAG(application, group);
        Map<String, List<Config>> configHolder = agConfigHolder.get(ag);
        if (configHolder == null) {
            return null;
        }

        List<Config> configs = configHolder.get(key);
        if (CollectionUtils.isEmpty(configs)) {
            return null;
        }
        return configs.get(0).getOldValue();
    }

    public static void setConfigHolder(String application, String group, String key, Config config) {
        if (config == null) {
            return;
        }
        putIntoConfigHolder(application, group, key, config, agConfigHolder);
        if (config.getCallback() != null) {
            putIntoConfigHolder(application, group, key + CALLBACK_SUFFIX, config, agConfigExistsCallbackHolder);
        }
        if(!agMap.containsKey(application)){
            agMap.put(application, group);
        }
    }

    private static void putIntoConfigHolder(String application, String group, String key, Config config, Map<String, Map<String, List<Config>>> agConfigHolderMap) {
        String ag = ConfigContextHolder.mergeAG(application, group);
        Map<String, List<Config>> configHolderMap;
        if (agConfigHolderMap.containsKey(ag)) {
            configHolderMap = agConfigHolderMap.get(ag);
        } else {
            configHolderMap = new HashMap<>();
            agConfigHolderMap.put(ag, configHolderMap);
        }

        List<Config> configs;
        if (configHolderMap.containsKey(key)) {
            configs = configHolderMap.get(key);
        } else {
            configs = new ArrayList<>();
            configHolderMap.put(key, configs);
        }
        configs.add(config);
    }

    public static List<Config> getConfigs(String application, String group, String key) {
        String ag = ConfigContextHolder.mergeAG(application, group);
        Map<String, List<Config>> configHolder = agConfigHolder.get(ag);
        return configHolder.get(key);
    }

    public static Map<String, List<Config>> getAllConfigs(String application, String group) {
        String ag = ConfigContextHolder.mergeAG(application, group);
        return agConfigHolder.get(ag);
    }

    public static List<Config> getExistsCallbackConfigs(String application, String group, String key) {
        String ag = ConfigContextHolder.mergeAG(application, group);
        Map<String, List<Config>> configExistsCallbackHolder = agConfigExistsCallbackHolder.get(ag);
        return configExistsCallbackHolder.get(key + CALLBACK_SUFFIX);
    }

    public static void setClientConfigsHolder(String application, String group, String key, Object oldValue, Object newValue) {
        String ag = ConfigContextHolder.mergeAG(application, group);
        Map<String, String> clientConfigsHolder = agClientConfigsHolder.get(ag);
        if(clientConfigsHolder == null){
            clientConfigsHolder = new HashMap<>();
            agClientConfigsHolder.put(ag,clientConfigsHolder);
        }
        ClientValueBody body = new ClientValueBody();
        body.setNewValue(newValue);
        body.setOldValue(oldValue);
        clientConfigsHolder.put(key, JSONObject.toJSONString(body));
    }

    public static Map<String, String> getClientConfigs(String application, String group) {
        String ag = ConfigContextHolder.mergeAG(application, group);
        return agClientConfigsHolder.get(ag);
    }

    public static Map<String,String> getApplicationGroups(){
        return agMap;
    }
}
