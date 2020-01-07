package com.nadia.config.notification.dao;

import com.nadia.config.notification.domain.OperationLog;
import com.nadia.config.notification.domain.OperationLogCriteria;
import com.nadia.config.notification.domain.OperationLogWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OperationLogMapper {
    long countByExample(OperationLogCriteria example);

    int deleteByExample(OperationLogCriteria example);

    int deleteByPrimaryKey(Long id);

    int insert(OperationLogWithBLOBs record);

    int insertSelective(OperationLogWithBLOBs record);

    List<OperationLogWithBLOBs> selectByExampleWithBLOBs(OperationLogCriteria example);

    List<OperationLog> selectByExample(OperationLogCriteria example);

    OperationLogWithBLOBs selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OperationLogWithBLOBs record, @Param("example") OperationLogCriteria example);

    int updateByExampleWithBLOBs(@Param("record") OperationLogWithBLOBs record, @Param("example") OperationLogCriteria example);

    int updateByExample(@Param("record") OperationLog record, @Param("example") OperationLogCriteria example);

    int updateByPrimaryKeySelective(OperationLogWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(OperationLogWithBLOBs record);

    int updateByPrimaryKey(OperationLog record);
}