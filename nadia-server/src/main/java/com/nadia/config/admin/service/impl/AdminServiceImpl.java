package com.nadia.config.admin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.nadia.config.admin.service.AdminService;
import com.nadia.config.admin.dto.response.CountsResponse;
import com.nadia.config.admin.dto.response.ErrorResponse;
import com.nadia.config.bean.ClientInfo;
import com.nadia.config.bean.ClientValueBody;
import com.nadia.config.enums.ConfigStatusEnum;
import com.nadia.config.meta.domain.Application;
import com.nadia.config.meta.domain.Config;
import com.nadia.config.meta.domain.Group;
import com.nadia.config.meta.repo.ApplicationRepo;
import com.nadia.config.meta.repo.ConfigRepo;
import com.nadia.config.meta.repo.GroupRepo;
import com.nadia.config.notification.service.TaskService;
import com.nadia.config.redis.RedisService;
import com.nadia.config.utils.RedisKeyUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private RedisService redisService;
    @Resource
    private TaskService taskService;
    @Resource
    private ApplicationRepo applicationRepo;
    @Resource
    private GroupRepo groupRepo;
    @Resource
    private ConfigRepo configRepo;

    @Override
    public void initRedisFromDB() {

        List<Application> applicationList = applicationRepo.selectAll();

        applicationList.forEach(application -> {
            redisService.sadd(RedisKeyUtil.getApplication(),application.getName());
            List<Group> groups = groupRepo.selectByApplicationId(application.getId());
            groups.forEach(group -> {
                redisService.sadd(RedisKeyUtil.getGroup(application.getName()),group.getName());
                List<Config> configs = configRepo.selectByApplicationIdAndGroupId(application.getId(), group.getId());
                configs.forEach(config -> {
                    if(ConfigStatusEnum.PUBLISHED.getStatus().equals(config.getStatus())){
                        redisService.hset(RedisKeyUtil.getGroupConfig(application.getName(),group.getName()),config.getKey(),config.getValue());
                    }
                });
            });
        });
    }

    @Override
    public void syncApplicationFromDB(String application) {
        boolean redisExist = redisService.exists(RedisKeyUtil.getApplication(), application);
        long dbExist = applicationRepo.countByName(application);
        if(!redisExist && dbExist > 0){
            redisService.sadd(RedisKeyUtil.getApplication(), application);
        }
    }

    @Override
    public void syncGroupFromDb(String application, String group) {
        boolean redisExist = redisService.exists(RedisKeyUtil.getGroup(application), group);
        Application applicationInfo = applicationRepo.selectByName(application);
        long dbExist = groupRepo.countByApplicationIdAndName(applicationInfo.getId(), group);
        if(!redisExist && dbExist > 0){
            redisService.sadd(RedisKeyUtil.getGroup(application),group);
        }
    }

    @Override
    public void syncConfigFromDb(String application, String group) {

    }

    @Override
    public CountsResponse getCounts() {
        int taskCount = taskService.count();
        CountsResponse countsResponse = new CountsResponse();
        countsResponse.setTaskCount(taskCount);

        Map<String, String> clientMap = redisService.hgetAll(RedisKeyUtil.getClints());
        for(String c : clientMap.keySet()){
            ClientInfo client = JSONObject.parseObject(clientMap.get(c), ClientInfo.class);
            Map<String, String> groupConfigs = redisService.hgetAll(RedisKeyUtil.getGroupConfig(client.getApplication(), client.getGroup()));
            Map<String, String> instanceConfigs = redisService.hgetAll(RedisKeyUtil.getInstanceConfig(client.getApplication(), client.getGroup(), client.getName()));
            for(String key : instanceConfigs.keySet()){
                boolean keyExist = groupConfigs.containsKey(key);
                if(!keyExist){
                    countsResponse.setErrorCount(1);
                    return countsResponse;
                }else {
                    String groupValue = groupConfigs.get(key);
                    ClientValueBody clientValueBody = JSONObject.parseObject(instanceConfigs.get(key), ClientValueBody.class);
                    if(groupValue.equals(clientValueBody.getNewValue())){
                        countsResponse.setErrorCount(1);
                        return countsResponse;
                    }
                }
            }
        }
        return countsResponse;
    }

    @Override
    public List<ErrorResponse> getErrors() {
        Map<String, String> clientMap = redisService.hgetAll(RedisKeyUtil.getClints());
        Collection<String> clientInfos = clientMap.values();
        List<ErrorResponse> responses = new LinkedList<>();
        clientInfos.forEach(clientInfo -> {
            ClientInfo client = JSONObject.parseObject(clientInfo, ClientInfo.class);
            Map<String, String> groupConfigs = redisService.hgetAll(RedisKeyUtil.getGroupConfig(client.getApplication(), client.getGroup()));
            Map<String, String> instanceConfigs = redisService.hgetAll(RedisKeyUtil.getInstanceConfig(client.getApplication(), client.getGroup(), client.getName()));
            instanceConfigs.forEach((k,v) -> {
                boolean keyExist = groupConfigs.containsKey(k);
                if(!keyExist){
                    ErrorResponse nokey = new ErrorResponse();
                    nokey.setType("Key is not exist");
                    nokey.setError("Application["+ client.getApplication() +"] Group[" + client.getGroup() + "] Instance[" + client.getName() + "] Key["+ k + "]");
                    responses.add(nokey);
                }else {
                    String groupValue = groupConfigs.get(k);
                    ClientValueBody clientValueBody = JSONObject.parseObject(v, ClientValueBody.class);
                    if(!groupValue.equals(clientValueBody.getNewValue())){
                        ErrorResponse valueError = new ErrorResponse();
                        valueError.setType("ClientValue <> ServerValue");
                        valueError.setError("Application["+ client.getApplication() +"] Group[" + client.getGroup() + "] Instance[" + client.getName() + "] Key["+ k + "] ClientValue["+clientValueBody.getNewValue()+"] ServerValue["+groupValue+"]");
                        responses.add(valueError);
                    }
                }
            });
        });

        return responses;
    }
}
