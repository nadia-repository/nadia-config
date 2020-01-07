package com.nadia.config.enums;

/**
 * @author: Wally.Wang
 * @date: 2019/12/05
 * @description:
 */
public enum ConfigStatusEnum {

	NEW("new"),
	EDITED("edited"),
	APPROVED("approved"),
	PUBLISHED("published"),
	REMOVING("removing"),
	DELETED("deleted"),
	INVALID("invalid"),
	;

	private String status;

	public String getStatus() {
		return status;
	}

	ConfigStatusEnum(String status) {
		this.status = status;
	}

}
