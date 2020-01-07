package com.nadia.config.system.service;

import com.nadia.config.system.dto.request.MenuButtonRequest;
import com.nadia.config.system.dto.response.MenuButtonResponse;

/**
 * @author xiang.shi
 * @date 2019-12-17 11:09
 */
public interface MenuButtonService {
    MenuButtonResponse roleMeunButton(MenuButtonRequest menuButtonRequest);
}
