package com.nadia.config.meta.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.nadia.config.common.context.UserContextHolder;
import com.nadia.config.common.exception.BusinessException;
import com.nadia.config.common.security.GlobalLock;
import com.nadia.config.common.transaction.AfterCommitTaskRegister;
import com.nadia.config.common.util.CollectUtils;
import com.nadia.config.enums.ConfigStatusEnum;
import com.nadia.config.meta.domain.*;
import com.nadia.config.meta.dto.request.ConfigRequest;
import com.nadia.config.meta.dto.request.ExportConfigRequest;
import com.nadia.config.meta.dto.request.ImportConfigRequest;
import com.nadia.config.meta.dto.request.ReceiveRequest;
import com.nadia.config.meta.dto.response.*;
import com.nadia.config.meta.repo.*;
import com.nadia.config.meta.service.ConfigService;
import com.nadia.config.meta.service.event.EventPublisher;
import com.nadia.config.notification.dto.request.TaskParameter;
import com.nadia.config.notification.enums.Event;
import com.nadia.config.notification.enums.OperationType;
import com.nadia.config.notification.enums.TargetType;
import com.nadia.config.notification.enums.View;
import com.nadia.config.notification.service.OperationLogService;
import com.nadia.config.notification.service.TaskService;
import com.nadia.config.redis.ConfigCenterRedisService;
import com.nadia.config.utils.RedisKeyUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: Wally.Wang
 * @date: 2019/12/04
 * @description:
 */
