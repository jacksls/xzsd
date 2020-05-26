package com.neusoft.bookstore.order.model;

import com.neusoft.bookstore.util.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
public class OrderVo extends BaseModel {

    private  List<GoodsVo> goodsVos;
    private String loginAccount;

}
