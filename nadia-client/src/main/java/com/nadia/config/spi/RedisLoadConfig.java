package com.nadia.config.spi;

import com.alibaba.fastjson.JSONObject;
import com.nadia.config.bean.ClientInfo;
import com.nadia.config.bean.ConfigContextHolder;
import com.nadia.config.bean.RemoteContextHolder;
import com.nadia.config.enums.LogLevelEnum;
import com.nadia.config.listener.enumerate.EventType;
import com.nadia.config.publish.RedisPubSub;
import com.nadia.config.redis.ConfigCenterRedisService;
import com.nadia.config.utils.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisLoadConfig extends AbstractLoadConfig {
    @Resource
    private ConfigCenterRedisService configCenterRedisService;
    @Resource
    private RedisPubSub redisPubSub;

    @Override
    public void load() {
        //get redis info
        Map<String, String> agMap = initEnvironment.getClientInfo().getApplicationGroupMap();
        for(String application : agMap.keySet()){
            String group = agMap.get(application);
            this.load(application, group);
        }
    }

    @Override
    public void load(String application, String group) {
        String configKey = RedisKeyUtil.getGroupConfig(application, group);
        Map<String, String> configs = configCenterRedisService.hgetAll(configKey);
        RemoteContextHolder.setRemoteHolder(configKey, configs);
    }

    @Override
    public Object getValue(String key) {
        return null;
    }

    @Override
    public void onlineClientInGroup() {
        Map<String, String> agMap = initEnvironment.getClientInfo().getApplicationGroupMap();
        for(String application : agMap.keySet()){
            String group = agMap.get(application);
            this.onlineClientInGroup(application,group);
        }
    }

    @Override
    public void onlineClientInGroup(String application, String newGroup) {
        ClientInfo clientInfo = initEnvironment.getClientInfo();
        String applicationGroup = RedisKeyUtil.getInstance(application,newGroup);
        configCenterRedisService.sadd(applicationGroup, clientInfo.getName());
        log.info("========================= onlineClientInGroup key[{}] value[{}] =========================", applicationGroup, clientInfo.getName());
    }

    @Override
    public void offlineClientInGroup(String application,String oldGroup) {
        ClientInfo clientInfo = initEnvironment.getClientInfo();
        String applicationGroup = RedisKeyUtil.getInstance(application, oldGroup);
        configCenterRedisService.del(applicationGroup, clientInfo.getName());
        log.info("========================= offlineClientInGroup key[{}] value[{}] =========================", applicationGroup, clientInfo.getName());
    }

    @Override
    public void offlineClientConfigs(String application,String oldGroup) {
        ClientInfo clientInfo = initEnvironment.getClientInfo();
        String instanceConfig = RedisKeyUtil.getInstanceConfig(application, oldGroup, clientInfo.getName());
        configCenterRedisService.del(instanceConfig);
        log.info("========================= offlineClientConfigs key[{}] =========================", instanceConfig);
    }

    @Override
    public void keepClientAlive() {
        ClientInfo clientInfo = initEnvironment.getClientInfo();
        //push client info to redis schedule
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Thread("ConfigCenter-keepAlive") {
            @Override
            public void run() {
                try{
                    log.info("========================= Client Keep Alive with Redis Start =========================");
                    clientInfo.setTimestamp(new Date().getTime());
                    String hashCode = String.valueOf(clientInfo.hashCode());
                    log.info("========================= Client Keep Alive hashCode:[{}] clientInfo:[{}] =========================", hashCode, clientInfo);
                    configCenterRedisService.hset(RedisKeyUtil.getClints(), hashCode, JSONObject.toJSONString(clientInfo));
                    log.info("========================= Client Keep Alive with Redis End   =========================");
                }catch (Exception e){
                    log.error("Client Keep Alive with Redis error:{}",e);
                }
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    @Override
    public void pushClientConfig(String key, String value,String application,String group) {
        String configKey = RedisKeyUtil.getInstanceConfig(application,
                group, initEnvironment.getClientInfo().getName());
        configCenterRedisService.hset(configKey, key, value);
    }

    @Override
    public void pushClientConfigs() {
        Map<String, String> agMap = initEnvironment.getClientInfo().getApplicationGroupMap();
        for(String application : agMap.keySet()) {
            String group = agMap.get(application);
            this.pushClientConfigs(application,group,group);
        }
    }

    @Override
    public void pushClientConfigs(String application,String oldGroup,String newGroup) {
        ClientInfo clientInfo = initEnvironment.getClientInfo();
        configCenterRedisService.del(RedisKeyUtil.getInstanceConfig(application, oldGroup, clientInfo.getName()));
        Map<String, String> clientConfigs = ConfigContextHolder.getClientConfigs(application,newGroup);
        clientConfigs.forEach((k, v) -> {
            configCenterRedisService.hset(RedisKeyUtil.getInstanceConfig(application, newGroup, clientInfo.getName()), k, v);
        });
    }

    @Override
    protected void notificServer(String message,EventType type , LogLevelEnum level) {
        redisPubSub.notifyServer(initEnvironment.getClientInfo(), type, level,message);
    }

    @Override
    public void offlineClientInfo() {
        ClientInfo clientInfo = initEnvironment.getClientInfo();
        configCenterRedisService.del(RedisKeyUtil.getClints(),String.valueOf(clientInfo.hashCode()));
        log.info("========================= delete client info  key[{}] value[{}]=========================", RedisKeyUtil.getClints(),clientInfo.hashCode());
    }

    @Override
    public void pushClinetInfo() {
        ClientInfo clientInfo = initEnvironment.getClientInfo();
        configCenterRedisService.hset(RedisKeyUtil.getClints(), String.valueOf(clientInfo.hashCode()), JSONObject.toJSONString(clientInfo));
        log.info("========================= push client info  key[{}] value[{}]=========================", RedisKeyUtil.getClints(),clientInfo.hashCode());
    }
}
