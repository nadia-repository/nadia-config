package com.nadia.config.notification.domain;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ConfigInfo {

    private String name;

    private Long id;

    private List<Map<String, String>> children;

}
