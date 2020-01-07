package com.nadia.config.notification.enums;

/**
 * @author xiang.shi
 * @date 2019-12-19 13:28
 */
public enum TargetType {
    APPLICATION("Application"),
    GROUP("Group"),
    CONFIG("Config"),
    INSTANCE("Instance"),
    ROLE("Role"),
    USER("User"),
    ;

    private String type;

    public String getType() {
        return type;
    }

    TargetType(String type) {
        this.type = type;
    }
}
