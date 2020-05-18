package com.neusoft.bookstore.order.service;


import com.neusoft.bookstore.order.model.Order;
import com.neusoft.bookstore.order.model.OrderVo;
import com.neusoft.bookstore.util.ResponseVo;

import java.util.List;

/**
 * @author Liang
 * @date 2020/5/13 10:53
 */
public interface OrderService {

    ResponseVo addOrder(List<OrderVo> orderVos);

    ResponseVo listOrder(Order order);

    ResponseVo findOrderByOrderCode(String orderCode);

    ResponseVo findOrdersByloginAccount(String loginAccount, Integer payStatus);

}
