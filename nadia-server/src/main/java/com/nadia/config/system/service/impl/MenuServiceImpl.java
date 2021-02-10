package com.nadia.config.system.service.impl;

import com.nadia.config.system.dto.response.MenuResponse;
import com.nadia.config.system.repo.MenuRepo;
import com.nadia.config.system.service.MenuService;
import com.nadia.config.user.repo.RoleMenuRepo;
import com.nadia.config.user.service.RoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xiang.shi
 * @date 2019-12-16 19:33
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Resource
    private MenuRepo menuRepo;
    @Resource
    private RoleMenuRepo roleMenuRepo;
    @Resource
    private RoleService roleService;


    @Override
    public List<MenuResponse> roleMenus(List<String> roles) {
//        List<Role> roleInfos = roleService.getRoleByNames(roles);
//
//        RoleMenuCriteria roleMenuCriteria = new RoleMenuCriteria();
//        roleMenuCriteria.createCriteria().andRoleIdIn(roleInfos.stream().map(role -> role.getId()).collect(Collectors.toList()));
//
//        List<RoleMenu> roleMenus = roleMenuMapper.selectWithNameByExample(roleMenuCriteria);
//
//        List<RoleMenu> parentMenus = roleMenus.stream().filter(menu -> menu.getParentId() == 0).collect(Collectors.toList());
//
//        List<MenuResponse> responses = new LinkedList<>();
//        parentMenus.forEach(parentMenu -> {
//            MenuResponse response = new MenuResponse();
//            response.setName(parentMenu.getName());
//
//            List<MenuResponse> children = parentMenus.stream().filter(menu -> menu.getParentId().equals(parentMenu.getMenuId())).map(m -> {
//                MenuResponse child = new MenuResponse();
//                child.setName(m.getName());
//                return child;
//            }).collect(Collectors.toList());
//            response.setChildren(children);
//        });

        return null;
    }
}
