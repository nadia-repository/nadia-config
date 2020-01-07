package com.nadia.config.listener.enumerate;

import com.nadia.config.listener.messageBody.SwitchInstanceMessageBody;
import com.nadia.config.listener.messageBody.ClientMessageBody;
import com.nadia.config.listener.messageBody.HeartbeatMessageBody;
import com.nadia.config.listener.messageBody.UpdateValueMessageBody;
import lombok.Getter;

/**
 * @author xiang.shi
 * @date 2019-12-05 18:42
 */
public enum EventType {
    SWITCH_INSTANCE(SwitchInstanceMessageBody.class),
    UPDATE_VALUE(UpdateValueMessageBody.class),
    HEARTBEAT(HeartbeatMessageBody.class),
    CLIENT_MESSAGE(ClientMessageBody.class),
    ;

    @Getter
    private Class clazz;

    EventType(Class clazz) {
        this.clazz = clazz;
    }

    public static Class getClass(String name) {
        return EventType.valueOf(name).clazz;
    }
}
