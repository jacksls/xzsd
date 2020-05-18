package com.neusoft.bookstore.order.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neusoft.bookstore.customer.mapper.CustomerMapper;
import com.neusoft.bookstore.customer.model.Customer;
import com.neusoft.bookstore.goods.mapper.GoodsMapper;
import com.neusoft.bookstore.goods.model.Goods;
import com.neusoft.bookstore.order.mapper.OrderMapper;
import com.neusoft.bookstore.order.model.Order;
import com.neusoft.bookstore.order.model.OrderDetail;
import com.neusoft.bookstore.order.model.OrderVo;
import com.neusoft.bookstore.order.service.OrderService;
import com.neusoft.bookstore.shoppingcar.mapper.ShoppingCarMapper;
import com.neusoft.bookstore.shoppingcar.model.ShoppingCar;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.GoodsInfoException;
import com.neusoft.bookstore.util.ResponseVo;
import com.neusoft.bookstore.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author Liang
 * @date 2020/5/13 10:53
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private CustomerMapper customerMappper;

    @Autowired
    private ShoppingCarMapper shoppingCarMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseVo addOrder(List<OrderVo> orderVos) {
        /*
        1:登录
        2:数据校验
        3:业务逻辑校验: 商品状态，商品库存，用户余额
        4:创建订单:
            a:根据商家把订单分组:一个商家只能生成一个订单，一个订单可以包含多个商品
            b:计算订单总金额，每个商品总金额
        5:订单完成后，需要减少商品库存，减少用户余额，增加商品的销售数量，删除购物车
        */
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "创建失败");
        // 1:获取登录人
        if (orderVos == null || orderVos.size() <= 0) {
            responseVo.setMsg("未购买任何商品");
            return responseVo;
        }
        //取出登录账号
        String loginAccount = orderVos.get(0).getLoginAccount();
        Integer userId = null;
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(loginAccount);
        if (customerByRedis != null) {
            // redis已经保存
            userId = customerByRedis.getId();
        } else {
            //提示需要登录
            responseVo.setMsg("请登录后重试! ");
            return responseVo;
        }
        //先取出用户余额
        Customer customer = customerMappper.findCustomerById(userId);
        BigDecimal score = customer.getScore();

        //定义总价格所有订单的总价格
        BigDecimal orderAmountAll = new BigDecimal(0.0);


        //先找出有多少个商家找个该商家下 有多个商品
        // 存放商家和对应的商品集合  key：商家code，value :商家对应的商品集合
        //java8流式写法
        Map<String, List<OrderVo>> hashMap = orderVos.stream().collect(Collectors.groupingBy(OrderVo::getBusinessCode));
        for (Map.Entry<String, List<OrderVo>> entry : hashMap.entrySet()) {
            //取出商家编码
            String bussinessCode = entry.getKey();
            //购买的商品集合
            List<OrderVo> voList = entry.getValue();
            //计算订单总金额
            BigDecimal orderAmount = new BigDecimal(0.0);
            //生成订单编码
            String orderCode = StringUtil.getCommonCode(2);
            //处理订单详情
            for (int i = 0; i < voList.size(); i++) {
                //业务逻辑校验: 商品状态，商品库存，用户余额
                OrderVo orderVo = voList.get(i);
                //商品状态，校验是否下架
                Goods goodsBySkuCode = goodsMapper.findGoodsBySkuCode(orderVo.getSkuCode());
                if (goodsBySkuCode == null || goodsBySkuCode.getSkuStatus() != 0) {
                    //商品不满足购买条件
                    /*responseVo.setMsg("商品已经下架，无法购买！");
                    return responseVo;*/
                    throw new GoodsInfoException("商品已经下架，无法购买！");
                }
                //校验商品库存
                if (orderVo.getShopNum() > goodsBySkuCode.getStoreNum()) {
                    /*responseVo.setMsg("商品库存不足，无法购买！");
                    return responseVo;*/
                    throw new GoodsInfoException("商品库存不足，无法购买！");
                }
                //用户余额,先计算每种商品的总价格:售价*数量
                BigDecimal skuAmount = goodsBySkuCode.getSalePrice().multiply(new BigDecimal(orderVo.getShopNum()));
                //计算订单价格
                orderAmount = orderAmount.add(skuAmount);
                //总价
                orderAmountAll = orderAmountAll.add(skuAmount);
                //比较用户余额和总价
                if (orderAmountAll.compareTo(score) == 1) {
                    /*responseVo.setMsg("用户余额不足，无法购买！");
                    return responseVo;*/
                    throw new GoodsInfoException("用户余额不足，无法购买！");
                }
                //创建订单详情
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderCode(orderCode);
                orderDetail.setShopNum(orderVo.getShopNum());
                orderDetail.setSkuCode(orderVo.getSkuCode());
                orderDetail.setSkuAmount(skuAmount);
                orderDetail.setCreatedBy(loginAccount);
                orderMapper.addOrderDetail(orderDetail);

                //需要减少商品库存，增加商品的销售数量
                Map<Object, Object> map = new HashMap<>();
                map.put("skuCode", orderVo.getSkuCode());
                map.put("shopNum", orderVo.getShopNum());
                goodsMapper.updateGoodsStoreAndSaleNum(map);

                //删除购物车相应的商品
                ShoppingCar shoppingCar = new ShoppingCar();
                shoppingCar.setSkuCode(orderVo.getSkuCode());
                shoppingCar.setBusinessCode(bussinessCode);
                shoppingCar.setOrderUserId(userId);
                shoppingCarMapper.deleteGoodsFromCar(shoppingCar);
            }
            //订单表生成:
            Order order = new Order();
            order.setOrderUserId(userId);
            order.setBusinessCode(bussinessCode);
            order.setOrderAmount(orderAmount);
            order.setOrderStatus(0);
            order.setPayStatus(1);
            order.setOrderCode(orderCode);
            orderMapper.addOrder(order);
        }

        //减少用户余额
        BigDecimal frontScore = score.subtract(orderAmountAll);
        Map<Object, Object> map = new HashMap<>();
        map.put("frontScore", frontScore);
        map.put("userId", userId);
        customerMappper.updateScore(map);
        responseVo.setMsg("创建成功");
        responseVo.setSuccess(true);
        responseVo.setCode(ErrorCode.SUCCESS);
        return responseVo;
    }

    /**
     * 订单列表查询
     */
    @Override
    public ResponseVo listOrder(Order order) {
        /*
        订单列表查询带分页  模糊查询的条件:下单人姓名、订单编码、手机号、订单状态
        */
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功！");
        //判断分页
        if (order.getPageNum() != null && order.getPageSize() != null) {
            //pc端列表查询必需带分页，封装分页信息
            PageHelper.startPage(order.getPageNum(), order.getPageSize());
        }
        //直接查询
        List<Order> orderList = orderMapper.listOrder(order);
        if (order.getPageNum() != null && order.getPageSize() != null) {
            // pc端列表查询返回分页信息
            PageInfo<Order> pageInfo = new PageInfo<>(orderList);
            responseVo.setData(pageInfo);
        }
        return responseVo;
    }


    /**
     * 订单详情查询
     */
    @Override
    public ResponseVo findOrderByOrderCode(String orderCode) {
        /**
         * 订单详情查询,查询该订单下的商品信息。
         * 1:校验校验orderCode是否存在
         * 2:根据orderCode查询商品详情
         * 3:返回订单详情
         */

        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功! ");
        //校验skuCode 是否存在
        if (StringUtils.isEmpty(orderCode)) {
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            responseVo.setMsg("订单编码不能为空!");
            return responseVo;
        }
        //根据orderCode查询商品详情
        List<OrderDetail> orderDetailList = orderMapper.findOrderDetailByOrderCode(orderCode);

        if (orderDetailList != null && orderDetailList.size() > 0) {
            for (int i = 0; i < orderDetailList.size(); i++) {
                OrderDetail orderDetail = orderDetailList.get(i);
                //将图片一起返回
                String skuCode = orderDetail.getSkuCode();
                List<String> images = goodsMapper.findImagesBySkuCode(skuCode);
                orderDetail.setSkuImagesPath(images);
            }
        } else {
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            responseVo.setMsg("未查询到指定商品!");
            return responseVo;
        }
        //返回订单详情
        responseVo.setData(orderDetailList);
        return responseVo;
    }


    /**
     * app查询订单信息
     */
    @Override
    public ResponseVo findOrdersByloginAccount(String loginAccount, Integer payStatus) {
        /**
         * app查询订单信息：查询该用户所有的订单以及订单下的商品信息
         * 1:校验loginAccount、 payStatus 传值是否存在
         * 2:返回该用户所有的订单及订单下的商品信息（支付状态、订单编码、商品名称、售价商品购买数量、订单总金额、商品图片、创建时间）
         */
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "查询失败!");
        if (StringUtils.isEmpty(loginAccount) || StringUtils.isEmpty(payStatus)) {
            responseVo.setMsg("输入信息不能为空！");
            return responseVo;
        }
        //payStatus传值是否是0或1或2或3
        if (payStatus != 0 && payStatus != 1 && payStatus != 2 && payStatus != 3) {
            responseVo.setMsg("商品状态不正确！");
            return responseVo;
        }
        //根据loginAccount查询商品详情
        List<OrderDetail> ordersByloginAccount = orderMapper.findOrdersByloginAccount(loginAccount, payStatus);
        if (ordersByloginAccount != null && ordersByloginAccount.size() > 0) {
            for (int i = 0; i < ordersByloginAccount.size(); i++) {
                OrderDetail orderDetail = ordersByloginAccount.get(i);
                //将图片一起返回
                String skuCode = orderDetail.getSkuCode();
                List<String> images = goodsMapper.findImagesBySkuCode(skuCode);
                orderDetail.setSkuImagesPath(images);
            }
        } else {
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            responseVo.setMsg("未查询到指定商品!");
            return responseVo;
        }
        //返回该用户所有的订单及订单下的商品信息
        responseVo.setData(ordersByloginAccount);
        responseVo.setMsg("查询成功!");
        responseVo.setCode(ErrorCode.SUCCESS);
        responseVo.setSuccess(true);
        return responseVo;
    }
















    /*public static void main(String[] args) {

        OrderVo vo = new OrderVo("SKU1","BUSSIIN1",30,"YINS");
        OrderVo vo1 = new OrderVo("SKU2","BUSSIIN1",40,"YINS");
        OrderVo vo2 = new OrderVo("SKU3","BUSSIIN2",30,"YINS");
        OrderVo vo3 = new OrderVo("SKU4","BUSSIIN3",30,"YINS");

        List<OrderVo> orderVos = Arrays.asList(vo, vo1, vo2, vo3);

        *//*HashSet hashSet = new HashSet();
        for (int i = 0; i < orderVos.size(); i++) {
            hashSet.add(orderVos.get(i).getBusinessCode());
        }
        Iterator iterator = hashSet.iterator();
        Map<String,List<OrderVo>> hashMap = new HashMap();   // 存放 商家和对应的商品集合  key：商家code，value :商家对应的商品集合

        while (iterator.hasNext()){
            String next = (String) iterator.next(); // 商家code
            List<OrderVo> list = new ArrayList<OrderVo>();//商家对应的商品集合
            for (int i = 0; i < orderVos.size(); i++) {
                String businessCode = orderVos.get(i).getBusinessCode();
                if(next.equals(businessCode)){
                    //
                    list.add( orderVos.get(i));
                }
            }
            hashMap.put(next,list);
        }*//*

        //java8流式写码
        Map<String, List<OrderVo>> hashMap = orderVos.stream().collect(Collectors.groupingBy(OrderVo::getBusinessCode));

       *//* for (int i = 0; i < orderVos.size(); i++) {
            System.out.println(orderVos.get(i));
        }
        System.out.println("-------------------------");
        orderVos.stream().forEach(System.out::println);
        System.out.println("-------------------------");
        orderVos.stream().forEach(orderVo->{
            System.out.println(orderVo);
        });*//*


        System.out.println(hashMap);
    }*/
}