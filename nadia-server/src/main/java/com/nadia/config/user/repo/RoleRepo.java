package com.nadia.config.user.repo;

import com.nadia.config.user.dao.RoleMapper;
import com.nadia.config.user.domain.Role;
import com.nadia.config.user.domain.RoleCriteria;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class RoleRepo {

    @Resource
    private RoleMapper roleMapper;

    public List<Role> selectAll() {
        return roleMapper.selectByExample(null);
    }

    public Role selectByName(String role) {
        RoleCriteria example = new RoleCriteria();
        RoleCriteria.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(role);
        List<Role> roles = roleMapper.selectByExample(example);
        return CollectionUtils.isEmpty(roles) ? null : roles.get(0);
    }

    public List<Role> selectByRoleIds(List<Long> roleIds) {
        RoleCriteria example = new RoleCriteria();
        RoleCriteria.Criteria criteria = example.createCriteria();
        criteria.andIdIn(roleIds);
        return roleMapper.selectByExample(example);
    }

    public List<Role> selectWithOutRoleIds(List<Long> roleIds) {
        RoleCriteria example = new RoleCriteria();
        if(CollectionUtils.isNotEmpty(roleIds)){
            RoleCriteria.Criteria criteria = example.createCriteria();
            criteria.andIdNotIn(roleIds);
        }
        return roleMapper.selectByExample(example);
    }

    public Role selectByRoleId(Long roleId) {
        return roleMapper.selectByPrimaryKey(roleId);
    }

    public List<Role> selectByNames(List<String> names) {
        RoleCriteria example = new RoleCriteria();
        RoleCriteria.Criteria criteria = example.createCriteria();
        criteria.andNameIn(names);
        return roleMapper.selectByExample(example);
    }

    public boolean nameExists(String name) {
        RoleCriteria example = new RoleCriteria();
        RoleCriteria.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(name);
        return roleMapper.countByExample(example) > 0;
    }

    public void insertSelective(Role roleRecord) {
        roleMapper.insertSelective(roleRecord);
    }

    public void updateByPrimaryKey(Role role) {
        roleMapper.updateByPrimaryKey(role);
    }

    public void deleteByPrimaryKey(Long roleId){
        roleMapper.deleteByPrimaryKey(roleId);
    }

}
