package com.nadia.config.common.configuration;

import com.nadia.config.common.context.UserContextFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiang.shi
 * @date 2019-12-09 18:25
 */
@Configuration
public class FilterConfigurer {

    @Bean
    public FilterRegistrationBean userDetailFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        UserContextFilter filter = new UserContextFilter();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("userDetail-filter");
        registration.setOrder(1);
        return registration;
    }
}
