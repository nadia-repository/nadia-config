package com.nadia.config.spring;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author: Wally.Wang
 * @date: 2020/12/23
 * @description:
 */
@Getter
public class ConfigBoundEvent extends ApplicationEvent {

    public final static String SUCCESS = "success";
    public final static String FAILED = "failed";

    private String result;

    public ConfigBoundEvent(Object source) {
        super(source);
    }

    public ConfigBoundEvent(Object source, String result) {
        super(source);
        this.result = result;
    }

}
