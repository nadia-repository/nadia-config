package com.nadia.config.user.service.impl;

import com.nadia.config.user.domain.RoleApprover;
import com.nadia.config.user.repo.RoleApproverRepo;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * @author xiang.shi
 * @date 2019-12-27 10:43
 */
public class CalculatorApprover extends RecursiveTask<List> {
    private List<RoleApprover> children;
    private RoleApproverRepo roleApproverRepo;
    private Long cuurentRole;

    public CalculatorApprover(Long cuurentRole ,List<RoleApprover> children,RoleApproverRepo roleApproverRepo){
        this.cuurentRole = cuurentRole;
        this.children = children;
        this.roleApproverRepo = roleApproverRepo;
    }

    @Override
    protected List compute() {
        List<Long> roles = new LinkedList<>();
        if(cuurentRole != null){
            roles.add(cuurentRole);
            List<RoleApprover> roleApprovers = roleApproverRepo.selectChildren(Arrays.asList(cuurentRole));
            if(CollectionUtils.isNotEmpty(roleApprovers) && roleApprovers.size() > 0){
                CalculatorApprover calculatorApprover = new CalculatorApprover(null, roleApprovers, roleApproverRepo);
                calculatorApprover.fork();
                List<Long> join = calculatorApprover.join();
                roles.addAll(join);
            }
            return roles;
        }else {
            for(RoleApprover roleApprover : children){
                roles.add(roleApprover.getRoleId());
                List<RoleApprover> roleApprovers = roleApproverRepo.selectChildren(Arrays.asList(roleApprover.getRoleId()));
                if(CollectionUtils.isNotEmpty(roleApprovers) && roleApprovers.size() > 0){
                    CalculatorApprover calculatorApprover = new CalculatorApprover(null, roleApprovers, roleApproverRepo);
                    calculatorApprover.fork();
                    List<Long> join = calculatorApprover.join();
                    roles.addAll(join);
                }
            }
            return roles;
        }
    }
}
