package com.nadia.config.enums;

/**
 * @author xiang.shi
 * @date 2019-12-23 10:36
 */
public enum LogLevelEnum {
    LOG("log"),
    WARN("warn"),
    ERROR("error"),
    ;

    private String level;

    public String getLevel() {
        return level;
    }

    LogLevelEnum(String level) {
        this.level = level;
    }

}
