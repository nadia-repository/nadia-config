package com.nadia.config.meta.service.event;

/**
 * @author: Wally.Wang
 * @date: 2020/11/04
 * @description:
 */
public interface EventPublisher {

    void onPublished(Long configId);

}
