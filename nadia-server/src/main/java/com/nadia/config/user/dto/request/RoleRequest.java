package com.nadia.config.user.dto.request;

import com.nadia.config.system.dto.response.RouteResponse;
import lombok.Data;

import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-12 14:00
 */
@Data
public class RoleRequest {
    private String name;
    private String description;
    private List<String> approvers;
    List<RouteResponse> routes;
}
