package com.nadia.config.spring;

import com.nadia.config.annotation.NadiaConfig;
import com.nadia.config.bean.Config;
import com.nadia.config.bean.ConfigContextHolder;
import com.nadia.config.callback.Callback;
import com.nadia.config.enumerate.ConfigTypeEnum;
import com.nadia.config.spi.AbstractMetadata;
import com.nadia.config.utils.FieldUtil;
import com.nadia.config.utils.PlaceholderHelper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SpringValueProcessor extends AbstractMetadata implements BeanPostProcessor, PriorityOrdered {

    private String[] basePackages;


    private String defaultApplication;

    private String defaultGroup;

    public void setDefaultApplication(String defaultApplication) {
        this.defaultApplication = defaultApplication;
    }

    public void setDefaultGroup(String defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        setBasePackage();
        if (!filterPackages(bean)) {
            return bean;
        }

        Class clazz = bean.getClass();

        ConfigurationProperties annotation = AnnotationUtils.findAnnotation(clazz, ConfigurationProperties.class);
        List<Field> allFields = findAllField(clazz);
        if (annotation == null) {
            for (Field field : allFields) {
                processField(bean, beanName, field);
            }
        } else {
            String prefix = annotation.prefix();
            for (Field field : allFields) {
                processPrefixField(bean, beanName, field, prefix);
            }
        }
        return bean;
    }

    private void setBasePackage() {
        if (this.basePackages == null) {
            List<String> basePackages = ConfigRegistrar.getBasePackages();
            if(!CollectionUtils.isEmpty(basePackages)){
                this.basePackages = basePackages.toArray(new String[]{});
            }
        }
    }

    public static void main(String[] args) {

    }

    public boolean filterPackages(Object bean) {
        if (basePackages == null || basePackages.length == 0) {
            return true;
        }
        String name = bean.getClass().getPackage().getName();
        // TODO 通配符
        List<String> afterFilter = Arrays.stream(basePackages).filter(name::startsWith).collect(Collectors.toList());
        return !afterFilter.isEmpty();
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private void processField(Object bean, String beanName, Field field) {
        // register @Value on field
        Value value = field.getAnnotation(Value.class);
        if (value == null) {
            return;
        }
        Set<String> keys = PlaceholderHelper.extractPlaceholderKeys(value.value());
        if (keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            Config config = generateConfig(bean, beanName, key, field);
            if(config != null){
                ConfigContextHolder.setConfigHolder(config.getApplication(),config.getGroup(),key, config);
            }
        }
    }

    private Config generateConfig(Object bean, String beanName, String key, Field field) {
        NadiaConfig nadiaConfig = field.getAnnotation(NadiaConfig.class);
        Class<? extends Callback> callback = null;
        Set<NadiaConfig.CallbackScenes> callbackScenesSet = null;
        String application = defaultApplication;
        String group = defaultGroup;
        if (nadiaConfig != null) {
            if(nadiaConfig.exclude()){
                return null;
            }
            callback = nadiaConfig.clazz();
            if (callback.equals(Callback.class)) {
                callback = null;
            }
            callbackScenesSet = getCallbackScenes(nadiaConfig);

            if(!"".equals(nadiaConfig.application()) && nadiaConfig.application() != null){
                application = nadiaConfig.application();
            }
            if(!"".equals(nadiaConfig.group()) && nadiaConfig.group() != null){
                group = nadiaConfig.group();
            }
        }
        return new Config(ConfigTypeEnum.FIELD,
                beanName,
                bean.getClass(),
                key, field,
                null,
                FieldUtil.getValue(bean, field),
                callback,
                callbackScenesSet,
                field.getType(),
                bean,
                application,
                group);
    }

    private Set<NadiaConfig.CallbackScenes> getCallbackScenes(NadiaConfig nadiaConfig) {
        return Arrays.stream(nadiaConfig.callbackScenes()).collect(Collectors.toSet());
    }

    private void processPrefixField(Object bean, String beanName, Field field, String prefix) {

        String key = prefix + "." + field.getName();

        Config config = generateConfig(bean, beanName, key, field);
        if(config != null){
            ConfigContextHolder.setConfigHolder(config.getApplication(),config.getGroup(),key, config);
        }
    }


    private List<Field> findAllField(Class clazz) {
        final List<Field> res = new LinkedList<>();
        ReflectionUtils.doWithFields(clazz, res::add);
        return res;
    }

    @Override
    public int getOrder() {
        //make it as late as possible
        return Ordered.LOWEST_PRECEDENCE;
    }
}
