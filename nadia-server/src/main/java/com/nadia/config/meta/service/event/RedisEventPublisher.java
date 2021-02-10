package com.nadia.config.meta.service.event;

import com.nadia.config.listener.enumerate.EventType;
import com.nadia.config.listener.messageBody.UpdateValueMessageBody;
import com.nadia.config.meta.domain.Application;
import com.nadia.config.meta.domain.Config;
import com.nadia.config.meta.domain.Group;
import com.nadia.config.meta.repo.ApplicationRepo;
import com.nadia.config.meta.repo.ConfigRepo;
import com.nadia.config.meta.repo.GroupRepo;
import com.nadia.config.publish.RedisPubSub;
import com.nadia.config.redis.ConfigCenterRedisService;
import com.nadia.config.utils.RedisKeyUtil;
import com.nadia.config.utils.TopicUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: Wally.Wang
 * @date: 2020/11/04
 * @description:
 */
@Component("redisEventPublisher")
public class RedisEventPublisher implements EventPublisher {

    @Autowired
    private ConfigRepo configRepo;

    @Autowired
    private ApplicationRepo applicationRepo;

    @Autowired
    private GroupRepo groupRepo;

    @Autowired
    private ConfigCenterRedisService configCenterRedisService;

    @Autowired
    private RedisPubSub redisPubSub;

    @Override
    @Transactional(readOnly = true)
    public void onPublished(Long configId) {
        Config config = configRepo.selectByPrimaryKey(configId);
        Application application = applicationRepo.selectByPrimaryKey(config.getApplicationId());
        Group group = groupRepo.selectByPrimaryKey(config.getGroupId());
        configCenterRedisService.hset(RedisKeyUtil.getGroupConfig(application.getName(), group.getName()), config.getKey(), config.getValue());
        UpdateValueMessageBody messageBody = new UpdateValueMessageBody();
        messageBody.setApplication(application.getName());
        messageBody.setGroup(group.getName());
        messageBody.setKey(config.getKey());
        messageBody.setValue(config.getValue());
        redisPubSub.notifyClient(TopicUtil.getTopicApplication(application.getName()), messageBody, EventType.UPDATE_VALUE);
    }

}
