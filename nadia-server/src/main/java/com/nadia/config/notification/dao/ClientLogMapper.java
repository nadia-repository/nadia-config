package com.nadia.config.notification.dao;

import com.nadia.config.notification.domain.ClientLog;
import com.nadia.config.notification.domain.ClientLogCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ClientLogMapper {
    long countByExample(ClientLogCriteria example);

    int deleteByExample(ClientLogCriteria example);

    int deleteByPrimaryKey(Long id);

    int insert(ClientLog record);

    int insertSelective(ClientLog record);

    List<ClientLog> selectByExampleWithBLOBs(ClientLogCriteria example);

    List<ClientLog> selectByExample(ClientLogCriteria example);

    ClientLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ClientLog record, @Param("example") ClientLogCriteria example);

    int updateByExampleWithBLOBs(@Param("record") ClientLog record, @Param("example") ClientLogCriteria example);

    int updateByExample(@Param("record") ClientLog record, @Param("example") ClientLogCriteria example);

    int updateByPrimaryKeySelective(ClientLog record);

    int updateByPrimaryKeyWithBLOBs(ClientLog record);

    int updateByPrimaryKey(ClientLog record);
}