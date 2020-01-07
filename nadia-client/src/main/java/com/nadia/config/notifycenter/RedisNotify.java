package com.nadia.config.notifycenter;

import com.nadia.config.publish.RedisPubSub;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xiang.shi
 * @date 2019-12-30 10:32
 */
public class RedisNotify extends AbstractNotifyFactory {
    @Autowired
    private RedisPubSub redisPubSub;

    @Override
    void notifyServer(String message) {
        redisPubSub.notifyServer(message);
    }
}
