package com.nadia.config.callback;

import com.nadia.config.spi.InitEnvironment;
import com.nadia.config.spi.LoadConfig;
import com.nadia.config.constant.OrderConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

public abstract class AbstractListener implements Ordered {
    @Autowired
    protected InitEnvironment initEnvironment;
    @Autowired
    protected LoadConfig loadConfig;

    @Override
    public int getOrder() {
        return OrderConstant.LISTENER_ORDER;
    }
}
