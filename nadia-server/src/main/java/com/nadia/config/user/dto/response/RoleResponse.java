package com.nadia.config.user.dto.response;

import lombok.Data;

@Data
public class RoleResponse {
    private Long id;
    private String name;
    private String description;
    private String approvers;
    private MenuResponse[] routes;
}
