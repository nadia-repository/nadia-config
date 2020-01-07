package com.nadia.config.system.service;

import com.nadia.config.system.dto.response.MenuResponse;

import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-16 19:31
 */
public interface MenuService {
    List<MenuResponse> roleMenus(List<String> roles);
}
