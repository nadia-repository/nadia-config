package com.nadia.config.meta.dto.response;

import lombok.Data;

import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-05 16:03
 */
@Data
public class GroupInstanceResponse {
    private int key;
    private String group;
    private List<InstanceResponse> instances;
}
