package com.nadia.config.admin.controller;

import com.nadia.config.admin.dto.response.CountsResponse;
import com.nadia.config.admin.dto.response.ErrorResponse;
import com.nadia.config.admin.service.AdminService;
import com.nadia.config.common.rest.RestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @Resource
    private AdminService adminService;

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

}
