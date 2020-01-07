package com.nadia.config.system.dto.response;

import lombok.Data;

import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-16 19:28
 */
@Data
public class MenuResponse {
    private String name;
    private List<MenuResponse> children;
}
