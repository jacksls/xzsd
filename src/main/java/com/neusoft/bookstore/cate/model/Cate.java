package com.neusoft.bookstore.cate.model;

import com.neusoft.bookstore.util.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Liang
 * @date 2020/4/30 9:25
 */
@Data
public class Cate extends BaseModel {

    @ApiModelProperty("分类名称")
    private String cateName;

    @ApiModelProperty("分类编码")
    private String cateCode;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("父级分类编码")
    private String parentCateCode;

    @ApiModelProperty("创建子级时，前端点击分类的分类编码")
    private String frontCateCode;

}
