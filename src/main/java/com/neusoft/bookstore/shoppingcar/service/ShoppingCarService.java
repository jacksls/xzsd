package com.neusoft.bookstore.shoppingcar.service;


import com.neusoft.bookstore.shoppingcar.model.ShoppingCar;
import com.neusoft.bookstore.util.ResponseVo;

/**
 * @author Liang
 * @date 2020/5/12 9:26
 */
public interface ShoppingCarService {

    ResponseVo addShoppingCar(ShoppingCar shoppingCar);
}
