package com.nadia.config.meta.repo;

import com.nadia.config.meta.dao.ApplicationMapper;
import com.nadia.config.meta.domain.Application;
import com.nadia.config.meta.domain.ApplicationCriteria;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class ApplicationRepo {

    @Resource
    private ApplicationMapper applicationMapper;


    public List<Application> selectAll() {
        return applicationMapper.selectByExample(null);
    }

    public Application selectByPrimaryKey(Long id) {
        return applicationMapper.selectByPrimaryKey(id);
    }

    public List<Application> selectByNames(List<String> names) {
        ApplicationCriteria example = new ApplicationCriteria();
        ApplicationCriteria.Criteria criteria = example.createCriteria();
        if (CollectionUtils.isNotEmpty(names)) {
            criteria.andNameIn(names);
        }
        return applicationMapper.selectByExample(example);
    }

    public void insertSelective(Application applicationRecord) {
        applicationMapper.insertSelective(applicationRecord);
    }

    public Application selectByName(String name) {
        ApplicationCriteria example = new ApplicationCriteria();
        ApplicationCriteria.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(name);
        List<Application> applications = applicationMapper.selectByExample(example);
        return CollectionUtils.isEmpty(applications) ? null : applications.get(0);
    }

    public long countByName(String name){
        ApplicationCriteria example = new ApplicationCriteria();
        ApplicationCriteria.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(name);
        return applicationMapper.countByExample(example);
    }

    public List<Application> selectByIds(List<Long> ids) {
        ApplicationCriteria example = new ApplicationCriteria();
        ApplicationCriteria.Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);
        return applicationMapper.selectByExample(example);
    }

    public void deleteByName(String name) {
        ApplicationCriteria example = new ApplicationCriteria();
        ApplicationCriteria.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(name);
        applicationMapper.deleteByExample(example);
    }

    public void updateByPrimaryKeySelective(Application application) {
        applicationMapper.updateByPrimaryKeySelective(application);
    }

}
