package com.nadia.config.system.repo;

import com.nadia.config.system.dao.MenuButtonMapper;
import com.nadia.config.system.domain.MenuButton;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class MenuButtonRepo {

    @Resource
    private MenuButtonMapper menuButtonMapper;

    public List<MenuButton> selectAll() {
        return menuButtonMapper.selectByExample(null);
    }
}
