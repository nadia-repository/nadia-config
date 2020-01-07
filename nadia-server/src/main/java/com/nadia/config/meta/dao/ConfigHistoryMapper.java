package com.nadia.config.meta.dao;

import java.util.List;

import com.nadia.config.meta.domain.ConfigHistory;
import com.nadia.config.meta.domain.ConfigHistoryCriteria;
import org.apache.ibatis.annotations.Param;

public interface ConfigHistoryMapper {
    long countByExample(ConfigHistoryCriteria example);

    int deleteByExample(ConfigHistoryCriteria example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigHistory record);

    int insertSelective(ConfigHistory record);

    List<ConfigHistory> selectByExampleWithBLOBs(ConfigHistoryCriteria example);

    List<ConfigHistory> selectByExample(ConfigHistoryCriteria example);

    ConfigHistory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigHistory record, @Param("example") ConfigHistoryCriteria example);

    int updateByExampleWithBLOBs(@Param("record") ConfigHistory record, @Param("example") ConfigHistoryCriteria example);

    int updateByExample(@Param("record") ConfigHistory record, @Param("example") ConfigHistoryCriteria example);

    int updateByPrimaryKeySelective(ConfigHistory record);

    int updateByPrimaryKeyWithBLOBs(ConfigHistory record);

    int updateByPrimaryKey(ConfigHistory record);
}