package com.nadia.config.meta.controller;

import com.nadia.config.common.rest.RestBody;
import com.nadia.config.meta.dto.response.ApplicationResponse;
import com.nadia.config.meta.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ApplicationController {

	@Autowired
	private ApplicationService applicationService;

	@RequestMapping(value = "/applications", method = RequestMethod.GET)
	public RestBody<List<ApplicationResponse>> getApplications() {
		return RestBody.succeed(applicationService.getApplications());
	}

}
