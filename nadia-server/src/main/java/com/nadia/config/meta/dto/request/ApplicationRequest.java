package com.nadia.config.meta.dto.request;

import lombok.Data;

@Data
public class ApplicationRequest {
    private String application;
    private String description;
    private String mail;
    private String group;
}
