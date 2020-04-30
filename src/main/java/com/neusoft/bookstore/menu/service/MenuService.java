package com.neusoft.bookstore.menu.service;

import com.neusoft.bookstore.util.ResponseVo;
import com.neusoft.bookstore.menu.model.Menu;

/**
 * @author Liang
 * @date 2020/4/27 11:00
 */
public interface MenuService {

    ResponseVo addMenu(Menu menu);

    ResponseVo listMenuTree();

    ResponseVo findMenuByCode(String menuCode);

    ResponseVo updateMenuByCode(Menu menu);

    ResponseVo deleteMenuByCode(String menuCode);
}