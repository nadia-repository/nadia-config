package com.nadia.config.meta.dto.response;

import lombok.Data;

import java.util.Date;

/**
 * @author: Wally.Wang
 * @date: 2019/12/13
 * @description:
 */
@Data
public class ConfigHistoryResponse {

	private Long id;
	private String description;
	private Date updatedAt;
	private String updatedBy;
	private String value;

}
