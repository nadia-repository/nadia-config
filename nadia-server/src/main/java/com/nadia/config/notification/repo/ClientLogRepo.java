package com.nadia.config.notification.repo;

import com.nadia.config.notification.dao.ClientLogMapper;
import com.nadia.config.notification.domain.ClientLog;
import com.nadia.config.notification.domain.ClientLogCriteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-23 10:43
 */
@Repository
public class ClientLogRepo {
    @Resource
    private ClientLogMapper clientLogMapper;

    public int insertSelective(ClientLog record){
        return clientLogMapper.insertSelective(record);
    }

    public List<ClientLog> logs(String application,String group,String instance,String traceId,String type,String level,List<String> createdAt){
        ClientLogCriteria example = new ClientLogCriteria();
        example.setOrderByClause("created_at desc");
        ClientLogCriteria.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(application)){
            criteria.andApplicationEqualTo(application);
        }
        if(!StringUtils.isEmpty(group)){
            criteria.andGroupEqualTo(group);
        }
        if (!StringUtils.isEmpty(instance)) {
            criteria.andInstanceEqualTo(instance);
        }
        if (!StringUtils.isEmpty(traceId)) {
            criteria.andTraceIdEqualTo(traceId);
        }
        if (!StringUtils.isEmpty(type)) {
            criteria.andTypeEqualTo(type);
        }
        if (!StringUtils.isEmpty(level)) {
            criteria.andLevelEqualTo(level);
        }
        if(!CollectionUtils.isEmpty(createdAt)){
            String s1 = createdAt.get(0);
            String s2 = createdAt.get(1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try{
                Date start = sdf.parse(s1);
                Date end = sdf.parse(s2);
                criteria.andCreatedAtBetween(start ,end);
            }catch(ParseException e){
                e.printStackTrace();
            }
        }

        return clientLogMapper.selectByExampleWithBLOBs(example);
    }

}
