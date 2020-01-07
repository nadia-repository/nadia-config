package com.nadia.config.notification.domain;

public class OperationLogWithBLOBs extends OperationLog {
    private String before;

    private String after;

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before == null ? null : before.trim();
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after == null ? null : after.trim();
    }
}