package com.nadia.config.user.repo;

import com.nadia.config.user.dao.UserRoleMapper;
import com.nadia.config.user.domain.UserRole;
import com.nadia.config.user.domain.UserRoleCriteria;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class UserRoleRepo {

    @Resource
    private UserRoleMapper userRoleMapper;


    public List<UserRole> selectByUserId(Long userId) {
        UserRoleCriteria example = new UserRoleCriteria();
        UserRoleCriteria.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        return userRoleMapper.selectByExample(example);
    }

    public void deleteByUserId(Long userId) {
        UserRoleCriteria example = new UserRoleCriteria();
        UserRoleCriteria.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        userRoleMapper.deleteByExample(example);
    }

    public void insertSelective(UserRole record) {
        userRoleMapper.insertSelective(record);
    }

    public long countByRoleId(Long roleId){
        UserRoleCriteria example = new UserRoleCriteria();
        example.createCriteria().andRoleIdEqualTo(roleId);
        return userRoleMapper.countByExample(example);
    }
}
