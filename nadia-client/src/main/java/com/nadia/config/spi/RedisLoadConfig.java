package com.nadia.config.spi;

import com.alibaba.fastjson.JSONObject;
import com.nadia.config.bean.ClientInfo;
import com.nadia.config.enums.LogLevelEnum;
import com.nadia.config.listener.enumerate.EventType;
import com.nadia.config.publish.RedisPubSub;
import com.nadia.config.redis.RedisService;
import com.nadia.config.utils.RedisKeyUtil;
import com.nadia.config.bean.ConfigContextHolder;
import com.nadia.config.bean.RemoteContextHolder;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisLoadConfig extends AbstractLoadConfig {
    @Resource
    private RedisService redisService;
    @Resource
    private RedisPubSub redisPubSub;

    @Override
    public void load() {
        //get redis info
        this.load(initEnvironment.getClientInfo().getApplication(), initEnvironment.getClientInfo().getGroup());
    }

    @Override
    public void load(String application, String group) {
        String configKey = RedisKeyUtil.getGroupConfig(application, group);
        Map<String, String> configs = redisService.hgetAll(configKey);
        RemoteContextHolder.setRemoteHolder(configKey, configs);
    }

    @Override
    public Object getValue(String key) {
        return null;
    }

    @Override
    public void onlineClientInGroup() {
        ClientInfo clientInfo = initEnvironment.getClientInfo();
        String applicationGroup = RedisKeyUtil.getInstance(clientInfo.getApplication(), clientInfo.getGroup());
        redisService.sadd(applicationGroup, clientInfo.getName());
        log.info("========================= onlineClientInGroup key[{}] value[{}] =========================", applicationGroup, clientInfo.getName());
    }

    @Override
    public void offlineClientInGroup() {
        ClientInfo clientInfo = initEnvironment.getClientInfo();
        String applicationGroup = RedisKeyUtil.getInstance(clientInfo.getApplication(), clientInfo.getGroup());
        redisService.del(applicationGroup, clientInfo.getName());
        log.info("========================= offlineClientInGroup key[{}] value[{}] =========================", applicationGroup, clientInfo.getName());
    }

    @Override
    public void offlineClientConfigs() {
        ClientInfo clientInfo = initEnvironment.getClientInfo();
        String instanceConfig = RedisKeyUtil.getInstanceConfig(clientInfo.getApplication(), clientInfo.getGroup(), clientInfo.getName());
        redisService.del(instanceConfig);
        log.info("========================= offlineClientConfigs key[{}] =========================", instanceConfig);
    }

    @Override
    public void keepClientAlive() {
        ClientInfo clientInfo = initEnvironment.getClientInfo();
        //push client info to redis schedule
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                log.info("========================= Client Keep Alive with Redis Start =========================");
                clientInfo.setTimestamp(new Date().getTime());
                String hashCode = String.valueOf(clientInfo.hashCode());
                log.info("========================= Client Keep Alive hashCode:[{}] clientInfo:[{}] =========================", hashCode, clientInfo);
                redisService.hset(RedisKeyUtil.getClints(), hashCode, JSONObject.toJSONString(clientInfo));
                log.info("========================= Client Keep Alive with Redis End   =========================");
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    @Override
    public void pushClientConfig(String key, String value) {
        String configKey = RedisKeyUtil.getInstanceConfig(initEnvironment.getClientInfo().getApplication(),
                initEnvironment.getClientInfo().getGroup(), initEnvironment.getClientInfo().getName());
        redisService.hset(configKey, key, value);
    }

    @Override
    public void pushClientConfigs() {
        ClientInfo clientInfo = initEnvironment.getClientInfo();
        redisService.del(RedisKeyUtil.getInstanceConfig(clientInfo.getApplication(), clientInfo.getGroup(), clientInfo.getName()));
        Map<String, String> clientConfigs = ConfigContextHolder.getClientConfigs();
        clientConfigs.forEach((k, v) -> {
            redisService.hset(RedisKeyUtil.getInstanceConfig(clientInfo.getApplication(), clientInfo.getGroup(), clientInfo.getName()), k, v);
        });
    }

    @Override
    protected void notificServer(String message, EventType type , LogLevelEnum level) {
        redisPubSub.notifyServer(initEnvironment.getClientInfo(), type, level,message);
    }

    @Override
    public void offlineClientInfo() {
        ClientInfo clientInfo = initEnvironment.getClientInfo();
        redisService.del(RedisKeyUtil.getClints(),String.valueOf(clientInfo.hashCode()));
        log.info("========================= delete client info  key[{}] value[{}]=========================", RedisKeyUtil.getClints(),clientInfo.hashCode());
    }

    @Override
    public void pushClinetInfo() {
        ClientInfo clientInfo = initEnvironment.getClientInfo();
        redisService.hset(RedisKeyUtil.getClints(), String.valueOf(clientInfo.hashCode()), JSONObject.toJSONString(clientInfo));
        log.info("========================= push client info  key[{}] value[{}]=========================", RedisKeyUtil.getClints(),clientInfo.hashCode());
    }
}
