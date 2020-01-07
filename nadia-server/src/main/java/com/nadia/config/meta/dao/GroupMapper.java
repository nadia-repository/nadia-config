package com.nadia.config.meta.dao;


import com.nadia.config.meta.domain.Group;
import com.nadia.config.meta.domain.GroupCriteria;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GroupMapper {
    long countByExample(GroupCriteria example);

    int deleteByExample(GroupCriteria example);

    int deleteByPrimaryKey(Long id);

    int insert(Group record);

    int insertSelective(Group record);

    List<Group> selectByExample(GroupCriteria example);

    Group selectByPrimaryKey(Long id);

    Group selectByName(String name);

    int updateByExampleSelective(@Param("record") Group record, @Param("example") GroupCriteria example);

    int updateByExample(@Param("record") Group record, @Param("example") GroupCriteria example);

    int updateByPrimaryKeySelective(Group record);

    int updateByPrimaryKey(Group record);
}