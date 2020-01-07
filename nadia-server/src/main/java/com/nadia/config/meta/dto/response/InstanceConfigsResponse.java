package com.nadia.config.meta.dto.response;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author xiang.shi
 * @date 2019-12-04 15:10
 */
@Data
@Accessors(chain=true)
public class InstanceConfigsResponse {
    private String serverKey;
    private Object serverConfig;
    private String clientKey;
    private Object clientConfigNew;
    private Object clientConfigOld;
}
