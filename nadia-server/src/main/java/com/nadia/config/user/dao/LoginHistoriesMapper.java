package com.nadia.config.user.dao;

import com.nadia.config.user.domain.LoginHistories;
import com.nadia.config.user.domain.LoginHistoriesCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface LoginHistoriesMapper {
    long countByExample(LoginHistoriesCriteria example);

    int deleteByExample(LoginHistoriesCriteria example);

    int deleteByPrimaryKey(Long id);

    int insert(LoginHistories record);

    int insertSelective(LoginHistories record);

    List<LoginHistories> selectByExample(LoginHistoriesCriteria example);

    LoginHistories selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") LoginHistories record, @Param("example") LoginHistoriesCriteria example);

    int updateByExample(@Param("record") LoginHistories record, @Param("example") LoginHistoriesCriteria example);

    int updateByPrimaryKeySelective(LoginHistories record);

    int updateByPrimaryKey(LoginHistories record);
}