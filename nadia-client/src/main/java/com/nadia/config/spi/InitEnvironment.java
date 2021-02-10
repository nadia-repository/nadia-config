package com.nadia.config.spi;

import com.nadia.config.bean.ClientInfo;
import org.springframework.context.EnvironmentAware;

public interface InitEnvironment extends EnvironmentAware {

    void init();

    LoadConfig getLoadConfig();

    ClientInfo getClientInfo();

    void updateGroup(String application ,String newGroup);

    String getInstanceName();

    void setBasePackages(String[] basePackages);

    String[] getBasePackages();

    String getDefaultApplication();

    String getDefaultGroup();


}
