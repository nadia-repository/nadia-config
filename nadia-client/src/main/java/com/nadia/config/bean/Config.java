package com.nadia.config.bean;

import com.nadia.config.annotation.NadiaConfig;
import com.nadia.config.callback.Callback;
import com.nadia.config.enumerate.ConfigTypeEnum;
import lombok.Data;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Set;

@Data
public class Config {
    private ConfigTypeEnum configType;
    private String beanName;
    private Class<?> beanClass;
    private String key;
    private Field field;
    private String method;
    private Object oldValue;
    private Object currentValue;
    private Class<? extends Callback> callback;
    private Set<NadiaConfig.CallbackScenes> callbackScenesSet;
    private Class<?> fieldType;
    private WeakReference<Object> beanRef;

    public Config(ConfigTypeEnum configType, String beanName, Class<?> beanClass, String key, Field field, String method,
                  Object oldValue, Class<? extends Callback> callback, Set<NadiaConfig.CallbackScenes> callbackScenesSet, Class<?> fieldType, Object bean) {
        this.configType = configType;
        this.beanName = beanName;
        this.beanClass = beanClass;
        this.key = key;
        this.field = field;
        this.method = method;
        this.oldValue = oldValue;
        this.currentValue = oldValue;
        this.callback = callback;
        this.callbackScenesSet = callbackScenesSet;
        this.fieldType = fieldType;
        this.beanRef = new WeakReference<>(bean);
    }
}
