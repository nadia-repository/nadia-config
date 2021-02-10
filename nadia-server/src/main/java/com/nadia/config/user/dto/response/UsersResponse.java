package com.nadia.config.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nadia.config.user.enums.UserStatusEnum;
import lombok.Data;

import java.util.Date;

/**
 * @author xiang.shi
 * @date 2019-12-11 14:59
 */
@Data
public class UsersResponse {
    private String name;
    private String mail;
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updatedAt;
    private String status;
    private String roles;
}
