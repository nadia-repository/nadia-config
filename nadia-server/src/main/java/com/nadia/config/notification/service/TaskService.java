package com.nadia.config.notification.service;

import com.nadia.config.common.rest.RestBody;
import com.nadia.config.meta.dto.response.PageBean;
import com.nadia.config.notification.dto.request.ProcessingTaskRequest;
import com.nadia.config.notification.dto.request.TaskParameter;
import com.nadia.config.notification.dto.request.ApproveTaskRequest;
import com.nadia.config.notification.dto.request.PendingTaskRequest;
import com.nadia.config.notification.dto.response.HistoryTaskResponse;
import com.nadia.config.notification.dto.response.PendingTaskResponse;
import com.nadia.config.notification.dto.response.ProcessingTaskResponse;

public interface TaskService {

    void startTask(TaskParameter config);

    PageBean<PendingTaskResponse> getTask(PendingTaskRequest request);

    RestBody approveTask(ApproveTaskRequest request);

    PageBean<HistoryTaskResponse> getHistoryTask(PendingTaskRequest request);

    PageBean<ProcessingTaskResponse> getProcessingTask(ProcessingTaskRequest request);

    int count();

//    void rollBackHistoryTask();

}
