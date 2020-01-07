package com.nadia.config.meta.dto.response;

import lombok.Data;

/**
 * @author: Wally.Wang
 * @date: 2019/12/23
 * @description:
 */
@Data
public class ConfigInstanceResponse {

    private String serverConfig;
    private ClientConfig clientConfig;

    @Data
    public static class ClientConfig {
        private String instance;
        private String value;
    }

}
