package com.nadia.config.notification.domain;

import lombok.Data;

import java.util.List;

@Data
public class GroupInfo {

    private String name;

    private List<ConfigInfo> children;

}
