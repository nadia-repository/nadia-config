package com.nadia.config.user.controller;

import com.nadia.config.user.exception.RoleException;
import com.nadia.config.user.service.RoleService;
import com.nadia.config.common.context.UserContextHolder;
import com.nadia.config.common.context.UserDetail;
import com.nadia.config.common.rest.RestBody;
import com.nadia.config.common.security.GlobalLock;
import com.nadia.config.user.dto.request.ApproverRequest;
import com.nadia.config.user.dto.request.RoleRequest;
import com.nadia.config.user.dto.request.UserRoleRequest;
import com.nadia.config.user.dto.response.ApproverResponse;
import com.nadia.config.user.dto.response.RoleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-09 16:33
 */
@Slf4j
@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public RestBody<List<RoleResponse>> list(){
        RestBody<List<RoleResponse>> response = new RestBody<>();
        List<RoleResponse> list = roleService.list();
        response.setData(list);
        return response;
    }

    @RequestMapping(value = "/user/role/list", method = RequestMethod.GET)
    public RestBody<List<RoleResponse>> userRole(){
        RestBody<List<RoleResponse>> response = new RestBody<>();
        UserDetail userDetail = UserContextHolder.getUserDetail();
        if(userDetail != null){
            List<RoleResponse> list = roleService.listByIds(userDetail.getRoleIds());
            response.setData(list);
            return response;
        }else {
            throw new RoleException(401L);
        }
    }

    @RequestMapping(value = "/user/role/list/{userName}", method = RequestMethod.GET)
    public RestBody<List<RoleResponse>> userRoleByUserName(@PathVariable("userName")String userName){
        RestBody<List<RoleResponse>> response = new RestBody<>();
        UserDetail userDetail = UserContextHolder.getUserDetail();
        if(userDetail != null){
            List<RoleResponse> list = roleService.listByUserName(userName);
            response.setData(list);
            return response;
        }else {
            throw new RoleException(401L);
        }
    }

    @GlobalLock(key = "updateUserRole")
    @RequestMapping(value = "/user/role/update", method = RequestMethod.POST)
    public RestBody updateUserRole(@RequestBody UserRoleRequest userRoleRequest){
        UserDetail userDetail = UserContextHolder.getUserDetail();
        if(userDetail != null){
            roleService.updateUserRole(userRoleRequest);
            return new RestBody();
        }else {
            throw new RoleException(401L);
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public RestBody add(@RequestBody RoleRequest roleRequest){
        UserDetail userDetail = UserContextHolder.getUserDetail();
        if(userDetail != null){
            roleService.add(roleRequest);
            return new RestBody();
        }else {
            throw new RoleException(401L);
        }
    }

    @GlobalLock(key = "updateRole")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public RestBody update(@RequestBody RoleRequest roleRequest){
        UserDetail userDetail = UserContextHolder.getUserDetail();
        if(userDetail != null){
            roleService.update(roleRequest);
            return new RestBody();
        }else {
            throw new RoleException(401L);
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public RestBody delete(@RequestBody RoleRequest roleRequest){
        UserDetail userDetail = UserContextHolder.getUserDetail();
        if(userDetail != null){
            roleService.delete(roleRequest);
            return new RestBody();
        }else {
            throw new RoleException(401L);
        }
    }

    @RequestMapping(value = "/approver", method = RequestMethod.POST)
    public RestBody<ApproverResponse> approvers(@RequestBody ApproverRequest approverRequest){
        RestBody<ApproverResponse> response = new RestBody<>();
        ApproverResponse approver = roleService.approver(approverRequest.getRoleName());
        response.setData(approver);
        return response;
    }
}
