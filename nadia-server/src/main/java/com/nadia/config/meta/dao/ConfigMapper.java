package com.nadia.config.meta.dao;

import com.nadia.config.meta.domain.Config;
import com.nadia.config.meta.domain.ConfigCriteria;
import com.nadia.config.meta.dto.request.ExportConfigRequest;
import com.nadia.config.meta.dto.response.ExportConfigResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConfigMapper {
    long countByExample(ConfigCriteria example);

    int deleteByExample(ConfigCriteria example);

    int deleteByPrimaryKey(Long id);

    int insert(Config record);

    int insertSelective(Config record);

    List<Config> selectByExampleWithBLOBs(ConfigCriteria example);

    List<Config> selectByExample(ConfigCriteria example);

    Config selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Config record, @Param("example") ConfigCriteria example);

    int updateByExampleWithBLOBs(@Param("record") Config record, @Param("example") ConfigCriteria example);

    int updateByExample(@Param("record") Config record, @Param("example") ConfigCriteria example);

    int updateByPrimaryKeySelective(Config record);

    int updateByPrimaryKeyWithBLOBs(Config record);

    int updateByPrimaryKey(Config record);

    List<ExportConfigResponse> getExportedConfigs(ExportConfigRequest request);
}