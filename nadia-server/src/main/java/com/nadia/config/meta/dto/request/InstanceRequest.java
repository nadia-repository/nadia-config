package com.nadia.config.meta.dto.request;

import lombok.Data;

/**
 * @author xiang.shi
 * @date 2019-12-05 10:59
 */
@Data
public class InstanceRequest {
    private String application;
    private String group;
    private String instance;
}
