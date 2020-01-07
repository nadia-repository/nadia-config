package com.nadia.config.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.Advised;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
final public class SpringUtils {

    private static AtomicReference<SpringUtils> instance = new AtomicReference<>();

    @Resource
    private MessageSource messageSource;

    @Resource
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        instance.compareAndSet(null, this);
    }

    public static MessageSource getMessageSource(){
        return instance.get().messageSource;
    }

    public static ApplicationContext getApplicationContext() {
        return instance.get().applicationContext;
    }

    public static <T> T getTargetBean(T bean) {
        if (bean instanceof Advised) {
            try {
                return (T) ((Advised) bean).getTargetSource().getTarget();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return bean;
    }

}
