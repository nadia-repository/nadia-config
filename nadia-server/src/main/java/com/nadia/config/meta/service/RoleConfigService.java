package com.nadia.config.meta.service;

import com.nadia.config.meta.dto.request.RoleConfigRequest;
import com.nadia.config.meta.dto.response.RoleConfigResponse;
import com.nadia.config.meta.dto.response.RoleConfigsResponse;

import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-10 11:30
 */
public interface RoleConfigService {
    List<RoleConfigResponse> list(String role);
    RoleConfigsResponse configs(String role);
    void update(RoleConfigRequest roleConfigRequest);
}
