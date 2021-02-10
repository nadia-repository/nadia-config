package com.nadia.config.user.dao;

import com.nadia.config.user.domain.Role;
import com.nadia.config.user.domain.RoleApprover;
import com.nadia.config.user.domain.RoleApproverCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RoleApproverMapper {
    long countByExample(RoleApproverCriteria example);

    int deleteByExample(RoleApproverCriteria example);

    int deleteByPrimaryKey(Long id);

    int insert(RoleApprover record);

    int insertSelective(RoleApprover record);

    List<Role> selectApproversByExample(RoleApproverCriteria example);

    List<RoleApprover> selectByExample(RoleApproverCriteria example);

    List<RoleApprover> selectWithNameByRoleId(@Param("roleId") Long roleId);

    RoleApprover selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") RoleApprover record, @Param("example") RoleApproverCriteria example);

    int updateByExample(@Param("record") RoleApprover record, @Param("example") RoleApproverCriteria example);

    int updateByPrimaryKeySelective(RoleApprover record);

    int updateByPrimaryKey(RoleApprover record);
}