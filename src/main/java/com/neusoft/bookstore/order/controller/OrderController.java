package com.neusoft.bookstore.order.controller;

import com.neusoft.bookstore.order.model.Order;
import com.neusoft.bookstore.order.model.OrderVo;
import com.neusoft.bookstore.order.service.OrderService;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.GoodsInfoException;
import com.neusoft.bookstore.util.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Liang
 * @date 2020/5/13 10:53
 */
@Api("order")
@RequestMapping("order")
@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 订单创建
     */
    @ApiOperation(value ="订单创建",notes = "订单创建")
    @PostMapping("addOrder")
    public ResponseVo addOrder(@RequestBody List<OrderVo> orderVos){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = orderService.addOrder(orderVos);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            ////处理异常，提示前台服务端有异常
            throw e;
        }
        return responseVo;
    }

    /**
     * 订单列表查询
     */
    @ApiOperation(value ="订单列表查询",notes = "订单列表查询")
    @PostMapping("listOrder")
    public ResponseVo listOrder( Order order){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = orderService.listOrder(order);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            ////处理异常，提示前台服务端有异常
            throw e;
        }
        return responseVo;
    }

    /**
     * 订单详情查询
     */
    @ApiOperation(value ="订单详情查询",notes = "订单详情查询")
    @PostMapping("findOrderByOrderCode")
    public ResponseVo findOrderByOrderCode(String orderCode){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = orderService.findOrderByOrderCode(orderCode);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            ////处理异常，提示前台服务端有异常
            throw e;
        }
        return responseVo;
    }

    /**
     * app查询所有订单信息
     */
    @ApiOperation(value ="app查询所有订单信息",notes = "app查询所有订单信息")
    @PostMapping("findOrdersByloginAccount")
    public ResponseVo findOrdersByloginAccount(String loginAccount, Integer payStatus){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = orderService.findOrdersByloginAccount(loginAccount,payStatus);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            ////处理异常，提示前台服务端有异常
            throw e;
        }
        return responseVo;
    }
}
