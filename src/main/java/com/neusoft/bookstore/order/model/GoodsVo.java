package com.neusoft.bookstore.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Liang
 * @date 2020/5/26 18:35
 */
@Data
@AllArgsConstructor
public class GoodsVo {

    //新建订单时接收前端传值的参数

    private String skuCode;
    private String businessCode ;
    private Integer shopNum ;
}
