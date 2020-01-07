package com.nadia.config.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class PendingTaskResponse {

    private String type;

    private Long id;

    private String action;

    private String nextApproverRoles;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createdAt;

    private String createdBy;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
//    private Date updatedAt;
//
//    private String updatedBy;

}
