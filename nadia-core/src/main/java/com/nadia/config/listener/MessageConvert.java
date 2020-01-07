package com.nadia.config.listener;

import com.alibaba.fastjson.JSONObject;
import com.nadia.config.listener.messageBody.MessageBody;
import com.nadia.config.listener.messageBody.SwitchInstanceMessageBody;
import com.nadia.config.listener.enumerate.EventType;

/**
 * @author xiang.shi
 * @date 2019-12-06 11:34
 */
@SuppressWarnings("all")
public class MessageConvert {

    public static MessageBody unpackageMessage(String message) {
        ListenerBody listenerBody = JSONObject.parseObject(message, ListenerBody.class);
        return (MessageBody) JSONObject.parseObject(listenerBody.getMessage(), listenerBody.getType().getClazz());
    }

    public static String packageMessage(MessageBody messageBody, EventType type) {
        ListenerBody message = new ListenerBody();
        message.setType(type);
        message.setMessage(JSONObject.toJSONString(messageBody));
        return JSONObject.toJSONString(message);
    }

    public static void main(String[] args) {
        SwitchInstanceMessageBody switchInstanceStructure = new SwitchInstanceMessageBody();
        switchInstanceStructure.setApplication("Trade");
        switchInstanceStructure.setGroupFrom("Default");
        switchInstanceStructure.setGroupTo("dev");
        switchInstanceStructure.setInstance("10.0.0.1");
        String message = MessageConvert.packageMessage(switchInstanceStructure, EventType.SWITCH_INSTANCE);
        System.out.println(message);
        System.out.println();

        MessageBody convert = MessageConvert.unpackageMessage(message);
        System.out.println(convert);
    }
}
