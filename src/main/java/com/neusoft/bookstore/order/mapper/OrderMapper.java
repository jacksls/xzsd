package com.neusoft.bookstore.order.mapper;


import com.neusoft.bookstore.order.model.Order;
import com.neusoft.bookstore.order.model.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author Liang
 * @date 2020/5/13 10:53
 */
@Mapper
public interface OrderMapper {


    void addOrderDetail(OrderDetail orderDetail);

    void addOrder(Order order);

    List<Order> listOrder(Order order);

    List<OrderDetail> findOrderDetailByOrderCode(String orderCode);

    List<OrderDetail> findOrdersByloginAccount(@Param("loginAccount") String loginAccount, @Param("payStatus")Integer payStatus);

}
