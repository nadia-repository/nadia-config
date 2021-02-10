package com.nadia.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author xiang.shi
 * @date 2020/4/30 5:55 下午
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface ConfigPostConstruct{
}
