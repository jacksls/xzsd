package com.neusoft.bookstore.goods.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neusoft.bookstore.customer.model.Customer;
import com.neusoft.bookstore.goods.mapper.GoodsMapper;
import com.neusoft.bookstore.goods.model.Goods;
import com.neusoft.bookstore.goods.model.GoodsImage;
import com.neusoft.bookstore.goods.service.GoodsService;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import com.neusoft.bookstore.util.StringUtil;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * @author Liang
 * @date 2020/5/6 20:33
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    // 设置好账号的ACCESS_KEY和SECRET_KEY
    @Value("${qiniu.accessKey}")
    private String ACCESS_KEY;

    @Value("${qiniu.secretKey}")
    private String SECRET_KEY;

    // 测试域名，只有30天有效期
    @Value("${qiniu.qiniuDomin}")
    private String QINIU_IMAGE_DOMAIN;

    // 要上传的空间
    @Value("${qiniu.bucketName}")
    private String bucketName;


    // 密钥配置
    public Auth getAuth() {
        return Auth.create(ACCESS_KEY, SECRET_KEY);
    }

    public UploadManager getUploadManager() {
        // 构造一个带指定Zone对象的配置类,不同的七云牛存储区域调用不同的zone
        Configuration cfg = new Configuration(Zone.zone2());
        // ...其他参数参考类注释
        return new UploadManager(cfg);
    }

    // 简单上传，使用默认策略，只需要设置上传的空间名就可以了
    public String getUpToken() {
        return getAuth().uploadToken(bucketName);
    }

    @Override
    public ResponseVo listBusiness() {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询全部商家");
        List<Map<String, Object>> mapList = goodsMapper.listBusiness();
        responseVo.setData(mapList);
        return responseVo;
    }

    @Override
    public ResponseVo uploadImage(MultipartFile file) {
        /*
        1:校验file是否存在
        2:校验图片格式 jpg、png、bif
        3:文件上传:
            a:图片需要重命名(图片格式不能改变)
            b:七牛云:需要一些认证参数信息
         */
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "上传失败！");
        //1:校验file是否存在
        if (file == null) {
            responseVo.setMsg("图片文件不存在，请检查后重试！");
            return responseVo;
        }
        //2:校验图片格式 jpg、png、gif
        //获取文件的原始名称
        String originalFilename = file.getOriginalFilename();
        //获取后缀名
        String type = FilenameUtils.getExtension(originalFilename);
        if (StringUtils.isEmpty(type)) {
            responseVo.setMsg("图片文件类型为空，请检查后重试！");
            return responseVo;
        }
        if ("JPG".equals(type.toUpperCase()) || "PNG".equals(type.toUpperCase()) || "GIF".equals(type.toUpperCase())) {
            //图片格式正确文件上传
            // a:图片需要重命名(图片格式不能改变)
            String finalName = System.currentTimeMillis() + "." + type;
            try {
                //调用put方法进行图片上传
                Response response = getUploadManager().put(file.getBytes(), finalName, getUpToken());
                //返回信息
                if (response.isOK() && response.isJson()) {
                    Object key = JSONObject.parseObject(response.bodyString()).get("key");
                    responseVo.setData(QINIU_IMAGE_DOMAIN + key);
                    responseVo.setSuccess(true);
                    responseVo.setMsg("上传成功");
                    responseVo.setCode(ErrorCode.SUCCESS);
                    return responseVo;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            responseVo.setMsg("请上传jpg、png、gif格式的图片！");
            return responseVo;
        }
        return responseVo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseVo addGoods(Goods goods) {
        /*1 :校验商品不能重复:同一个商家，同个分类，同个isbn是否有相同的商品名称
        2 :处理:售价和定价 (数据格式转换)
        3 :处理图片:页面多张图片。集合接收图片(地址保存数据库)
        4 :商品和图片两张表 :先增 商品在增图片 (事务) (通过skuCode)*/
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "新增失败！");
        // 获取登陆人
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(goods.getLoginAccount());
        String createBy = "";
        if (customerByRedis != null) {
            // redis已经保存
            createBy = customerByRedis.getUserAccount();
            goods.setCreatedBy(createBy);
        } else {
            //提示需要登录
            responseVo.setMsg("请登录后重试! ");
            return responseVo;
        }
        //校验商品不能重复:同一个商家，同个分类，同个isbn是否有相同的商品名称
        Goods goodsByDb = goodsMapper.findGoodsByCondition(goods);
        if (goodsByDb != null) {
            responseVo.setMsg("该商品已经存在，请勿重复添加! ");
            return responseVo;
        }
        //处理售价和定价 (数据格式转换)
        if (!StringUtils.isEmpty(goods.getFrontCostPrice())) {
            //处理定价
            BigDecimal costPrice = new BigDecimal(goods.getFrontCostPrice());
            goods.setCostPrice(costPrice);
        }
        if (!StringUtils.isEmpty(goods.getFrontSalePrice())) {
            //处理定价
            BigDecimal salePrice = new BigDecimal(goods.getFrontSalePrice());
            goods.setSalePrice(salePrice);
        }
        //新增商品 先生成skuCode
        String skuCode = StringUtil.getCommonCode(2);
        goods.setSkuCode(skuCode);

        int result = goodsMapper.addGoods(goods);
        if (result != 1) {
            responseVo.setMsg("新增失败!");
            return responseVo;
        }
        //处理商品图片
        List<String> images = goods.getImages();
        if (images != null && images.size() > 0) {
            //新商品图片
            for (int i = 0; i < images.size(); i++) {
                //新增商品图片
                GoodsImage goodsImage = new GoodsImage();
                goodsImage.setSkuCode(skuCode);
                goodsImage.setSkuImagesPath(images.get(i));
                goodsImage.setCreatedBy(createBy);
                goodsMapper.addImages(goodsImage);
            }
        }
        responseVo.setSuccess(true);
        responseVo.setMsg("新增成功！");
        responseVo.setCode(ErrorCode.SUCCESS);
        return responseVo;
    }

    @Override
    public ResponseVo listGoods(Goods goods) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功！");
        PageHelper.startPage(goods.getPageNum(), goods.getPageSize());
        List<Goods> goodsList = goodsMapper.listGoods(goods);
        if (goodsList != null && goodsList.size() > 0) {
            //查询图片
            for (int i = 0; i < goodsList.size(); i++) {
                //根据skuCode 查询图片
                Goods goodByDb = goodsList.get(i);
                List<String> goodImages = goodsMapper.findImagesBySkuCode(goodByDb.getSkuCode());
                goodByDb.setImages(goodImages);
            }
        }
        //返回
        PageInfo<Goods> pageInfo = new PageInfo<>(goodsList);
        responseVo.setData(pageInfo);
        return responseVo;
    }

    @Override
    public ResponseVo findGoodsBySkuCode(String skuCode) {
       /* 1:校验skuCode 是否存在
        2:根据skuCode 查询商品详情
        3:根据skuCode 查询商品图片
        4:将图片和商品一起返回*/
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功! ");
        //校验skuCode 是否存在
        if (StringUtils.isEmpty(skuCode)) {
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            responseVo.setMsg("商品编码不能为空!");
            return responseVo;
        }
        //根据skuCode 查询商品详情(补充一个接口根据商家编码查 询商家名称)
        Goods goods = goodsMapper.findGoodsBySkuCode(skuCode);
        if (goods == null) {
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            responseVo.setMsg("未查询到指定商品!");
            return responseVo;
        }
        //根据skuCode查询商品图片
        List<String> goodImages = goodsMapper.findImagesBySkuCode(skuCode);
        //4:将图片和商品一起返回
        goods.setImages(goodImages);
        responseVo.setData(goods);
        return responseVo;
    }

    @Override
    public ResponseVo findBusinessByCode(String businessCode) {
        /**
         * 1:校验businessCode 是否存在
         * 2 :查询返回Map
         */
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功! ");
        // 校验businessCode是否存在
        if (StringUtils.isEmpty(businessCode)) {
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            responseVo.setMsg("商家编码不能为空!");
            return responseVo;
        }
        //查询返回Map
        Map<String, Object> businessMap = goodsMapper.findBusinessByCode(businessCode);
        if (businessMap == null) {
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            responseVo.setMsg("未查询到指定商家! ");
            return responseVo;

        }
        responseVo.setData(businessMap);
        return responseVo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseVo updateGoodsInfo(Goods goods) {
        /*1 :需要校验登录
        2 :校验商品不能重复:同一个商家，同个分类。同个isbn是否有相同的商品名称(排除自己)
        3:处理:售价和定价 (数据格式转换)
        4:处理图片:页面多张图片。集合接收图片(地址保存数据库) (*)
        5:商品和图片两张表:修改商品在增图片(事务) (过skuCode)*/
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "修改失败!");
        //需要校验登录
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(goods.getLoginAccount());
        String createBy = "";
        if (customerByRedis != null) {
            // redis已经保存
            createBy = customerByRedis.getUserAccount();
            goods.setUpdatedBy(createBy);
        } else {
            //提示需要登录
            responseVo.setMsg("请登陆后重试! ");
            return responseVo;
        }
        //校验商品不能重复:同一个商家，同个分类，同个isbn是否有相同的商品名称(排除自己)
        Goods goodsByDb = goodsMapper.findGoodsByCondition(goods);
        if (goodsByDb != null) {
            responseVo.setMsg("该商品已经存在，请勿重复添加! ");
            return responseVo;
        }
        //处理售价和定价(数据格式转换)
        if (!StringUtils.isEmpty(goods.getFrontCostPrice())) {
            //处理定价
            BigDecimal costPrice = new BigDecimal(goods.getFrontCostPrice());
            goods.setCostPrice(costPrice);
        }
        if (!StringUtils.isEmpty(goods.getFrontSalePrice())) {
            //处理定价
            BigDecimal salePrice = new BigDecimal(goods.getFrontSalePrice());
            goods.setSalePrice(salePrice);
        }
        //修改商品信息
        int result = goodsMapper.updateGoodsInfo(goods);
        if (result != 1) {
            responseVo.setMsg("修改失败!");
            return responseVo;
        }
        //处理图片:页面多张图片，集合接收图片(地址保存数据库) (*)
        //先删除原来的图片
        goodsMapper.deleteGoodsImages(goods.getSkuCode());
        //新圳商品图片
        List<String> images = goods.getImages();
        if (images != null && images.size() > 0) {
            //新埋商品图片
            for (int i = 0; i < images.size(); i++) {
                GoodsImage goodsImage = new GoodsImage();
                goodsImage.setSkuCode(goods.getSkuCode());
                goodsImage.setSkuImagesPath(images.get(i));
                goodsImage.setCreatedBy(createBy);
                goodsMapper.addImages(goodsImage);
            }
        }
        responseVo.setMsg("修改成功！");
        responseVo.setSuccess(true);
        responseVo.setCode(ErrorCode.SUCCESS);
        return responseVo;
    }

    @Override
    public ResponseVo deleteGoods(String skuCode, String loginAccount) {
        /*1:先校验登陆
        2 :校验skuCode是否存在
        3 :根据skuCode删除商品信息(物理删除)
        4 :根据skuCode删除商品图片信息(物理删除)*/
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "修改失败!");
        if (StringUtils.isEmpty(skuCode) || StringUtils.isEmpty(loginAccount)) {
            responseVo.setMsg("信息不完整!");
            return responseVo;
        }
        //登录
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(loginAccount);
        if (customerByRedis == null) {
            //提示需要登陆
            responseVo.setMsg("请登陆后重试! ");
            return responseVo;
        }
        //根据skuCode删除商品信息(物理删除)
        int result = goodsMapper.deleteGoods(skuCode);
        if (result != 1) {
            return responseVo;
        }
        //根据skuCode删除商品图片信息(物理删除)
        goodsMapper.deleteGoodsImages(skuCode);
        responseVo.setMsg("删除成功! ");
        responseVo.setSuccess(true);
        responseVo.setCode(ErrorCode.SUCCESS);
        return responseVo;

    }

    @Override
    public ResponseVo updateGoodStatus(String skuCode, String loginAccount, String status) {
        /*
        1:先校验登录
        2 :校验skuCode、 stotus (传值是否是1或0)是否存在
        3 :根据skuCode去更新
        */
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "修改失败!");
        if (StringUtils.isEmpty(skuCode) || StringUtils.isEmpty(loginAccount)) {
            responseVo.setMsg("信息不完整!");
            return responseVo;
        }
        //登录
        String updatedBy = "";
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(loginAccount);
        if (customerByRedis == null) {
            //提示需要登录
            responseVo.setMsg("请登录后重试! ");
            return responseVo;
        } else {
            updatedBy = customerByRedis.getUserAccount();
        }
        //status (传值是否是1或0)
        if (!"0".equals(status) && !"1".equals(status)) {
            responseVo.setMsg("商品状态不正确! ");
            return responseVo;
        }
        int result = goodsMapper.updateGoodStatus(skuCode, updatedBy, status);
        if (result == 1) {
            responseVo.setMsg("修改成功! ");
            responseVo.setSuccess(true);
            responseVo.setCode(ErrorCode.SUCCESS);
        }
        return responseVo;
    }


}
