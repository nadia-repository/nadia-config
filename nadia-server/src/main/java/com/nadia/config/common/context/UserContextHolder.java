package com.nadia.config.common.context;

import java.util.Date;

public class UserContextHolder {

    final private static ThreadLocal<Date> now = new ThreadLocal<>();
    final private static ThreadLocal<UserDetail> context = new ThreadLocal<>();

    final private static InheritableThreadLocal<UserDetail> parentContext = new InheritableThreadLocal<>();

    final public static UserDetail getUserDetail() {
        return context.get() != null ? context.get() : parentContext.get();
    }

    final public static void setUserDetail(UserDetail userDetail) {
        context.set(userDetail);
    }

    final public static Date getNow() {
        return now.get();
    }

    final public static void setNow(Date date) {
        now.set(date);
    }

    final public static void clear() {
        context.remove();
        now.remove();
    }

}
