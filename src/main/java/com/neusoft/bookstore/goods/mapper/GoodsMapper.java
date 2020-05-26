package com.neusoft.bookstore.goods.mapper;


import com.neusoft.bookstore.goods.model.Goods;
import com.neusoft.bookstore.goods.model.GoodsImage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;


/**
 * @author Liang
 * @date 2020/5/6 20:32
 */
@Mapper
public interface GoodsMapper {


    List<Map<String, Object>> listBusiness();

    Goods findGoodsBySkuCode(String skuCode);

    List<String> findImagesBySkuCode(String skuCode);

    Map<String, Object> findBusinessByCode(String businessCode);

    Goods findGoodsByCondition(Goods goods);

    int updateGoodsInfo(Goods goods);

    void deleteGoodsImages(String skuCode);

    int deleteGoods(String skuCode);

    int updateGoodStatus(String skuCode, String updatedBy, String skuStatus);

    int addGoods(Goods goods);

    void addImages(GoodsImage goodsImage);

    List<Goods> listGoods(Goods goods);

    void updateGoodsStoreAndSaleNum(Map<Object, Object> map);
}
