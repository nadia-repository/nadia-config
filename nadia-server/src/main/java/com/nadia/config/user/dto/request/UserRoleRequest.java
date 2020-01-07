package com.nadia.config.user.dto.request;

import lombok.Data;

import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-12 09:57
 */
@Data
public class UserRoleRequest {
    private List<String> roles;
    private String userName;
}
