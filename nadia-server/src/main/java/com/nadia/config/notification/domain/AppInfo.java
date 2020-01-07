package com.nadia.config.notification.domain;

import lombok.Data;

import java.util.List;

@Data
public class AppInfo {

    private String name;

    private List<GroupInfo> children;

}
