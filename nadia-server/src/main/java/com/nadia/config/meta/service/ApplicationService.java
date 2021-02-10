package com.nadia.config.meta.service;

import com.nadia.config.meta.dto.response.ApplicationResponse;
import com.nadia.config.meta.dto.response.GroupedApplicationResponse;

import java.util.List;

/**
 * @author: Wally.Wang
 * @date: 2019/12/06
 * @description:
 */
public interface ApplicationService {

	List<ApplicationResponse> getApplications();

	List<GroupedApplicationResponse> getGroupedApplications();

}
