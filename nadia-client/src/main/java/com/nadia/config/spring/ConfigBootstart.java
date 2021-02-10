package com.nadia.config.spring;

import com.nadia.config.callback.Listener;
import com.nadia.config.notifycenter.NotifyFactory;
import com.nadia.config.spi.InitEnvironment;
import com.nadia.config.spi.LoadConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

//@Component
public class ConfigBootstart {

    @Autowired
    private InitEnvironment initEnvironment;
    @Autowired
    private LoadConfig loadConfig;
    @Autowired
    private Listener listener;
    @Autowired
    private NotifyFactory notifyFactory;

    public void start(){
        //bootstart notify start
        notifyFactory.startPush();
        //env
        initEnvironment.init();
        //config
        loadConfig.load();
        loadConfig.updateClientValues();
        loadConfig.onlineClientInGroup();
        loadConfig.pushClientConfigs();
        loadConfig.keepClientAlive();
        //watch
        listener.init();
        //bootstart notify end
        notifyFactory.stopPush();
    }

}
