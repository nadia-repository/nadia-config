package com.nadia.config.meta.dto.response;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-05 13:57
 */
@Data
public class CompareResponse {
    private List<JSONObject> configTable;
    private List<JSONObject> configHeader;
}
