package com.nadia.config.meta.dto.response;

import lombok.Data;

/**
 * @author: Wally.Wang
 * @date: 2019/12/24
 * @description:
 */
@Data
public class ExportConfigResponse {

    private String application;
    private String group;
    private String name;
    private String key;
    private String value;
    private String description;

}
