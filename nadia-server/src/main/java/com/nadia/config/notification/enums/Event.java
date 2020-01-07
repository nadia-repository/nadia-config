package com.nadia.config.notification.enums;

public enum Event {
    ADD("add"),
    MODIFY("modify"),
    DELETE("delete");

    private String type;

    public String getType() {
        return type;
    }

    Event(String type) {
        this.type = type;
    }
}
