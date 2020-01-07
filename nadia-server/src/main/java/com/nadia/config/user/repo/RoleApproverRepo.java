package com.nadia.config.user.repo;

import com.nadia.config.user.dao.RoleApproverMapper;
import com.nadia.config.user.domain.Role;
import com.nadia.config.user.domain.RoleApprover;
import com.nadia.config.user.domain.RoleApproverCriteria;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class RoleApproverRepo {

    @Resource
    private RoleApproverMapper roleApproverMapper;

    public List<Role> selectApproversByExample(List<Long> roleIds) {
        RoleApproverCriteria example = new RoleApproverCriteria();
        RoleApproverCriteria.Criteria criteria = example.createCriteria();
        criteria.andRoleIdIn(roleIds);
        return roleApproverMapper.selectApproversByExample(example);
    }

    public List<RoleApprover> selectByRoleId(Long roleId) {
        RoleApproverCriteria example = new RoleApproverCriteria();
        RoleApproverCriteria.Criteria criteria = example.createCriteria();
        criteria.andRoleIdEqualTo(roleId);
        return roleApproverMapper.selectByExample(example);
    }

    public List<RoleApprover> selectByRoleIds(List<Long> roleIds) {
        RoleApproverCriteria example = new RoleApproverCriteria();
        RoleApproverCriteria.Criteria criteria = example.createCriteria();
        criteria.andRoleIdIn(roleIds);
        return roleApproverMapper.selectByExample(example);
    }

    public List<RoleApprover> selectWithNameByRoleId(Long roleId) {
        return roleApproverMapper.selectWithNameByRoleId(roleId);
    }

    public void insertSelective(RoleApprover roleApprover) {
        roleApproverMapper.insertSelective(roleApprover);
    }

    public void deleteByRoleId(Long roleId) {
        RoleApproverCriteria roleApproverCriteria = new RoleApproverCriteria();
        roleApproverCriteria.createCriteria().andRoleIdEqualTo(roleId);
        roleApproverMapper.deleteByExample(roleApproverCriteria);
    }

    public List<RoleApprover> selectChildren(List<Long> roleIds){
        RoleApproverCriteria example = new RoleApproverCriteria();
        example.createCriteria().andApproverRoleIdIn(roleIds);
        return roleApproverMapper.selectByExample(example);
    }
}

