package com.nadia.config.meta.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.nadia.config.meta.domain.Application;
import com.nadia.config.meta.dto.response.ApplicationResponse;
import com.nadia.config.meta.dto.response.GroupedApplicationResponse;
import com.nadia.config.meta.repo.ApplicationRepo;
import com.nadia.config.meta.service.ApplicationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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

    @Override
    public List<GroupedApplicationResponse> getGroupedApplications() {
        List<GroupedApplicationResponse> groupedApplications = Lists.newArrayList();

        List<Application> list = applicationRepo.selectAll();
        if (CollectionUtils.isNotEmpty(list)) {
            Set<String> labels = Sets.newTreeSet();
            Map<String, List<ApplicationResponse>> container = Maps.newHashMap();
            list.forEach(application -> {
                String label = application.getName().substring(0, 1).toUpperCase();
                List<ApplicationResponse> items = container.get(label);
                if (CollectionUtils.isEmpty(items)) {
                    items = Lists.newArrayList();
                    ApplicationResponse item = new ApplicationResponse();
                    BeanUtils.copyProperties(application, item);
                    items.add(item);
                } else {
                    ApplicationResponse item = new ApplicationResponse();
                    BeanUtils.copyProperties(application, item);
                    items.add(item);
                }
                labels.add(label);
                container.put(label, items);
            });
            Iterator<String> iterator = labels.iterator();
            while (iterator.hasNext()) {
            	String label = iterator.next();
	            List<ApplicationResponse> items = container.get(label);
            	Collections.sort(items, (o1, o2) -> o1.hashCode() - o2.hashCode());
            	GroupedApplicationResponse groupedApplication = new GroupedApplicationResponse();
                groupedApplication.setLabel(label);
                groupedApplication.setItems(items);
            	groupedApplications.add(groupedApplication);
            }
        }

        return groupedApplications;
    }

}
