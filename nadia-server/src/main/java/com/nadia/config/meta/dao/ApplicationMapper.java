package com.nadia.config.meta.dao;

import java.util.List;

import com.nadia.config.meta.domain.Application;
import com.nadia.config.meta.domain.ApplicationCriteria;
import org.apache.ibatis.annotations.Param;

public interface ApplicationMapper {
    long countByExample(ApplicationCriteria example);

    int deleteByExample(ApplicationCriteria example);

    int deleteByPrimaryKey(Long id);

    int insert(Application record);

    int insertSelective(Application record);

    List<Application> selectByExample(ApplicationCriteria example);

    Application selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Application record, @Param("example") ApplicationCriteria example);

    int updateByExample(@Param("record") Application record, @Param("example") ApplicationCriteria example);

    int updateByPrimaryKeySelective(Application record);

    int updateByPrimaryKey(Application record);
}