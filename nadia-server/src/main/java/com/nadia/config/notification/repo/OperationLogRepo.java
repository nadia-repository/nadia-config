package com.nadia.config.notification.repo;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nadia.config.notification.dao.OperationLogMapper;
import com.nadia.config.notification.domain.OperationLogCriteria;
import com.nadia.config.notification.domain.OperationLogWithBLOBs;
import com.nadia.config.notification.dto.request.OperationLogRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
public class OperationLogRepo {

    @Resource
    private OperationLogMapper operationLogMapper;

    public Page<OperationLogWithBLOBs> selectByOperationLogRequestWithBLOBs(OperationLogRequest request) {
        return selectByOperationLogRequestWithBLOBs(request, true);
    }

    public Page<OperationLogWithBLOBs> selectByOperationLogRequestWithBLOBs(OperationLogRequest request, boolean pageAble) {
        if (pageAble) {
            PageHelper.startPage(request.getPage(), request.getLimit());
        }
        OperationLogCriteria example = new OperationLogCriteria();
        OperationLogCriteria.Criteria criteria = example.createCriteria();

        if (StringUtils.isNotBlank(request.getView())) {
            criteria.andPageNameEqualTo(request.getView());
        }
        if (StringUtils.isNotBlank(request.getCreatedBy())) {
            criteria.andCreatedByLike("%".concat(request.getCreatedBy()).concat("%"));
        }
        if (StringUtils.isNotBlank(request.getType())) {
            criteria.andTypeEqualTo(request.getType());
        }
        if (StringUtils.isNotBlank(request.getTarget())) {
            criteria.andTargetEqualTo(request.getTarget());
        }
        if (!Objects.isNull(request.getCreatedAt()) && request.getCreatedAt().size() == 2) {
            String s1 = request.getCreatedAt().get(0);
            String s2 = request.getCreatedAt().get(1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try{
                Date start = sdf.parse(s1);
                Date end = sdf.parse(s2);
                criteria.andCreatedAtBetween(start ,end);
            }catch(ParseException e){
                e.printStackTrace();
            }
        }
        example.setOrderByClause("created_at DESC");
        return (Page<OperationLogWithBLOBs>) operationLogMapper.selectByExampleWithBLOBs(example);
    }

    public void deleteByClearBeforeTime(Date clearBeforeTime) {
        OperationLogCriteria example = new OperationLogCriteria();
        OperationLogCriteria.Criteria criteria = example.createCriteria();
        criteria.andCreatedAtLessThanOrEqualTo(clearBeforeTime);
        operationLogMapper.deleteByExample(example);
    }

    public void insertSelective(OperationLogWithBLOBs record) {
        operationLogMapper.insertSelective(record);
    }
}
