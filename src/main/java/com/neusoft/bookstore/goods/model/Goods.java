package com.neusoft.bookstore.goods.model;

import com.neusoft.bookstore.util.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Liang
 * @date 2020/5/7 9:25
 */
@Data
public class Goods extends BaseModel {

    @ApiModelProperty("商品名称")
    private String skuName;

    @ApiModelProperty("标准书号")
    private String isbn;

    @ApiModelProperty("一级商品分类")
    private String firstCateCode;

    @ApiModelProperty("二级商品分类")
    private String secondCateCode;

    @ApiModelProperty("广告词")
    private String skuAd;

    @ApiModelProperty("商品介绍")
    private String skuIntroduction;

    @ApiModelProperty("商家编码")
    private String businessCode;

    @ApiModelProperty("商品库存")
    private String storeNum;

    @ApiModelProperty("定价")
    private BigDecimal costPrice;

    @ApiModelProperty("售价")
    private BigDecimal salePrice;

    @ApiModelProperty("商品状态（0:在售  1:已下架  2:未发布）")
    private String skuStatus;

    @ApiModelProperty("商品编码")
    private String skuCode;

    @ApiModelProperty("商品的销售数量")
    private String saleNum;

    @ApiModelProperty("商品的商上架时间")
    private String saleTime;

    @ApiModelProperty("从前端获取定价")
    private String frontCostPrice;

    @ApiModelProperty("从前端获取销售价")
    private String frontSalePrice;

    @ApiModelProperty("商品图片")
    private List<String> images;

    @ApiModelProperty("一级分类名称")
    private String firstCateName;

    @ApiModelProperty("二级分类名称")
    private String secondCateName;

    @ApiModelProperty("商品名称")
    private String businessName;

    @ApiModelProperty("状态名称")
    private String skuStatusName;

}
