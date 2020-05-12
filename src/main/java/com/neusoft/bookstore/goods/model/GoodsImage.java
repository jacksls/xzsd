package com.neusoft.bookstore.goods.model;

import com.neusoft.bookstore.util.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Liang
 * @date 2020/5/10 23:01
 */
@Data
public class GoodsImage extends BaseModel {

    @ApiModelProperty("商品编码")
    private String skuCode ;

    @ApiModelProperty ("图片路径")
    private String skuImagesPath;

}
