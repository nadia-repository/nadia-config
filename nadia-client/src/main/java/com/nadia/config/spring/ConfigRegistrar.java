package com.nadia.config.spring;

import com.nadia.config.notifycenter.NotifyFactory;
import com.nadia.config.spi.ConfigCenter;
import com.nadia.config.spi.InitEnvironment;
import com.nadia.config.spi.LoadConfig;
import com.nadia.config.utils.BeanRegistrationUtil;
import com.nadia.config.utils.SpiServiceUtil;
import com.nadia.config.utils.SpringUtils;
import com.nadia.config.annotation.EnableNadiaConfig;
import com.nadia.config.callback.Listener;
import com.nadia.config.constant.PropertyConstant;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class ConfigRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment env;

    private static List<String> basePackages;

    public static List<String> getBasePackages(){
        return basePackages;
    }

    public static void setBasePackages(AnnotationAttributes attributes){
        String[] packages = attributes.getStringArray("basePackages");
        if(CollectionUtils.isEmpty(ConfigRegistrar.basePackages)) {
            ConfigRegistrar.basePackages = new ArrayList<>();
        }
        ConfigRegistrar.basePackages.addAll(Arrays.asList(packages));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
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

        //工具
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, SpringUtils.class.getName(), SpringUtils.class);

        //收集本地环境变量、生成服务信息
        //----->SPI实现，本地变量的手机和Service端显示的服务信息可自定义实现
        Map<String, Object> envPropertyValues = new HashMap<>();
        envPropertyValues.put("importingClassMetadata", importingClassMetadata);
        InitEnvironment env = SpiServiceUtil.loadFirst(InitEnvironment.class);
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, InitEnvironment.class.getName(), env.getClass(), envPropertyValues);

        //收集本地配置&回调方法
        Map<String, Object> processorPropertyValues = new HashMap<>();
        processorPropertyValues.put("importingClassMetadata", importingClassMetadata);
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, SpringValueProcessor.class.getName(), SpringValueProcessor.class, processorPropertyValues);

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
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, ConfigBootstart.class.getName(), ConfigBootstart.class);

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
}
