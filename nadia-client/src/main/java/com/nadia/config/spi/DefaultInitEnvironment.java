package com.nadia.config.spi;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultInitEnvironment extends AbstractInitEnvironment {

    @Override
    public String getInstanceName() {
        return this.getClientInfo().toString();
    }
}
