package com.nadia.config.meta.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.nadia.config.bean.ClientValueBody;
import com.nadia.config.common.context.UserContextHolder;
import com.nadia.config.common.security.GlobalLock;
import com.nadia.config.common.transaction.AfterCommitTaskRegister;
import com.nadia.config.constant.GroupConstant;
import com.nadia.config.enums.ConfigStatusEnum;
import com.nadia.config.listener.enumerate.EventType;
import com.nadia.config.listener.messageBody.SwitchInstanceMessageBody;
import com.nadia.config.meta.domain.Application;
import com.nadia.config.meta.domain.Config;
import com.nadia.config.meta.domain.Group;
import com.nadia.config.meta.dto.request.ApplicationRequest;
import com.nadia.config.meta.dto.request.CompareRequest;
import com.nadia.config.meta.dto.request.GroupRequest;
import com.nadia.config.meta.dto.request.SwitchGroupInstancesRequest;
import com.nadia.config.meta.dto.response.*;
import com.nadia.config.meta.repo.ApplicationRepo;
import com.nadia.config.meta.repo.ConfigRepo;
import com.nadia.config.meta.repo.GroupRepo;
import com.nadia.config.meta.service.MetadataService;
import com.nadia.config.notification.enums.OperationType;
import com.nadia.config.notification.enums.TargetType;
import com.nadia.config.notification.enums.View;
import com.nadia.config.notification.service.OperationLogService;
import com.nadia.config.publish.RedisPubSub;
import com.nadia.config.redis.RedisService;
import com.nadia.config.utils.ClientValueUtil;
import com.nadia.config.utils.RedisKeyUtil;
import com.nadia.config.utils.TopicUtil;
import com.nadia.config.meta.exception.MetaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MetadataServiceImpl implements MetadataService {

    @Resource
    private ApplicationRepo applicationRepo;
    @Resource
    private GroupRepo groupRepo;
    @Resource
    private ConfigRepo configRepo;
    @Resource
    private RedisService redisService;
    @Resource
    private RedisPubSub redisPubSub;
    @Resource
    private OperationLogService operationLogService;
    @Resource
    private TaskExecutor taskExecutor;

    @Override
    public List<ApplicationResponse> getApplications() {
        List<String> applications = redisService.toList(RedisKeyUtil.getApplication());
        List<ApplicationResponse> responses = applications.stream().map(s -> {
            ApplicationResponse applicationResponse = new ApplicationResponse();
            applicationResponse.setName(s);
            return applicationResponse;
        }).collect(Collectors.toList());
        return responses;
    }

    @Override
    public List<MetadataResponse> list(List<String> applications) {
        List<Application> applicationList = applicationRepo.selectByNames(applications);
        List<MetadataResponse> responses = new LinkedList<>();

        applicationList.forEach(application -> {
            MetadataResponse response = new MetadataResponse();
            response.setApplication(application.getName());
            response.setDescription(application.getDescription());
            response.setMail(application.getEmail());
            response.setId(application.getId());

            List<Group> groups = groupRepo.selectByApplicationId(application.getId());
            if (groups.size() == 1) {
                response.setGroup(groups.get(0).getName());
                List<String> instances = redisService.toList(RedisKeyUtil.getInstance(application.getName(), groups.get(0).getName()));
                response.setInstances(String.join("\n", instances));
//                response.setHasChildren(false);
            } else {
                List<MetadataResponse> childrens = new LinkedList<>();
                groups.forEach(group -> {
                    if (group.getName().equals(GroupConstant.DEFAULT)) {
                        response.setGroup(groups.get(0).getName());
                        List<String> instances = redisService.toList(RedisKeyUtil.getInstance(application.getName(), groups.get(0).getName()));
                        response.setInstances(String.join("\n", instances));
//                        response.setHasChildren(false);
                    } else {
                        MetadataResponse children = new MetadataResponse();
                        BeanUtils.copyProperties(response, children);
                        children.setGroup(group.getName());
                        List<String> instances = redisService.toList(RedisKeyUtil.getInstance(application.getName(), group.getName()));
                        children.setInstances(String.join("\n", instances));
                        children.setId(Long.valueOf(String.valueOf(application.getId()) + String.valueOf(group.getId())));
                        childrens.add(children);
                    }
                });
//                response.setHasChildren(true);
                response.setChildren(childrens);
            }
            responses.add(response);
        });

        return responses;
    }

    @GlobalLock(key = "addApplication")
    @Transactional
    @Override
    public void addApplication(ApplicationRequest applicationRequest) {

        boolean exists = redisService.exists(RedisKeyUtil.getApplication(), applicationRequest.getApplication());
        if (!exists) {
            //DB-application
            Application applicationRecord = new Application();
            applicationRecord.setCreatedBy(UserContextHolder.getUserDetail().getName());
            applicationRecord.setUpdatedBy(UserContextHolder.getUserDetail().getName());
            applicationRecord.setDescription(applicationRequest.getDescription());
            applicationRecord.setEmail(applicationRequest.getMail());
            applicationRecord.setName(applicationRequest.getApplication());
            applicationRepo.insertSelective(applicationRecord);
            operationLogService.log(View.METADATA, OperationType.ADD, TargetType.APPLICATION,"",applicationRecord);

            //DB-group
            Group groupRecord = new Group();
            groupRecord.setApplicationId(applicationRecord.getId());
            groupRecord.setName(GroupConstant.DEFAULT);
            groupRecord.setCreatedBy(UserContextHolder.getUserDetail().getName());
            groupRecord.setUpdatedBy(UserContextHolder.getUserDetail().getName());
            groupRepo.insertSelective(groupRecord);
            operationLogService.log(View.METADATA, OperationType.ADD,TargetType.GROUP,"",groupRecord);

            //push to redis after DB commit
            AfterCommitTaskRegister.registerTask(
                    () -> taskExecutor.execute(
                            () -> addRedisApplication(applicationRequest.getApplication())
                    )
            );
        } else {
            throw new MetaException(2001L);
        }
    }

    private void addRedisApplication(String application){
        //add application
        redisService.sadd(RedisKeyUtil.getApplication(), application);
        //add default group
        redisService.sadd(RedisKeyUtil.getGroup(application), GroupConstant.DEFAULT);
    }

    @GlobalLock(key = "deleteApplication")
    @Transactional
    @Override
    public void deleteApplication(ApplicationRequest applicationRequest) {
        boolean exists = redisService.exists(RedisKeyUtil.getInstance(applicationRequest.getApplication(), applicationRequest.getGroup()));
        if (exists) {
            throw new MetaException(2002L);
        }
        if (GroupConstant.DEFAULT.equals(applicationRequest.getGroup())) {
            Set<String> groups = redisService.toSet(RedisKeyUtil.getGroup(applicationRequest.getApplication()));
            if (groups.size() > 1) {
                throw new MetaException(2003L);
            }
        }
        Application application = applicationRepo.selectByName(applicationRequest.getApplication());
        Group group = groupRepo.selectByApplicationIdAndName(application.getId(), applicationRequest.getGroup());

        //delete group
        groupRepo.deleteByApplicationIdAndGroupId(application.getId(),group.getId());
        operationLogService.log(View.METADATA, OperationType.DELETE,TargetType.GROUP,applicationRequest.getGroup(),"");

        //delete group configs
        configRepo.deleteByApplicationIdAndGroupId(application.getId(), group.getId());
        operationLogService.log(View.METADATA, OperationType.DELETE,TargetType.CONFIG,RedisKeyUtil.getGroupConfig(applicationRequest.getApplication(), applicationRequest.getGroup()),"");

        if (GroupConstant.DEFAULT.equals(applicationRequest.getGroup())) {
            applicationRepo.deleteByName(applicationRequest.getApplication());
            operationLogService.log(View.METADATA,OperationType.DELETE,TargetType.APPLICATION,applicationRequest.getApplication(),"");
        }

        //push to redis after DB commit
        AfterCommitTaskRegister.registerTask(
                () -> taskExecutor.execute(
                        () -> deleteRedisApplication(applicationRequest.getApplication(),applicationRequest.getGroup())
                )
        );
    }

    private void deleteRedisApplication(String application,String group){
        redisService.del(RedisKeyUtil.getGroup(application), group);
        redisService.del(RedisKeyUtil.getGroupConfig(application, group));
        if (GroupConstant.DEFAULT.equals(group)) {
            redisService.del(RedisKeyUtil.getApplication(), application);
        }
    }

    @GlobalLock(key = "updateApplication")
    @Transactional
    @Override
    public void updateApplication(ApplicationRequest applicationRequest) {
        Application sourceApplication = applicationRepo.selectByName(applicationRequest.getApplication());
        Application targetApplication = new Application();
        BeanUtils.copyProperties(sourceApplication,targetApplication);
        targetApplication.setDescription(applicationRequest.getDescription());
        targetApplication.setEmail(applicationRequest.getMail());
        targetApplication.setUpdatedBy(UserContextHolder.getUserDetail().getName());
        applicationRepo.updateByPrimaryKeySelective(targetApplication);
        operationLogService.log(View.METADATA,OperationType.MODIFY,TargetType.APPLICATION,sourceApplication,targetApplication);
    }

    @Override
    public List<GroupResponse> getGroups(String application) {
        List<String> groups = redisService.toList(RedisKeyUtil.getGroup(application));
        List<GroupResponse> responses = groups.stream().map(s -> {
            GroupResponse response = new GroupResponse();
            response.setName(s);
            return response;
        }).collect(Collectors.toList());
        return responses;
    }

    @GlobalLock(key = "addGroup")
    @Transactional
    @Override
    public void addGroup(GroupRequest groupRequest) {
        boolean exists = redisService.exists(RedisKeyUtil.getGroup(groupRequest.getApplication()), groupRequest.getTargetGroup());
        if (exists) {
            throw new MetaException(2004L);
        }

        //add metadata
        Group targetGroupRecord = new Group();
        targetGroupRecord.setName(groupRequest.getTargetGroup());
        Application application = applicationRepo.selectByName(groupRequest.getApplication());
        targetGroupRecord.setApplicationId(application.getId());
        targetGroupRecord.setCreatedBy(String.valueOf(UserContextHolder.getUserDetail().getUid()));
        targetGroupRecord.setUpdatedBy(String.valueOf(UserContextHolder.getUserDetail().getUid()));
        groupRepo.insertSelective(targetGroupRecord);
        operationLogService.log(View.METADATA, OperationType.MODIFY,TargetType.GROUP,"",targetGroupRecord);

        //add configs
        if(!StringUtils.isEmpty(groupRequest.getSourceGroup())){
            Group sourceGroup = groupRepo.selectByApplicationIdAndName(application.getId(),groupRequest.getSourceGroup());
            List<Config> configs = configRepo.selectByApplicationIdAndGroupId(application.getId(), sourceGroup.getId());
            if (!CollectionUtils.isEmpty(configs)) {
                for (Config config : configs) {
                    if(ConfigStatusEnum.PUBLISHED.getStatus().equals(config.getStatus())){
                        Config targetConfig = new Config();
                        targetConfig.setStatus(config.getStatus());
                        targetConfig.setApplicationId(config.getApplicationId());
                        targetConfig.setGroupId(targetGroupRecord.getId());
                        targetConfig.setStashValue(config.getStashValue());
                        targetConfig.setDescription(config.getDescription());
                        targetConfig.setValue(config.getValue());
                        targetConfig.setKey(config.getKey());
                        targetConfig.setName(config.getName());
                        targetConfig.setCreatedBy(UserContextHolder.getUserDetail().getName());
                        targetConfig.setUpdatedBy(UserContextHolder.getUserDetail().getName());
                        targetConfig.setUpdatedAt(new Date());
                        targetConfig.setCreatedAt(new Date());
                        configRepo.insertSelective(targetConfig);
                    }
                }
            }
        }

        //push to redis after DB commit
        AfterCommitTaskRegister.registerTask(
                () -> taskExecutor.execute(
                        () -> addRedisGroup(groupRequest.getApplication(),groupRequest.getSourceGroup(),groupRequest.getTargetGroup())
                )
        );
    }

    private void addRedisGroup(String application,String sourceGroup,String targetGroup){
        redisService.sadd(RedisKeyUtil.getGroup(application), targetGroup);
        if(!StringUtils.isEmpty(sourceGroup)){
            Map<String, String> sourceConfigs = redisService.hgetAll(RedisKeyUtil.getGroupConfig(application, sourceGroup));
            redisService.hmset(RedisKeyUtil.getGroupConfig(application, targetGroup), sourceConfigs);
        }
    }

    @Override
    public List<InstanceResponse> getInstances(String application, String group) {
        List<String> instances = redisService.toList(RedisKeyUtil.getInstance(application, group));
        List<InstanceResponse> responses = instances.stream().map(s -> {
            InstanceResponse response = new InstanceResponse();
            response.setName(s);
            return response;
        }).collect(Collectors.toList());
        return responses;
    }

    @Override
    public List<InstanceConfigsResponse> getInstanceConfig(String application, String group, String instance) {
        Map<String, String> groupConfigs = redisService.hgetAll(RedisKeyUtil.getGroupConfig(application, group));
        Map<String, String> instanceConfigs = redisService.hgetAll(RedisKeyUtil.getInstanceConfig(application, group, instance));
        List<InstanceConfigsResponse> responses = new LinkedList<>();
        if (groupConfigs != null && groupConfigs.size() > 0) {
            for (String key : groupConfigs.keySet()) {
                String serverValue = groupConfigs.get(key);
                String clientValues = instanceConfigs.get(key);
                String clientKey = StringUtils.isEmpty(clientValues) ? null : key;
                responses.add(this.backageResponse(key, serverValue, clientKey, clientValues));
                instanceConfigs.remove(key);
            }
        }
        if (instanceConfigs != null && instanceConfigs.size() > 0) {
            for (String key : instanceConfigs.keySet()) {
                String clientValues = instanceConfigs.get(key);
                responses.add(this.backageResponse(null, null, key, clientValues));
            }
        }
        return responses;
    }

    private InstanceConfigsResponse backageResponse(String serverkey, String serverValue, String clientKey, String clientValues) {
        ClientValueBody deserializer = ClientValueUtil.deserializer(clientValues);
        InstanceConfigsResponse response = new InstanceConfigsResponse();
        response.setClientConfigNew(deserializer.getNewValue())
                .setClientConfigOld(deserializer.getOldValue())
                .setServerConfig(serverValue)
                .setServerKey(serverkey)
                .setClientKey(clientKey);
        return response;
    }

    @Override
    public CompareResponse compareGroupConfigs(CompareRequest compareRequest) {
        List<String> groups = compareRequest.getGroups();
        List<JSONObject> configHeader = new LinkedList<>();
        Map<String, JSONObject> keyTable = new HashMap<>();
        groups.forEach(group -> {
            JSONObject keyHeader = new JSONObject();
            keyHeader.put("label", group + "_key");
            keyHeader.put("props", group + "_key");
            configHeader.add(keyHeader);
            JSONObject valueHeader = new JSONObject();
            valueHeader.put("label", group + "_value");
            valueHeader.put("props", group + "_value");
            configHeader.add(valueHeader);

            Map<String, String> configs = redisService.hgetAll(RedisKeyUtil.getGroupConfig(compareRequest.getApplication(), group));
            for (String key : configs.keySet()) {
                String value = configs.get(key);
                if (keyTable.containsKey(key)) {
                    JSONObject table = keyTable.get(key);
                    table.put(group + "_key", key);
                    table.put(group + "_value", value);
                    keyTable.put(key, table);
                } else {
                    JSONObject table = new JSONObject();
                    table.put(group + "_key", key);
                    table.put(group + "_value", value);
                    keyTable.put(key, table);
                }
            }
        });
        CompareResponse response = new CompareResponse();
        response.setConfigHeader(configHeader);
        response.setConfigTable(new ArrayList<>(keyTable.values()));
        return response;
    }

    @Override
    public List<GroupInstanceResponse> getGroupInstances(String application) {
        List<String> groups = redisService.toList(RedisKeyUtil.getGroup(application));
        List<GroupInstanceResponse> responses = new LinkedList<>();
        for (int i = 0; i < groups.size(); i++) {
            GroupInstanceResponse response = new GroupInstanceResponse();
            response.setKey(i);
            response.setGroup(groups.get(i));
            List<String> instances = redisService.toList(RedisKeyUtil.getInstance(application, groups.get(i)));
            List<InstanceResponse> instanceList = new LinkedList<>();
            for (int k = 0; k < instances.size(); k++) {
                InstanceResponse instance = new InstanceResponse();
                instance.setId(k);
                instance.setName(instances.get(k));
                instanceList.add(instance);
            }
            response.setInstances(instanceList);
            responses.add(response);
        }
        return responses;
    }

    @Override
    public void switchGroupInstances(SwitchGroupInstancesRequest switchGroupInstancesRequest) {
        Map<String, SwitchInstanceMessageBody> instanceMessageBodyMap = new HashMap<>();

        List<GroupInstanceResponse> newGroup = switchGroupInstancesRequest.getData();
        newGroup.forEach(groupInstanceResponse -> {
            String group = groupInstanceResponse.getGroup();
            Set<String> newInstanceList = groupInstanceResponse.getInstances().stream().map(instance -> instance.getName()).collect(Collectors.toSet());
            Set<String> oldInstanceList = redisService.toSet(RedisKeyUtil.getInstance(switchGroupInstancesRequest.getApplication(), group));

            //instance switch to
            Set<String> result = new HashSet<String>();
            result.addAll(newInstanceList);
            result.removeAll(oldInstanceList);
            result.forEach(instance -> {
                this.packageSwitchGroupInstanceMessageBody(instanceMessageBodyMap, switchGroupInstancesRequest.getApplication(), group, instance, false);
            });

            //instance switch from
            result.clear();
            result.addAll(oldInstanceList);
            result.removeAll(newInstanceList);
            result.forEach(instance -> {
                this.packageSwitchGroupInstanceMessageBody(instanceMessageBodyMap, switchGroupInstancesRequest.getApplication(), group, instance, true);
            });
        });

        //notfiy client
        instanceMessageBodyMap.values().forEach(message -> {
            operationLogService.log(View.METADATA,OperationType.SWITCH,TargetType.INSTANCE,message.getGroupFrom(),message.getGroupTo());
            redisPubSub.notifyClient(TopicUtil.getTopicApplication(switchGroupInstancesRequest.getApplication()), message, EventType.SWITCH_INSTANCE);
        });
    }

    private void packageSwitchGroupInstanceMessageBody(Map<String, SwitchInstanceMessageBody> instanceMessageBodyMap, String application, String group, String instance, boolean isFrom) {
        SwitchInstanceMessageBody messageBody = instanceMessageBodyMap.get(instance);
        if (messageBody == null) {
            messageBody = new SwitchInstanceMessageBody();
        }
        messageBody.setApplication(application);
        messageBody.setInstance(instance);
        if (isFrom) {
            messageBody.setGroupFrom(group);
        } else {
            messageBody.setGroupTo(group);
        }
        instanceMessageBodyMap.put(instance, messageBody);
    }
}
