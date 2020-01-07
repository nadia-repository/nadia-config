package com.nadia.config.meta.dto.request;

import lombok.Data;

@Data
public class GroupRequest {
    private String application;
    private String sourceGroup;
    private String targetGroup;
    private Long applicationId;
}
