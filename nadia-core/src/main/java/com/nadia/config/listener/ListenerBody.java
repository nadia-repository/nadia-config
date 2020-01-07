package com.nadia.config.listener;

import com.nadia.config.listener.enumerate.EventType;
import lombok.Data;

/**
 * @author xiang.shi
 * @date 2019-12-05 18:48
 */
@Data
public class ListenerBody {
    private EventType type;
    private String message;
}
