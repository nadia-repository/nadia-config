package com.nadia.config.user.repo;

import com.nadia.config.user.dao.RoleMenuMapper;
import com.nadia.config.user.domain.RoleMenu;
import com.nadia.config.user.domain.RoleMenuCriteria;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class RoleMenuRepo {

    @Resource
    private RoleMenuMapper roleMenuMapper;


    public List<RoleMenu> selectWithNameByRoleIds(List<Long> roleIds) {
        return roleMenuMapper.selectWithNameByRoleIds(roleIds);
    }

    public void insertSelective(RoleMenu roleMenu) {
        roleMenuMapper.insertSelective(roleMenu);
    }

    public RoleMenu selectByRoleIdAndMenuId(Long roleId, Long menuId) {
        RoleMenuCriteria example = new RoleMenuCriteria();
        RoleMenuCriteria.Criteria criteria = example.createCriteria();
        criteria.andRoleIdEqualTo(roleId);
        criteria.andMenuIdEqualTo(menuId);
        List<RoleMenu> roleMenus = roleMenuMapper.selectByExample(example);
        return CollectionUtils.isEmpty(roleMenus) ? null : roleMenus.get(0);
    }

    public void deleteByRoleId(Long roleId) {
        RoleMenuCriteria example = new RoleMenuCriteria();
        RoleMenuCriteria.Criteria criteria = example.createCriteria();
        criteria.andRoleIdEqualTo(roleId);
        roleMenuMapper.deleteByExample(example);
    }

    public List<RoleMenu> selectByRoleId(Long roleId) {
        RoleMenuCriteria example = new RoleMenuCriteria();
        RoleMenuCriteria.Criteria criteria = example.createCriteria();
        criteria.andRoleIdEqualTo(roleId);
        return roleMenuMapper.selectByExample(example);
    }
}
