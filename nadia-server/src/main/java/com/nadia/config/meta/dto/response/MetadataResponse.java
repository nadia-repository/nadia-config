package com.nadia.config.meta.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class MetadataResponse {
    private Long id;
    private String application;
    private String description;
    private String mail;
    private String group;
    private String instances;
    private boolean hasChildren;
    private List<MetadataResponse> children;
}
