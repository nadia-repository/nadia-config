package com.nadia.config.user.repo;

import com.nadia.config.user.dao.RoleMenuButtonMapper;
import com.nadia.config.user.domain.RoleMenuButton;
import com.nadia.config.user.domain.RoleMenuButtonCriteria;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class RoleMenuButtonRepo {

    @Resource
    private RoleMenuButtonMapper roleMenuButtonMapper;


    public List<RoleMenuButton> selectByRoleMenuIds(List<Long> roleMenuIds) {
        return roleMenuButtonMapper.selectByRoleMenuIds(roleMenuIds);
    }

    public void insertSelective(RoleMenuButton roleMenuButtonRecord) {
        roleMenuButtonMapper.insertSelective(roleMenuButtonRecord);
    }

    public void deleteByRoleId(Long roleId) {
        roleMenuButtonMapper.deleteByRoleId(roleId);
    }

    public List<RoleMenuButton> selectByRoleMenuId(Long roleMenuId) {
        RoleMenuButtonCriteria example = new RoleMenuButtonCriteria();
        example.createCriteria().andRoleMenuIdEqualTo(roleMenuId);
        return roleMenuButtonMapper.selectByExample(example);
    }
}
