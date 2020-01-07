package com.nadia.config.user.dto.request;

import lombok.Data;

/**
 * @author xiang.shi
 * @date 2019-12-11 15:05
 */
@Data
public class UserRequest {
    private String status;
    private String name;
    private String mail;
    private String roles;
}
