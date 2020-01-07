package com.nadia.config.meta.dto.request;

import lombok.Data;

import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-11 11:35
 */
@Data
public class RoleConfigRequest {
    private String role;
    private List<Long> configIds;
}
