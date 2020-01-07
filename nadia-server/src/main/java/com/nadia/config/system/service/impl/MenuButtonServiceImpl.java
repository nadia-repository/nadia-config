package com.nadia.config.system.service.impl;

import com.nadia.config.system.dto.request.MenuButtonRequest;
import com.nadia.config.system.dto.response.MenuButtonResponse;
import com.nadia.config.system.service.MenuButtonService;
import com.nadia.config.user.domain.Role;
import com.nadia.config.user.domain.RoleMenu;
import com.nadia.config.user.domain.RoleMenuButton;
import com.nadia.config.user.repo.RoleMenuButtonRepo;
import com.nadia.config.user.repo.RoleRepo;
import com.nadia.config.user.repo.RoleMenuRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xiang.shi
 * @date 2019-12-17 11:10
 */
@Service
@Slf4j
public class MenuButtonServiceImpl implements MenuButtonService {
    @Resource
    private RoleMenuRepo roleMenuRepo;
    @Resource
    private RoleRepo roleRepo;
    @Resource
    private RoleMenuButtonRepo roleMenuButtonRepo;

    @Override
    public MenuButtonResponse roleMeunButton(MenuButtonRequest menuButtonRequest) {
        MenuButtonResponse result = new MenuButtonResponse();
        if (CollectionUtils.isEmpty(menuButtonRequest.getRoles())) {
            return result;
        }
        List<Role> roleInfos = roleRepo.selectByNames(menuButtonRequest.getRoles());

        List<RoleMenu> roleMenus = roleMenuRepo.selectWithNameByRoleIds(roleInfos.stream().map(role -> role.getId()).collect(Collectors.toList()));

        List<RoleMenuButton> roleMenuButtons = roleMenuButtonRepo.selectByRoleMenuIds(roleMenus.stream().map(id -> id.getId()).collect(Collectors.toList()));

        List<RoleMenu> children = roleMenus.stream().filter(menu -> menu.getParentId() > 0).collect(Collectors.toList());
        Map<String, List<String>> bottonMap = new HashMap<>();
        children.forEach(child -> {
            List<String> buttons = roleMenuButtons.stream().filter(roleMenubutton -> child.getMenuId().equals(roleMenubutton.getMenuId())).map(button -> button.getName()).collect(Collectors.toList());
            bottonMap.put(child.getName(), buttons);
        });

        result.setMenus(roleMenus.stream().map(roleMenu -> roleMenu.getName()).collect(Collectors.toList()));
        result.setButtons(bottonMap);

        return result;
    }
}
