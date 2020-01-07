package com.nadia.config.utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class MethodHandleUtil {

    public static MethodHandle getMethod(Class<?> rtype, Class<?> ptype0,Class<?> bean,String method){
        MethodType mt = MethodType.methodType(rtype,ptype0);
        MethodHandle mh = null;
        try {
            mh = MethodHandles.lookup().findVirtual(bean,method,mt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mh;
    }

    public static void setField(Class<?> rtype,Class<?> bean,String name,Object newValue){

        MethodHandle setter = null;
        try {
            setter = MethodHandles.lookup().findSetter(bean, name, rtype);
            setter.invokeExact(newValue);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
