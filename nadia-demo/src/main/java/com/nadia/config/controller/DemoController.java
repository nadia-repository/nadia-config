package com.nadia.config.controller;

import com.alibaba.fastjson.JSONObject;
import com.nadia.config.bean.ConfigConfigurationPropertiesDemo;
import com.nadia.config.bean.ConfigValueDemo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author xiang.shi
 * @date 2019-12-12 17:07
 */
@Slf4j
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Resource
    private ConfigValueDemo configValueDemo;
    @Resource
    private ConfigConfigurationPropertiesDemo configConfigurationPropertiesDemo;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public JSONObject status(){
        JSONObject result = new JSONObject();
        result.put("configValueDemo",configValueDemo);
        result.put("configConfigurationPropertiesDemo",configConfigurationPropertiesDemo);
        return result;
    }

}
