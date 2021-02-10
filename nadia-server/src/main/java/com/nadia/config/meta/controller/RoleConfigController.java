package com.nadia.config.meta.controller;

import com.nadia.config.common.rest.RestBody;
import com.nadia.config.meta.dto.request.RoleConfigRequest;
import com.nadia.config.meta.dto.response.GroupResponse;
import com.nadia.config.meta.dto.response.RoleConfigResponse;
import com.nadia.config.meta.dto.response.RoleConfigsResponse;
import com.nadia.config.meta.service.RoleConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-10 11:27
 */
@Slf4j
@RestController
@RequestMapping("/role/config")
public class RoleConfigController {

    @Resource
    private RoleConfigService roleConfigService;

    @RequestMapping(value = "/list/{role}", method = RequestMethod.GET)
    public RestBody<List<RoleConfigResponse>> list(@PathVariable("role") String role){
        RestBody<List<RoleConfigResponse>> response = new RestBody<>();
        List<RoleConfigResponse> list = roleConfigService.list(role);
        response.setData(list);
        return response;
    }

    @RequestMapping(value = "/configs/{role}", method = RequestMethod.GET)
    public RestBody<RoleConfigsResponse> groups(@PathVariable("role") String role){
        RestBody<RoleConfigsResponse> response = new RestBody<>();
        RoleConfigsResponse configs = roleConfigService.configs(role);
        response.setData(configs);
        return response;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public RestBody update(@RequestBody RoleConfigRequest roleConfigRequest){
        RestBody response = new RestBody<>();
        roleConfigService.update(roleConfigRequest);
        return response;
    }
}
