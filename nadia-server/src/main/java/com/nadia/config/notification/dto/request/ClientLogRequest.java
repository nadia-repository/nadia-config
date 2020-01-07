package com.nadia.config.notification.dto.request;

import com.nadia.config.meta.dto.request.BaseRequest;
import lombok.Data;

import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-24 10:18
 */
@Data
public class ClientLogRequest extends BaseRequest {
    private Long application;
    private Long group;
    private String instance;
    private String traceId;
    private String type;
    private String leve;
    private List<String> createdAt;
}
