package com.nadia.config.meta.service.impl;

import com.google.common.collect.Lists;
import com.nadia.config.meta.domain.Group;
import com.nadia.config.meta.dto.request.GroupRequest;
import com.nadia.config.meta.dto.response.GroupResponse;
import com.nadia.config.meta.repo.GroupRepo;
import com.nadia.config.meta.service.GroupService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: Wally.Wang
 * @date: 2019/12/06
 * @description:
 */
@Service
public class GroupServiceImpl implements GroupService {

	@Resource
    private GroupRepo groupRepo;

	@Override
	public List<GroupResponse> getGroups(GroupRequest request) {
		List<GroupResponse> groups = Lists.newArrayList();

		List<Group> list = groupRepo.selectByGroupRequest(request);
		if (CollectionUtils.isNotEmpty(list)) {
			list.forEach(group -> {
				GroupResponse item = new GroupResponse();
				item.setId(group.getId());
				item.setName(group.getName());
				groups.add(item);
			});
		}

		return groups;
	}
}
