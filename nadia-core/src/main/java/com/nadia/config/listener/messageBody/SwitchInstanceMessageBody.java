package com.nadia.config.listener.messageBody;

import lombok.Data;

/**
 * @author xiang.shi
 * @date 2019-12-05 18:00
 */
@Data
public class SwitchInstanceMessageBody implements MessageBody {
    private String application;
    private String instance;
    private String groupFrom;
    private String groupTo;
}
