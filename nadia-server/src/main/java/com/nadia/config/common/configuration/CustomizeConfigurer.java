package com.nadia.config.common.configuration;

import com.nadia.config.utils.SpringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
