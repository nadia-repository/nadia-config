package com.nadia.config.meta.dto.request;

import lombok.Data;

import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-05 13:56
 */
@Data
public class CompareRequest {
    private String application;
    private List<String> groups;
}
