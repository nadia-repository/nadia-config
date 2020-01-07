package com.nadia.config.spring;

import com.nadia.config.annotation.NadiaConfig;
import com.nadia.config.bean.Config;
import com.nadia.config.bean.ConfigContextHolder;
import com.nadia.config.spi.AbstractMetadata;
import com.nadia.config.utils.FieldUtil;
import com.nadia.config.utils.PlaceholderHelper;
import com.nadia.config.callback.Callback;
import com.nadia.config.enumerate.ConfigTypeEnum;
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
            ConfigContextHolder.setConfigHolder(key, config);
        }
    }

    private Config generateConfig(Object bean, String beanName, String key, Field field) {
        NadiaConfig nadiaConfig = field.getAnnotation(NadiaConfig.class);
        Class<? extends Callback> callback = null;
        Set<NadiaConfig.CallbackScenes> callbackScenesSet = null;
        if (nadiaConfig != null) {
            if(nadiaConfig.exclude()){
                return null;
            }
            callback = nadiaConfig.clazz();
            callbackScenesSet = getCallbackScenes(nadiaConfig);
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
                bean);
    }

    private Set<NadiaConfig.CallbackScenes> getCallbackScenes(NadiaConfig nadiaConfig) {
        return Arrays.stream(nadiaConfig.callbackScenes()).collect(Collectors.toSet());
    }

    private void processPrefixField(Object bean, String beanName, Field field, String prefix) {

        String key = prefix + "." + field.getName();

        Config config = generateConfig(bean, beanName, key, field);
        ConfigContextHolder.setConfigHolder(key, config);
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
