package com.nadia.config.meta.controller;

import com.alibaba.fastjson.JSON;
import com.nadia.config.common.rest.RestBody;
import com.nadia.config.meta.dto.request.GroupRequest;
import com.nadia.config.meta.dto.response.GroupResponse;
import com.nadia.config.meta.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: Wally.Wang
 * @date: 2019/12/06
 * @description:
 */
@Slf4j
@RestController
public class GroupController {

	@Autowired
	private GroupService groupService;

	@RequestMapping(value = "/{applicationId}/groups", method = RequestMethod.POST)
	public RestBody<List<GroupResponse>> getGroups(@RequestBody GroupRequest groupRequest) {
		log.info("getGroups request parameter: {}", JSON.toJSONString(groupRequest));
		return RestBody.succeed(groupService.getGroups(groupRequest));
	}

}
