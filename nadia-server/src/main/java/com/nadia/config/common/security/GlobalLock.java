package com.nadia.config.common.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xiang.shi
 * @date 2019-12-06 10:41
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GlobalLock {
    Class<?> clazz() default GlobalLockAspect.class;

    //lock key
    String key() default "";
}
