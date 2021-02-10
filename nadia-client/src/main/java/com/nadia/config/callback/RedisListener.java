package com.nadia.config.callback;

import com.nadia.config.annotation.NadiaConfig;
import com.nadia.config.bean.ConfigContextHolder;
import com.nadia.config.enums.LogLevelEnum;
import com.nadia.config.listener.*;
import com.nadia.config.listener.enumerate.EventType;
import com.nadia.config.listener.messageBody.HeartbeatMessageBody;
import com.nadia.config.listener.messageBody.MessageBody;
import com.nadia.config.listener.messageBody.SwitchInstanceMessageBody;
import com.nadia.config.listener.messageBody.UpdateValueMessageBody;
import com.nadia.config.notifycenter.NotifyFactory;
import com.nadia.config.publish.RedisPubSub;
import com.nadia.config.spi.LoadConfig;
import com.nadia.config.utils.ClientValueUtil;
import com.nadia.config.utils.TopicUtil;
import com.nadia.config.utils.TraceGenerate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
public class RedisListener extends AbstractListener implements Listener {

    @Resource(name = "configCenterRedisListenerContainer")
    private RedisMessageListenerContainer redisMessageListenerContainer;

    @Autowired
    private RedisPubSub redisPubSub;

    @Autowired
    private NotifyFactory notifyFactory;

    public void init() {
        log.info("RedisListener.subscribe start");
        Map<String, String> applicationGroupMap = initEnvironment.getClientInfo().getApplicationGroupMap();
        for(String application : applicationGroupMap.keySet()){
            redisMessageListenerContainer.addMessageListener(new MyMessageListener(loadConfig), new ChannelTopic(TopicUtil.getTopicApplication(application)));
        }
    }

    class MyMessageListener implements MessageListener {

        private LoadConfig loadConfig;

        MyMessageListener(LoadConfig loadConfig) {
            this.loadConfig = loadConfig;
        }

        @Override
        public void onMessage(Message message, byte[] pattern) {
            notifyFactory.startPush();
            MessageBody messageBody = MessageConvert.unpackageMessage(message.toString());
            if (messageBody instanceof UpdateValueMessageBody) {
                log.info("receive UpdateValueMessageBody start");
                redisPubSub.notifyServer(initEnvironment.getClientInfo(), EventType.CLIENT_MESSAGE, LogLevelEnum.LOG,"receive UpdateValueMessageBody start");
                UpdateValueMessageBody updateValueMessageBody = (UpdateValueMessageBody) messageBody;
                String application = updateValueMessageBody.getApplication();
                String group = initEnvironment.getClientInfo().getApplicationGroupMap().get(application);
                if (group.equals(updateValueMessageBody.getGroup())) {
                    //updateClientValue local value
                    boolean hasFaile = loadConfig.updateClientValue(application,
                            group,
                            updateValueMessageBody.getKey(),
                            updateValueMessageBody.getValue(),
                            NadiaConfig.CallbackScenes.UPDATE_VALUE);
                    if(!hasFaile){
                        Object oldValue = ConfigContextHolder.getOldValue(application,group,updateValueMessageBody.getKey());
                        // update server value
                        loadConfig.pushClientConfig(updateValueMessageBody.getKey(),
                                ClientValueUtil.serializer(updateValueMessageBody.getValue(), oldValue),
                                application,
                                group);
                    }
                }
            } else if (messageBody instanceof SwitchInstanceMessageBody) {
                log.info("receive SwitchInstanceMessageBody start");
                redisPubSub.notifyServer(initEnvironment.getClientInfo(), EventType.CLIENT_MESSAGE, LogLevelEnum.LOG,"receive SwitchInstanceMessageBody start");
                SwitchInstanceMessageBody switchInstanceMessageBody = (SwitchInstanceMessageBody) messageBody;
                //update local value
                loadConfig.switchGroup(switchInstanceMessageBody.getApplication(),
                        switchInstanceMessageBody.getGroupFrom(),
                        switchInstanceMessageBody.getGroupTo(),
                        switchInstanceMessageBody.getInstance());
            } else if (messageBody instanceof HeartbeatMessageBody) {
                log.info("receive HeartbeatMessageBody");

            }
            notifyFactory.stopPush();
        }
    }

}
