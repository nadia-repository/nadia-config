package com.nadia.config.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CallbackDemo implements Callback {
    @Override
    public void callback(String key, Object oldValue, Object newValue) {
        log.info("CallbackDemo.callback key:{}, oldValue:{}, newValue:{}", key, oldValue, newValue);
    }
}
