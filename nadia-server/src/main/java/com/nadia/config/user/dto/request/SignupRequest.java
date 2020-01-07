package com.nadia.config.user.dto.request;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author xiang.shi
 * @date 2019-12-27 14:08
 */
@Data
public class SignupRequest {
    @NotBlank(message = "username should not blank")
    private String username;
    @NotBlank(message = "password should not blank")
    private String password;
    @NotBlank(message = "password should not blank")
    private String mail;
}
