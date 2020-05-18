package com.neusoft.bookstore.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Liang
 * @date 2020/5/13 11:34
 */

    /*
    专门用来新建订单时接收前端传值的参数
    前端----xxxVO--》controller   xxxxDTO--》service    xxxxDO---》dao
    xxxxVO 前端传输层
    xxxxDTO service传输层
    XXXXDO dao传输层
    */

@Data
@NoArgsConstructor       //无参构造
@AllArgsConstructor
public class OrderVo {

    //新建订单时接收前端传值的参数

    private String skuCode;
    private String businessCode ;
    private Integer shopNum ;
    private String loginAccount;

}
