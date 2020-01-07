package com.nadia.config.notification.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.nadia.config.common.context.UserContextHolder;
import com.nadia.config.common.context.UserDetail;
import com.nadia.config.common.rest.RestBody;
import com.nadia.config.notification.domain.OperationLogWithBLOBs;
import com.nadia.config.notification.dto.request.CleanLogRequest;
import com.nadia.config.notification.dto.request.OperationLogRequest;
import com.nadia.config.notification.dto.response.OperationLogResponse;
import com.nadia.config.notification.enums.OperationType;
import com.nadia.config.notification.enums.TargetType;
import com.nadia.config.notification.enums.View;
import com.nadia.config.notification.repo.OperationLogRepo;
import com.nadia.config.meta.dto.response.PageBean;
import com.nadia.config.notification.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Resource
    private OperationLogRepo operationLogRepo;

    @Override
    public PageBean<OperationLogResponse> getLogs(OperationLogRequest request) {
        List<OperationLogResponse> operationLogList = Lists.newArrayList();
        Page<OperationLogWithBLOBs> list = operationLogRepo.selectByOperationLogRequestWithBLOBs(request);
        //倒叙
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(operationLog -> {
                OperationLogResponse item = new OperationLogResponse();
                BeanUtils.copyProperties(operationLog, item);
                operationLogList.add(item);
            });
        }

        return new PageBean<>(request, operationLogList, CollectionUtils.isNotEmpty(list) ? list.getTotal() : 0L);
    }

    public Date addDays(final Date date, final int amount) {
        if (Objects.isNull(date)) {
            return null;
        }
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, amount);
        return c.getTime();
    }

    @Override
    public RestBody clean(CleanLogRequest request) {
        if (Objects.isNull(request)) {
            return null;
        }
        int type = request.getType();
        Date clearBeforeTime = null;
        if (type == 0) {
            clearBeforeTime = addDays(new Date(), -7);    // 清理七天之前日志数据
        } else if (type == 1) {
            clearBeforeTime = addDays(new Date(), -30);    // 清理三十天之前日志数据
        } else if (type == 2) {
            clearBeforeTime = addDays(new Date(), -90);    // 清理九十天之前日志数据
        } else if (type == 3) {
            clearBeforeTime = addDays(new Date(), -365);    // 清理一年之前日志数据
        } else {
            return RestBody.fail("Invaild Method Type");
        }
        operationLogRepo.deleteByClearBeforeTime(clearBeforeTime);
        return RestBody.succeed();
    }

    @Async
    @Override
    public void log(View view, OperationType type, TargetType target, Object before, Object after) {
        //modifier
        //String modifier = Context.getOperator()
        UserDetail userDetail = UserContextHolder.getUserDetail();
        String modifier = userDetail == null ? "":userDetail.getName();

        //tostring
        String beforeJSONStr = ObjectUtils.isEmpty(before) ? "" : JSON.toJSONString(before);
        String afterJSONStr = ObjectUtils.isEmpty(after) ? "" : JSON.toJSONString(after);
        //add mark
        if(StringUtils.isNoneBlank(beforeJSONStr,afterJSONStr) && type.getType().equals(OperationType.MODIFY.getType())){
            String[] strings = { beforeJSONStr, afterJSONStr };
            String[] markedStrings = this.addMark(strings);
            beforeJSONStr = markedStrings[0];
            afterJSONStr = markedStrings[1];
        }

        OperationLogWithBLOBs record = new OperationLogWithBLOBs();
        record.setCreatedBy(modifier);
        record.setPageName(view.getName());
        record.setType(type.getType());
        record.setTarget(target.getType());
        record.setAfter(afterJSONStr);
        record.setBefore(beforeJSONStr);

        operationLogRepo.insertSelective(record);
        log.info("OperationLogServiceImpl page:{},actionType:{},user:{},before:{},after:{}", view.getName(), type.getType(), modifier, beforeJSONStr, afterJSONStr);
    }

    public String[] addMark(String[] params){
        String before = params[0];
        String after = params[1];

        Map<String, Object> beforeParms = null;
        Map<String, Object> afterParams = null;

        if(StringUtils.isNoneBlank(before, after)){
            beforeParms = JSONObject.parseObject(before);
            afterParams = JSONObject.parseObject(after);
        }else{
            String[] strs={before, after};
            return strs;
        }

        List<String> modifyList = Lists.newArrayList();
        List<String> deleteList = Lists.newArrayList();
        List<String> addList = Lists.newArrayList();
        Set<String> beforekeys = beforeParms.keySet();
        Set<String> afterkeys = afterParams.keySet();
        for(String key : beforekeys){
            if(afterParams.containsKey(key)){
                if(!afterParams.get(key).equals(beforeParms.get(key))){
                    modifyList.add(key);
                }
            }else{
                deleteList.add(key);
            }
        }
        for (String key : afterkeys) {
            if(!beforeParms.containsKey(key)){
                addList.add(key);
            }
        }

        for(String key : modifyList){
            if(afterParams.containsKey(key)){
                String value = (String)afterParams.get(key);
                after = after.replace("\"" + key + "\":\"" + value + "\""
                        , "\"<u style='color: #46A3FF'>" + key + "</u>\":\"<u style='color: #46A3FF'>" + value + "</u>\"");
            }
        }
        for(String key : addList){
            if(afterParams.containsKey(key)){
                String value = (String)afterParams.get(key);
                after = after.replace("}",
                        "\"<u style='color: #01B468'>" + key + "</u>\":\"<u style='color: #01B468'>" + value + "</u>\"}");
            }
        }
        for(String key : deleteList){
            if(afterParams.containsKey(key)){
                String value = (String)afterParams.get(key);
                after = after.replace("}",
                        "\"<s style='color: #EA0000'>" + key + "</s>\":\"<s style='color: #EA0000'>" + value + "</s>\"}");
            }
        }
        String[] strs={before, after};
        return strs;
    }

}

