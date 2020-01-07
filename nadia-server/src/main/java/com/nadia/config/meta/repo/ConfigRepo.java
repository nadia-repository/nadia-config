package com.nadia.config.meta.repo;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.nadia.config.common.context.UserContextHolder;
import com.nadia.config.enums.ConfigStatusEnum;
import com.nadia.config.meta.dao.ConfigMapper;
import com.nadia.config.meta.dao.RoleConfigMapper;
import com.nadia.config.meta.dto.request.ConfigRequest;
import com.nadia.config.meta.dto.request.ExportConfigRequest;
import com.nadia.config.meta.dto.response.ExportConfigResponse;
import com.nadia.config.meta.domain.Config;
import com.nadia.config.meta.domain.ConfigCriteria;
import com.nadia.config.meta.domain.RoleConfig;
import com.nadia.config.meta.domain.RoleConfigCriteria;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Repository
public class ConfigRepo {

    @Resource
    private ConfigMapper configMapper;

    @Resource
    private RoleConfigMapper roleConfigMapper;

    public void insert(Config config) {
        configMapper.insert(config);
    }

    public void insertSelective(Config config) {
        configMapper.insertSelective(config);
    }

    public Page<Config> pageableSelectByConfigRequest(ConfigRequest request) {
        ConfigCriteria example = new ConfigCriteria();
        ConfigCriteria.Criteria criteria = example.createCriteria();

        RoleConfigCriteria roleConfigCriteria = new RoleConfigCriteria();
        roleConfigCriteria.createCriteria().andRoleIdIn(UserContextHolder.getUserDetail().getRoleIds());
        List<RoleConfig> roleConfigs = roleConfigMapper.selectByExample(roleConfigCriteria);
        if (CollectionUtils.isNotEmpty(roleConfigs)) {
            List<Long> configIds = Lists.newArrayList();
            roleConfigs.forEach(roleConfig -> {
                configIds.add(roleConfig.getConfigId());
            });
            criteria.andIdIn(configIds);
        } else {
            return new Page<>();
        }
        criteria.andStatusNotIn(Arrays.asList(
            ConfigStatusEnum.DELETED.getStatus(),
            ConfigStatusEnum.INVALID.getStatus())
        );
        if (request.getApplicationId() != null) {
            criteria.andApplicationIdEqualTo(request.getApplicationId());
        }
        if (request.getGroupId() != null) {
            criteria.andGroupIdEqualTo(request.getGroupId());
        }
        if (StringUtils.isNotBlank(request.getName())) {
            criteria.andNameLike("%".concat(request.getName()));
        }
        if (StringUtils.isNotBlank(request.getKey())) {
            criteria.andKeyLike("%".concat(request.getKey()));
        }

        PageHelper.startPage(request.getPage(), request.getLimit());
        return (Page<Config>) configMapper.selectByExampleWithBLOBs(example);
    }

    public void updateToRemovingStatusByIds(List<Long> ids, String deletedBy, Date deletedAt) {
        updateStatusByIds(ids, deletedBy, deletedAt, ConfigStatusEnum.REMOVING.getStatus());
    }

    public void updateStatusByIds(List<Long> ids, String deletedBy, Date deletedAt, String status) {
        ConfigCriteria where = new ConfigCriteria();
        ConfigCriteria.Criteria criteria = where.createCriteria();
        criteria.andIdIn(ids);
        criteria.andStatusIn(Arrays.asList(ConfigStatusEnum.PUBLISHED.getStatus()));
        Config set = new Config();
        set.setUpdatedBy(deletedBy);
        set.setUpdatedAt(deletedAt);
        set.setStatus(status);
        configMapper.updateByExampleSelective(set, where);
    }

    public Config selectByPrimaryKey(Long id) {
        return configMapper.selectByPrimaryKey(id);
    }

    public void updateByPrimaryKey(Config config) {
        configMapper.updateByPrimaryKey(config);
    }

    public void deleteByApplicationIdAndGroupId(Long applicationId, Long groupId) {
        ConfigCriteria example = new ConfigCriteria();
        ConfigCriteria.Criteria criteria = example.createCriteria();
        criteria.andApplicationIdEqualTo(applicationId);
        criteria.andGroupIdEqualTo(groupId);
        configMapper.deleteByExample(example);
    }

    public List<Config> selectByApplicationIdAndGroupId(Long applicationId, Long groupId) {
        ConfigCriteria example = new ConfigCriteria();
        ConfigCriteria.Criteria criteria = example.createCriteria();
        criteria.andApplicationIdEqualTo(applicationId);
        criteria.andGroupIdEqualTo(groupId);
        return configMapper.selectByExampleWithBLOBs(example);
    }

