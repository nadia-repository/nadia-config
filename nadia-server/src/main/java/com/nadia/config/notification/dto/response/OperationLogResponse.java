package com.nadia.config.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class OperationLogResponse {
    private Long id;

    private String pageName;

    private String type;

    private String target;

    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createdAt;

    private String before;

    private String after;
}
