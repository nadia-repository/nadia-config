package com.nadia.config.common.configuration;

import com.nadia.config.common.context.ContextDecorator;
import com.nadia.config.utils.SpringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author xiang.shi
 * @date 2019-12-09 18:26
 */
@Configuration
public class CustomizeConfigurer {

    @Bean
    SpringUtils springUtils(){
        return new SpringUtils();
    }
}
