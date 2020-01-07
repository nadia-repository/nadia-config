package com.nadia.config.user.controller;

import com.alibaba.fastjson.JSON;
import com.nadia.config.user.dto.response.LoginResponse;
import com.nadia.config.user.dto.response.UserResponse;
import com.nadia.config.user.dto.response.UserStatusResponse;
import com.nadia.config.user.dto.response.UsersResponse;
import com.nadia.config.user.service.UserService;
import com.nadia.config.common.rest.RestBody;
import com.nadia.config.common.context.UserContextHolder;
import com.nadia.config.common.context.UserDetail;
import com.nadia.config.user.dto.request.LoginRequest;
import com.nadia.config.user.dto.request.SignupRequest;
import com.nadia.config.user.dto.request.UserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public RestBody<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        log.info("login loginRequest:{}", JSON.toJSONString(loginRequest));
        RestBody<LoginResponse> response = new RestBody<>();
        LoginResponse login = userService.login(loginRequest);
        response.setData(login);
        return response;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public RestBody signup(@RequestBody SignupRequest signupRequest){
        log.info("signup signupRequest:{}", JSON.toJSONString(signupRequest));
        userService.signup(signupRequest);
        return new RestBody();
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public RestBody logout(){
        userService.logout();
        return new RestBody();
    }

    @RequestMapping(value = "/info", method = RequestMethod.POST)
    public RestBody<UserResponse> info(){
        RestBody<UserResponse> response = new RestBody<>();
        UserDetail userDetail = UserContextHolder.getUserDetail();
        if(userDetail != null){
            UserResponse userInfo = userService.getUserInfo(userDetail.getUid());
            response.setData(userInfo);
        }
        return response;
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public RestBody<List<UsersResponse>> list(@RequestBody UserRequest userRequest){
        RestBody<List<UsersResponse>> response = new RestBody<>();
        List<UsersResponse> list = userService.list(userRequest);
        response.setData(list);
        return response;
    }

    @RequestMapping(value = "/status/list", method = RequestMethod.GET)
    public RestBody<List<UserStatusResponse>> status(){
        RestBody<List<UserStatusResponse>> response = new RestBody<>();
        List<UserStatusResponse> list = userService.statusList();
        response.setData(list);
        return response;
    }

    @RequestMapping(value = "/delete/{userName}", method = RequestMethod.GET)
    public RestBody delete(@PathVariable("userName")String userName){
        userService.delete(userName);
        return new RestBody();
    }

    @RequestMapping(value = "/activate/{userName}", method = RequestMethod.GET)
    public RestBody activate(@PathVariable("userName")String userName){
        userService.activate(userName);
        return new RestBody();
    }
}
