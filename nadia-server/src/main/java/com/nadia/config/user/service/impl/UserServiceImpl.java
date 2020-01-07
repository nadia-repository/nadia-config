package com.nadia.config.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.nadia.config.user.domain.User;
import com.nadia.config.user.domain.UserRole;
import com.nadia.config.user.dto.response.*;
import com.nadia.config.user.enums.UserStatusEnum;
import com.nadia.config.user.repo.UserRepo;
import com.nadia.config.user.service.RoleService;
import com.nadia.config.user.service.UserService;
import com.nadia.config.common.context.UserContextHolder;
import com.nadia.config.common.context.UserDetail;
import com.nadia.config.notification.enums.OperationType;
import com.nadia.config.notification.enums.TargetType;
import com.nadia.config.notification.enums.View;
import com.nadia.config.notification.service.OperationLogService;
import com.nadia.config.redis.RedisService;
import com.nadia.config.user.dto.request.LoginRequest;
import com.nadia.config.user.dto.request.SignupRequest;
import com.nadia.config.user.dto.request.UserRequest;
import com.nadia.config.user.exception.AccountException;
import com.nadia.config.user.repo.LoginHistoriesRepo;
import com.nadia.config.user.repo.UserRoleRepo;
import com.nadia.config.utils.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private RedisService redisService;
    @Resource
    private UserRepo userRepo;
    @Resource
    private LoginHistoriesRepo loginHistoriesRepo;
    @Resource
    private UserRoleRepo userRoleRepo;
    @Resource
    private RoleService roleService;
    @Resource
    private OperationLogService operationLogService;

    private static final long timeOut = 30000;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        if (getLeftFailTimes(loginRequest.getUsername()) == 0) {
            throw new AccountException(1005L);
        }

        User user = userRepo.selectByNameOrMail(loginRequest.getUsername());
        if (user == null || !loginRequest.getPassword().equals(user.getPassword())) { //todo
//            if (user == null || !BCrypt.checkpw(loginRequest.getPassword(), user.getPassword())) {
            processLoginFailTimes(loginRequest.getUsername());
        }

        LoginResponse response = new LoginResponse();
        BeanUtils.copyProperties(user, response);

        String token = afterLogin(user.getId());
        response.setToken(token);

        loginHistoriesRepo.saveLoginHistory(user.getId(), "login");
        log.info("login response:{}", response);
        return response;
    }

    private int getLeftFailTimes(String name) {
        String key = name + "_login_failed";

        if (redisService.exists(key)) {
            int failTimes = Integer.parseInt(redisService.get(key));
            if (failTimes <= 5) {
                return 5 - failTimes;
            }
            return 0;
        }
        return 5;
    }

    private User processLoginFailTimes(String name) {
        processFailTimes(name);
        int leftFailTimes = getLeftFailTimes(name);
        Object[] args = new Object[1];
        args[0] = leftFailTimes;
        throw new AccountException(1006L, args);
    }

    private void processFailTimes(String name) {
        String key = name + "_login_failed";
        if (redisService.exists(key)) {
            redisService.incr(key);
            int failTimes = Integer.parseInt(redisService.get(key));
            if (failTimes >= 5) {
                throw new AccountException(1005L);
            }
        } else {
            redisService.set(key, "1", 3 * 60 * 60);
        }
    }

    @Override
    public void signup(SignupRequest signupRequest) {
        long nameExist = userRepo.countByName(signupRequest.getUsername());
        if(nameExist>0){
            throw new AccountException(1001L);
        }
        long mailExist = userRepo.countByMail(signupRequest.getMail());
        if(mailExist>0){
            throw new AccountException(1003L);
        }

        User record = new User();
        record.setStatus(UserStatusEnum.SUBMITTED.getStatus());
        record.setName(signupRequest.getUsername());
        record.setEmail(signupRequest.getMail());
        record.setPassword(signupRequest.getPassword());//todo
        record.setCreatedBy(signupRequest.getUsername());
        record.setUpdatedBy(signupRequest.getUsername());
        int insert = userRepo.insertSelective(record);
        if(insert == 0){
            throw new AccountException(1004L);

        }
    }

    @Override
    public void logout() {
        String token = UserContextHolder.getUserDetail().getToken();
        redisService.del(token);
    }

    @Override
    public String afterLogin(long userId) {
        String token = createToken(userId);
        String detail = redisService.get(RedisKeyUtil.getToken(token));
        UserDetail userDetail = JSONObject.parseObject(detail, UserDetail.class);
        UserContextHolder.setUserDetail(userDetail);
        return token;
    }

    public String createToken(long userId) {
        String token = UUID.randomUUID().toString();
        redisService.delAll(RedisKeyUtil.getToken(token));
        List<UserRole> userRoles = userRoleRepo.selectByUserId(userId);
        List<Long> roleIds = userRoles.stream().map(userRole -> userRole.getRoleId()).collect(Collectors.toList());
        UserDetail userDetail = new UserDetail();
        userDetail.setRoleIds(roleIds);
        userDetail.setUid(userId);
        User user = userRepo.selectByPrimaryKey(userId);
        userDetail.setName(user.getName());
        userDetail.setEmail(user.getEmail());
        redisService.set(RedisKeyUtil.getToken(token), JSONObject.toJSONString(userDetail), timeOut);
        return token;
    }

    @Override
    public void refreshToken() {
        UserDetail userDetail = UserContextHolder.getUserDetail();
        String token = userDetail.getToken();
        redisService.delAll(RedisKeyUtil.getToken(token));
        redisService.set(RedisKeyUtil.getToken(token), JSONObject.toJSONString(userDetail), timeOut);
    }

    @Override
    public UserResponse getUserInfo(long userId) {
        User user = userRepo.selectByPrimaryKey(userId);
        UserResponse response = new UserResponse();
        response.setName(user.getName());

        List<UserRole> userRoles = userRoleRepo.selectByUserId(userId);
        List<RoleResponse> roleResponses = roleService.listByIds(userRoles.stream().map(userRole -> userRole.getRoleId()).collect(Collectors.toList()));
        response.setRoles(roleResponses.stream().map(role -> role.getName()).collect(Collectors.toList()));
        return response;
    }

    @Override
    public List<UsersResponse> list(UserRequest userRequest) {
        List<User> users = userRepo.selectByUserRequest(userRequest);
        List<UsersResponse> responses = new LinkedList<>();

        users.forEach(user -> {
            UsersResponse response = new UsersResponse();
            response.setMail(user.getEmail());
            response.setName(user.getName());
            response.setStatus(user.getStatus());
            response.setUpdatedAt(user.getUpdatedAt());
            response.setUpdatedBy(user.getUpdatedBy());
            List<RoleResponse> roleResponses = roleService.listByUserName(user.getName());
            String roles = roleResponses.stream().map(roleResponse -> roleResponse.getName()).collect(Collectors.joining("|"));
            response.setRoles(roles);
            responses.add(response);
        });
        return responses;
    }

    @Override
    public List<UserStatusResponse> statusList() {
        List<UserStatusResponse> responses = new LinkedList<>();
        for (UserStatusEnum value : UserStatusEnum.values()) {
            UserStatusResponse response = new UserStatusResponse();
            response.setName(value.getStatus());
            responses.add(response);
        }
        return responses;
    }

    @Override
    public void delete(String userName) {
        //todo 如果还有任务，不能删除

        User user = userRepo.selectByName(userName);
        user.setStatus(UserStatusEnum.DELETED.getStatus());
        userRepo.updateByPrimaryKeySelective(user);
        userRoleRepo.deleteByUserId(user.getId());
        operationLogService.log(View.USERS, OperationType.DELETE, TargetType.USER,user,null);
    }

    @Override
    public void activate(String userName) {
        User user = userRepo.selectByName(userName);
        User beforeUser = new User();
        BeanUtils.copyProperties(user,beforeUser);
        user.setStatus(UserStatusEnum.SUBMITTED.getStatus());
        userRepo.updateByPrimaryKeySelective(user);
        operationLogService.log(View.USERS, OperationType.ACTIVATE, TargetType.USER,beforeUser,user);
    }
}
