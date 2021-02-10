package com.nadia.config.publish;

import com.alibaba.fastjson.JSONObject;
import com.nadia.config.bean.ClientInfo;
import com.nadia.config.enums.LogLevelEnum;
import com.nadia.config.listener.messageBody.ClientMessageBody;
import com.nadia.config.listener.messageBody.MessageBody;
import com.nadia.config.listener.MessageConvert;
import com.nadia.config.listener.enumerate.EventType;
import com.nadia.config.redis.ConfigCenterRedisService;
import com.nadia.config.utils.TopicUtil;
import com.nadia.config.utils.TraceGenerate;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;

/**
 * @author xiang.shi
 * @date 2019-12-06 14:11
 */
public class RedisPubSub {
    @Resource
    private ConfigCenterRedisService configCenterRedisService;

    public void notifyClient(String topic, MessageBody messageBody, EventType type) {
        String message = MessageConvert.packageMessage(messageBody, type);
        configCenterRedisService.publish(topic, message);
    }

    @Async
    public void notifyServer(ClientInfo clientInfo , EventType type , LogLevelEnum level, Object log){
        ClientMessageBody messageBody = new ClientMessageBody();
        messageBody.setClientInfo(clientInfo);
        messageBody.setTraceId(TraceGenerate.getTraceId());
        messageBody.setType(type);
        messageBody.setLevel(level);
        messageBody.setMessage(JSONObject.toJSONString(log));
        String message = MessageConvert.packageMessage(messageBody, EventType.CLIENT_MESSAGE);
//        redisService.publish(TopicUtil.getTopicClientMessage(), message);
        configCenterRedisService.lpush(TopicUtil.getTopicClientMessage(),message);
    }

    @Async
    public void notifyServer(String message){
        configCenterRedisService.lpush(TopicUtil.getTopicClientMessage(),message);
    }
}
