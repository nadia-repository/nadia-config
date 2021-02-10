package com.nadia.config.notification.service;

import com.nadia.config.common.rest.RestBody;
import com.nadia.config.meta.dto.response.PageBean;
import com.nadia.config.notification.dto.request.CleanLogRequest;
import com.nadia.config.notification.dto.request.OperationLogRequest;
import com.nadia.config.notification.dto.response.OperationLogResponse;
import com.nadia.config.notification.enums.OperationType;
import com.nadia.config.notification.enums.View;
import com.nadia.config.notification.enums.TargetType;

public interface OperationLogService {

    /**
     * 获取日志
     * @param request
     * @return
     */
    PageBean<OperationLogResponse> getLogs(OperationLogRequest request);

    /**
     * 清除日志
     * @param request
     * @return
     */
    RestBody clean(CleanLogRequest request);

    /**
     *  记录日志增删改操作
     * @param view 操作页面
     * @param type 操作类型
     * @param before 操作前参数（可为空）
     * @param after 操作后参数
     */
    void log(View view, OperationType type, TargetType target, Object before, Object after);

}
