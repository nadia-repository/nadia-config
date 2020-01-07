package com.nadia.config.meta.controller;

import com.alibaba.fastjson.JSON;
import com.nadia.config.common.rest.RestBody;
import com.nadia.config.common.security.LockContext;
import com.nadia.config.common.util.CollectUtils;
import com.nadia.config.meta.dto.request.ConfigRequest;
import com.nadia.config.meta.dto.request.ExportConfigRequest;
import com.nadia.config.meta.dto.request.ImportConfigRequest;
import com.nadia.config.meta.service.ConfigService;
import com.nadia.config.validation.Addition;
import com.nadia.config.validation.Updation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: Wally.Wang
 * @date: 2019/12/04
 * @description:
 */
@Slf4j
@RestController
public class ConfigController {

	@Autowired
	private ConfigService configService;

	@RequestMapping(value = "/configs", method = RequestMethod.POST)
	public Object getConfigs(@RequestBody ConfigRequest configRequest){
		log.info("getConfigs request parameter: {}", JSON.toJSONString(configRequest));
		return RestBody.succeed(configService.getConfigs(configRequest));
	}

	@RequestMapping(value = "/config", method = RequestMethod.POST)
	public Object addConfig(@RequestBody @Validated(Addition.class) ConfigRequest configRequest) {
		log.info("addConfig request parameter: {}", configRequest);
		LockContext.lock(
			"config:addition:".
			concat(configRequest.getApplicationId().toString()).
			concat(configRequest.getGroupId().toString()).
			concat(configRequest.getKey().trim())
		);
		configService.addConfig(configRequest);
		return RestBody.succeed();
	}

	@RequestMapping(value = "/config", method = RequestMethod.PUT)
	public Object updateConfig(@RequestBody @Validated(Updation.class) ConfigRequest configRequest) {
		log.info("updateConfig request parameter: {}", configRequest);
		LockContext.lock("config:updation:".concat(configRequest.getId().toString()));
		configService.updateConfig(configRequest);
		return RestBody.succeed();
	}

	@RequestMapping(value = "/config", method = RequestMethod.DELETE)
	public Object deleteConfigs(@RequestBody List<Long> ids) {
		log.info("deleteConfigs request parameter: {}", ids);
		ids = CollectUtils.cleanAndFilter(ids);
		if (ids.size() == 0) {
			return RestBody.fail("Invaild config ids");
		}
		LockContext.lock("config:deletion:".concat(createLockKeyWhenDeletion(ids)));
		configService.deleteConfigs(ids);
		return RestBody.succeed();
	}

	private String createLockKeyWhenDeletion(List<Long> ids) {
		StringBuilder builder = new StringBuilder();
		ids.forEach(id -> {
			builder.append(id.toString()).append("-");
		});
		return builder.toString();
	}

	@RequestMapping(value = "/config/{id}/histories", method = RequestMethod.GET)
	public Object getConfigHistories(@PathVariable("id") Long id){
		log.info("getConfigHistories request parameter: {}", id);
		if (id <= 0) {
			log.warn("Invalid config id: {}", id);
			return RestBody.fail("Invalid config id");
		}
		return RestBody.succeed(configService.getConfigHistories(id));
	}

	@RequestMapping(value = "/config/{id}/publish", method = RequestMethod.PUT)
	public Object publishConfig(@PathVariable("id") Long id) {
		log.info("publishConfig request parameter: {}", id);
		if (id <= 0) {
			log.warn("Invalid config id: {}", id);
			return RestBody.fail("Invalid config id");
		}
		configService.publish(id);
		return RestBody.succeed();
	}

	@RequestMapping(value = "/config/{id}/instances", method = RequestMethod.GET)
	public Object getInstanceConfigs(@PathVariable("id") Long id) {
		log.info("getConfigInstances request parameter: {}", id);
		if (id <= 0) {
			log.warn("Invalid config id: {}", id);
			return RestBody.fail("Invalid config id");
		}
		return RestBody.succeed(configService.getConfigInstances(id));
	}

	@RequestMapping(value = "/config/export", method = RequestMethod.POST)
	public Object exportConfigs(@RequestBody ExportConfigRequest exportConfigRequest) {
		return RestBody.succeed(configService.exportConfigs(exportConfigRequest));
	}

	@RequestMapping(value = "/config/import", method = RequestMethod.POST)
	public Object importConfigs(@RequestBody ImportConfigRequest importConfigRequest) {
		if (CollectionUtils.isEmpty(importConfigRequest.getConfigs())) {
			return RestBody.fail("Invalid data from imported file");
		}
		configService.importConfigs(importConfigRequest);
		return RestBody.succeed();
	}

}
