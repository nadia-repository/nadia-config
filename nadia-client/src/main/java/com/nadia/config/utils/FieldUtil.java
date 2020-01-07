package com.nadia.config.utils;

import com.nadia.config.enumerate.TypeConvertEnum;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
public class FieldUtil {

    public static void updateValue(Object bean, Field field, Object newValue, Class<?> fieldType) throws Exception{
        if (bean == null) {
            return;
        }
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        field.set(bean, TypeConvertEnum.converter(TypeConvertEnum.getConvert(fieldType.getName()), newValue));
        field.setAccessible(accessible);
    }

    public static Object getValue(Object bean, Field field) {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        Object value = null;
        try {
            value = field.get(bean);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        field.setAccessible(accessible);
        return value;
    }
}
