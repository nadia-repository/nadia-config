package com.nadia.config.notifycenter;

import com.nadia.config.bean.ClientInfo;
import com.nadia.config.enums.LogLevelEnum;
import com.nadia.config.listener.enumerate.EventType;

/**
 * @author xiang.shi
 * @date 2019-12-30 10:35
 */
public interface NotifyFactory {

    void setStop(boolean status);

    void startPush();

    void addMessage(ClientInfo clientInfo , EventType type , LogLevelEnum level, Object log);

    void stopPush();

}
