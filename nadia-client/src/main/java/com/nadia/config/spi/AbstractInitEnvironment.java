package com.nadia.config.spi;

import com.alibaba.fastjson.JSONObject;
import com.nadia.config.bean.ClientInfo;
import com.nadia.config.constant.GroupConstant;
import com.nadia.config.enums.LogLevelEnum;
import com.nadia.config.listener.enumerate.EventType;
import com.nadia.config.publish.RedisPubSub;
import com.nadia.config.utils.IpPortUtil;
import com.nadia.config.annotation.EnableNadiaConfig;
import com.nadia.config.constant.OrderConstant;
import com.nadia.config.constant.PropertyConstant;
import com.nadia.config.utils.SpiServiceUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Slf4j
public abstract class AbstractInitEnvironment extends AbstractMetadata implements InitEnvironment, Ordered {

    @Autowired
    private Environment env;

    @Getter
    @Setter
    private String application;

    @Setter
    private ClientInfo clientInfo;

    @Getter
    @Setter
    private String ip;

    @Getter
    @Setter
    private String port;

    @Getter
    @Setter
    private String group;

    @Getter
    @Setter
    private String[] basePackages;

    @Override
    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    @Autowired
    private RedisPubSub redisPubSub;

    @Override
    public void init() {
        log.info("========================= InitEnvironment start =========================");
        setGroup();
        setBasePackage();
        setApplication();
        setIp();
        setPort();
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

    private void setClientInfo() {
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setApplication(this.application);
        clientInfo.setGroup(this.group);
        clientInfo.setIp(this.ip);
        clientInfo.setPort(this.port);
        clientInfo.setName(this.ip + ":" + this.port);
        setClientInfo(clientInfo);
    }

    private void logConfig() {
        log.info("========================= InitEnvironment application:{}", clientInfo.getApplication());
        log.info("========================= InitEnvironment group:{}", clientInfo.getGroup());
        log.info("========================= InitEnvironment ip:{}", clientInfo.getIp());
        log.info("========================= InitEnvironment port:{}", clientInfo.getPort());
        redisPubSub.notifyServer(getClientInfo(), EventType.CLIENT_MESSAGE, LogLevelEnum.LOG,"client info:" + JSONObject.toJSONString(getClientInfo()));
    }

    private void setApplication() {
        AnnotationAttributes attributes = AnnotationAttributes
                .fromMap(getImportingClassMetadata().getAnnotationAttributes(EnableNadiaConfig.class.getName()));
        String application = attributes.getString("application");
        String propertyApplication = env.getProperty(PropertyConstant.PROPERTRY_APPLICATION);
        if (StringUtils.isEmpty(application) && StringUtils.isEmpty(propertyApplication)) {
            setApplication(env.getProperty(PropertyConstant.PROPERTRY_APPLICATION_NAME));
        } else if (!StringUtils.isEmpty(propertyApplication)) {
            setApplication(propertyApplication);
        } else if (!StringUtils.isEmpty(application)) {
            setApplication(application);
        } else {
            redisPubSub.notifyServer(getClientInfo(), EventType.CLIENT_MESSAGE, LogLevelEnum.ERROR,"Please set application");
            throw new RuntimeException("Please set application!");//todo
        }
    }

    private void setGroup() {
        AnnotationAttributes attributes = AnnotationAttributes
                .fromMap(getImportingClassMetadata().getAnnotationAttributes(EnableNadiaConfig.class.getName()));
        String group = attributes.getString("group");
        String propertyGroup = env.getProperty(PropertyConstant.PROPERTRY_GROUP);
        if (StringUtils.isEmpty(group) && StringUtils.isEmpty(propertyGroup)) {
            setGroup(GroupConstant.DEFAULT);
        } else if (!StringUtils.isEmpty(propertyGroup)) {
            setGroup(propertyGroup);
        } else if (!StringUtils.isEmpty(group)) {
            setGroup(group);
        }
    }

    private void setBasePackage() {
        AnnotationAttributes attributes = AnnotationAttributes
                .fromMap(getImportingClassMetadata().getAnnotationAttributes(EnableNadiaConfig.class.getName()));
        String[] basePackages = attributes.getStringArray("basePackages");
        setBasePackages(basePackages);
    }

    @Override
    public void updateGroup(String newGroup) {
        this.group = newGroup;
        getClientInfo().setGroup(newGroup);
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

