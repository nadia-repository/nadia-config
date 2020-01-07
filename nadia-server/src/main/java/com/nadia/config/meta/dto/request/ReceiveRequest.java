package com.nadia.config.meta.dto.request;

import com.nadia.config.notification.enums.Event;
import lombok.Data;

import java.util.List;

/**
 * @author: Wally.Wang
 * @date: 2019/12/19
 * @description:
 */
@Data
public class ReceiveRequest {

	private List<Long> configIds;
	private Event event;
	private String result;

}
