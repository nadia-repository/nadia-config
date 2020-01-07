package com.nadia.config.enumerate;

import com.nadia.config.common.exception.UpdateConfigException;

import java.math.BigDecimal;
import java.math.BigInteger;

public enum TypeConvertEnum {
    BBOOLEAN(Boolean.class.getName()) {
        @Override
        public Object cast(Object value) {
            if("true".equals(String.valueOf(value))
            || "True".equals(String.valueOf(value))
            || "TRUE".equals(String.valueOf(value))
            || "false".equals(String.valueOf(value))
            || "False".equals(String.valueOf(value))
            || "FALSE".equals(String.valueOf(value))){
                return Boolean.parseBoolean(String.valueOf(value));
            }else {
                throw new UpdateConfigException("value type is wrong");
            }
        }
    },
    BOOLEAN(boolean.class.getName()) {
        @Override
        public Object cast(Object value) {
            return BBOOLEAN.cast(value);
        }
    },
    STRING(String.class.getName()) {
        @Override
        public Object cast(Object value) {
            return String.valueOf(value);
        }
    },
    INTEGER(Integer.class.getName()) {
        @Override
        public Object cast(Object value) {
            return Integer.valueOf(String.valueOf(value));
        }
    },
    INT(int.class.getName()) {
        @Override
        public Object cast(Object value) {
            return INTEGER.cast(value);
        }
    },
    LLONG(Long.class.getName()) {
        @Override
        public Object cast(Object value) {
            return Long.valueOf(String.valueOf(value));
        }
    },
    LONG(long.class.getName()) {
        @Override
        public Object cast(Object value) {
            return LLONG.cast(value);
        }
    },
    BIG_INTEGER(BigInteger.class.getName()) {
        @Override
        public Object cast(Object value) {
            return new BigInteger(String.valueOf(value));
        }
    },
    BIG_DECIMAL(BigDecimal.class.getName()) {
        @Override
        public Object cast(Object value) {
            return new BigDecimal(String.valueOf(value));
        }
    },
    // TODO List Map Set CustomizeObject
    ;
    private String className;

    public abstract Object cast(Object value);

    TypeConvertEnum(String className) {
        this.className = className;
    }

    public static TypeConvertEnum getConvert(String className) {
        for (TypeConvertEnum e : TypeConvertEnum.values()) {
            if (e.className.equals(className)) {
                return e;
            }
        }
        return TypeConvertEnum.STRING;
    }

    public static Object converter(TypeConvertEnum type, Object value) {
        return type.cast(value);
    }
}
