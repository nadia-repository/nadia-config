package com.nadia.config.system.repo;

import com.nadia.config.system.dao.MenuMapper;
import com.nadia.config.system.domain.Menu;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class MenuRepo {

    @Resource
    private MenuMapper menuMapper;

    public List<Menu> selectAll() {
        return menuMapper.selectByExample(null);
    }

    public Menu selectByPrimaryKey(Long id) {
        return menuMapper.selectByPrimaryKey(id);
    }
}
