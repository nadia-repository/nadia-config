package com.nadia.config.common.listener;

import com.nadia.config.listener.MessageConvert;
import com.nadia.config.listener.messageBody.ClientMessageBody;
import com.nadia.config.listener.messageBody.MessageBody;
import com.nadia.config.notification.domain.ClientLog;
import com.nadia.config.notification.repo.ClientLogRepo;
import com.nadia.config.redis.RedisService;
import com.nadia.config.utils.TopicUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-23 10:02
 */
@Component
@Slf4j
public class ServerRedisListener {

    @Resource
    private RedisService redisService;
    @Resource
    private ClientLogRepo clientLogRepo;
    @Resource
    private TaskExecutor taskExecutor;

    @PostConstruct
    public void init() {
        log.info("Server RedisListener.subscribe start");
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String[] keys = {TopicUtil.getTopicClientMessage()};
                for(;;){
                    List<String> messages = redisService.bLPop(5000, keys);
                    if(CollectionUtils.isNotEmpty(messages)){
                        MessageBody messageBody = MessageConvert.unpackageMessage(messages.get(1).toString());
                        if (messageBody instanceof ClientMessageBody) {
                            ClientMessageBody mg = (ClientMessageBody) messageBody;
                            ClientLog record = new ClientLog();
                            record.setApplication(mg.getClientInfo().getApplication());
                            record.setGroup(mg.getClientInfo().getGroup());
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
            }
        });
    }
}
