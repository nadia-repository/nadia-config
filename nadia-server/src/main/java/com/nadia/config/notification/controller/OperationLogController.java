package com.nadia.config.notification.controller;

import com.alibaba.fastjson.JSON;
import com.nadia.config.common.rest.RestBody;
import com.nadia.config.notification.dto.request.CleanLogRequest;
import com.nadia.config.notification.dto.request.OperationLogRequest;
import com.nadia.config.notification.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Slf4j
@RequestMapping("/operationlog")
@RestController
public class OperationLogController {

    @Resource
    private OperationLogService operationLogService;

    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public RestBody getOperationLog(@RequestBody OperationLogRequest request) {
        log.info("getOperationLog request parameter: {}", JSON.toJSONString(request));
        return RestBody.succeed(operationLogService.getLogs(request));
    }

    @RequestMapping(value = "/clear", method = RequestMethod.POST)
    public RestBody clearLog(@RequestBody CleanLogRequest request) {
        log.info("clearLog request parameter: {}", JSON.toJSONString(request));
        return operationLogService.clean(request);
    }
}
