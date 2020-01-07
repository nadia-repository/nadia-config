package com.nadia.config.notification.enums;

public enum ActionType {
    CREATE("create"),
    APPROVE("approve"),
    REJECT("reject"),
    COMPLETE("complete");

    private String type;

    public String getType() {
        return type;
    }

    ActionType(String type) {
        this.type = type;
    }
}
