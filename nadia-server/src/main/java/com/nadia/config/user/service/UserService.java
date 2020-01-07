package com.nadia.config.user.service;

import com.nadia.config.user.dto.request.LoginRequest;
import com.nadia.config.user.dto.request.SignupRequest;
import com.nadia.config.user.dto.request.UserRequest;
import com.nadia.config.user.dto.response.LoginResponse;
import com.nadia.config.user.dto.response.UserResponse;
import com.nadia.config.user.dto.response.UserStatusResponse;
import com.nadia.config.user.dto.response.UsersResponse;

import java.util.List;

public interface UserService {
    LoginResponse login(LoginRequest loginRequest);

    void signup(SignupRequest signupRequest);

    void logout();

    String afterLogin(long userId);

    UserResponse getUserInfo(long userId);

    List<UsersResponse> list(UserRequest userRequest);

    List<UserStatusResponse> statusList();

    void refreshToken();

    void delete(String userName);

    void activate(String userName);
}
