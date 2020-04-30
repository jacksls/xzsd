package com.neusoft.bookstore.menu.mapper;

import com.neusoft.bookstore.menu.model.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Liang
 * @date 2020/4/27 11:01
 */
@Mapper
public interface MenuMapper {

    void updateTypeAndUrlByCode(String parentMenuCode);

    int insertMenu(Menu menu);

    Menu findMenuByParentMenuCodeAndName(Menu menu);

    Menu findMenuByMenuCode(String parentMenuCode);

    List<Menu> listMenus();

    int updateMenuByCode(Menu menu);

    List<Menu> findChildMenus(String menuCode);

    int deleteMenuByCode(String menuCode);
}
