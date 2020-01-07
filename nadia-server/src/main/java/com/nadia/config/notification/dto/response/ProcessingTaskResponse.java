package com.nadia.config.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author JunjieShen
 */
@Data
@Accessors(chain=true)
public class ProcessingTaskResponse {

    private String type;

    private String action;

    private String nextApproverRoles;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updatedAt;

}
