package com.nadia.config.user.dao;

import com.nadia.config.user.domain.RoleMenuButton;
import com.nadia.config.user.domain.RoleMenuButtonCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RoleMenuButtonMapper {
    long countByExample(RoleMenuButtonCriteria example);

    int deleteByExample(RoleMenuButtonCriteria example);

    int deleteByPrimaryKey(Long id);

    int deleteByRoleId(@Param("roleId")Long roleId);

    int insert(RoleMenuButton record);

    int insertSelective(RoleMenuButton record);

    List<RoleMenuButton> selectByExample(RoleMenuButtonCriteria example);

    List<RoleMenuButton> selectByRoleMenuIds(@Param("roleMenuIds") List<Long> roleMenuIds);

    RoleMenuButton selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") RoleMenuButton record, @Param("example") RoleMenuButtonCriteria example);

    int updateByExample(@Param("record") RoleMenuButton record, @Param("example") RoleMenuButtonCriteria example);

    int updateByPrimaryKeySelective(RoleMenuButton record);

    int updateByPrimaryKey(RoleMenuButton record);
}