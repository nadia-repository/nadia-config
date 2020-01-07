package com.nadia.config.user.service;

import com.nadia.config.user.dto.request.RoleRequest;
import com.nadia.config.user.dto.request.UserRoleRequest;
import com.nadia.config.user.dto.response.ApproverResponse;
import com.nadia.config.user.dto.response.RoleResponse;

import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-09 16:35
 */
public interface RoleService {
    List<RoleResponse> list();

    List<RoleResponse> listByIds(List<Long> ids);

    List<RoleResponse> listByUserName(String userName);

    void updateUserRole(UserRoleRequest userRoleRequest);

    void add(RoleRequest roleRequest);

    void update(RoleRequest roleRequest);

    void delete(RoleRequest roleRequest);

    ApproverResponse approver(String role);
}
