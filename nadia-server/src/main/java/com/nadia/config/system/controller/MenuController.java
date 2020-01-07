package com.nadia.config.system.controller;

import com.nadia.config.common.rest.RestBody;
import com.nadia.config.system.dto.request.MenuButtonRequest;
import com.nadia.config.system.dto.response.MenuButtonResponse;
import com.nadia.config.system.dto.response.MenuResponse;
import com.nadia.config.system.service.MenuButtonService;
import com.nadia.config.system.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-16 19:26
 */
@Slf4j
@RestController
@RequestMapping("/system")
public class MenuController {

    @Resource
    private MenuService menuService;
    @Resource
    private MenuButtonService menuButtonService;

    @RequestMapping(value = "/menu/button/{role}", method = RequestMethod.GET)
    public RestBody<List<MenuResponse>> roleMenus(@PathVariable("role") String role){
        RestBody<List<MenuResponse>> response = new RestBody<>();
//        List<MenuResponse> responses = menuService.roleMenus(role);
//        response.setData(responses);
        return response;
    }

    @RequestMapping(value = "/menu/button", method = RequestMethod.POST)
    public RestBody<MenuButtonResponse> roleMenuButton(@RequestBody MenuButtonRequest menuButtonRequest){
        RestBody<MenuButtonResponse> response = new RestBody<>();
        MenuButtonResponse result = menuButtonService.roleMeunButton(menuButtonRequest);
        response.setData(result);
        return response;
    }
}
