package com.neusoft.bookstore.shoppingcar.mapper;


import com.neusoft.bookstore.shoppingcar.model.ShoppingCar;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * @author Liang
 * @date 2020/5/12 9:27
 */
@Mapper
public interface ShoppingCarMapper {


    ShoppingCar findGoodsFromCar(ShoppingCar shoppingCar);

    int addShoppingCar(ShoppingCar shoppingCar);

    int updateShoppingCar(ShoppingCar shoppingCar);

    List<ShoppingCar> listGoodsFromCar(Integer userId);

    int deleteGoodsFromCar(ShoppingCar shoppingCar);

    void deleteCarById(Integer id);
}
