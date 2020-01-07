package com.nadia.config.notification.dto.request;

import com.nadia.config.meta.dto.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
public class OperationLogRequest extends BaseRequest {
    private Long id;

    private String view;

    private String type;

    private String target;

    private String createdBy;

    private List<String> createdAt;

    private String before;

    private String after;
}
