package com.nadia.config.annotation;

import com.nadia.config.callback.Callback;

import java.lang.annotation.*;

@Target({ElementType.FIELD,ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NadiaConfig {
    Class<? extends Callback> clazz() default Callback.class;

    NadiaConfig.CallbackScenes[] callbackScenes() default {NadiaConfig.CallbackScenes.UPDATE_VALUE, NadiaConfig.CallbackScenes.SWITCH_GROUP};

    boolean exclude() default false;

    enum CallbackScenes {
        INIT,
        SWITCH_GROUP,
        UPDATE_VALUE,
    }

    //可以指定该配置所属Application
    String application() default "";
    //可以指定该配置所属Group
    String group() default "";
}
