package com.nadia.config.meta.repo;

import com.nadia.config.meta.dao.ConfigHistoryMapper;
import com.nadia.config.meta.domain.ConfigHistory;
import com.nadia.config.meta.domain.ConfigHistoryCriteria;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class ConfigHistoryRepo {

    @Resource
    private ConfigHistoryMapper configHistoryMapper;

    public List<ConfigHistory> selectByConfigId(Long id) {
        ConfigHistoryCriteria example = new ConfigHistoryCriteria();
        ConfigHistoryCriteria.Criteria criteria = example.createCriteria();
        criteria.andConfigIdEqualTo(id);
        return configHistoryMapper.selectByExample(example);

    }

    public void insertSelective(ConfigHistory record){
        configHistoryMapper.insertSelective(record);
    }
}
