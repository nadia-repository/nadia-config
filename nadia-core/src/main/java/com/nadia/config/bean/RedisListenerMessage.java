package com.nadia.config.bean;

import lombok.Data;

@Data
public class RedisListenerMessage {
    private String key;
    private Object value;
}
