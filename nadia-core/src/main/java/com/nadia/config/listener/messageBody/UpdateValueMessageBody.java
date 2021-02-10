package com.nadia.config.listener.messageBody;

import lombok.Data;

/**
 * @author junjie.xun
 * @date 2019-12-10 14:00
 */
@Data
public class UpdateValueMessageBody implements MessageBody {
    private String key;
    private String value;
    private String application;
    private String group;
}
