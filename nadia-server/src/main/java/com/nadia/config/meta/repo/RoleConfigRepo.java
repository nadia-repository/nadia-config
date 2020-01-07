package com.nadia.config.meta.repo;

import com.nadia.config.meta.dao.RoleConfigMapper;
import com.nadia.config.meta.domain.RoleConfig;
import com.nadia.config.meta.domain.RoleConfigCriteria;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class RoleConfigRepo {

    @Resource
    private RoleConfigMapper roleConfigMapper;

    public List<RoleConfig> selectByRoleId(Long roleId) {
        RoleConfigCriteria example = new RoleConfigCriteria();
        RoleConfigCriteria.Criteria criteria = example.createCriteria();
        criteria.andRoleIdEqualTo(roleId);
        return roleConfigMapper.selectByExample(example);
    }

    public void deleteByConfigIdAndRoleId(Long configId, Long roleId) {
        RoleConfigCriteria example = new RoleConfigCriteria();
        RoleConfigCriteria.Criteria criteria = example.createCriteria();
        criteria.andConfigIdEqualTo(configId);
        criteria.andRoleIdEqualTo(roleId);
        roleConfigMapper.deleteByExample(example);
    }

    public void insertSelective(RoleConfig record) {
        roleConfigMapper.insertSelective(record);
    }
}
