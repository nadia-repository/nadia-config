package com.nadia.config.user.service.impl;

import com.nadia.config.user.domain.*;
import com.nadia.config.user.enums.UserStatusEnum;
import com.nadia.config.user.repo.*;
import com.nadia.config.common.context.UserContextHolder;
import com.nadia.config.common.context.UserDetail;
import com.nadia.config.notification.enums.OperationType;
import com.nadia.config.notification.enums.View;
import com.nadia.config.notification.enums.TargetType;
import com.nadia.config.notification.service.OperationLogService;
import com.nadia.config.system.dto.response.RouteResponse;
import com.nadia.config.user.dto.request.RoleRequest;
import com.nadia.config.user.dto.request.UserRoleRequest;
import com.nadia.config.user.dto.response.ApproverResponse;
import com.nadia.config.user.dto.response.RoleResponse;
import com.nadia.config.user.exception.RoleException;
import com.nadia.config.user.service.RoleService;
import com.nadia.config.user.service.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * @author xiang.shi
 * @date 2019-12-09 16:36
 */
@Service
public class RoleServiceImpl implements RoleService {
    @Resource
    private RoleRepo roleRepo;
    @Resource
    private UserRoleRepo userRoleRepo;
    @Resource
    private UserService userService;
    @Resource
    private UserRepo userRepo;
    @Resource
    private RoleApproverRepo roleApproverRepo;
    @Resource
    private RoleMenuRepo roleMenuRepo;
    @Resource
    private RoleMenuButtonRepo roleMenuButtonRepo;
    @Resource
    private OperationLogService operationLogService;
    @Resource
    private TaskExecutor taskExecutor;

    @Override
    public List<RoleResponse> list() {
        List<Role> roles = roleRepo.selectAll();
        List<RoleResponse> response = roles.stream().map(role -> {
            RoleResponse roleResponse = new RoleResponse();
            roleResponse.setId(role.getId());
            roleResponse.setName(role.getName());
            roleResponse.setDescription(role.getDescription());
            //approverId
            List<RoleApprover> roleApprovers = roleApproverRepo.selectByRoleId(role.getId());
            if (CollectionUtils.isNotEmpty(roleApprovers)) {
                List<Long> approverIds = roleApprovers.stream().map(roleApprover -> roleApprover.getApproverRoleId()).collect(Collectors.toList());
                List<Role> approveRoleIds = roleRepo.selectByRoleIds(approverIds);
                roleResponse.setApprovers(approveRoleIds.stream().map(approve -> approve.getName()).collect(Collectors.joining("|")));
            }
            return roleResponse;
        }).collect(Collectors.toList());
        return response;
    }

