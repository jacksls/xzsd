package com.neusoft.bookstore.shoppingcar.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neusoft.bookstore.customer.model.Customer;
import com.neusoft.bookstore.goods.mapper.GoodsMapper;
import com.neusoft.bookstore.goods.model.Goods;
import com.neusoft.bookstore.shoppingcar.mapper.ShoppingCarMapper;
import com.neusoft.bookstore.shoppingcar.model.ShoppingCar;
import com.neusoft.bookstore.shoppingcar.service.ShoppingCarService;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;


/**
 * @author Liang
 * @date 2020/5/12 9:25
 */
@Service
public class ShoppingCarServiceImpl implements ShoppingCarService {
    @Autowired
    private ShoppingCarMapper shoppingCarMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 添加商品到购物车
     */
    @Override
    public ResponseVo addShoppingCar(ShoppingCar shoppingCar) {
        /*
            1: 登录
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
        if (StringUtils.isEmpty(shoppingCar.getSkuCode()) || StringUtils.isEmpty(shoppingCar.getBusinessCode())) {
            responseVo.setMsg("商品信息不完整!");
            return responseVo;
        }
        //判断是从详情页加入购车还是购物车列表页加入购物车
        ShoppingCar shoppingCarByDb = shoppingCarMapper.findGoodsFromCar(shoppingCar);
        if (shoppingCarByDb == null) {
            //购物车里面没有记录
            //校验库存
            Goods goodsBySkuCode = goodsMapper.findGoodsBySkuCode(shoppingCar.getSkuCode());
            if (goodsBySkuCode.getStoreNum() <= 0) {
                responseVo.setMsg("商品库存不足，无法添加！");
                return responseVo;
            }
            int result = shoppingCarMapper.addShoppingCar(shoppingCar);
            if (result == 1) {
                responseVo.setMsg("加入购物车成功！");
                responseVo.setSuccess(true);
                responseVo.setCode(ErrorCode.SUCCESS);
                return responseVo;
            }
        } else {
            //购物车里面有记录，更新一条数据
            Goods goods = goodsMapper.findGoodsBySkuCode(shoppingCar.getSkuCode());
            //判断是从商品详情中添加还是列表的添加和减少
            //依据就是商品详情: shopNum传null  列表的添加和减少  shopNum就是原本购物车中商品数量加1或者减1后的值
            if (shoppingCar.getShopNum() == null) {
                //从详情页默认加1，当前购物车中已经有的商品数量
                Integer shopNum = shoppingCarByDb.getShopNum();
                //比较库存
                if ((shopNum + 1) > goods.getStoreNum()) {
                    responseVo.setMsg("商品库存不足，无法添加！");
                    return responseVo;
                } else {
                    //可以添加
                    shoppingCar.setShopNum(shopNum + 1);
                }
            } else {
                //购物车列表
                if (shoppingCar.getShopNum() > goods.getStoreNum()) {
                    responseVo.setMsg("商品库存不足，无法添加! ");
                    return responseVo;
                }
            }
            //修改商品库存
            int result = shoppingCarMapper.updateShoppingCar(shoppingCar);
            if (result == 1) {
                responseVo.setSuccess(true);
                responseVo.setCode(ErrorCode.SUCCESS);
                responseVo.setMsg("加入购物车成功! ");
                return responseVo;
            }
        }
        return responseVo;
    }

    @Override
    public ResponseVo findGoodsFromCar(Integer userId, Integer pageSize, Integer pageNum) {
        //分页
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功! ");
        if (pageSize != null && pageNum != null) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<ShoppingCar> shoppingCarList = shoppingCarMapper.listGoodsFromCar(userId);
        //加载对应的商品图片  遍历查找
        if (shoppingCarList != null && shoppingCarList.size() > 0) {
            for (int i = 0; i < shoppingCarList.size(); i++) {
                //根据skuCode查询图片
                ShoppingCar car = shoppingCarList.get(i);
                List<String> goodImages = goodsMapper.findImagesBySkuCode(car.getSkuCode());
                car.setImages(goodImages);
            }
        }
        //返回
        if (pageSize != null && pageNum != null) {
            PageInfo<ShoppingCar> pageInfo = new PageInfo<>(shoppingCarList);
            responseVo.setData(pageInfo);
        } else {
            responseVo.setData(shoppingCarList);

        }
        return responseVo;
    }

    @Override
    public ResponseVo deleteGoodsFromCar(ShoppingCar shoppingCar) {

         /*
         1:登录
         2:校验必传数据
         3:删除根据商品編码，商家编码,购买人id (物理删除)
         */
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "删除失败! ");
        //1:登录
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(shoppingCar.getLoginAccount());
        if (customerByRedis != null) {
            // redis已经保存
            shoppingCar.setOrderUserId(customerByRedis.getId());
        } else {
            //提示需要登陆
            responseVo.setMsg("请登录后重试! ");
            return responseVo;
        }
        //2:校验必传数据
        //校验商品信息和商家信息
        if (StringUtils.isEmpty(shoppingCar.getSkuCode()) || StringUtils.isEmpty(shoppingCar.getBusinessCode())) {
            responseVo.setMsg("商品信息不完整! ");
            return responseVo;
        }
        int result = shoppingCarMapper.deleteGoodsFromCar(shoppingCar);
        if (result == 1) {
            responseVo.setSuccess(true);
            responseVo.setCode(ErrorCode.SUCCESS);
            responseVo.setMsg("删除成功! ");
            return responseVo;
        }
        return responseVo;
    }
}