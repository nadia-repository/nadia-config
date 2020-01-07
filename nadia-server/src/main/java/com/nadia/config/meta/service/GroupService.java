package com.nadia.config.meta.service;

import com.nadia.config.meta.dto.request.GroupRequest;
import com.nadia.config.meta.dto.response.GroupResponse;

import java.util.List;

/**
 * @author: Wally.Wang
 * @date: 2019/12/06
 * @description:
 */
public interface GroupService {

	List<GroupResponse> getGroups(GroupRequest request);

}
