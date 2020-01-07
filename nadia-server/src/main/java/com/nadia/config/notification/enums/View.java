package com.nadia.config.notification.enums;

public enum View {
    METADATA("Metadata"),
    CONFIGS("Configs"),
    ALLOCATE("Allocate"),
    ROLE("Role"),
    USERS("Users");

    private String name;

    public String getName() {
        return name;
    }

    View(String name) {
        this.name = name;
    }
}
