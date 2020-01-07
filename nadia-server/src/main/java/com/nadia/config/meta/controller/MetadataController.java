package com.nadia.config.meta.controller;

import com.alibaba.fastjson.JSON;
import com.nadia.config.common.rest.RestBody;
import com.nadia.config.meta.dto.request.*;
import com.nadia.config.meta.dto.response.*;
import com.nadia.config.meta.service.MetadataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/meta")
public class MetadataController {

    @Resource
    private MetadataService metadataService;


    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public RestBody<List<MetadataResponse>> list(@RequestBody MetadataRequest metadataRequest){
        log.info("list metadataRequest:{}", JSON.toJSONString(metadataRequest));
        RestBody<List<MetadataResponse>> response = new RestBody<>();
        List<MetadataResponse> list = metadataService.list(metadataRequest.getApplications());
        response.setData(list);
        return response;
    }

    @RequestMapping(value = "/application/list", method = RequestMethod.GET)
    public RestBody<List<ApplicationResponse>> applications(){
        RestBody<List<ApplicationResponse>> response = new RestBody<>();
        List<ApplicationResponse> applications = metadataService.getApplications();
        response.setData(applications);
        return response;
    }

    @RequestMapping(value = "/application/add", method = RequestMethod.POST)
    public RestBody addApplications(@RequestBody ApplicationRequest applicationRequest){
        metadataService.addApplication(applicationRequest);
        return new RestBody();
    }

    @RequestMapping(value = "/application/delete", method = RequestMethod.POST)
    public RestBody deleteApplications(@RequestBody ApplicationRequest applicationRequest){
        metadataService.deleteApplication(applicationRequest);
        return new RestBody();
    }

    @RequestMapping(value = "/application/update", method = RequestMethod.POST)
    public RestBody updateApplications(@RequestBody ApplicationRequest applicationRequest){
        metadataService.updateApplication(applicationRequest);
        return new RestBody();
    }

    @RequestMapping(value = "/group/list/{application}", method = RequestMethod.GET)
    public RestBody<List<GroupResponse>> groups(@PathVariable("application") String application){
        RestBody<List<GroupResponse>> response = new RestBody<>();
        List<GroupResponse> groups = metadataService.getGroups(application);
        response.setData(groups);
        return response;
    }

    @RequestMapping(value = "/group/add", method = RequestMethod.POST)
    public RestBody addGroups(@RequestBody GroupRequest groupRequest){
        metadataService.addGroup(groupRequest);
        return new RestBody();
    }

    @RequestMapping(value = "/instances/list/{application}/{group}", method = RequestMethod.GET)
    public RestBody<List<InstanceResponse>> instances(@PathVariable("application") String application, @PathVariable("group") String group){
        RestBody<List<InstanceResponse>> response = new RestBody<>();
        List<InstanceResponse> instances = metadataService.getInstances(application, group);
        response.setData(instances);
        return response;
    }

    @RequestMapping(value = "/instance/configs/list", method = RequestMethod.POST)
    public RestBody<List<InstanceConfigsResponse>> instanceConfigs(@RequestBody InstanceRequest instanceRequest){
        RestBody<List<InstanceConfigsResponse>> response = new RestBody<>();
        List<InstanceConfigsResponse> instanceConfig = metadataService.getInstanceConfig(instanceRequest.getApplication(), instanceRequest.getGroup(), instanceRequest.getInstance());
        response.setData(instanceConfig);
        return response;
    }

    @RequestMapping(value = "/group/configs/compare", method = RequestMethod.POST)
    public RestBody<CompareResponse> compareGroupConfigs(@RequestBody CompareRequest compareRequest){
        RestBody<CompareResponse> response = new RestBody<>();
        CompareResponse compareResponse = metadataService.compareGroupConfigs(compareRequest);
        response.setData(compareResponse);
        return response;
    }

    @RequestMapping(value = "/groups/instances/list/{application}", method = RequestMethod.GET)
    public RestBody<List<GroupInstanceResponse>> instances(@PathVariable("application") String application){
        RestBody<List<GroupInstanceResponse>> response = new RestBody<>();
        List<GroupInstanceResponse> groupInstances = metadataService.getGroupInstances(application);
        response.setData(groupInstances);
        return response;
    }

    @RequestMapping(value = "/groups/instances/switch", method = RequestMethod.POST)
    public RestBody switchGroupInstances(@RequestBody SwitchGroupInstancesRequest switchGroupInstancesRequest){
        metadataService.switchGroupInstances(switchGroupInstancesRequest);
        return new RestBody();
    }

}
