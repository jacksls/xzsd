package com.neusoft.bookstore.shoppingcar.controller;

import com.neusoft.bookstore.shoppingcar.model.ShoppingCar;
import com.neusoft.bookstore.shoppingcar.service.ShoppingCarService;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseVo addShoppingCar(ShoppingCar shoppingCar){
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

}
