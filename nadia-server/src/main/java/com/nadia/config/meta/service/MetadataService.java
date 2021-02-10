package com.nadia.config.meta.service;

import com.nadia.config.meta.dto.request.ApplicationRequest;
import com.nadia.config.meta.dto.request.CompareRequest;
import com.nadia.config.meta.dto.request.GroupRequest;
import com.nadia.config.meta.dto.request.SwitchGroupInstancesRequest;
import com.nadia.config.meta.dto.response.*;

import java.util.List;

public interface MetadataService {

    List<ApplicationResponse> getApplications();

    List<MetadataResponse> list(List<String> applications);

    void addApplication(ApplicationRequest applicationRequest);

    void deleteApplication(ApplicationRequest applicationRequest);

    void updateApplication(ApplicationRequest applicationRequest);

    List<GroupResponse> getGroups(String application);

    void addGroup(GroupRequest groupRequest);

    List<InstanceResponse> getInstances(String application, String group);

    List<InstanceConfigsResponse> getInstanceConfig(String application, String group ,String instance);

    CompareResponse compareGroupConfigs(CompareRequest compareRequest);

    List<GroupInstanceResponse> getGroupInstances(String application);

    void switchGroupInstances(SwitchGroupInstancesRequest switchGroupInstancesRequest);
}
