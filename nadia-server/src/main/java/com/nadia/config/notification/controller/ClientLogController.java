package com.nadia.config.notification.controller;

import com.nadia.config.common.rest.RestBody;
import com.nadia.config.notification.dto.request.ClientLogRequest;
import com.nadia.config.notification.service.ClientLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Slf4j
@RequestMapping("/client")
@RestController
public class ClientLogController {

    @Resource
    private ClientLogService clientLogService;

    @RequestMapping(value = "/logs", method = RequestMethod.POST)
    public RestBody logs(@RequestBody ClientLogRequest clientLogRequest) {
        return RestBody.succeed(clientLogService.logs(clientLogRequest));
    }

}
