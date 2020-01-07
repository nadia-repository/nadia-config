package com.nadia.config.notification.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nadia.config.common.context.UserContextHolder;
import com.nadia.config.common.rest.RestBody;
import com.nadia.config.notification.domain.*;
import com.nadia.config.notification.dto.request.ApproveTaskRequest;
import com.nadia.config.notification.dto.request.TaskParameter;
import com.nadia.config.notification.enums.ActionType;
import com.nadia.config.notification.enums.Event;
import com.nadia.config.notification.enums.TaskStatus;
import com.nadia.config.notification.repo.TaskRepo;
import com.nadia.config.user.domain.Role;
import com.nadia.config.user.domain.RoleApprover;
import com.nadia.config.user.repo.RoleApproverRepo;
import com.nadia.config.user.repo.RoleRepo;
import com.nadia.config.meta.domain.Application;
import com.nadia.config.meta.domain.Config;
import com.nadia.config.meta.domain.Group;
import com.nadia.config.meta.dto.request.ReceiveRequest;
import com.nadia.config.meta.dto.response.PageBean;
import com.nadia.config.meta.repo.ApplicationRepo;
import com.nadia.config.meta.repo.ConfigRepo;
import com.nadia.config.meta.repo.GroupRepo;
import com.nadia.config.meta.service.ConfigService;
import com.nadia.config.notification.dto.request.PendingTaskRequest;
import com.nadia.config.notification.dto.request.ProcessingTaskRequest;
import com.nadia.config.notification.dto.response.HistoryTaskResponse;
import com.nadia.config.notification.dto.response.PendingTaskResponse;
import com.nadia.config.notification.dto.response.ProcessingTaskResponse;
import com.nadia.config.notification.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    @Resource
    private RoleApproverRepo roleApproverRepo;

    @Resource
    private RoleRepo roleRepo;

    @Resource
    private ConfigRepo configRepo;

    @Resource
    private ApplicationRepo applicationRepo;

    @Resource
    private GroupRepo groupRepo;

    @Resource
    private TaskRepo taskRepo;

    @Resource
    private ConfigService configService;

    private String getUserName() {
        return String.valueOf(UserContextHolder.getUserDetail().getName());
    }

    private void createNextTasks(Long roleId, String action, String createdBy, Date createdAt) {
        List<RoleApprover> roleApprovers = roleApproverRepo.selectByRoleId(roleId);
        if(roleApprovers.size() == 0){
            this.applyConfig(action, TaskStatus.APPROVE.getStatus());
        }else{
            roleApprovers.forEach(roleApprover -> {
                Task record = new Task();
                record.setAction(action);
                record.setStatus(TaskStatus.PENDING.getStatus());
                record.setRoleId(roleApprover.getApproverRoleId());
                record.setCreatedAt(createdAt);
                record.setCreatedBy(createdBy);
                taskRepo.insertSelective(record);
            });
        }
    }

    @Override
    public void startTask(TaskParameter config) {
        String userName = UserContextHolder.getUserDetail().getName();
        List<Long> roleIds = UserContextHolder.getUserDetail().getRoleIds();

        TaskParameter.Action action = config.getAction();
        //根据action接口实现类生成入库需要的JSON字符串格式的actionText
        String actionText = this.formatJSONString(action);
        //遍历审批发起人的roleId
        roleIds.forEach(roleId -> {
            this.createNextTasks(roleId, actionText, userName, new Date());
        });
    }

    public String formatJSONString(TaskParameter.Action action){
        //根据对应的action实现类，获取到type,configId,value(before,after)
        if(action instanceof TaskParameter.Addition){
            return this.additionFormat(action);
        }else if(action instanceof TaskParameter.Deletion){
            return this.deletionFormat(action);
        }else if(action instanceof TaskParameter.Modification){
            return this.modificationFormat(action);
        }else{
            log.info("invalid TaskConfig.Action parameters:{}",JSON.toJSONString(action));
            return null;
        }
    }

    public String additionFormat(TaskParameter.Action action){
        TaskParameter.Addition addition = (TaskParameter.Addition) action;
        List<TaskParameter.AdditionItem> additionItems = addition.getItems();

        //创建application list
        ArrayList<AppInfo> applicationList = Lists.newArrayList();

        //遍历additionItem获得相应的configId和value
        for (TaskParameter.AdditionItem additionItem : additionItems) {
            Long configId = additionItem.getConfigId();
            //根据configId获取app、group、config信息
            Config addConfig = configRepo.selectByPrimaryKey(configId);
            Application application = applicationRepo.selectByPrimaryKey(addConfig.getApplicationId());
            Group group = groupRepo.selectByPrimaryKey(addConfig.getGroupId());
            String value = additionItem.getValue();
            //构建ConfigInfo
            ConfigInfo configInfo = new ConfigInfo();
            configInfo.setName("config name: ".concat(addConfig.getName()));
            configInfo.setId(configId);
            Map<String, String> keyMap = Maps.newHashMap();
            Map<String, String> valueMap = Maps.newHashMap();
            Map<String, String> descriptionMap = Maps.newHashMap();
            keyMap.put("name","key: ".concat(addConfig.getKey()));
            valueMap.put("name","value: ".concat(value));
            descriptionMap.put("name","description: ".concat(addConfig.getDescription()));
            List<Map<String, String>> children = Lists.newArrayList();
            children.add(keyMap);
            children.add(valueMap);
            children.add(descriptionMap);
            configInfo.setChildren(children);

            //app和group的标记值
            int i = 0;
            int j = 0;

            for (AppInfo existAppInfo : applicationList) {
                //判断此次操作前applicationList有没有这个AppInfo
                if(existAppInfo.getName().equals(application.getName())){
                    i++;
                    //存在app
                    List<GroupInfo> existAppGroupList = existAppInfo.getChildren();
                    //遍历他已存在的groupList
                    for (GroupInfo existGroupInfo : existAppGroupList) {
                        if(existGroupInfo.getName().equals(group.getName())){
                            //相同app,相同组，新配置
                            j++;
                            List<ConfigInfo> existGroupConfigList = existGroupInfo.getChildren();
                            existGroupConfigList.add(configInfo);
                        }
                    }
                    if(j == 0){
                        //相同app，新组，新配置
                        ArrayList<ConfigInfo> configList = Lists.newArrayList();
                        configList.add(configInfo);
                        GroupInfo groupInfo = new GroupInfo();
                        groupInfo.setName(group.getName());
                        groupInfo.setChildren(configList);
                        existAppGroupList.add(groupInfo);
                    }
                }
            }
            if(i == 0){
                //新app，新组，新配置
                ArrayList<ConfigInfo> configList = Lists.newArrayList();
                configList.add(configInfo);
                ArrayList<GroupInfo> groupList = Lists.newArrayList();
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setName(group.getName());
                groupInfo.setChildren(configList);
                groupList.add(groupInfo);
                AppInfo newAppInfo = new AppInfo();
                newAppInfo.setName(application.getName());
                newAppInfo.setChildren(groupList);
                applicationList.add(newAppInfo);
            }

        }


        //根据action查询type
        String type = action.getEvent().getType();

        //创建ActionInfo类
        ActionInfo actionInfo = new ActionInfo();
        actionInfo.setType(type);
        actionInfo.setChildren(applicationList);
        return JSON.toJSONString(actionInfo);
    }

    public String modificationFormat(TaskParameter.Action action) {
        Long configId = ((TaskParameter.Modification) action).getConfigId();
        String before = ((TaskParameter.Modification) action).getBefore();
        String after = ((TaskParameter.Modification) action).getAfter();

        //根据configId查询app，group，configname信息
        Config modificationConfig = configRepo.selectByPrimaryKey(configId);
        Application applicationInfo = applicationRepo.selectByPrimaryKey(modificationConfig.getApplicationId());
        Group groupInfo = groupRepo.selectByPrimaryKey(modificationConfig.getGroupId());

        Map<String, String> keyMap = Maps.newHashMap();
        Map<String, String> beforeMap = Maps.newHashMap();
        Map<String, String> afterMap = Maps.newHashMap();
        Map<String, String> descriptionMap = Maps.newHashMap();
        keyMap.put("name","key: ".concat(modificationConfig.getKey()));
        beforeMap.put("name","beforeValue: ".concat(before));
        afterMap.put("name","afterValue: ".concat(after));
        descriptionMap.put("name","description: ".concat(modificationConfig.getDescription()));
        List<Map<String, String>> configDetails = Lists.newArrayList();
        configDetails.add(keyMap);
        configDetails.add(beforeMap);
        configDetails.add(afterMap);
        configDetails.add(descriptionMap);

        ConfigInfo configInfo = new ConfigInfo();
        configInfo.setName(modificationConfig.getName());
        configInfo.setId(modificationConfig.getId());
        configInfo.setChildren(configDetails);
        List<ConfigInfo> configInfos = Lists.newArrayList();
        configInfos.add(configInfo);

        GroupInfo newGroupInfo = new GroupInfo();
        newGroupInfo.setName(groupInfo.getName());
        newGroupInfo.setChildren(configInfos);
        List<GroupInfo> groupInfos = Lists.newArrayList();
        groupInfos.add(newGroupInfo);

        AppInfo appInfo = new AppInfo();
        appInfo.setName(applicationInfo.getName());
        appInfo.setChildren(groupInfos);
        List<AppInfo> appInfos = Lists.newArrayList();
        appInfos.add(appInfo);

        ActionInfo actionInfo = new ActionInfo();
        actionInfo.setType(action.getEvent().getType());
        actionInfo.setChildren(appInfos);

        return JSON.toJSONString(actionInfo);
    }

    public String deletionFormat(TaskParameter.Action action) {
        List<Long> configIds = ((TaskParameter.Deletion) action).getIds();

        //创建application list
        ArrayList<AppInfo> applicationList = Lists.newArrayList();

        //遍历additionItem获得相应的configId和value
        for (Long configId : configIds) {
            //根据configId获取app、group、config信息
            Config deleteConfig = configRepo.selectByPrimaryKey(configId);
            Application application = applicationRepo.selectByPrimaryKey(deleteConfig.getApplicationId());
            Group group = groupRepo.selectByPrimaryKey(deleteConfig.getGroupId());
            //构建ConfigInfo
            ConfigInfo configInfo = new ConfigInfo();
            configInfo.setName("config name: ".concat(deleteConfig.getName()));
            configInfo.setId(configId);
            Map<String, String> keyMap = Maps.newHashMap();
            Map<String, String> valueMap = Maps.newHashMap();
            Map<String, String> descriptionMap = Maps.newHashMap();
            keyMap.put("name","key: ".concat(deleteConfig.getKey()));
            valueMap.put("name","value: ".concat(deleteConfig.getValue()));
            descriptionMap.put("name","description: ".concat(deleteConfig.getDescription()));
            List<Map<String, String>> children = Lists.newArrayList();
            children.add(keyMap);
            children.add(valueMap);
            children.add(descriptionMap);
            configInfo.setChildren(children);

            //app和group的标记值
            int i = 0;
            int j = 0;

            for (AppInfo existAppInfo : applicationList) {
                if(existAppInfo.getName().equals(application.getName())){
                    i++;
                    List<GroupInfo> existAppGroupList = existAppInfo.getChildren();
                    for (GroupInfo existGroupInfo : existAppGroupList) {
                        if(existGroupInfo.getName().equals(group.getName())){
                            //相同app,相同组，新配置
                            j++;
                            List<ConfigInfo> existGroupConfigList = existGroupInfo.getChildren();
                            existGroupConfigList.add(configInfo);
                        }
                    }
                    if(j == 0){
                        //相同app，新组，新配置
                        ArrayList<ConfigInfo> configList = Lists.newArrayList();
                        configList.add(configInfo);
                        GroupInfo groupInfo = new GroupInfo();
                        groupInfo.setName(group.getName());
                        groupInfo.setChildren(configList);
                        existAppGroupList.add(groupInfo);
                    }
                }
            }
            if(i == 0){
                //新app，新组，新配置
                ArrayList<ConfigInfo> configList = Lists.newArrayList();
                configList.add(configInfo);
                ArrayList<GroupInfo> groupList = Lists.newArrayList();
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setName(group.getName());
                groupInfo.setChildren(configList);
                groupList.add(groupInfo);
                AppInfo newAppInfo = new AppInfo();
                newAppInfo.setName(application.getName());
                newAppInfo.setChildren(groupList);
                applicationList.add(newAppInfo);
            }
        }

        //根据action查询type
        String type = action.getEvent().getType();

        //创建ActionInfo类
        ActionInfo actionInfo = new ActionInfo();
        actionInfo.setType(type);
        actionInfo.setChildren(applicationList);
        return JSON.toJSONString(actionInfo);
    }

        @Override
    public PageBean<PendingTaskResponse> getTask(PendingTaskRequest request) {
        List<PendingTaskResponse> getTaskList = Lists.newArrayList();

        List<Long> roleIds = UserContextHolder.getUserDetail().getRoleIds();
        Page<Task> taskList = new Page<>();
        List<Task> tasks = taskRepo.selectByStatusAndRoleIds(TaskStatus.PENDING.getStatus(), roleIds);
        taskList.addAll(tasks);
        StringBuilder nextApproverRoles= new StringBuilder();
        List<Role> approvers = roleApproverRepo.selectApproversByExample(roleIds);
        approvers.forEach(approver -> {
            if(nextApproverRoles.indexOf(approver.getName()) == -1){
                nextApproverRoles.append(approver.getName()).append("|");
            }
        });
        if (CollectionUtils.isNotEmpty(taskList)) {
            taskList.forEach(task -> {
                PendingTaskResponse item = new PendingTaskResponse();
                BeanUtils.copyProperties(task, item);
                JSONObject jsonObject = JSON.parseObject(item.getAction());
                String type = (String) jsonObject.get("type");
                if(nextApproverRoles.length() > 0){
                    item.setNextApproverRoles(nextApproverRoles.substring(0, nextApproverRoles.length() - 1));
                }else{
                    item.setNextApproverRoles("You are the final approver!");
                }
                item.setType(type);
                getTaskList.add(item);
            });
        }

        return new PageBean(request, this.paging(getTaskList, request.getPage(), request.getLimit()), CollectionUtils.isNotEmpty(getTaskList) ? getTaskList.size() : 0L);
    }

    @Override
    @Transactional
    public RestBody approveTask(ApproveTaskRequest request) {
        String userName = UserContextHolder.getUserDetail().getName();
        if (Objects.isNull(request.getTaskId())) {
            return RestBody.fail("taskId is null");
        }
        Task task = taskRepo.selectByPrimaryKey(request.getTaskId());
        //检查是否审批过
        if (Objects.isNull(task)) {
            return RestBody.fail("invalid task.");
        }
        //开始审批
        //将同一个config且的其他任务删除，防止重复审批
        List<Task> needToDeleteTaskList = taskRepo.selectByStatusAndAction(TaskStatus.PENDING.getStatus(), task.getAction());
        needToDeleteTaskList.stream().map(needToDeleteTask -> needToDeleteTask.getId())
                .filter(taskId -> !request.getTaskId().equals(taskId))
                .forEach(taskRepo::deleteByPrimaryKey);
        if (task.getStatus().equals(TaskStatus.PENDING.getStatus())) {
            //意见为通过
            if (request.isFlag()) {
                //判断是否是最后一个节点
                if (this.isLastApprover(task.getRoleId())) {
                    //审批流最后一个节点
                    task.setStatus(TaskStatus.COMPLETED.getStatus());
                    //使得配置生效，回调config接口
                    this.applyConfig(task.getAction(), TaskStatus.APPROVE.getStatus());
                } else {
                    task.setStatus(TaskStatus.APPROVE.getStatus());
                    //生成下一个节点的任务
                    List<Long> roleIds = UserContextHolder.getUserDetail().getRoleIds();
                    roleIds.forEach(roleId -> {
                        this.createNextTasks(roleId, task.getAction(), task.getCreatedBy(), task.getCreatedAt());
                    });
                }
            } else {//意见为不通过
                task.setStatus(TaskStatus.REJECT.getStatus());
                //使得配置生效，回调config接口
                this.applyConfig(task.getAction(), TaskStatus.REJECT.getStatus());
            }
            task.setUpdatedBy(userName);
            task.setUpdatedAt(new Date());
            //更新当前task状态入库
            taskRepo.updateByPrimaryKeySelective(task);
        } else if (task.getStatus().equals(TaskStatus.APPROVE.getStatus())) {
            return RestBody.fail("task has been approved");
        } else if (task.getStatus().equals(TaskStatus.REJECT.getStatus())) {
            return RestBody.fail("task has been REJECTd");
        } else if (task.getStatus().equals(TaskStatus.COMPLETED.getStatus())) {
            return RestBody.fail("task has been completed");
        } else {
            return RestBody.fail("invalid task");
        }
        return RestBody.succeed();
    }

    private boolean isLastApprover(Long roleId){
        List<RoleApprover> roleApprovers = roleApproverRepo.selectByRoleId(roleId);
        return roleApprovers.size() == 0;
    }

    private boolean isLastApprover(List<Long> roleIds) {
        List<RoleApprover> roleApprovers = roleApproverRepo.selectByRoleIds(roleIds);
        return roleApprovers.size() == 0;
    }

    //回调ConfigService
    private void applyConfig(String action, String result) {
        JSONObject jsonObject = JSON.parseObject(action);
        List<JSONObject> applicationList = (List<JSONObject>) jsonObject.get("children");
        List<Long> configIds = Lists.newArrayList();
        applicationList.stream().map(application -> JSONObject.toJavaObject(application, AppInfo.class)).forEach(appInfo -> {
            appInfo.getChildren().forEach(groupInfo -> {
                groupInfo.getChildren().forEach(configInfo -> {
                    configIds.add(configInfo.getId());
                });
            });
        });
        ReceiveRequest request = new ReceiveRequest();
        request.setConfigIds(configIds);
        request.setResult(result);

        String type = (String)jsonObject.get("type");
        switch(type){
            case "add" :
                request.setEvent(Event.ADD);
                break;
            case "modify" :
                request.setEvent(Event.MODIFY);
                break;
            case "delete" :
                request.setEvent(Event.DELETE);
                break;
        }

        configService.receive(request);
    }

    @Override
    public PageBean<HistoryTaskResponse> getHistoryTask(PendingTaskRequest request) {
        List<HistoryTaskResponse> getTaskList = Lists.newArrayList();

        String userName = UserContextHolder.getUserDetail().getName();
        //分两种情况
        List<String> statusList = Lists.newArrayList();
        //第一种情况：作为发起人的已完成任务
        statusList.add(TaskStatus.REJECT.getStatus());
        statusList.add(TaskStatus.COMPLETED.getStatus());
        List<Task> tasks = taskRepo.selectByStatusAndCreatedBy(statusList, userName);
        tasks.forEach(task -> {
            HistoryTaskResponse response = new HistoryTaskResponse();
            response.setCreatedAt(task.getCreatedAt());
            response.setEndAt(task.getUpdatedAt());
            List<ProcessNode> nodeList = Lists.newArrayList();
            ProcessNode lastNode = new ProcessNode();
            lastNode.setOperator(task.getUpdatedBy());
            lastNode.setOperateAt(task.getUpdatedAt());
            if(task.getStatus().equals(TaskStatus.COMPLETED.getStatus())){
                response.setStatus(TaskStatus.COMPLETED.getStatus());
                lastNode.setActionType(ActionType.COMPLETE.getType());
            }else if(task.getStatus().equals(TaskStatus.REJECT.getStatus())) {
                response.setStatus(TaskStatus.REJECT.getStatus());
                lastNode.setActionType(ActionType.REJECT.getType());
            }
            ProcessNode firstNode = new ProcessNode();
            firstNode.setOperator(task.getCreatedBy());
            firstNode.setOperateAt(task.getCreatedAt());
            firstNode.setActionType(ActionType.CREATE.getType());
            nodeList.add(firstNode);
            List<Task> finishedTaskListWithoutLastNode = taskRepo.selectByStatusAndAction(TaskStatus.APPROVE.getStatus(), task.getAction());
            int i = 0;
            while(i < finishedTaskListWithoutLastNode.size()){
                ProcessNode node = new ProcessNode();
                node.setOperateAt(finishedTaskListWithoutLastNode.get(i).getUpdatedAt());
                node.setOperator(finishedTaskListWithoutLastNode.get(i).getUpdatedBy());
                node.setActionType(ActionType.APPROVE.getType());
                nodeList.add(node);
                i++;
            }
            nodeList.add(lastNode);

            response.setNodeList(nodeList);
            response.setAction(task.getAction());
            JSONObject jsonObject = JSON.parseObject(task.getAction());
            String type = (String) jsonObject.get("type");
            response.setType(type);
            getTaskList.add(response);
        });

        //第二种情况:作为审批人的已完成任务
        statusList.clear();
        statusList.add(TaskStatus.REJECT.getStatus());
        statusList.add(TaskStatus.APPROVE.getStatus());
        statusList.add(TaskStatus.COMPLETED.getStatus());
        List<Task> taskList = taskRepo.selectByStatusAndUpdatedBy(statusList, userName);
        taskList.forEach(task -> {
            HistoryTaskResponse response = new HistoryTaskResponse();
            response.setCreatedAt(task.getCreatedAt());
            response.setEndAt(task.getUpdatedAt());
            List<ProcessNode> nodeList = Lists.newArrayList();
            //第一个节点为当前节点
            ProcessNode firstnode = new ProcessNode();
            firstnode.setOperator(userName);
            firstnode.setActionType(ActionType.APPROVE.getType());
            firstnode.setOperateAt(task.getUpdatedAt());
            nodeList.add(firstnode);
            //如果状态为completed或reject，当前为最后一个节点
            if(task.getStatus().equals(TaskStatus.COMPLETED.getStatus())){
                response.setStatus(TaskStatus.COMPLETED.getStatus());
                firstnode.setActionType(ActionType.COMPLETE.getType());
            }else if(task.getStatus().equals(TaskStatus.REJECT.getStatus())){
                response.setStatus(TaskStatus.REJECT.getStatus());
                firstnode.setActionType(ActionType.REJECT.getType());
            }else{
                //状态为approve不是最后一个节点，查出后续节点是否已经审批完
                List<Task> afterTaskList = taskRepo.selectByActionAndUpdatedAt(task.getAction(), task.getUpdatedAt());
                List<Task> finishedTaskList = afterTaskList.stream().filter(afterTask -> afterTask.getStatus().equals(TaskStatus.COMPLETED.getStatus()) || afterTask.getStatus().equals(TaskStatus.REJECT.getStatus())).collect(Collectors.toList());
                if(finishedTaskList.size() > 0){
                    //审批已经结束
                    int i = 0;
                    while(i < afterTaskList.size()){
                        ProcessNode node = new ProcessNode();
                        node.setOperator(afterTaskList.get(i).getUpdatedBy());
                        node.setOperateAt(afterTaskList.get(i).getUpdatedAt());
                        if(i!=afterTaskList.size()-1){
                            node.setActionType(ActionType.APPROVE.getType());
                        }else{
                            if(afterTaskList.get(i).getStatus().equals(TaskStatus.COMPLETED.getStatus())){
                                node.setActionType(ActionType.COMPLETE.getType());
                                response.setStatus(TaskStatus.COMPLETED.getStatus());
                            }else if(afterTaskList.get(i).getStatus().equals(TaskStatus.REJECT.getStatus())){
                                node.setActionType(ActionType.REJECT.getType());
                                response.setStatus(TaskStatus.REJECT.getStatus());
                            }
                        }
                        nodeList.add(node);
                        i++;
                    }
                }else{
                    //还未结束
                    return;
                }
            }

            response.setAction(task.getAction());
            JSONObject jsonObject = JSON.parseObject(task.getAction());
            String type = (String) jsonObject.get("type");
            response.setType(type);
            response.setNodeList(nodeList);
            getTaskList.add(response);

        });

        //按结束时间降序
        List<HistoryTaskResponse> reversedList = getTaskList.stream().sorted(Comparator.comparing(HistoryTaskResponse::getEndAt).reversed()).collect(Collectors.toList());

        return new PageBean<>(request, this.paging(reversedList, request.getPage(), request.getLimit()), CollectionUtils.isNotEmpty(getTaskList) ? getTaskList.size() : 0L);
    }

    @Override
    public PageBean<ProcessingTaskResponse> getProcessingTask(ProcessingTaskRequest request) {
        List<Long> roleIds = UserContextHolder.getUserDetail().getRoleIds();

        if (this.isLastApprover(roleIds)) {
            return new PageBean(request, null, 0L);
        }

        List<ProcessingTaskResponse> getTaskList = Lists.newArrayList();
        String name = UserContextHolder.getUserDetail().getName();
        List<Task> tasks = taskRepo.selectByStatusAndCreatedBy(TaskStatus.PENDING.getStatus(), name);
        ArrayList<String> actionList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(tasks)) {
            tasks.forEach(task -> {
                ProcessingTaskResponse item = new ProcessingTaskResponse();

                if(actionList.contains(task.getAction())){
                    getTaskList.stream().filter(addedTask -> addedTask.getAction().equals(task.getAction()))
                            .forEach(addedTask -> {
                                Role role = roleRepo.selectByRoleId(task.getRoleId());
                                if (addedTask.getNextApproverRoles().indexOf(role.getName()) != -1) {
                                    addedTask.setNextApproverRoles(addedTask.getNextApproverRoles().concat("|").concat(role.getName()));
                                }
                                return;
                            });
                } else {
                    actionList.add(task.getAction());
                    item.setAction(task.getAction());
                }
                item.setCreatedAt(task.getCreatedAt());
                item.setUpdatedAt(task.getUpdatedAt());
                JSONObject jsonObject = JSON.parseObject(item.getAction());
                String type = (String) jsonObject.get("type");
                Role role = roleRepo.selectByRoleId(task.getRoleId());
                item.setNextApproverRoles(role.getName());
                item.setType(type);
                getTaskList.add(item);
            });
        }

        return new PageBean(request, this.paging(getTaskList, request.getPage(), request.getLimit()), CollectionUtils.isNotEmpty(getTaskList) ? getTaskList.size() : 0L);
    }


    @Override
    public int count() {
        List<Long> roleIds = UserContextHolder.getUserDetail().getRoleIds();
        List<Task> tasks = taskRepo.selectByStatusAndRoleIds(TaskStatus.PENDING.getStatus(), roleIds);
        if(!Objects.isNull(tasks)) {
            return tasks.size();
        }
        return 0;
    }

    public List<HistoryTaskResponse> paging(List pageList, int page,
                                            int limit) {
        if (pageList == null) {
            return null;
        }
        if (pageList.size() == 0) {
            return null;
        }

        int count = pageList.size();
        int pageCount = 0;
        if (count % limit == 0) {
            pageCount = count / limit;
        } else {
            pageCount = count / limit + 1;
        }

        int fromIndex = 0;
        int toIndex = 0;

        if (page != pageCount) {
            fromIndex = (page - 1) * limit;
            toIndex = fromIndex + limit;
        } else {
            fromIndex = (page - 1) * limit;
            toIndex = count;
        }

        List<HistoryTaskResponse> completedPageList = pageList.subList(fromIndex, toIndex);

        return completedPageList;
    }
}

