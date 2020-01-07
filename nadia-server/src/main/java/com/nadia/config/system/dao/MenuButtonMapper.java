package com.nadia.config.system.dao;

import com.nadia.config.system.domain.MenuButton;
import com.nadia.config.system.domain.MenuButtonCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MenuButtonMapper {
    long countByExample(MenuButtonCriteria example);

    int deleteByExample(MenuButtonCriteria example);

    int deleteByPrimaryKey(Long id);

    int insert(MenuButton record);

    int insertSelective(MenuButton record);

    List<MenuButton> selectByExample(MenuButtonCriteria example);

    MenuButton selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MenuButton record, @Param("example") MenuButtonCriteria example);

    int updateByExample(@Param("record") MenuButton record, @Param("example") MenuButtonCriteria example);

    int updateByPrimaryKeySelective(MenuButton record);

    int updateByPrimaryKey(MenuButton record);
}