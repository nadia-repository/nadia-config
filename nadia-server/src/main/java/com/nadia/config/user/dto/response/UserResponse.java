package com.nadia.config.user.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class UserResponse {
    private String name;
    private List<String> roles;
}
