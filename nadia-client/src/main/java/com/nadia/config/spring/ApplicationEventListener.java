package com.nadia.config.spring;

import com.nadia.config.bean.ClientInfo;
import com.nadia.config.redis.ConfigCenterRedisService;
import com.nadia.config.spi.InitEnvironment;
import com.nadia.config.utils.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author xiang.shi
 * @date 2019-12-12 19:34
 */
@Slf4j
public class ApplicationEventListener implements ApplicationListener<ApplicationEvent> {
    @Resource
    private ConfigCenterRedisService configCenterRedisService;
    @Resource
    private InitEnvironment initEnvironment;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if(event instanceof ContextClosedEvent){
            log.info("================== ContextClosedEvent START ====================");
            ClientInfo clientInfo = initEnvironment.getClientInfo();
            Map<String, String> agMap = clientInfo.getApplicationGroupMap();
            for(String application : agMap.keySet()){
                String group = agMap.get(application);
                //delete instance configs
                log.info("delete instance configs: key[{}]",RedisKeyUtil.getInstanceConfig(application,group,clientInfo.getName()));
                configCenterRedisService.del(RedisKeyUtil.getInstanceConfig(application,group,clientInfo.getName()));
                //delete instance in group
                log.info("delete instance in group: key[{}] value[{}]",RedisKeyUtil.getInstanceConfig(application,group,clientInfo.getName()),clientInfo.getName());
                configCenterRedisService.del(RedisKeyUtil.getInstance(application, group),clientInfo.getName());
            }
            //delete instance info in clients
            log.info("delete instance info in clients: key[{}] value[{}]",RedisKeyUtil.getClints(),clientInfo.getName());
            configCenterRedisService.del(RedisKeyUtil.getClints(),String.valueOf(clientInfo.hashCode()));
            log.info("================== ContextClosedEvent END   ====================");
        }
    }

    public static void main(String[] args) {
        Integer integer = Integer.valueOf("1.22");
        System.out.println(integer);
    }
}
