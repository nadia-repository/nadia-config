package com.nadia.config.utils;

import com.alibaba.fastjson.JSONObject;
import com.nadia.config.bean.ClientValueBody;

/**
 * @author xiang.shi
 * @date 2019-12-04 19:42
 */
public class ClientValueUtil {

    public static ClientValueBody deserializer(String text){
        if(text == null){
            return new ClientValueBody();
        }
        ClientValueBody clientValueBody = JSONObject.parseObject(text, ClientValueBody.class);
        if(clientValueBody == null){
            clientValueBody = new ClientValueBody();
        }
        return clientValueBody;
    }

    public static String serializer(Object newValue,Object oldValue){
        ClientValueBody structure = new ClientValueBody();
        structure.setNewValue(newValue);
        structure.setOldValue(oldValue);
        String s = JSONObject.toJSONString(structure);
        return s;
    }

    public static void main(String[] args) {
        String serializer = ClientValueUtil.serializer("12121", "333dffff");
        System.out.println(serializer);
    }
}
