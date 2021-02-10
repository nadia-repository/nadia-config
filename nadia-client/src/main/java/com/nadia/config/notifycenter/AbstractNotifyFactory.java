package com.nadia.config.notifycenter;

import com.alibaba.fastjson.JSONObject;
import com.nadia.config.bean.ClientInfo;
import com.nadia.config.enums.LogLevelEnum;
import com.nadia.config.listener.MessageConvert;
import com.nadia.config.listener.enumerate.EventType;
import com.nadia.config.listener.messageBody.ClientMessageBody;
import com.nadia.config.publish.RedisPubSub;
import com.nadia.config.utils.TopicUtil;
import com.nadia.config.utils.TraceGenerate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author xiang.shi
 * @date 2019-12-30 10:01
 */
@Slf4j
public abstract class AbstractNotifyFactory implements NotifyFactory{

    private boolean stop;

    private Queue<String> messageQueue = new ArrayBlockingQueue<String>(50);

    private String traceId;

    public void setStop(boolean status){
        stop = status;
    }

    @Async
    public void startPush(){
        if(!stop){
           log.warn("notfiy has been started");
           return;
        }
        traceId = TraceGenerate.getTraceId();
        stop = false;
        if(!stop && messageQueue.size()>0){
            String message = messageQueue.poll();
            notifyServer(message);
        }
    }

    abstract void notifyServer(String message);

    public void addMessage(ClientInfo clientInfo , EventType type , LogLevelEnum level, Object log){
        ClientMessageBody messageBody = new ClientMessageBody();
        messageBody.setClientInfo(clientInfo);
        messageBody.setTraceId(traceId);
        messageBody.setType(type);
        messageBody.setLevel(level);
        messageBody.setMessage(JSONObject.toJSONString(log));
        String message = MessageConvert.packageMessage(messageBody, EventType.CLIENT_MESSAGE);
        messageQueue.add(message);
    }

    public void stopPush() {
        stop = true;
        TraceGenerate.clear();
        traceId = "";
    }

}
