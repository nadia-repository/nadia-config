package com.nadia.config.user.enums;

/**
 * @author xiang.shi
 * @date 2019-12-11 15:02
 */
public enum UserStatusEnum {
    SUBMITTED("submitted"),
    PUBLISHED("published"),
    DELETED("deleted"),
    ;

    private String status;

    public String getStatus() {
        return status;
    }

    UserStatusEnum(String status) {
        this.status = status;
    }
}
