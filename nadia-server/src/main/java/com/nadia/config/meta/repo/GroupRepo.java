package com.nadia.config.meta.repo;

import com.nadia.config.meta.dao.GroupMapper;
import com.nadia.config.meta.dto.request.GroupRequest;
import com.nadia.config.meta.domain.Group;
import com.nadia.config.meta.domain.GroupCriteria;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class GroupRepo {

    @Resource
    private GroupMapper groupMapper;

    public Group selectByPrimaryKey(Long id) {
        return groupMapper.selectByPrimaryKey(id);
    }

    public Group selectByName(String name) {
        return groupMapper.selectByName(name);
    }

    public List<Group> selectByApplicationId(Long applicationId) {
        GroupCriteria example = new GroupCriteria();
        GroupCriteria.Criteria criteria = example.createCriteria();
        criteria.andApplicationIdEqualTo(applicationId);
        return groupMapper.selectByExample(example);
    }

    public List<Group> selectByGroupRequest(GroupRequest groupRequest) {
        GroupCriteria example = new GroupCriteria();
        GroupCriteria.Criteria criteria = example.createCriteria();
        if (groupRequest.getApplicationId() != null) {
            criteria.andApplicationIdEqualTo(groupRequest.getApplicationId());
        }
        return groupMapper.selectByExample(example);
    }

    public void insertSelective(Group groupRecord) {
        groupMapper.insertSelective(groupRecord);
    }

    public Group selectByApplicationIdAndName(Long applicationId, String name) {
        GroupCriteria example = new GroupCriteria();
        GroupCriteria.Criteria criteria = example.createCriteria();
        criteria.andApplicationIdEqualTo(applicationId);
        criteria.andNameEqualTo(name);
        List<Group> groups = groupMapper.selectByExample(example);
        return CollectionUtils.isEmpty(groups) ? null : groups.get(0);
    }

    public long countByApplicationIdAndName(Long applicationId, String name) {
        GroupCriteria example = new GroupCriteria();
        GroupCriteria.Criteria criteria = example.createCriteria();
        criteria.andApplicationIdEqualTo(applicationId);
        criteria.andNameEqualTo(name);
        return groupMapper.countByExample(example);
    }

    public void deleteByName(String name) {
        GroupCriteria example = new GroupCriteria();
        GroupCriteria.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(name);
        groupMapper.deleteByExample(example);
    }

    public void deleteByApplicationIdAndGroupId(Long applicationId,Long groupId) {
        GroupCriteria example = new GroupCriteria();
        GroupCriteria.Criteria criteria = example.createCriteria();
        criteria.andApplicationIdEqualTo(applicationId);
        criteria.andIdEqualTo(groupId);
        groupMapper.deleteByExample(example);
    }

    public void deleteByIds(List<Long> ids) {
        GroupCriteria example = new GroupCriteria();
        GroupCriteria.Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);
        groupMapper.deleteByExample(example);
    }

    public List<Group> selectByIds(List<Long> ids) {
        GroupCriteria example = new GroupCriteria();
        GroupCriteria.Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);
        return groupMapper.selectByExample(example);
    }

}
