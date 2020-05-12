package com.neusoft.bookstore.picture.model;

import com.neusoft.bookstore.util.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Liang
 * @date 2020/5/11 9:27
 */
@Data
public class Picture extends BaseModel {

    @ApiModelProperty("图片路轻")
    private String picUrL;

    @ApiModelProperty("图片状态  1:启用  2:禁用")
    private Integer picStatus;

    @ApiModelProperty("图片路轻")
    private String picStatusName;
}
