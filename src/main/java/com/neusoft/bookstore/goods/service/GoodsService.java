package com.neusoft.bookstore.goods.service;

import com.neusoft.bookstore.goods.model.Goods;
import com.neusoft.bookstore.util.ResponseVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Liang
 * @date 2020/5/6 20:34
 */
public interface GoodsService {

    ResponseVo listBusiness();

    ResponseVo uploadImage(MultipartFile file) throws IOException;

    ResponseVo findGoodsBySkuCode(String skuCode);

    ResponseVo findBusinessByCode(String businessCode);

    ResponseVo updateGoodsInfo(Goods goods);

    ResponseVo deleteGoods(String skuCode, String loginAccount);

    ResponseVo updateGoodStatus(String skuCode, String loginAccount, String status);

    ResponseVo addGoods(Goods goods);

    ResponseVo listGoods(Goods goods);
}
