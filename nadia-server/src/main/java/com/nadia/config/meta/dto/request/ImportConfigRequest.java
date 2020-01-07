package com.nadia.config.meta.dto.request;

import lombok.Data;

import java.util.List;

/**
 * @author: Wally.Wang
 * @date: 2019/12/20
 * @description:
 */
@Data
public class ImportConfigRequest {

	private List<ImportedConfig> configs;

	@Data
	public static class ImportedConfig {
		private String application;
		private String group;
		private String name;
		private String key;
		private String value;
		private String description;
	}

}
