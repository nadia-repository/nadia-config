package com.nadia.config.user.dao;

import com.nadia.config.user.domain.RoleMenu;
import com.nadia.config.user.domain.RoleMenuCriteria;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RoleMenuMapper {
    long countByExample(RoleMenuCriteria example);

    int deleteByExample(RoleMenuCriteria example);

    int deleteByPrimaryKey(Long id);

    int deleteByRoleId(@Param("roleId")Long roleId);

    int insert(RoleMenu record);

    int insertSelective(RoleMenu record);

    List<RoleMenu> selectByExample(RoleMenuCriteria example);

    List<RoleMenu> selectWithNameByRoleIds(@Param("roleIds") List<Long> roleIds);

    List<RoleMenu> selectByRoleId(@Param("roleId")Long roleId);

    RoleMenu selectByPrimaryKey(Long id);

    RoleMenu selectByRoleIdAndMenuId(@Param("roleId")Long roleId,@Param("menuId")Long menuId);

    int updateByExampleSelective(@Param("record") RoleMenu record, @Param("example") RoleMenuCriteria example);

    int updateByExample(@Param("record") RoleMenu record, @Param("example") RoleMenuCriteria example);

    int updateByPrimaryKeySelective(RoleMenu record);

    int updateByPrimaryKey(RoleMenu record);
}