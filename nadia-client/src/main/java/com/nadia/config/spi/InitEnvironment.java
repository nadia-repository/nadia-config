package com.nadia.config.spi;

import com.nadia.config.bean.ClientInfo;

public interface InitEnvironment {

    void init();

    LoadConfig getLoadConfig();

    ClientInfo getClientInfo();

    void setGroup(String g);

    void updateGroup(String newGroup);

    String getInstanceName();

    void setBasePackages(String[] basePackages);

    String[] getBasePackages();
}
