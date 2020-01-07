package com.nadia.config.meta.dto.request;

import lombok.Data;

import java.util.List;

/**
 * @author: Wally.Wang
 * @date: 2019/12/24
 * @description:
 */
@Data
public class ExportConfigRequest {

    private Long applicationId;
    private Long groupId;
    private String name;
    private String key;
    private List<Long> ids;
    private List<String> statuses;

}
