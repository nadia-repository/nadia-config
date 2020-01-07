package com.nadia.config.spi;

import com.nadia.config.annotation.NadiaConfig;

public interface LoadConfig {

    void load();

    void load(String application ,String group);

    Object getValue(String application, String key);

    Object getValue(String key);

    /**
     * 当前Client信息从redis中的Group中剔除
     */
    void offlineClientInGroup();

    /**
     * 当前Client配置从redis中的Group中提出
     */
    void offlineClientConfigs();

    /**
     * 将当前Client信息添加至redis中的Group中
     */
    void onlineClientInGroup();

    /**
     * 将当前Client的心跳信息从redis中提出
     */
    void offlineClientInfo();

    /**
     * 将Client信息上传至redis
     */
    void pushClinetInfo();

    /**
     * 将当前Client的所有配置信息更新至redis中
     */
    void pushClientConfigs();

    /**
     * 将当前Client的某以配置信息更新至redis中
     * @param key
     * @param value
     */
    void pushClientConfig(String key, String value);

    /**
     * 更新Client某一配置，并回调方法
     * @param key
     * @param newValue
     * @param callbackScenes
     */
    boolean updateClientValue(String key, Object newValue, NadiaConfig.CallbackScenes callbackScenes);

    /**
     * 根据redis中的配置，更新Client的所有配置信息
     */
    void updateClientValues();

    /**
     * 调整当前Client的Group,并更新所有的配置
     * @param application
     * @param groupFrom
     * @param groupTo
     * @param instance
     */
    void switchGroup(String application, String groupFrom, String groupTo, String instance);

    /**
     * 保持Client心跳
     */
    void keepClientAlive();

}
