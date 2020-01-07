package com.nadia.config.notification.dao;

import java.util.List;

import com.nadia.config.notification.domain.Task;
import com.nadia.config.notification.domain.TaskCriteria;
import org.apache.ibatis.annotations.Param;

public interface TaskMapper {
    long countByExample(TaskCriteria example);

    int deleteByExample(TaskCriteria example);

    int deleteByPrimaryKey(Long id);

    int insert(Task record);

    int insertSelective(Task record);

    List<Task> selectByExampleWithBLOBs(TaskCriteria example);

    List<Task> selectByExample(TaskCriteria example);

    Task selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Task record, @Param("example") TaskCriteria example);

    int updateByExampleWithBLOBs(@Param("record") Task record, @Param("example") TaskCriteria example);

    int updateByExample(@Param("record") Task record, @Param("example") TaskCriteria example);

    int updateByPrimaryKeySelective(Task record);

    int updateByPrimaryKeyWithBLOBs(Task record);

    int updateByPrimaryKey(Task record);
}