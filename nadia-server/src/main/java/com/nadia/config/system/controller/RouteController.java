package com.nadia.config.system.controller;

import com.nadia.config.common.rest.RestBody;
import com.nadia.config.system.dto.response.RouteResponse;
import com.nadia.config.system.service.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-18 14:21
 */
@Slf4j
@RestController
@RequestMapping("/system")
public class RouteController {

    @Resource
    private RouteService routeService;

    @RequestMapping(value = "/route/list", method = RequestMethod.GET)
    public RestBody<List<RouteResponse>> routes(){
        RestBody<List<RouteResponse>> response = new RestBody<>();
        List<RouteResponse> routes = routeService.routes();
        response.setData(routes);
        return response;
    }

    @RequestMapping(value = "/route/{role}", method = RequestMethod.GET)
    public RestBody<List<String>> roleRoutes(@PathVariable("role")String role){
        RestBody<List<String>> response = new RestBody<>();
        List<String> keys = routeService.roleRoutes(role);
        response.setData(keys);
        return response;
    }
}
