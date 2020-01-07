package com.nadia.config.system.service;

import com.nadia.config.system.dto.response.RouteResponse;

import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-18 14:22
 */
public interface RouteService {
    List<RouteResponse> routes();

    List<String> roleRoutes(String role);
}
