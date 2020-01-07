package com.nadia.config.system.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author xiang.shi
 * @date 2019-12-17 11:06
 */
@Data
public class MenuButtonResponse {
    List<String> menus;
    Map<String, List<String>> buttons;
}
