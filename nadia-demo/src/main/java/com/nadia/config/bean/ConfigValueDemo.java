package com.nadia.config.bean;

import com.nadia.config.annotation.NadiaConfig;
import com.nadia.config.callback.CallbackDemo;
import com.nadia.config.annotation.EnableNadiaConfig;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
@EnableNadiaConfig(basePackages = {"com.nadia.xxxx","com.aaaa"})
public class ConfigValueDemo {

    @NadiaConfig(clazz = CallbackDemo.class)
    @Value("${nadia.Integer}")
    private Integer nadia_Integer;

    @Value("${nadia.String}")
    private String nadia_String;

    @Value("${nadia.boolean}")
    private boolean nadia_boolean;
}
