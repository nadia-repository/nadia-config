package com.nadia.config.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "prefix")
@Data
public class ConfigConfigurationPropertiesDemo {
    private String name;
    private Integer value;
}
