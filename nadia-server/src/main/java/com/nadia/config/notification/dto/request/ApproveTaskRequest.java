package com.nadia.config.notification.dto.request;

import lombok.Data;


@Data
public class ApproveTaskRequest {

    private Long taskId;

    private boolean flag;

}