    @Override
    public List<RoleResponse> listByIds(List<Long> ids) {
        List<RoleResponse> response = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ids)) {
            List<Role> roles = roleRepo.selectByRoleIds(ids);
            response = roles.stream().map(role -> {
                RoleResponse roleResponse = new RoleResponse();
                roleResponse.setName(role.getName());
                return roleResponse;
            }).collect(Collectors.toList());
        }
        return response;
    }

    @Override
    public List<RoleResponse> listByUserName(String userName) {
        User userInfo = userRepo.selectByName(userName);
        List<UserRole> userRoles = userRoleRepo.selectByUserId(userInfo.getId());
        List<Long> roleIds = userRoles.stream().map(userRole -> userRole.getRoleId()).collect(Collectors.toList());
        return this.listByIds(roleIds);
    }

    @Override
    public void updateUserRole(UserRoleRequest userRoleRequest) {
        UserDetail userDetail = UserContextHolder.getUserDetail();
        //delete all user roles
        User userInfo = userRepo.selectByName(userRoleRequest.getUserName());
        userRoleRepo.deleteByUserId(userInfo.getId());
        //add new
        List<String> roles = userRoleRequest.getRoles();

        if (CollectionUtils.isNotEmpty(roles)) {
            List<Role> rolesList = roleRepo.selectByNames(roles);
            List<Long> roleIds = rolesList.stream().map(role -> role.getId()).collect(Collectors.toList());
            UserRole record = new UserRole();
            record.setUserId(userInfo.getId());
            record.setCreatedBy(userDetail.getName());
            record.setUpdatedBy(userDetail.getName());
            roleIds.forEach(role -> {
                record.setRoleId(role);
                userRoleRepo.insertSelective(record);
            });
            //login user == modify user --> refresh token roles
            if (userInfo.getId().equals(userDetail.getUid())) {
                userDetail.setRoleIds(roleIds);
                UserContextHolder.setUserDetail(userDetail);
                userService.refreshToken();
            }
            userInfo.setStatus(UserStatusEnum.PUBLISHED.getStatus());
            userRepo.updateByPrimaryKeySelective(userInfo);
        } else {
            userInfo.setStatus(UserStatusEnum.SUBMITTED.getStatus());
            userRepo.updateByPrimaryKeySelective(userInfo);
        }
    }

    @Transactional
    @Override
    public void add(RoleRequest roleRequest) {
        if (roleRepo.nameExists(roleRequest.getName())) {
            throw new RoleException(2005L);
        }

        UserDetail userDetail = UserContextHolder.getUserDetail();
        Role roleRecord = new Role();
        roleRecord.setDescription(roleRequest.getDescription());
        roleRecord.setName(roleRequest.getName());
        roleRecord.setCreatedBy(userDetail.getName());
        roleRecord.setUpdatedBy(userDetail.getName());
        roleRepo.insertSelective(roleRecord);

        if (CollectionUtils.isNotEmpty(roleRequest.getApprovers())) {
            List<Role> roles = roleRepo.selectByNames(roleRequest.getApprovers());

            RoleApprover roleApprover = new RoleApprover();
            roleApprover.setRoleId(roleRecord.getId());
            roleApprover.setCreatedBy(userDetail.getName());
            roleApprover.setUpdatedBy(userDetail.getName());
            roles.forEach(role -> {
                roleApprover.setApproverRoleId(role.getId());
                roleApproverRepo.insertSelective(roleApprover);
            });
        }

        //add menu button
        List<RouteResponse> routes = roleRequest.getRoutes();
        if (CollectionUtils.isNotEmpty(routes)) {
            routes.forEach(route -> {
                if ("menu".equals(route.getType())) {
                    RoleMenu roleMenuRecord = new RoleMenu();
                    roleMenuRecord.setRoleId(roleRecord.getId());
                    roleMenuRecord.setMenuId(route.getMenuId());
                    roleMenuRecord.setCreatedBy(userDetail.getName());
                    roleMenuRecord.setUpdatedBy(userDetail.getName());
                    roleMenuRepo.insertSelective(roleMenuRecord);
                } else if ("button".equals(route.getType())) {
                    RoleMenu roleMenu = roleMenuRepo.selectByRoleIdAndMenuId(roleRecord.getId(), route.getMenuId());
                    RoleMenuButton roleMenuButtonRecord = new RoleMenuButton();
                    roleMenuButtonRecord.setRoleMenuId(roleMenu.getId());
                    roleMenuButtonRecord.setButtonId(route.getButtonId());
                    roleMenuButtonRecord.setRoleId(roleRecord.getId());
                    roleMenuButtonRecord.setCreatedBy(userDetail.getName());
                    roleMenuButtonRecord.setUpdatedBy(userDetail.getName());
                    roleMenuButtonRepo.insertSelective(roleMenuButtonRecord);
                }
            });
        }
        operationLogService.log(View.ROLE, OperationType.ADD, TargetType.ROLE,null,roleRecord);
    }

    @Transactional
    @Override
    public void update(RoleRequest roleRequest) {
        Role roleInfo = roleRepo.selectByName(roleRequest.getName());

        roleInfo.setDescription(roleRequest.getDescription());
        roleRepo.updateByPrimaryKey(roleInfo);

        roleApproverRepo.deleteByRoleId(roleInfo.getId());

        UserDetail userDetail = UserContextHolder.getUserDetail();
        RoleCriteria roleCriteria = new RoleCriteria();
        if (CollectionUtils.isNotEmpty(roleRequest.getApprovers())) {
            List<Role> roles = roleRepo.selectByNames(roleRequest.getApprovers());

            RoleApprover roleApprover = new RoleApprover();
            roleApprover.setRoleId(roleInfo.getId());
            roleApprover.setCreatedBy(userDetail.getName());
            roleApprover.setUpdatedBy(userDetail.getName());
            roles.forEach(role -> {
                roleApprover.setApproverRoleId(role.getId());
                roleApproverRepo.insertSelective(roleApprover);
            });
        }

        //add menu button
        roleMenuRepo.deleteByRoleId(roleInfo.getId());
        roleMenuButtonRepo.deleteByRoleId(roleInfo.getId());
        //add menu button
        List<RouteResponse> routes = roleRequest.getRoutes();
        if (CollectionUtils.isNotEmpty(routes)) {
            routes.forEach(route -> {
                if ("menu".equals(route.getType())) {
                    RoleMenu roleMenuRecord = new RoleMenu();
                    roleMenuRecord.setRoleId(roleInfo.getId());
                    roleMenuRecord.setMenuId(route.getMenuId());
                    roleMenuRecord.setCreatedBy(userDetail.getName());
                    roleMenuRecord.setUpdatedBy(userDetail.getName());
                    roleMenuRepo.insertSelective(roleMenuRecord);
                } else if ("button".equals(route.getType())) {
                    RoleMenu roleMenu = roleMenuRepo.selectByRoleIdAndMenuId(roleInfo.getId(), route.getMenuId());
                    RoleMenuButton roleMenuButtonRecord = new RoleMenuButton();
                    roleMenuButtonRecord.setRoleMenuId(roleMenu.getId());
                    roleMenuButtonRecord.setRoleId(roleInfo.getId());
                    roleMenuButtonRecord.setButtonId(route.getButtonId());
                    roleMenuButtonRecord.setCreatedBy(userDetail.getName());
                    roleMenuButtonRecord.setUpdatedBy(userDetail.getName());
                    roleMenuButtonRepo.insertSelective(roleMenuButtonRecord);
                }
            });
        }
        operationLogService.log(View.ROLE, OperationType.MODIFY, TargetType.ROLE,null,roleInfo);
    }

    @Override
    public void delete(RoleRequest roleRequest) {
        Role role = roleRepo.selectByName(roleRequest.getName());
        long cnt = userRoleRepo.countByRoleId(role.getId());
        if(cnt == 0){
            roleRepo.deleteByPrimaryKey(role.getId());
            operationLogService.log(View.ROLE, OperationType.DELETE, TargetType.ROLE,role,"");
        }else {
            throw new RoleException(2006L);
        }
    }

    @Override
    public ApproverResponse approver(String roleName) {
        ApproverResponse response = new ApproverResponse();
        List<Long> childirn = new ArrayList<>();
        if (!StringUtils.isEmpty(roleName)) {
            Role role = roleRepo.selectByName(roleName);

            List<RoleApprover> roleApprovers = roleApproverRepo.selectWithNameByRoleId(role.getId());
            List<String> approverNames = roleApprovers.stream().map(roleApprover -> roleApprover.getName()).collect(Collectors.toList());
            response.setApprovers(approverNames);


            ForkJoinPool forkJoinPool = new ForkJoinPool();
            childirn = forkJoinPool.invoke(new CalculatorApprover(role.getId(), null, roleApproverRepo));
        }


        List<Role> roles = roleRepo.selectWithOutRoleIds(childirn);
        List<ApproverResponse.Approver> approverOptions = roles.stream().map(r -> {
            ApproverResponse.Approver approver = new ApproverResponse().new Approver();
            approver.setId(r.getId());
            approver.setName(r.getName());
            return approver;
        }).collect(Collectors.toList());
        response.setApproverOptions(approverOptions);

        return response;
    }


    private void test(){

    }

    public static void main(String[] args) {
        System.out.println(UserStatusEnum.PUBLISHED.getStatus());
    }
}
