package com.nadia.config.meta.dao;


import java.util.List;

import com.nadia.config.meta.domain.RoleConfig;
import com.nadia.config.meta.domain.RoleConfigCriteria;
import org.apache.ibatis.annotations.Param;

public interface RoleConfigMapper {
    long countByExample(RoleConfigCriteria example);

    int deleteByExample(RoleConfigCriteria example);

    int deleteByPrimaryKey(Long id);

    int insert(RoleConfig record);

    int insertSelective(RoleConfig record);

    List<RoleConfig> selectByExample(RoleConfigCriteria example);

    RoleConfig selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") RoleConfig record, @Param("example") RoleConfigCriteria example);

    int updateByExample(@Param("record") RoleConfig record, @Param("example") RoleConfigCriteria example);

    int updateByPrimaryKeySelective(RoleConfig record);

    int updateByPrimaryKey(RoleConfig record);
}