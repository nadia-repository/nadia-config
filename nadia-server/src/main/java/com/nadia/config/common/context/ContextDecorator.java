package com.nadia.config.common.context;

import org.springframework.core.task.TaskDecorator;

/**
 * @author xiang.shi
 * @date 2019-12-19 14:41
 */
public class ContextDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        UserDetail userDetail = UserContextHolder.getUserDetail();
        return () ->{
            UserContextHolder.setUserDetail(userDetail);
            runnable.run();
        };
    }
}
