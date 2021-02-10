package com.nadia.config.common.listener;

import com.nadia.config.listener.MessageConvert;
import com.nadia.config.listener.messageBody.ClientMessageBody;
import com.nadia.config.listener.messageBody.MessageBody;
import com.nadia.config.notification.domain.ClientLog;
import com.nadia.config.notification.repo.ClientLogRepo;
import com.nadia.config.redis.ConfigCenterRedisService;
import com.nadia.config.utils.TopicUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author xiang.shi
 * @date 2019-12-23 10:02
 */
@Component
@Slf4j
public class ServerRedisListener {

    @Resource
    private ConfigCenterRedisService configCenterRedisService;
    @Resource
    private ClientLogRepo clientLogRepo;
    @Resource
    private TaskExecutor taskExecutor;

    public static void main(String[] args) {
        System.out.println(TopicUtil.getTopicClientMessage());
    }

    @PostConstruct
    public void init() {
        log.info("Server RedisListener.subscribe start");
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String[] keys = {TopicUtil.getTopicClientMessage()};
                for(;;){
                    try{
                        List<String> messages = configCenterRedisService.bLPop(5, keys);
                        if(CollectionUtils.isNotEmpty(messages)){
                            MessageBody messageBody = MessageConvert.unpackageMessage(messages.get(1).toString());
                            if (messageBody instanceof ClientMessageBody) {
                                ClientMessageBody mg = (ClientMessageBody) messageBody;
                                Map<String, String> agMap = mg.getClientInfo().getApplicationGroupMap();
                                for (String application : agMap.keySet()) {
                                    String group = agMap.get(application);
                                    ClientLog record = new ClientLog();
                                    record.setApplication(application);
                                    record.setGroup(group);
                                    record.setInstance(mg.getClientInfo().getName());
                                    record.setType(mg.getType().name());
                                    record.setLevel(mg.getLevel().getLevel());
                                    record.setTraceId(mg.getTraceId());
                                    record.setLog(mg.getMessage());
                                    record.setCreatedBy("System");
                                    record.setUpdatedBy("System");
                                    clientLogRepo.insertSelective(record);
                                }
                            }
                        }
                    }catch (Exception e){
                        log.error("RedisListener error:{}",e);
                    }
                }
            }
        });
    }
}
