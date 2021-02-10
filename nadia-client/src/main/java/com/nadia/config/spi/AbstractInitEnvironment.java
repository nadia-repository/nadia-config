package com.nadia.config.spi;

import com.alibaba.fastjson.JSONObject;
import com.nadia.config.annotation.EnableNadiaConfig;
import com.nadia.config.bean.ClientInfo;
import com.nadia.config.bean.ConfigContextHolder;
import com.nadia.config.common.exception.ConfigClientException;
import com.nadia.config.constant.GroupConstant;
import com.nadia.config.constant.OrderConstant;
import com.nadia.config.constant.PropertyConstant;
import com.nadia.config.enums.LogLevelEnum;
import com.nadia.config.listener.enumerate.EventType;
import com.nadia.config.publish.RedisPubSub;
import com.nadia.config.utils.ClientValueUtil;
import com.nadia.config.utils.IpPortUtil;
import com.nadia.config.utils.SpiServiceUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractInitEnvironment extends AbstractMetadata implements InitEnvironment, Ordered {

    private Environment env;

    private String defaultApplication;

    private String defaultGroup;

    private ClientInfo clientInfo;

    @Getter
    @Setter
    private String ip;

    @Getter
    @Setter
    private String port;

    @Getter
    @Setter
    private String appName;

    @Getter
    @Setter
    private String[] basePackages;

    @Override
    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;

    }

    public void setDefaultApplication(String defaultApplication) {
        this.defaultApplication = defaultApplication;
    }

    public void setDefaultGroup(String defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    @Override
    public String getDefaultApplication() {
        return this.defaultApplication;
    }

    @Override
    public String getDefaultGroup() {
        return this.defaultGroup;
    }

    @Autowired
    private RedisPubSub redisPubSub;

    @Override
    public void init() {
        log.info("========================= InitEnvironment start =========================");
        setBasePackage();
        setDefaultApplication();
        setDefaultGroup();
        setIp();
        setPort();
        setAppName();
        setClientInfo();
        logConfig();
        log.info("========================= InitEnvironment end   =========================");
    }

    private void setPort() {
        String port = env.getProperty(PropertyConstant.PROPERTRY_PORT);
        if(StringUtils.isEmpty(port)){
            port = "80";
        }
        setPort(port);
    }

    private void setIp() {
        setIp(IpPortUtil.getLocalIP());
    }

    private void setAppName(){
        String appName = env.getProperty(PropertyConstant.PROPERTRY_APPLICATION_NAME);
        setAppName(appName);
    }

    public void setClientInfo() {
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setIp(this.ip);
        clientInfo.setPort(this.port);
        clientInfo.setName(this.appName + "[" + this.ip + "]");
        clientInfo.setApplicationGroupMap(ConfigContextHolder.getApplicationGroups());
        this.clientInfo =  clientInfo;
    }

    private void logConfig() {
        log.info("========================= InitEnvironment ip:{}", clientInfo.getIp());
        log.info("========================= InitEnvironment port:{}", clientInfo.getPort());
        redisPubSub.notifyServer(getClientInfo(), EventType.CLIENT_MESSAGE, LogLevelEnum.LOG,"client info:" + JSONObject.toJSONString(getClientInfo()));
    }

    private void setDefaultApplication() {
        if("".equals(defaultApplication) || defaultApplication == null){
            redisPubSub.notifyServer(getClientInfo(), EventType.CLIENT_MESSAGE, LogLevelEnum.ERROR,"Please set application");
            throw new RuntimeException("Please set application!");//todo
        }
        log.info("========================= InitEnvironment defaultApplication:{}", this.defaultApplication);
    }

    private void setDefaultGroup() {
        log.info("========================= InitEnvironment defaultGroup:{}", this.defaultGroup);
    }


    private void setBasePackage() {
        AnnotationAttributes attributes = AnnotationAttributes
                .fromMap(getImportingClassMetadata().getAnnotationAttributes(EnableNadiaConfig.class.getName()));
        String[] basePackages = attributes.getStringArray("basePackages");
        setBasePackages(basePackages);
    }

    @Override
    public void updateGroup(String application, String newGroup) {
        if(defaultApplication.equals(application)){
            this.defaultGroup = newGroup;
        }
        Map<String, String> agMap = getClientInfo().getApplicationGroupMap();
        agMap.put(application, newGroup);
    }

    @Override
    public LoadConfig getLoadConfig() {
        return SpiServiceUtil.loadFirst(LoadConfig.class);
    }

    @Override
    public int getOrder() {
        return OrderConstant.ENV_ORDER;
    }
}

