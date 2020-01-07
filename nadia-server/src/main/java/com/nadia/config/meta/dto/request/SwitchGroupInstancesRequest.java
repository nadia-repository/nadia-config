package com.nadia.config.meta.dto.request;

import com.nadia.config.meta.dto.response.GroupInstanceResponse;
import lombok.Data;

import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-05 16:53
 */
@Data
public class SwitchGroupInstancesRequest {
    private String application;
    private List<GroupInstanceResponse> data;
}
