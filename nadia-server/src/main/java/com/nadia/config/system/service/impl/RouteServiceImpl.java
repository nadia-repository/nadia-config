package com.nadia.config.system.service.impl;

import com.nadia.config.system.domain.Menu;
import com.nadia.config.system.domain.MenuButton;
import com.nadia.config.system.dto.response.RouteResponse;
import com.nadia.config.system.repo.MenuButtonRepo;
import com.nadia.config.system.repo.MenuRepo;
import com.nadia.config.system.service.RouteService;
import com.nadia.config.user.domain.Role;
import com.nadia.config.user.domain.RoleMenu;
import com.nadia.config.user.domain.RoleMenuButton;
import com.nadia.config.user.repo.RoleMenuButtonRepo;
import com.nadia.config.user.repo.RoleRepo;
import com.nadia.config.user.repo.RoleMenuRepo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiang.shi
 * @date 2019-12-18 14:25
 */
@Service
public class RouteServiceImpl implements RouteService {
    @Resource
    private MenuRepo menuRepo;
    @Resource
    private RoleMenuRepo roleMenuRepo;
    @Resource
    private RoleMenuButtonRepo roleMenuButtonRepo;
    @Resource
    private RoleRepo roleRepo;
    @Resource
    private MenuButtonRepo menuButtonRepo;

    @Override
    public List<RouteResponse> routes() {
        List<Menu> menus = menuRepo.selectAll();

        List<MenuButton> buttons = menuButtonRepo.selectAll();

        List<Menu> parentMenus = menus.stream().filter(menu -> menu.getParentId() == 0).collect(Collectors.toList());

        List<RouteResponse> responses = new LinkedList<>();

        parentMenus.forEach(parentMenu -> {
            RouteResponse pm = new RouteResponse();
            pm.setMenuId(parentMenu.getId());
            pm.setKey(parentMenu.getId() + "");
            pm.setTitle("Menu: " + parentMenu.getName());
            pm.setType("menu");

            List<RouteResponse> children = new LinkedList<>();
            menus.forEach(menu -> {
                if (parentMenu.getId().equals(menu.getParentId())) {
                    RouteResponse child = new RouteResponse();
                    child.setMenuId(menu.getId());
                    child.setKey(menu.getId() + "");
                    child.setTitle("Menu: " + menu.getName());
                    child.setType("menu");
                    List<RouteResponse> bbs = buttons.stream().filter(button -> button.getMenuId().equals(menu.getId())).map(b -> {
                        RouteResponse br = new RouteResponse();
                        br.setMenuId(menu.getId());
                        br.setButtonId(b.getId());
                        br.setKey(menu.getId() + "|" + b.getId());
                        br.setTitle("Button: " + b.getName());
                        br.setType("button");
                        return br;
                    }).collect(Collectors.toList());
                    child.setChildren(bbs);
                    children.add(child);
                }
            });
            pm.setChildren(children);
            responses.add(pm);
        });
        return responses;
    }

    @Override
    public List<String> roleRoutes(String role) {
        List<String> keys = new LinkedList<>();
        Role roleInfo = roleRepo.selectByName(role);
        List<RoleMenu> roleMenus = roleMenuRepo.selectByRoleId(roleInfo.getId());
        roleMenus.forEach(roleMenu -> {
            keys.add(roleMenu.getMenuId() + "");
            List<RoleMenuButton> roleMenuButtons = roleMenuButtonRepo.selectByRoleMenuId(roleMenu.getId());
            roleMenuButtons.forEach(roleMenuButton -> {
                keys.add(roleMenu.getMenuId() + "|" + roleMenuButton.getButtonId());
            });

        });

        return keys;
    }
}
