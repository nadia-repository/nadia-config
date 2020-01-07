package com.nadia.config.user.dto.request;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "username should not blank")
    private String username;
    @NotBlank(message = "password should not blank")
    private String password;
}
