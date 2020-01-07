package com.nadia.config.common.security;

import com.alibaba.fastjson.JSONObject;
import com.nadia.config.common.exception.BusinessException;
import com.nadia.config.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @author xiang.shi
 * @date 2019-12-06 10:50
 */
@Component
@Aspect
@Slf4j
public class GlobalLockAspect {
    private final static int TIME_OUT = 3;

    @Resource
    private RedisService redisService;

    @Around(value = "@within(lock)")
    public Object logAround4Class(ProceedingJoinPoint point, GlobalLock lock) throws Throwable {
        return lockAround(point, lock);
    }

    @Around(value = "@annotation(lock)")
    public Object logAround4Method(ProceedingJoinPoint point, GlobalLock lock) throws Throwable {
        return lockAround(point, lock);
    }

    private Object lockAround(ProceedingJoinPoint point, GlobalLock myLog) throws Throwable {
        Object result = null;
        Logger logger = null;
        String key = LockContext.getKey();
        if(StringUtils.isEmpty(key)){
            key = myLog.key();
        }
        boolean locked = false;

        try {
            locked = redisService.lock(key, key, TIME_OUT);
            log.info("Locking distributed lock, lock key: {}, lock result: {}, thread ID: {}",
                key, locked, Thread.currentThread().getId());

            if (locked) {
                logger = LoggerFactory.getLogger(myLog.clazz());
                Object[] args = point.getArgs();
                String methodName = point.getSignature().getName();
                logger.info("method name [{}] in, args : {}", methodName, JSONObject.toJSONString(args));
                result = point.proceed();
                logger.info("method name [{}] out", methodName);
            } else {
                throw new BusinessException(601L);
            }
        } catch (Throwable t) {
            throw t;
        } finally {
            redisService.unlock(locked, key);
            LockContext.clean();
        }

        return result;
    }
}
