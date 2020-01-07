package com.nadia.config.common.context;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDetail {
    private Long uid;
    private String name;
    private String email;
    private List<Long> roleIds;
    private String token;
}
