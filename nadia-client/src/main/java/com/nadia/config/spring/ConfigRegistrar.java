package com.nadia.config.spring;

import com.nadia.config.annotation.EnableNadiaConfig;
import com.nadia.config.callback.Listener;
import com.nadia.config.constant.GroupConstant;
import com.nadia.config.constant.PropertyConstant;
import com.nadia.config.enums.LogLevelEnum;
import com.nadia.config.listener.enumerate.EventType;
import com.nadia.config.notifycenter.NotifyFactory;
import com.nadia.config.spi.ConfigCenter;
import com.nadia.config.spi.InitEnvironment;
import com.nadia.config.spi.LoadConfig;
import com.nadia.config.utils.BeanRegistrationUtil;
import com.nadia.config.utils.SpiServiceUtil;
import com.nadia.config.utils.SpringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

public class ConfigRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private static Environment env;

    private static List<String> basePackages;

    public static List<String> getBasePackages(){
        return basePackages;
    }

    private static String defaultApplication;

    private static String defaultGroup;

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(EnableNadiaConfig.class.getName()));

        //检查是否需要启动
        if (!enableConfig(importingClassMetadata, attributes)) {
            return;
        }

        //初始化需要扫描的包
        ConfigRegistrar.setBasePackages(attributes);

        //初始化DefalutApplication & DefalutGroup
        ConfigRegistrar.setDefaultApplication(attributes);
        ConfigRegistrar.setDefaultGroup(attributes);

        //工具
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, SpringUtils.class.getName(), SpringUtils.class);

        //收集本地环境变量、生成服务信息
        //----->SPI实现，本地变量的手机和Service端显示的服务信息可自定义实现
        Map<String, Object> envPropertyValues = new HashMap<>();
        envPropertyValues.put("importingClassMetadata", importingClassMetadata);
        envPropertyValues.put("defaultApplication", defaultApplication);
        envPropertyValues.put("defaultGroup", defaultGroup);
        InitEnvironment env = SpiServiceUtil.loadFirst(InitEnvironment.class);
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, InitEnvironment.class.getName(), env.getClass(), envPropertyValues);

        //收集本地配置&回调方法
        Map<String, Object> processorPropertyValues = new HashMap<>();
        processorPropertyValues.put("importingClassMetadata", importingClassMetadata);
        processorPropertyValues.put("defaultApplication", defaultApplication);
        processorPropertyValues.put("defaultGroup", defaultGroup);
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, SpringValueProcessor.class.getName(), SpringValueProcessor.class, processorPropertyValues);

        //启动器注册
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, ConfigPostConstructProcessor.class.getName(), ConfigPostConstructProcessor.class);

        //redis装载
        ConfigCenter configCenter = SpiServiceUtil.loadFirst(ConfigCenter.class);
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, ConfigCenter.class.getName(), configCenter.getClass());

        //按照本地环境变量收集redis中的配置（）
        //向redis提供当前服务信息
        //------>SPI实现，可替换redis为其他中间件
        LoadConfig loadConfig = SpiServiceUtil.loadFirst(LoadConfig.class);
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, LoadConfig.class.getName(), loadConfig.getClass());

        // FIXME 如果不是redis实现的时候这里的RedisMessageListenerContainer怎么做替换
//        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, RedisMessageListenerContainer.class.getName(), RedisMessageListenerContainer.class);

        //配置变更订阅
        Listener listener = SpiServiceUtil.loadFirst(Listener.class);
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, Listener.class.getName(), listener.getClass());

        //客户端通知服务端
        NotifyFactory notifyFactory = SpiServiceUtil.loadFirst(NotifyFactory.class);
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, NotifyFactory.class.getName(), notifyFactory.getClass());

        //配置变更启动类
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, ConfigPostConstructProcessor.class.getName(), ConfigPostConstructProcessor.class);

        //spring容器关闭时，销毁当前实例信息
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, ApplicationEventListener.class.getName(), ApplicationEventListener.class);

    }

    private boolean enableConfig(AnnotationMetadata importingClassMetadata, AnnotationAttributes attributes) {
        String[] actives = attributes.getStringArray("actives");
        if (actives.length == 0) {
            return true;
        }
        String property = env.getProperty(PropertyConstant.PROPERTRY_PROFILES_NAME);
        for (String active : actives) {
            if (active.equals(property)) {
                return true;
            }
        }
        return false;
    }

    public static void setBasePackages(AnnotationAttributes attributes){
        String[] packages = attributes.getStringArray("basePackages");
        if(CollectionUtils.isEmpty(ConfigRegistrar.basePackages)) {
            ConfigRegistrar.basePackages = new ArrayList<>();
        }
        ConfigRegistrar.basePackages.addAll(Arrays.asList(packages));
    }

    public static void setDefaultApplication(AnnotationAttributes attributes) {
        String application = attributes.getString("application");
        String propertyApplication = env.getProperty(PropertyConstant.PROPERTRY_APPLICATION);
        if (StringUtils.isEmpty(application) && StringUtils.isEmpty(propertyApplication)) {
            defaultApplication = env.getProperty(PropertyConstant.PROPERTRY_APPLICATION_NAME);
        } else if (!StringUtils.isEmpty(propertyApplication)) {
            defaultApplication = propertyApplication;
        } else if (!StringUtils.isEmpty(application)) {
            defaultApplication = application;
        }
    }

    private static void setDefaultGroup(AnnotationAttributes attributes) {
        String group = attributes.getString("group");
        String propertyGroup = env.getProperty(PropertyConstant.PROPERTRY_GROUP);
        if (StringUtils.isEmpty(group) && StringUtils.isEmpty(propertyGroup)) {
            defaultGroup = GroupConstant.DEFAULT;
        } else if (!StringUtils.isEmpty(propertyGroup)) {
            defaultGroup = propertyGroup;
        } else if (!StringUtils.isEmpty(group)) {
            defaultGroup = group;
        }
    }
}
