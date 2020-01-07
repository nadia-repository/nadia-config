package com.nadia.config.meta.dto.request;

/**
 * @author: Wally.Wang
 * @date: 2019/12/04
 * @description:
 */

public abstract class BaseRequest {

	protected Integer page = 1;
	protected Integer limit = 20;

	public Integer getPage() {
		return page;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

}
