package com.neusoft.bookstore.shoppingcar.service.impl;


import com.neusoft.bookstore.customer.model.Customer;
import com.neusoft.bookstore.shoppingcar.mapper.ShoppingCarMapper;
import com.neusoft.bookstore.shoppingcar.model.ShoppingCar;
import com.neusoft.bookstore.shoppingcar.service.ShoppingCarService;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
 * @author Liang
 * @date 2020/5/12 9:25
 */
@Service
public class ShoppingCarServiceImpl implements ShoppingCarService {
    @Autowired
    private ShoppingCarMapper shoppingCarMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 添加商品到购物车
     */
    @Override
    public ResponseVo addShoppingCar(ShoppingCar shoppingCar) {
        /*
            1: 登陆
            2: 商品信息(商品便编码)、商家编码、购买人(app登陆人)， 数量默认是1不需要传
            3: 判断是从详情页加入购车还是购物车列表页加入购物车， 加入时校验商品库存
                从详情页加入购车:如果是第一次加入 生成一条新的记录，否则购物车数据量加1
                购物车列表页加入购物车:更新数量加1
         */
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "加入购物车失败");
        // 1:获取登陆人
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(shoppingCar.getLoginAccount());
        if (customerByRedis != null) {
            // redis已经保存
            shoppingCar.setOrderUserId(customerByRedis.getId());
            shoppingCar.setCreatedBy(customerByRedis.getUserAccount());
            shoppingCar.setUpdatedBy(customerByRedis.getUserAccount());
        } else {
            //提示需要登陆
            responseVo.setMsg("请登录后重试!");
            return responseVo;
        }
        //校验商品信息和商家信息
        if(StringUtils.isEmpty(shoppingCar.getSkuCode())||StringUtils.isEmpty(shoppingCar.getBusinessCode())){
            responseVo.setMsg("商品信息不完整!");
            return responseVo;
        }
        //判断是从详情页加入购车还是购物车列表页加入购物车
        ShoppingCar shoppingCarByDb = shoppingCarMapper.findGoodsFromCar(shoppingCar);
        if(shoppingCarByDb==null){
            //购物车里面没有记录

        }else {
            //购物车里面有记录，更新一条数据

        }
        return responseVo;
    }
}
