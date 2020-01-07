package com.nadia.config.meta.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class GroupResponse {
    private String name;
    private Long id;
    private List<ConfigResponse> configs;
}
