package com.nadia.config.common.rest;

import lombok.Data;

import java.util.Map;

@Data
public class RestHeader {
    private long errorCode = 0;
    private String msg = "success";
    private String prevCode;
    private String nextCode;
    private Map<String,Object> action= null;
}
