package com.nadia.config.notification.service.impl;

import com.nadia.config.notification.domain.ClientLog;
import com.nadia.config.notification.dto.request.ClientLogRequest;
import com.nadia.config.notification.repo.ClientLogRepo;
import com.nadia.config.notification.service.ClientLogService;
import com.nadia.config.meta.domain.Application;
import com.nadia.config.meta.domain.Group;
import com.nadia.config.meta.dto.response.PageBean;
import com.nadia.config.meta.repo.ApplicationRepo;
import com.nadia.config.meta.repo.GroupRepo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-24 10:35
 */
@Service
public class ClientLogServiceImpl implements ClientLogService {
    @Resource
    private ClientLogRepo clientLogRepo;
    @Resource
    private ApplicationRepo applicationRepo;
    @Resource
    private GroupRepo groupRepo;

    @Override
    public PageBean<ClientLog> logs(ClientLogRequest clientLogRequest) {
        Application application = applicationRepo.selectByPrimaryKey(clientLogRequest.getApplication());
        Group group = groupRepo.selectByPrimaryKey(clientLogRequest.getGroup());
        List<ClientLog> logs = clientLogRepo.logs(application == null ? null : application.getName(),
                group == null ? null : group.getName(),
                clientLogRequest.getInstance(),
                clientLogRequest.getTraceId(),
                clientLogRequest.getType(),
                clientLogRequest.getLeve(),
                clientLogRequest.getCreatedAt());
        return new PageBean<>(clientLogRequest, logs, CollectionUtils.isNotEmpty(logs) ? logs.size() : 0L);
    }
}
