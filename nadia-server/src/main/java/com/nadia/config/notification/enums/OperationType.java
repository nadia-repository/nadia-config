package com.nadia.config.notification.enums;

public enum OperationType {
    ADD("add"),
    IMPORT("import"),
    MODIFY("modify"),
    PUBLISH("publish"),
    DELETE("delete"),
    SWITCH("switch"),
    //审批操作
    APPROVE("approve"),
    ROLLBACK("rollback"),
    //user
    ACTIVATE("activate"),
    ;

    private String type;

    public String getType() {
        return type;
    }

    OperationType(String type) {
        this.type = type;
    }
}
