package com.nadia.config.notification.domain;

import lombok.Data;

import java.util.List;

@Data
public class ActionInfo {

    private String type;

    private List<AppInfo> children;

}
