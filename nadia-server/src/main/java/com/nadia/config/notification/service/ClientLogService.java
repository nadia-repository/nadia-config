package com.nadia.config.notification.service;

import com.nadia.config.notification.domain.ClientLog;
import com.nadia.config.notification.dto.request.ClientLogRequest;
import com.nadia.config.meta.dto.response.PageBean;

/**
 * @author xiang.shi
 * @date 2019-12-24 10:33
 */
public interface ClientLogService {

    PageBean<ClientLog> logs(ClientLogRequest clientLogRequest);
}
