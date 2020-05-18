package com.neusoft.bookstore.order.model;

import com.neusoft.bookstore.util.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Liang
 * @date 2020/5/13 10:53
 */
@Data
public class OrderDetail extends BaseModel {

    @ApiModelProperty("订单编码")
    private String orderCode;

    @ApiModelProperty("商品编码")
    private String skuCode;

    @ApiModelProperty("购买数量")
    private Integer shopNum;

    @ApiModelProperty("该商品总金额")
    private BigDecimal skuAmount;




    @ApiModelProperty("商品名称")
    private String skuName;

    @ApiModelProperty("定价")
    private BigDecimal costPrice;

    @ApiModelProperty("售价")
    private BigDecimal salePrice;

    @ApiModelProperty("商品图片路径")
    private List<String> skuImagesPath;

}
