package com.nadia.config.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nadia.config.notification.domain.ProcessNode;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class HistoryTaskResponse {

    private String type;

    private String action;

    private String status;

    private List<ProcessNode> nodeList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date endAt;

}
