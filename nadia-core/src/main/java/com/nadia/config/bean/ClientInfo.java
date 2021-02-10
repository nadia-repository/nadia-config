package com.nadia.config.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(exclude = {"timestamp"})
public class ClientInfo {
    private String name;
    private String version;
    private String ip;
    private String port;
    private long timestamp;//for keep alive
    private Map<String, String> applicationGroupMap = new HashMap<>();

    @Override
    public String toString() {
        return String.format("%s:%s",   ip, port);
    }

    public static void main(String[] args) throws InterruptedException {
        for(int i =0;i<20;i++){
            Thread.sleep(1000);
            ClientInfo clientInfo = new ClientInfo();
            clientInfo.setName("10.112.12.53:80");
//            clientInfo.setApplication("Trade");
//            clientInfo.setGroup("Default");
            clientInfo.setIp("10.112.12.53");
            clientInfo.setPort("80");
            clientInfo.setTimestamp(new Date().getTime());
            System.out.println(clientInfo.hashCode());
            System.out.println("======================");
        }
    }
}