@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private ConfigRepo configRepo;
    @Resource
    private ConfigCenterRedisService configCenterRedisService;
    @Resource
    private ApplicationRepo applicationRepo;
    @Resource
    private GroupRepo groupRepo;
    @Resource
    private ConfigHistoryRepo configHistoryRepo;
    @Resource
    private TaskService taskService;
    @Resource
    private TaskExecutor taskExecutor;
    @Resource
    private RoleConfigRepo roleConfigRepo;
    @Resource
    private OperationLogService operationLogService;
    @Autowired
    @Qualifier("redisEventPublisher")
    private EventPublisher eventPublisher;

    @Override
    public PageBean<ConfigResponse> getConfigs(ConfigRequest request) {
        List<ConfigResponse> configs = Lists.newArrayList();

        Page<Config> list = configRepo.pageableSelectByConfigRequest(request);

        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(config -> {
                ConfigResponse item = new ConfigResponse();
                BeanUtils.copyProperties(config, item);
                item.setValue(getDisplayValue(config));
                configs.add(item);
            });
        }

        return new PageBean<>(request, configs, CollectionUtils.isNotEmpty(list) ? list.getTotal() : 0L);
    }

    private String getDisplayValue(Config config) {
        if (ConfigStatusEnum.NEW.getStatus().equals(config.getStatus())) {
            return config.getStashValue();
        } else if (ConfigStatusEnum.REMOVING.getStatus().equals(config.getStatus())) {
            return config.getValue();
        } else if (ConfigStatusEnum.EDITED.getStatus().equals(config.getStatus())) {
            return config.getStashValue();
        } else if (ConfigStatusEnum.INVALID.getStatus().equals(config.getStatus())) {
            return config.getStashValue();
        } else if (ConfigStatusEnum.APPROVED.getStatus().equals(config.getStatus())) {
            return config.getStashValue();
        } else if (ConfigStatusEnum.PUBLISHED.getStatus().equals(config.getStatus())) {
            return config.getValue();
        } else if (ConfigStatusEnum.DELETED.getStatus().equals(config.getStatus())) {
            return config.getValue();
        }
        return config.getValue();
    }

    @Override
    @GlobalLock
    @Transactional
    public void addConfig(ConfigRequest request) {
        checkBeforeAddition(request);

        Config config = new Config();
        BeanUtils.copyProperties(request, config);
        config.setStashValue(config.getValue());
        config.setValue(null);
        config.setCreatedAt(UserContextHolder.getNow());
        config.setUpdatedAt(UserContextHolder.getNow());
        config.setCreatedBy(UserContextHolder.getUserDetail().getName());
        config.setUpdatedBy(UserContextHolder.getUserDetail().getName());
        config.setStatus(ConfigStatusEnum.NEW.getStatus());
        configRepo.insert(config);

        //TODO Batch operation replace cycle operation
        List<Long> roleIds = new ArrayList<>();
        roleIds.addAll(UserContextHolder.getUserDetail().getRoleIds());
        roleIds.addAll(request.getRoleIds());
        roleIds = CollectUtils.cleanAndFilter(roleIds);

        roleIds.forEach(roleId -> {
            RoleConfig roleConfig = new RoleConfig();
            roleConfig.setRoleId(roleId);
            roleConfig.setConfigId(config.getId());
            roleConfig.setCreatedAt(UserContextHolder.getNow());
            roleConfig.setUpdatedAt(UserContextHolder.getNow());
            roleConfig.setCreatedBy(UserContextHolder.getUserDetail().getName());
            roleConfig.setUpdatedBy(UserContextHolder.getUserDetail().getName());
            roleConfigRepo.insertSelective(roleConfig);
        });

        AfterCommitTaskRegister.registerTask(
            () -> taskExecutor.execute(
                () -> doAfterSuccessfulAddition(config)
            )
        );
    }

    private void doAfterSuccessfulAddition(Config config) {
        TaskParameter factor = new TaskParameter(new TaskParameter.Addition().add(new TaskParameter.AdditionItem(config.getId(), config.getStashValue())));
        taskService.startTask(factor);

        operationLogService.log(View.CONFIGS, OperationType.ADD, TargetType.CONFIG, null,
            buildJSONObject("value", config.getStashValue()));
    }

    private void checkBeforeAddition(ConfigRequest request) {
        Config existedConfig = configRepo.selectUniqueConfigByApplicationIdAndGroupIdAndKey(
            request.getApplicationId(), request.getGroupId(), request.getKey());
        if (existedConfig != null) {
            log.warn("The same key {} exists in the same directory, application ID: {}, group ID: {}",
                request.getKey(), request.getApplicationId(), request.getGroupId());
            throw new BusinessException("The same key exists in the same directory");
        }
    }

    @Override
    @GlobalLock
    @Transactional
    public void deleteConfigs(List<Long> ids) {
        configRepo.updateToRemovingStatusByIds(ids, UserContextHolder.getUserDetail().getName(), UserContextHolder.getNow());
        AfterCommitTaskRegister.registerTask(
            () -> taskExecutor.execute(
                () -> doAfterSuccessfulDeletion(ids)
            )
        );
    }

    private void doAfterSuccessfulDeletion(List<Long> ids) {
        //delete redis
        List<Config> configs = configRepo.selectByIds(ids);
        configs.stream().forEach(config -> {
            Application application = applicationRepo.selectByPrimaryKey(config.getApplicationId());
            Group group = groupRepo.selectByPrimaryKey(config.getGroupId());
            String groupConfig = RedisKeyUtil.getGroupConfig(application.getName(), group.getName());
            configCenterRedisService.del(groupConfig,config.getKey());
        });
        taskService.startTask(new TaskParameter(new TaskParameter.Deletion(ids)));
        operationLogService.log(View.CONFIGS, OperationType.DELETE, TargetType.CONFIG, null, buildJSONObject("ids", ids));
    }

    @Override
    @GlobalLock
    @Transactional
    public void updateConfig(ConfigRequest request) {
        Config config = configRepo.selectByPrimaryKey(request.getId());
        if (!ConfigStatusEnum.PUBLISHED.getStatus().equalsIgnoreCase(config.getStatus())) {
            log.warn("Disordered operation, expected {} but actual {}",
                ConfigStatusEnum.PUBLISHED.getStatus(), config.getStatus());
            throw new BusinessException("Disordered operation");
        }

        boolean isValueChanged = !config.getValue().equals(request.getValue());
        if (isValueChanged) {
            config.setStashValue(request.getValue());
            config.setStatus(ConfigStatusEnum.EDITED.getStatus());
        }
        config.setDescription(request.getDescription());
        config.setUpdatedAt(UserContextHolder.getNow());
        config.setUpdatedBy(UserContextHolder.getUserDetail().getName());
        configRepo.updateByPrimaryKey(config);

        if (isValueChanged) {
            AfterCommitTaskRegister.registerTask(
                () -> taskExecutor.execute(
                    () -> doAfterSuccessfulModification(config)
                )
            );
        }
    }

    private void doAfterSuccessfulModification(Config config) {
        taskService.startTask(new TaskParameter(new TaskParameter.Modification(config.getId(), config.getValue(), config.getStashValue())));
        operationLogService.log(View.CONFIGS, OperationType.MODIFY, TargetType.CONFIG,
            buildJSONObject("value", config.getValue()), buildJSONObject("value", config.getStashValue()));
    }

    @Override
    public List<ConfigHistoryResponse> getConfigHistories(Long id) {
        List<ConfigHistoryResponse> histories = Lists.newArrayList();

        List<ConfigHistory> list = configHistoryRepo.selectByConfigId(id);
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(configHistory -> {
                ConfigHistoryResponse history = new ConfigHistoryResponse();
                BeanUtils.copyProperties(configHistory, history);
                histories.add(history);
            });
        }

        return histories;
    }

    private void approve(ReceiveRequest request) {
        if (Event.ADD.equals(request.getEvent())) {
            configRepo.updateWhenAdditionApproved(request.getConfigIds());
        } else if (Event.MODIFY.equals(request.getEvent())) {
            configRepo.updateWhenModificationApproved(request.getConfigIds());
        } else if (Event.DELETE.equals(request.getEvent())) {
            configRepo.updateWhenDeletionApproved(request.getConfigIds());
        }
    }

    private void reject(ReceiveRequest request) {
        if (Event.ADD.equals(request.getEvent())) {
            configRepo.updateWhenAdditionRejected(request.getConfigIds());
        } else if (Event.MODIFY.equals(request.getEvent())) {
            configRepo.updateWhenModificationRejected(request.getConfigIds());
        } else if (Event.DELETE.equals(request.getEvent())) {
            configRepo.updateWhenDeletionRejected(request.getConfigIds());
        }
    }

    @Override
    @Transactional
    public void publish(Long configId) {
        configRepo.updateWhenPublished(configId);
        configHistoryRepo.insertSelective(buildHistory(configId));
        operationLogService.log(
            View.CONFIGS, OperationType.PUBLISH, TargetType.CONFIG,
            null, buildJSONObject("id", configId));

        AfterCommitTaskRegister.registerTask(
            () -> taskExecutor.execute(
                () -> eventPublisher.onPublished(configId)
            )
        );
    }

    private ConfigHistory buildHistory(Long configId) {
        Config config = configRepo.selectByPrimaryKey(configId);
        ConfigHistory history = new ConfigHistory();
        history.setConfigId(configId);
        history.setDescription(config.getDescription());
        history.setValue(config.getValue());
        history.setCreatedBy(config.getCreatedBy());
        history.setUpdatedBy(config.getUpdatedBy());
        return history;
    }

    private static final String WORKFLOW_RESULT_APPROVE = "approve";
    private static final String WORKFLOW_RESULT_REJECT = "reject";

    @Override
    @Transactional
    public void receive(ReceiveRequest request) {
        if (request == null) {
            log.warn("Invalid parameter, request: null");
            return;
        }

        if (CollectionUtils.isEmpty(CollectUtils.cleanAndFilter(request.getConfigIds()))) {
            log.warn("Invalid parameter, configIds: {}", request.getConfigIds());
            return;
        }

        if (request.getEvent() == null) {
            log.warn("Invalid parameter, event: null");
            return;
        }

        if (!Event.ADD.equals(request.getEvent()) &&
            !Event.MODIFY.equals(request.getEvent()) &&
            !Event.DELETE.equals(request.getEvent())) {
            log.warn("Invalid parameter, event: {}", request.getEvent());
            return;
        }

        if (StringUtils.isBlank(request.getResult())) {
            log.warn("Invalid parameter, result: {}", request.getResult());
            return;
        }

        if (!request.getResult().equalsIgnoreCase(WORKFLOW_RESULT_APPROVE) &&
            !request.getResult().equalsIgnoreCase(WORKFLOW_RESULT_REJECT)) {
            log.warn("Invalid parameter, result: {}", request.getResult());
            return;
        }

        if (WORKFLOW_RESULT_APPROVE.equalsIgnoreCase(request.getResult())) {
            approve(request);
        } else {
            reject(request);
        }
    }

    @Data
    @AllArgsConstructor
    public static class TransitionalConfig {
        private Application existedApp;
        private Group existedGroup;
        private Application newApp;
        private Group newGroup;
        private String application;
        private String group;
        private String name;
        private String key;
        private String value;
        private String description;
        private boolean isAppExisted;
        private boolean isGroupExisted;
        private boolean isConfigExisted;

        public Application getAppInstance() {
            return existedApp == null ? newApp : existedApp;
        }

        public Group getGroupInstance() {
            return existedGroup == null ? newGroup : existedGroup;
        }
    }

    @Override
    @Transactional
    public void importConfigs(ImportConfigRequest request) {
        List<TransitionalConfig> transitionalConfigs = Lists.newArrayList();

        for (ImportConfigRequest.ImportedConfig importedConfig : request.getConfigs()) {
            if (StringUtils.isBlank(importedConfig.getApplication()) ||
                importedConfig.getApplication().trim().length() > 64) {
                log.warn("Illegal value, application: {}", importedConfig.getApplication());
                continue;
            }
            if (StringUtils.isBlank(importedConfig.getGroup()) ||
                importedConfig.getGroup().trim().length() > 64) {
                log.warn("Illegal value, group: {}", importedConfig.getGroup());
                continue;
            }
            if (StringUtils.isBlank(importedConfig.getName()) ||
                importedConfig.getName().trim().length() > 64) {
                log.warn("Illegal value, name: {}", importedConfig.getName());
                continue;
            }
            if (StringUtils.isBlank(importedConfig.getKey()) ||
                importedConfig.getKey().trim().length() > 128) {
                log.warn("Illegal value, key: {}", importedConfig.getKey());
                continue;
            }
            if (StringUtils.isBlank(importedConfig.getDescription()) ||
                importedConfig.getDescription().trim().length() > 128) {
                log.warn("Illegal value, description: {}", importedConfig.getDescription());
                continue;
            }

            boolean isAppExisted = false;
            Application existedApp = applicationRepo.selectByName(importedConfig.getApplication().trim());
            if (existedApp != null) {
                isAppExisted = true;
            }

            boolean isGroupExisted = false;
            Group existedGroup = null;
            if (isAppExisted) {
                existedGroup = groupRepo.selectByApplicationIdAndName(existedApp.getId(), importedConfig.getGroup().trim());
                if (existedGroup != null) {
                    isGroupExisted = true;
                }
            }
            boolean isConfigExisted = false;
            if (isAppExisted && isGroupExisted) {
                Config existedConfig = configRepo.selectUniqueConfigByApplicationIdAndGroupIdAndKey(
                    existedApp.getId(), existedGroup.getId(), importedConfig.getKey().trim());
                if (existedConfig != null) {
                    isConfigExisted = true;
                }
            }
            TransitionalConfig transitionalConfig = new TransitionalConfig(
                existedApp, existedGroup,
                null, null,
                importedConfig.getApplication().trim(), importedConfig.getGroup().trim(),
                importedConfig.getName().trim(), importedConfig.getKey().trim(),
                importedConfig.getValue().trim(), importedConfig.getDescription().trim(),
                isAppExisted, isGroupExisted, isConfigExisted);
            transitionalConfigs.add(transitionalConfig);
        }

        Set<String> insertedNewApps = Sets.newHashSet();
        Set<String> insertedNewGroups = Sets.newHashSet();
        Map<String, Application> insertedNewAppMap = Maps.newHashMap();
        Map<String, Group> insertedNewGroupMap = Maps.newHashMap();
        List<Config> importedConfigs = Lists.newArrayList();
        for (TransitionalConfig transitionalConfig : transitionalConfigs) {
            if (transitionalConfig.isConfigExisted()) {
                continue;
            }
            if (!transitionalConfig.isAppExisted()) {
                if (!insertedNewApps.contains(transitionalConfig.getApplication())) {
                    Application application = new Application();
                    application.setName(transitionalConfig.getApplication());
                    application.setEmail(UserContextHolder.getUserDetail().getEmail());
                    application.setDescription(transitionalConfig.getDescription());
                    application.setCreatedBy(UserContextHolder.getUserDetail().getName());
                    application.setCreatedAt(UserContextHolder.getNow());
                    application.setUpdatedBy(UserContextHolder.getUserDetail().getName());
                    application.setUpdatedAt(UserContextHolder.getNow());
                    applicationRepo.insertSelective(application);
                    transitionalConfig.setNewApp(application);
                    insertedNewApps.add(transitionalConfig.getApplication());
                    insertedNewAppMap.put(transitionalConfig.getApplication(), application);
                } else {
                    transitionalConfig.setNewApp(insertedNewAppMap.get(transitionalConfig.getApplication()));
                }
            }
            if (!transitionalConfig.isGroupExisted()) {
                if (!insertedNewGroups.contains(transitionalConfig.getApplication().concat(transitionalConfig.getGroup()))) {
                    Group group = new Group();
                    group.setName(transitionalConfig.getGroup());
                    group.setApplicationId(transitionalConfig.getAppInstance().getId());
                    group.setCreatedBy(UserContextHolder.getUserDetail().getName());
                    group.setCreatedAt(UserContextHolder.getNow());
                    group.setUpdatedBy(UserContextHolder.getUserDetail().getName());
                    group.setUpdatedAt(UserContextHolder.getNow());
                    groupRepo.insertSelective(group);
                    transitionalConfig.setNewGroup(group);
                    insertedNewGroups.add(
                        transitionalConfig.getApplication().concat(transitionalConfig.getGroup())
                    );
                    insertedNewGroupMap.put(
                        transitionalConfig.getApplication().concat(transitionalConfig.getGroup()), group);
                } else {
                    transitionalConfig.setNewGroup(
                        insertedNewGroupMap.get(transitionalConfig.getApplication().concat(transitionalConfig.getGroup())));
                }
            }
            if (!transitionalConfig.isConfigExisted()) {
                Config config = new Config();
                config.setApplicationId(transitionalConfig.getAppInstance().getId());
                config.setGroupId(transitionalConfig.getGroupInstance().getId());
                config.setName(transitionalConfig.getName());
                config.setKey(transitionalConfig.getKey());
                config.setStashValue(transitionalConfig.getValue());
                config.setDescription(transitionalConfig.getDescription());
                config.setCreatedAt(UserContextHolder.getNow());
                config.setUpdatedAt(UserContextHolder.getNow());
                config.setCreatedBy(UserContextHolder.getUserDetail().getName());
                config.setUpdatedBy(UserContextHolder.getUserDetail().getName());
                config.setStatus(ConfigStatusEnum.NEW.getStatus());
                configRepo.insert(config);
                importedConfigs.add(config);

                List<Long> roleIds = UserContextHolder.getUserDetail().getRoleIds();
                roleIds.forEach(roleId -> {
                    RoleConfig roleConfig = new RoleConfig();
                    roleConfig.setRoleId(roleId);
                    roleConfig.setConfigId(config.getId());
                    roleConfig.setCreatedBy(UserContextHolder.getUserDetail().getName());
                    roleConfig.setCreatedAt(UserContextHolder.getNow());
                    roleConfig.setUpdatedBy(UserContextHolder.getUserDetail().getName());
                    roleConfig.setUpdatedAt(UserContextHolder.getNow());
                    roleConfigRepo.insertSelective(roleConfig);
                });
            }
        }

        if (CollectionUtils.isNotEmpty(importedConfigs)) {
            AfterCommitTaskRegister.registerTask(
                () -> taskExecutor.execute(
                    () -> doAfterSuccessfulImport(importedConfigs)
                )
            );
        }
    }

    private void doAfterSuccessfulImport(List<Config> configs) {
        List<TaskParameter.AdditionItem> items = Lists.newArrayList();
        configs.forEach(config -> {
            items.add(new TaskParameter.AdditionItem(config.getId(), config.getStashValue()));
        });
        TaskParameter factor = new TaskParameter(new TaskParameter.Addition(items));
        taskService.startTask(factor);

        operationLogService.log(View.CONFIGS, OperationType.IMPORT, TargetType.CONFIG, null,
            buildJSONObject("configs", configs));
    }

    @Override
    public List<ExportConfigResponse> exportConfigs(ExportConfigRequest request) {
        return configRepo.getExportedConfigs(request);
    }

    @Override
    public List<ConfigInstanceResponse> getConfigInstances(Long configId) {
        List<ConfigInstanceResponse> configInstances = Lists.newArrayList();
        Config config = configRepo.selectByPrimaryKey(configId);
        if (config != null) {
            Application app = applicationRepo.selectByPrimaryKey(config.getApplicationId());
            Group group = groupRepo.selectByPrimaryKey(config.getGroupId());
            Set<String> instances = configCenterRedisService.smembers(RedisKeyUtil.getInstance(app.getName(), group.getName()));
            if (CollectionUtils.isNotEmpty(instances)) {
                for (String instance : instances) {
                    Map<String, String> configs = configCenterRedisService.hgetAll(RedisKeyUtil.getInstanceConfig(app.getName(), group.getName(), instance));
                    Set<String> keys = configs.keySet();
                    if (CollectionUtils.isNotEmpty(keys)) {
                        ConfigInstanceResponse configInstance = new ConfigInstanceResponse();
                        for (String key : keys) {
                            if (key.equals(config.getKey())) {
                                ConfigInstanceResponse.ClientConfig clientConfig = new ConfigInstanceResponse.ClientConfig();
                                clientConfig.setInstance(instance);
                                clientConfig.setValue(JSON.parseObject(configs.get(key)).getString("newValue"));
                                configInstance.setServerConfig(config.getValue());
                                configInstance.setClientConfig(clientConfig);
                                configInstances.add(configInstance);
                            }
                        }
                    }
                }
            }
        }
        return configInstances;
    }

    private JSONObject buildJSONObject(String key, Object value) {
        JSONObject object = new JSONObject();
        object.put(key, value);
        return object;
    }

}
