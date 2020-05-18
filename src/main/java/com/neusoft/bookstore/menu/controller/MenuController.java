package com.neusoft.bookstore.menu.controller;

import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import com.neusoft.bookstore.menu.model.Menu;
import com.neusoft.bookstore.menu.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Liang
 * @date 2020/4/27 10:54
 */
@Api("menu")
@RequestMapping("menu")
@RestController
public class MenuController {

    @Autowired
    private MenuService menuService;

    @ApiOperation(value = "新增菜单", notes = "新增菜单")
    @PostMapping("addMenu")
    private ResponseVo addMenu(@RequestBody Menu menu){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = menuService.addMenu(menu);
        } catch (Exception e) {
            //处理异常提示前台服务端有异常
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    @ApiOperation(value = "菜单树查询", notes = "菜单树查询")
    @GetMapping("listMenuTree")
    private ResponseVo listMenuTree(){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = menuService.listMenuTree();
        } catch (Exception e) {
            //处理异常提示前台服务端有异常
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    @ApiOperation(value = "菜单详情查询", notes = "菜单详情查询")
    @GetMapping("findMenuByCode")
    private ResponseVo findMenuByCode(String menuCode){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = menuService.findMenuByCode(menuCode);
        } catch (Exception e) {
            //处理异常提示前台服务端有异常
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    @ApiOperation(value = "修改菜单信息", notes = "修改菜单信息")
    @PostMapping("updateMenuByCode")
    private ResponseVo updateMenuByCode(@RequestBody Menu menu){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = menuService.updateMenuByCode(menu);
        } catch (Exception e) {
            //处理异常提示前台服务端有异常
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    @ApiOperation(value = "删除菜单信息", notes = "删除菜单信息")
    @GetMapping("deleteMenuByCode")
    private ResponseVo deleteMenuByCode(String menuCode){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = menuService.deleteMenuByCode(menuCode);
        } catch (Exception e) {
            //处理异常提示前台服务端有异常
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }
}