    public List<Config> selectByIds(List<Long> ids) {
        ConfigCriteria example = new ConfigCriteria();
        ConfigCriteria.Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);
        criteria.andStatusIn(Arrays.asList(new String[]{ConfigStatusEnum.PUBLISHED.getStatus(),
                ConfigStatusEnum.APPROVED.getStatus(),ConfigStatusEnum.EDITED.getStatus(),ConfigStatusEnum.NEW.getStatus()}));
        return configMapper.selectByExampleWithBLOBs(example);
    }

    public List<Config> selectByGroupIdWithBLOBs(Long groupId) {
        ConfigCriteria example = new ConfigCriteria();
        ConfigCriteria.Criteria criteria = example.createCriteria();
        criteria.andGroupIdEqualTo(groupId);
        criteria.andStatusIn(Arrays.asList(new String[]{ConfigStatusEnum.PUBLISHED.getStatus(),
                ConfigStatusEnum.APPROVED.getStatus(),ConfigStatusEnum.EDITED.getStatus(),ConfigStatusEnum.NEW.getStatus()}));
        return configMapper.selectByExampleWithBLOBs(example);
    }

    public Config selectUniqueConfigByApplicationIdAndGroupIdAndKey(Long applicationId, Long groupId, String key) {
        ConfigCriteria example = new ConfigCriteria();
        ConfigCriteria.Criteria criteria = example.createCriteria();
        criteria.andApplicationIdEqualTo(applicationId);
        criteria.andGroupIdEqualTo(groupId);
        criteria.andKeyEqualTo(key);
        criteria.andStatusNotIn(Arrays.asList(
            ConfigStatusEnum.DELETED.getStatus(),
            ConfigStatusEnum.INVALID.getStatus()
        ));
        List<Config> configs = configMapper.selectByExample(example);
        return CollectionUtils.isNotEmpty(configs) ? configs.stream().findFirst().get() : null;
    }

    public void updateWhenAdditionApproved(List<Long> configIds) {
        configIds.forEach(configId -> {
            Config config = new Config();
            config.setId(configId);
            config.setStatus(ConfigStatusEnum.APPROVED.getStatus());
            config.setUpdatedAt(UserContextHolder.getNow());
            config.setUpdatedBy(UserContextHolder.getUserDetail().getName());
            configMapper.updateByPrimaryKeySelective(config);
        });
    }

    public void updateWhenModificationApproved(List<Long> configIds) {
        configIds.forEach(configId -> {
            Config config = new Config();
            config.setId(configId);
            config.setStatus(ConfigStatusEnum.APPROVED.getStatus());
            config.setUpdatedAt(UserContextHolder.getNow());
            config.setUpdatedBy(UserContextHolder.getUserDetail().getName());
            configMapper.updateByPrimaryKeySelective(config);
        });
    }

    public void updateWhenDeletionApproved(List<Long> configIds) {
        configIds.forEach(configId -> {
            Config config = new Config();
            config.setId(configId);
            config.setStatus(ConfigStatusEnum.DELETED.getStatus());
            config.setUpdatedAt(UserContextHolder.getNow());
            config.setUpdatedBy(UserContextHolder.getUserDetail().getName());
            configMapper.updateByPrimaryKeySelective(config);
        });
    }

    public void updateWhenAdditionRejected(List<Long> configIds) {
        configIds.forEach(configId -> {
            Config config = new Config();
            config.setId(configId);
            config.setStatus(ConfigStatusEnum.INVALID.getStatus());
            config.setUpdatedAt(UserContextHolder.getNow());
            config.setUpdatedBy(UserContextHolder.getUserDetail().getName());
            configMapper.updateByPrimaryKeySelective(config);
        });
    }

    public void updateWhenModificationRejected(List<Long> configIds) {
        configIds.forEach(configId -> {
            Config config = new Config();
            config.setId(configId);
            config.setStatus(ConfigStatusEnum.PUBLISHED.getStatus());
            config.setUpdatedAt(UserContextHolder.getNow());
            config.setUpdatedBy(UserContextHolder.getUserDetail().getName());
            configMapper.updateByPrimaryKeySelective(config);
        });
    }

    public void updateWhenDeletionRejected(List<Long> configIds) {
        configIds.forEach(configId -> {
            Config config = new Config();
            config.setId(configId);
            config.setStatus(ConfigStatusEnum.PUBLISHED.getStatus());
            config.setUpdatedAt(UserContextHolder.getNow());
            config.setUpdatedBy(UserContextHolder.getUserDetail().getName());
            configMapper.updateByPrimaryKeySelective(config);
        });
    }

    public void updateWhenPublished(Long configId) {
        Config config = configMapper.selectByPrimaryKey(configId);
        config.setValue(config.getStashValue());
        config.setStashValue(null);
        config.setStatus(ConfigStatusEnum.PUBLISHED.getStatus());
        config.setUpdatedAt(UserContextHolder.getNow());
        config.setUpdatedBy(UserContextHolder.getUserDetail().getName());
        configMapper.updateByPrimaryKeyWithBLOBs(config);
    }

    public List<ExportConfigResponse> getExportedConfigs(ExportConfigRequest request) {
        RoleConfigCriteria roleConfigCriteria = new RoleConfigCriteria();
        roleConfigCriteria.createCriteria().andRoleIdIn(UserContextHolder.getUserDetail().getRoleIds());
        List<RoleConfig> roleConfigs = roleConfigMapper.selectByExample(roleConfigCriteria);
        if (CollectionUtils.isNotEmpty(roleConfigs)) {
            List<Long> configIds = Lists.newArrayList();
            roleConfigs.forEach(roleConfig -> {
                configIds.add(roleConfig.getConfigId());
            });
            request.setIds(configIds);
        } else {
            return Lists.newArrayList();
        }
        request.setStatuses(Arrays.asList(
            ConfigStatusEnum.DELETED.getStatus(),
            ConfigStatusEnum.INVALID.getStatus())
        );
        if (StringUtils.isNotBlank(request.getName())) {
            request.setName("%".concat(request.getName()));
        }
        if (StringUtils.isNotBlank(request.getKey())) {
            request.setKey("%".concat(request.getKey()));
        }

        return configMapper.getExportedConfigs(request);
    }

}
