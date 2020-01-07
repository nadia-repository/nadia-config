package com.nadia.config.system.dto.response;

import lombok.Data;

import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-18 14:22
 */
@Data
public class RouteResponse {
    private Long buttonId;
    private Long menuId;
    private String key;
    private String title;
    private String type;
    private List<RouteResponse> children;
}
