package com.nadia.config.annotation;

import com.nadia.config.spring.ConfigRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ConfigRegistrar.class)
public @interface EnableNadiaConfig {
    //启用环境
    //未设置时所有环境启动
    String[] actives() default {};

    String application() default "";

    //默认分组
    //未设置时为默认分组
    String group() default "";

    String[] basePackages() default {};
}
