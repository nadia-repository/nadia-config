package com.nadia.config.meta.service.impl;

import com.nadia.config.common.context.UserContextHolder;
import com.nadia.config.meta.domain.Application;
import com.nadia.config.meta.domain.Config;
import com.nadia.config.meta.domain.Group;
import com.nadia.config.meta.domain.RoleConfig;
import com.nadia.config.meta.dto.request.RoleConfigRequest;
import com.nadia.config.meta.dto.response.*;
import com.nadia.config.meta.repo.ApplicationRepo;
import com.nadia.config.meta.repo.ConfigRepo;
import com.nadia.config.meta.repo.GroupRepo;
import com.nadia.config.meta.repo.RoleConfigRepo;
import com.nadia.config.meta.service.RoleConfigService;
import com.nadia.config.user.domain.Role;
import com.nadia.config.user.repo.RoleRepo;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author xiang.shi
 * @date 2019-12-10 11:35
 */
@Service
public class RoleConfigServiceImpl implements RoleConfigService {

    @Resource
    private RoleConfigRepo roleConfigRepo;
    @Resource
    private RoleRepo roleRepo;
    @Resource
    private ConfigRepo configRepo;
    @Resource
    private ApplicationRepo applicationRepo;
    @Resource
    private GroupRepo groupRepo;

    @Override
    public List<RoleConfigResponse> list(String role) {
        //todo 需要优化
        Role roleInfo = roleRepo.selectByName(role);
        List<RoleConfig> roleConfigs = roleConfigRepo.selectByRoleId(roleInfo.getId());
        List<RoleConfigResponse> responses = new LinkedList<>();
        if (CollectionUtils.isNotEmpty(roleConfigs)) {
            List<Long> configIds = roleConfigs.stream().map(r -> r.getConfigId()).collect(Collectors.toList());

            List<Config> configs = configRepo.selectByIds(configIds);

            List<Long> applicationIds = configs.stream().map(c -> c.getApplicationId()).collect(Collectors.toList());
            List<Long> groupIds = configs.stream().map(c -> c.getGroupId()).collect(Collectors.toList());

            List<Application> applicationList = applicationRepo.selectByIds(applicationIds);
            Map<Long, Application> applicationMap = applicationList.stream().collect(Collectors.toMap(Application::getId, application -> application));


            List<Group> groupList = groupRepo.selectByIds(groupIds);
            Map<Long, Group> groupMap = groupList.stream().collect(Collectors.toMap(Group::getId, group -> group));

            configs.forEach(config -> {
                RoleConfigResponse response = new RoleConfigResponse();
                response.setApplication(applicationMap.get(config.getApplicationId()).getName());
                response.setGroup(groupMap.get(config.getGroupId()).getName());
                response.setKey(config.getKey());
                response.setName(config.getName());
                responses.add(response);
            });
        }
        return responses;
    }

    @Override
    public RoleConfigsResponse configs(String role) {
        Role roleInfo = roleRepo.selectByName(role);
        List<RoleConfig> roleConfigs = roleConfigRepo.selectByRoleId(roleInfo.getId());

        Set<Long> configIds = roleConfigs.stream().map(roleConfig -> roleConfig.getConfigId()).collect(Collectors.toSet());


        List<ApplicationResponse> responses = new LinkedList<>();
        List<Application> applicationList = applicationRepo.selectAll();

        //application
        applicationList.forEach(application -> {
            ApplicationResponse applicationResponse = new ApplicationResponse();
            applicationResponse.setName(application.getName());
            applicationResponse.setId(application.getId());
            //group
            List<GroupResponse> groupResponses = new LinkedList<>();
            List<Group> groups = groupRepo.selectByApplicationId(application.getId());

            groups.forEach(group -> {
                GroupResponse groupResponse = new GroupResponse();
                groupResponse.setName(group.getName());
                groupResponse.setId(Long.valueOf(String.valueOf(application.getId()) + String.valueOf(group.getId())));

                //configs
                List<ConfigResponse> configResponses = new LinkedList<>();
                List<Config> configs = configRepo.selectByGroupIdWithBLOBs(group.getId());

                configs.forEach(config -> {
                    ConfigResponse configResponse = new ConfigResponse();
                    BeanUtils.copyProperties(config, configResponse);
                    configResponse.setChecked(configIds.contains(configResponse.getId()) ? true : false);
                    configResponse.setId(Long.valueOf(String.valueOf(application.getId()) + String.valueOf(group.getId()) + String.valueOf(config.getId())));
                    configResponse.setConfigId(config.getId());
                    configResponses.add(configResponse);
                });
                groupResponse.setConfigs(configResponses);
                groupResponses.add(groupResponse);
            });
            applicationResponse.setGroups(groupResponses);
            responses.add(applicationResponse);
        });
        RoleConfigsResponse response = new RoleConfigsResponse();
        response.setData(responses);
        return response;
    }

    @Override
    public void update(RoleConfigRequest roleConfigRequest) {
        Role roleInfo = roleRepo.selectByName(roleConfigRequest.getRole());
        List<RoleConfig> roleConfigs = roleConfigRepo.selectByRoleId(roleInfo.getId());
        Set<Long> configIds = roleConfigs.stream().map(roleConfig -> roleConfig.getConfigId()).collect(Collectors.toSet());

        List<Long> deleteList = new LinkedList<>();
        List<Long> insertList = new LinkedList<>();

        roleConfigs.forEach(roleConfig -> {
            if (!roleConfigRequest.getConfigIds().contains(roleConfig.getConfigId())) {
                deleteList.add(roleConfig.getConfigId());
            }
        });
        roleConfigRequest.getConfigIds().forEach(configId -> {
            if (!configIds.contains(configId)) {
                insertList.add(configId);
            }
        });
        //delete
        deleteList.forEach(configId -> {
            roleConfigRepo.deleteByConfigIdAndRoleId(configId, roleInfo.getId());
        });
        //insert
        RoleConfig record = new RoleConfig();
        record.setCreatedBy(UserContextHolder.getUserDetail().getName());
        record.setUpdatedBy(UserContextHolder.getUserDetail().getName());
        record.setRoleId(roleInfo.getId());
        insertList.forEach(configId -> {
            record.setConfigId(configId);
            roleConfigRepo.insertSelective(record);
        });
    }
}
