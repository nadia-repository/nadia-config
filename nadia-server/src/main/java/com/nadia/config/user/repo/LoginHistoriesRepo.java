package com.nadia.config.user.repo;

import com.nadia.config.user.dao.LoginHistoriesMapper;
import com.nadia.config.user.domain.LoginHistories;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class LoginHistoriesRepo {

    @Resource
    private LoginHistoriesMapper loginHistoriesMapper;


    public void saveLoginHistory(Long userId, String action) {
        LoginHistories record = new LoginHistories();
        record.setAction(action);
        record.setUserId(userId);
        record.setCreatedBy(String.valueOf(userId));
        record.setUpdatedBy(String.valueOf(userId));
        loginHistoriesMapper.insertSelective(record);
    }
}
