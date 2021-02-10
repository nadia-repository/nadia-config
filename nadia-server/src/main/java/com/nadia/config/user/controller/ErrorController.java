package com.nadia.config.user.controller;

import com.nadia.config.common.rest.RestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Wally.Wang
 * @date: 2020/07/23
 * @description:
 */
@Slf4j
@RestController
@RequestMapping("/error")
public class ErrorController {

	@RequestMapping(value = "/session/empty")
	public RestBody<String> empty(){
		RestBody<String> response = new RestBody<>();
		response.setErrorCode(1L);
		response.setMsg("Please login before operation.");
		return response;
	}

	@RequestMapping(value = "/session/expired")
	public RestBody<String> invalid(){
		RestBody<String> response = new RestBody<>();
		response.setErrorCode(1L);
		response.setMsg("Please login as session has expired.");
		return response;
	}

}
