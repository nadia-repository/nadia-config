package com.nadia.config.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.nadia.config.admin.dto.response.CountsResponse;
import com.nadia.config.admin.dto.response.ErrorResponse;
import com.nadia.config.admin.service.AdminService;
import com.nadia.config.common.rest.RestBody;
import com.nadia.config.redis.ConfigCenterRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @Resource
    private AdminService adminService;

    @Resource
    private ConfigCenterRedisService configCenterRedisService;

    @GetMapping("/initRedisFromDB")
    public void initRedisFromDB() {
        log.warn("This operation will clear all redis data!");
        adminService.initRedisFromDB();
    }

    @GetMapping("/counts")
    public RestBody<CountsResponse> getCounts() {
        return RestBody.succeed(adminService.getCounts());
    }

    @GetMapping("/error")
    public RestBody<List<ErrorResponse>> getErrors() {
        return RestBody.succeed(adminService.getErrors());
    }

    @GetMapping("/configs")
    public void getconfigs() {
        Map<String, String> stringStringMap = configCenterRedisService.hgetAll("config:ACCOUNT:Default_ID:192.168.23.56:80:configs");
        for(String key :stringStringMap.keySet()){
            String s = stringStringMap.get(key);
            JSONObject jsonObject = JSONObject.parseObject(s);
            System.out.println(key + ";" + jsonObject.get("oldValue"));
        }
    }

}
