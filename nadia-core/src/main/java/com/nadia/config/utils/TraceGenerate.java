package com.nadia.config.utils;

import org.springframework.util.StringUtils;

/**
 * @author xiang.shi
 * @date 2019-12-23 13:51
 */
public class TraceGenerate {
    private static ThreadLocal<String> ctx = new InheritableThreadLocal();

    public static String getTraceId() {
        String s = ctx.get();
        if(StringUtils.isEmpty(s)){
            s = System.nanoTime() + "";//todo
            ctx.set(s);
            return s;
        }
        return s;
    }

    public static void clear(){
        ctx.remove();
    }
}
