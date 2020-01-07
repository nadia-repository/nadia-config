package com.nadia.config.admin.service;

import com.nadia.config.admin.dto.response.CountsResponse;
import com.nadia.config.admin.dto.response.ErrorResponse;

import java.util.List;

public interface AdminService {
    void initRedisFromDB();

    void syncApplicationFromDB(String application);

    void syncGroupFromDb(String application,String group);

    void syncConfigFromDb(String application,String group);

    CountsResponse getCounts();

    List<ErrorResponse> getErrors();
}
