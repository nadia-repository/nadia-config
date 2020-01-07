package com.nadia.config.bean;

import com.nadia.config.callback.Callback;

import java.util.List;
import java.util.Map;

public class CallbackContextHolder {
    private static ThreadLocal<Map<String, List<Callback>>> callbackHolder = new ThreadLocal<>();

}
