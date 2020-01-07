package com.nadia.config.notification.repo;

import com.nadia.config.notification.dao.TaskMapper;
import com.nadia.config.notification.domain.TaskCriteria;
import com.nadia.config.notification.domain.Task;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Repository
public class TaskRepo {

    @Resource
    private TaskMapper taskMapper;

    public void insertSelective(Task record) {
        taskMapper.insertSelective(record);
    }

    public Task selectByPrimaryKey(Long id) {
        return taskMapper.selectByPrimaryKey(id);
    }

    public List<Task> selectByStatusAndRoleId(String status, Long roleId) {
        TaskCriteria example = new TaskCriteria();
        TaskCriteria.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(status);
        criteria.andRoleIdEqualTo(roleId);
        return taskMapper.selectByExampleWithBLOBs(example);
    }

    public List<Task> selectByStatusAndRoleIds(String status, List<Long> roleIds) {
        TaskCriteria example = new TaskCriteria();
        TaskCriteria.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(status);
        criteria.andRoleIdIn(roleIds);
        return taskMapper.selectByExampleWithBLOBs(example);
    }

    public List<Task> selectByStatusAndAction(String status, String action) {
        TaskCriteria example = new TaskCriteria();
        TaskCriteria.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(status);
        criteria.andActionEqualTo(action);
        return taskMapper.selectByExampleWithBLOBs(example);
    }

    public List<Task> selectByStatusAndCreatedBy(List<String> statusList, String createdBy) {
        TaskCriteria example = new TaskCriteria();
        TaskCriteria.Criteria criteria = example.createCriteria();
        criteria.andStatusIn(statusList);
        criteria.andCreatedByEqualTo(createdBy);
        return taskMapper.selectByExampleWithBLOBs(example);
    }

    public List<Task> selectByStatusAndCreatedBy(String status, String createdBy) {
        TaskCriteria example = new TaskCriteria();
        TaskCriteria.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(status);
        criteria.andCreatedByEqualTo(createdBy);
        return taskMapper.selectByExampleWithBLOBs(example);
    }


    public List<Task> selectByStatusAndUpdatedBy(List<String> statusList, String updatedBy) {
        TaskCriteria example = new TaskCriteria();
        TaskCriteria.Criteria criteria = example.createCriteria();
        criteria.andStatusIn(statusList);
        criteria.andUpdatedByEqualTo(updatedBy);
        return taskMapper.selectByExampleWithBLOBs(example);
    }

    public List<Task> selectByActionAndUpdatedAt(String action, Date updatedAt) {
        TaskCriteria example = new TaskCriteria();
        TaskCriteria.Criteria criteria = example.createCriteria();
        criteria.andActionEqualTo(action);
        criteria.andCreatedAtGreaterThan(updatedAt);
        return taskMapper.selectByExampleWithBLOBs(example);
    }

    public void deleteByPrimaryKey(Long id) {
        taskMapper.deleteByPrimaryKey(id);
    }

    public void updateByPrimaryKeySelective(Task Task) {
        taskMapper.updateByPrimaryKeySelective(Task);
    }
}
