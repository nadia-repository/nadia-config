package com.nadia.config.meta.dto.response;

import lombok.Data;

import java.util.List;

/**
 * @author: Wally.Wang
 * @date: 2020/07/28
 * @description:
 */
@Data
public class GroupedApplicationResponse {

	private String label;
	private List<ApplicationResponse> items;

}
