package com.nadia.config.notification.enums;

public enum TaskStatus {
    PENDING("pending"),
    APPROVE("approve"),
    REJECT("reject"),
    COMPLETED("completed"),
    INVALID("invalid"),
    ;

    private String status;

    public String getStatus() {
        return status;
    }

    TaskStatus(String status) {
        this.status = status;
    }
}
