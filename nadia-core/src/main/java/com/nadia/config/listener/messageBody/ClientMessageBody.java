package com.nadia.config.listener.messageBody;

import com.nadia.config.bean.ClientInfo;
import com.nadia.config.enums.LogLevelEnum;
import com.nadia.config.listener.enumerate.EventType;
import lombok.Data;

/**
 * @author xiang.shi
 * @date 2019-12-13 16:13
 */
@Data
public class ClientMessageBody implements MessageBody {
    private ClientInfo clientInfo;
    private String traceId;
    private EventType type;
    private LogLevelEnum level;
    private String message;
}
