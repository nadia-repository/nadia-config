package com.nadia.config.meta.dto.response;

import com.nadia.config.meta.dto.request.BaseRequest;
import lombok.Data;

import java.util.List;

/**
 * @author: Wally.Wang
 * @date: 2019/12/05
 * @description:
 */
@Data
public class PageBean<T> {

	private Integer page;
	private Integer limit;
	private Long total;
	private List<T> items;

	public PageBean(BaseRequest request, List<T> items, long total) {
		this.page = request.getPage();
		this.limit = request.getLimit();
		this.total = total;
		this.items = items;
	}

}
