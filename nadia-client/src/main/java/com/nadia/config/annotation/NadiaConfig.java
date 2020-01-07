package com.nadia.config.annotation;

import com.nadia.config.callback.Callback;

import java.lang.annotation.*;

@Target({ElementType.FIELD,ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NadiaConfig {
    Class<? extends Callback> clazz();

    CallbackScenes[] callbackScenes() default {CallbackScenes.UPDATE_VALUE, CallbackScenes.SWITCH_GROUP};

    boolean exclude() default false;

    enum CallbackScenes {
        INIT,
        SWITCH_GROUP,
        UPDATE_VALUE,
    }
}
