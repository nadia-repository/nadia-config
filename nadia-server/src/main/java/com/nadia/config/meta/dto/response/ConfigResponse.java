package com.nadia.config.meta.dto.response;

import lombok.Data;

import java.util.Date;

/**
 * @author: Wally.Wang
 * @date: 2019/12/04
 * @description:
 */
@Data
public class ConfigResponse {
	private String name;
	private String key;
	private String value;
	private String description;
	private String updatedBy;
	private Date updatedAt;
	private String status;
	private Long id;
	private boolean checked;
	private Long configId;
}
