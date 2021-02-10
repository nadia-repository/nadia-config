package com.nadia.config.spi;

import com.alibaba.fastjson.JSONObject;
import com.nadia.config.annotation.NadiaConfig;
import com.nadia.config.bean.Config;
import com.nadia.config.bean.ConfigContextHolder;
import com.nadia.config.bean.RemoteContextHolder;
import com.nadia.config.callback.Callback;
import com.nadia.config.constant.OrderConstant;
import com.nadia.config.enumerate.ConfigTypeEnum;
import com.nadia.config.enums.LogLevelEnum;
import com.nadia.config.listener.enumerate.EventType;
import com.nadia.config.spring.ConfigBoundEvent;
import com.nadia.config.spring.SpringValueProcessor;
import com.nadia.config.utils.FieldUtil;
import com.nadia.config.utils.RedisKeyUtil;
import com.nadia.config.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractLoadConfig implements LoadConfig, Ordered {

    @Autowired
    protected InitEnvironment initEnvironment;

    @Autowired
    protected SpringValueProcessor springValueProcessor;

    @Autowired
    protected ApplicationContext applicationContext;

    @Override
    public Object getValue(String application, String key) {
//        Map<String, Object> configMap = keyValues.get(appliction);
//        while (configMap == null){
//            retry(appliction);
//        }


        return null;
    }

    private static void retry(String application) {
        //todo
    }

    protected void notificServer(String message, EventType type , LogLevelEnum level){

    }

    @Override
    public boolean updateClientValue(String application,String group,String key, Object newValue, NadiaConfig.CallbackScenes callbackScenes) {
        boolean hasFaile = false;
        List<Config> configs = ConfigContextHolder.getConfigs(application,group,key);
        if (CollectionUtils.isEmpty(configs)) {
            log.warn("========== UPDATE LOCAL VALUE FROM REMOTE ,Key is not exist ==========");
            this.notificServer("UPDATE LOCAL VALUE FROM REMOTE ,Key is not exist",EventType.UPDATE_VALUE,LogLevelEnum.ERROR);
            return true;
        }
        for (Config c : configs) {
            try {
                swapValue(c, newValue);
                log.info("========== UPDATE LOCAL VALUE FROM REMOTE========== application:[{}],group:[{}],key:[{}],oldValue:[{}],newValue:[{}]",
                        application,group,key, JSONObject.toJSONString(c.getOldValue()), JSONObject.toJSONString(c.getCurrentValue()));
                this.notificServer("UPDATE LOCAL VALUE FROM REMOTE application[" + application + "] group[" + group + "] key[" + key +"] oldValue["+ JSONObject.toJSONString(c.getOldValue()) + "] newValue["+ JSONObject.toJSONString(c.getCurrentValue()) +"]",EventType.UPDATE_VALUE,LogLevelEnum.LOG);
                if (c.getCallbackScenesSet() != null
                        && c.getCallbackScenesSet().contains(callbackScenes)
                        && c.getCallback() != null) {
                    Callback callback = SpringUtils.getApplicationContext().getBean(c.getCallback());
                    callback.callback(key, c.getOldValue(), c.getCurrentValue());
                }
            }catch (Exception e){
                log.error("========== UPDATE REDIS VALUE ERROR========== application:[{}],group:[{}],key:[{}],newValue:[{}]", application,group,c.getKey(), newValue);
                this.notificServer("UPDATE REDIS VALUE ERROR application[" + application + "] group[" + group + "] key:["+c.getKey()+"],newValue:["+newValue+"]",EventType.UPDATE_VALUE,LogLevelEnum.ERROR);
                hasFaile = true;
            }
            ConfigContextHolder.setClientConfigsHolder(application,group,key, c.getOldValue(), c.getCurrentValue());
        }
        return hasFaile;
    }

    public void refreshConfigs(String application, String group, NadiaConfig.CallbackScenes callbackScenes) {
        String configKey = RedisKeyUtil.getGroupConfig(application, group);
        Map<String, String> remoteConfigs = RemoteContextHolder.getRemoteHolder(configKey);
        if (remoteConfigs == null || remoteConfigs.size() == 0) {
            this.notificServer("Refresh ALL LOCAL VALUE REMOTE VALUE IS EMPTY !!! APPLICATION[" + application +"] GROUP[" + group +"]",EventType.CLIENT_MESSAGE,LogLevelEnum.ERROR);
            log.error("========== Refresh ALL LOCAL VALUE REMOTE VALUE IS EMPTY !!! APPLICATION[{}] GROUP[{}] ==========", application, group);
            throw new RuntimeException("REMOTE VALUE IS EMPTY"); //todo
        }
        Map<String, List<Config>> allLocalConfigs = ConfigContextHolder.getAllConfigs(application,group);
        allLocalConfigs.forEach((key, configs) -> {
            boolean existKey = remoteConfigs.containsKey(key);
            if (!existKey) {
                this.notificServer("UPDATE LOCAL VALUE ERROR ,SERVER KEY IS NOT EXIST!!!!!!!!!!! APPLICATION["+application+"] GROUP["+group+"] KEY["+key+"]",EventType.CLIENT_MESSAGE,LogLevelEnum.ERROR);
                log.error("========== UPDATE LOCAL VALUE ERROR ,SERVER KEY IS NOT EXIST!!!!!!!!!!! APPLICATION[{}] GROUP[{}] KEY[{}] ==========", application, group, key);
                ConfigContextHolder.setClientConfigsHolder(application,group,key, configs.get(0).getOldValue(), "");
            } else {
                updateClientValue(application,group,key, remoteConfigs.get(key), callbackScenes);
            }
        });
    }

    @Override
    public void updateClientValues() {
        Map<String, String> agMap = initEnvironment.getClientInfo().getApplicationGroupMap();
        for (String application : agMap.keySet()) {
            String group = agMap.get(application);
            this.refreshConfigs(application, group, NadiaConfig.CallbackScenes.INIT);
        }
        // publish ConfigBoundEvent when config bound
        applicationContext.publishEvent(new ConfigBoundEvent(this, ConfigBoundEvent.SUCCESS));
    }


    private void swapValue(Config config, Object newValue) throws Exception{
        if (ConfigTypeEnum.FIELD.equals(config.getConfigType())) {
            swapField(config, newValue);
        }
        config.setOldValue(config.getCurrentValue());
        config.setCurrentValue(newValue);
    }

    private void swapField(Config config, Object newValue) throws Exception{
        Object bean = config.getBeanRef().get();
        if (bean == null) {
            bean = SpringUtils.getApplicationContext().getBean(config.getBeanName());
            config.setBeanRef(new WeakReference<>(bean));
        }
        FieldUtil.updateValue(bean, config.getField(), newValue, config.getFieldType());
    }

    @Override
    public void switchGroup(String application, String groupFrom, String groupTo,String instance) {
        if (initEnvironment.getClientInfo().getName().equals(instance)) {
            this.load(application, groupTo);
            this.refreshConfigs(application, groupTo, NadiaConfig.CallbackScenes.SWITCH_GROUP);
            //offline
            this.offlineClientInGroup(application,groupFrom);
            this.offlineClientConfigs(application,groupFrom);
            this.offlineClientInfo();
            //online
            initEnvironment.updateGroup(application,groupTo);
            this.onlineClientInGroup(application,groupTo);
            this.pushClientConfigs(application,groupFrom,groupTo);
            this.pushClinetInfo();
        }
    }

    @Override
    public int getOrder() {
        return OrderConstant.LOAD_ORDER;
    }
}
