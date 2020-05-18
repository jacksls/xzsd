package com.neusoft.bookstore.shoppingcar.controller;

import com.neusoft.bookstore.shoppingcar.model.ShoppingCar;
import com.neusoft.bookstore.shoppingcar.service.ShoppingCarService;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Liang
 * @date 2020/5/12 9:27
 */
@Api("shoppingcar")
@RequestMapping("shoppingcar")
@RestController
public class ShoppingCarController {
    @Autowired
    private ShoppingCarService shoppingCarService;

    /**
     * 添加商品到购物车
     */
    @ApiOperation(value ="添加商品到购物车",notes = "添加商品到购物车")
    @PostMapping("addShoppingCar")
    public ResponseVo addShoppingCar(@RequestBody ShoppingCar shoppingCar){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = shoppingCarService.addShoppingCar(shoppingCar);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    /**
     * 购物车列表
     */
    @ApiOperation(value ="购物车列表",notes = "购物车列表")
    @GetMapping("findGoodsFromCar")
    public ResponseVo findGoodsFromCar(Integer userId, Integer pageSize, Integer pageNum){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = shoppingCarService.findGoodsFromCar(userId,pageSize,pageNum);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    /**
     * 删除购物车商品
     */
    @ApiOperation(value ="删除购物车商品",notes = "删除购物车商品")
    @GetMapping("deleteGoodsFromCar")
    public ResponseVo deleteGoodsFromCar(ShoppingCar shoppingCar){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = shoppingCarService.deleteGoodsFromCar(shoppingCar);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }
}
