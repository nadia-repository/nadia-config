package com.nadia.config.meta.service;

import com.nadia.config.meta.dto.request.ConfigRequest;
import com.nadia.config.meta.dto.request.ExportConfigRequest;
import com.nadia.config.meta.dto.request.ImportConfigRequest;
import com.nadia.config.meta.dto.request.ReceiveRequest;
import com.nadia.config.meta.dto.response.*;

import java.util.List;

/**
 * @author: Wally.Wang
 * @date: 2019/12/04
 * @description:
 */
public interface ConfigService {

	PageBean<ConfigResponse> getConfigs(ConfigRequest request);

	void addConfig(ConfigRequest request);

	void deleteConfigs(List<Long> ids);

	void updateConfig(ConfigRequest request);

	List<ConfigHistoryResponse> getConfigHistories(Long id);

	void publish(Long configId);

	void receive(ReceiveRequest request);

	void importConfigs(ImportConfigRequest request);

	List<ExportConfigResponse> exportConfigs(ExportConfigRequest request);

	List<ConfigInstanceResponse> getConfigInstances(Long configId);

}
