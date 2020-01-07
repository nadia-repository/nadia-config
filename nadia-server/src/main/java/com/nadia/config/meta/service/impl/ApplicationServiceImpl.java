package com.nadia.config.meta.service.impl;

import com.google.common.collect.Lists;
import com.nadia.config.meta.domain.Application;
import com.nadia.config.meta.dto.response.ApplicationResponse;
import com.nadia.config.meta.repo.ApplicationRepo;
import com.nadia.config.meta.service.ApplicationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: Wally.Wang
 * @date: 2019/12/06
 * @description:
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Resource
    private ApplicationRepo applicationRepo;

    @Override
    public List<ApplicationResponse> getApplications() {
        List<ApplicationResponse> applications = Lists.newArrayList();

        List<Application> list = applicationRepo.selectAll();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(application -> {
                ApplicationResponse item = new ApplicationResponse();
                item.setId(application.getId());
                item.setName(application.getName());
                applications.add(item);
            });
        }

        return applications;
    }

}
