package com.nadia.config.notification.controller;

import com.nadia.config.common.rest.RestBody;
import com.nadia.config.notification.dto.request.ApproveTaskRequest;
import com.nadia.config.notification.dto.request.PendingTaskRequest;
import com.nadia.config.notification.dto.request.ProcessingTaskRequest;
import com.nadia.config.notification.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class TaskController {

    @Resource
    private TaskService taskService;

    @RequestMapping(value = "/task/get", method = RequestMethod.POST)
    public RestBody getTask(@RequestBody PendingTaskRequest request) {
        return RestBody.succeed(taskService.getTask(request));
    }

    @RequestMapping(value = "/task/approve", method = RequestMethod.POST)
    public RestBody approveTask(@RequestBody ApproveTaskRequest request) {
        return taskService.approveTask(request);
    }

    @RequestMapping(value = "/historytask/get", method = RequestMethod.POST)
    public RestBody getHistoryTask(@RequestBody PendingTaskRequest request) {
        return RestBody.succeed(taskService.getHistoryTask(request));
    }

    @RequestMapping(value = "/processingtask/get", method = RequestMethod.POST)
    public RestBody getProcessingTask(@RequestBody ProcessingTaskRequest request) {
        return RestBody.succeed(taskService.getProcessingTask(request));
    }

}
