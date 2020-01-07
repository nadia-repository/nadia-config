package com.nadia.config.common.keepAlive;

import com.alibaba.fastjson.JSONObject;
import com.nadia.config.bean.ClientInfo;
import com.nadia.config.redis.RedisService;
import com.nadia.config.utils.IpPortUtil;
import com.nadia.config.utils.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author xiang.shi
 * @date 2019-12-12 14:40
 */
@Slf4j
@Component
public class ClientKeepAliveTask {

    @Resource
    private RedisService redisService;

    private String ip = IpPortUtil.getLocalIP();


    @PostConstruct
    public void keepAlive(){
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                //简单处理，每台服务起都启用判断客户端keepalive判断
                //但是同一时间，只有一台服务获得锁进行操作
                //todo get lock
                keepClientAlive();
            }
        }, 0, 2, TimeUnit.MINUTES);

    }

    private void keepClientAlive(){
        log.info("========================= Server[{}] Keep Alive with Redis Start =========================",ip);
        Map<String, String> serverMap = redisService.hgetAll(RedisKeyUtil.getClints());
        serverMap.forEach((k,v) -> {
            ClientInfo clientInfo = JSONObject.parseObject(v, ClientInfo.class);
            long timestamp = clientInfo.getTimestamp();
            if((new Date().getTime() - timestamp)/(1000*60) > 1){ //>1min
                String times = redisService.get(RedisKeyUtil.getClientRetryTimes(String.valueOf(clientInfo.hashCode())));
                if(StringUtils.isEmpty(times)){
                    redisService.setNX(RedisKeyUtil.getClientRetryTimes(String.valueOf(clientInfo.hashCode())),"1",3000);
                    //todo
                    //通知客户端
                }else if(Integer.valueOf(times) > 1){
                    //client offline ,delete instance info
                    redisService.del(RedisKeyUtil.getClints(),String.valueOf(clientInfo.hashCode()));
                    redisService.del(RedisKeyUtil.getInstance(clientInfo.getApplication(),clientInfo.getGroup()),clientInfo.getName());
                    redisService.del(RedisKeyUtil.getInstanceConfig(clientInfo.getApplication(),clientInfo.getGroup(),clientInfo.getName()));
                }
            }
        });
        log.info("========================= Server[{}] Keep Alive with Redis End   =========================",ip);
    }
}
