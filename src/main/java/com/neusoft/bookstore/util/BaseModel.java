package com.neusoft.bookstore.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Liang
 * @date 2020/4/23 11:21
 */
@Data
public class BaseModel implements Serializable {

    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("作废标记 0:否(未删除) 1:是(已删除)")
    private Integer isDelete;

    @ApiModelProperty("创建人 (当前登录用户的用户账号如果不存在取固定值\"admin\" )")
    private String createdBy;

    @ApiModelProperty("创建时间")
    private Date createdTime;

    @ApiModelProperty("更新人 (当前登录用户的用户账号如果不存在取固定值\"admin\" )")
    private String updatedBy;

    @ApiModelProperty("更新时间")
    private Date updatedTime;

    @ApiModelProperty("当前页")
    private Integer pageNum;

    @ApiModelProperty("每页现实的条数")
    private Integer pageSize;

    @ApiModelProperty("获取从前台输入的用户账号或者是手机号")
    private String loginAccount;

    public void setImages(List<String> goodImages) {

    }
}
