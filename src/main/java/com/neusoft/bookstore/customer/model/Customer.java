package com.neusoft.bookstore.customer.model;

import com.neusoft.bookstore.util.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Liang
 * @date 2020/4/23 11:09
 */
@Data
public class Customer extends BaseModel {

    @ApiModelProperty("用户账户")
    private String userAccount;

    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty("用户性别  0:女  1:男  2:未知")
    private Integer userSex;

    @ApiModelProperty("用户手机号")
    private String phone;

    @ApiModelProperty("用户邮箱")
    private String email;

    @ApiModelProperty("身份证")
    private String idCard;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("积分")
    private BigDecimal score;

    @ApiModelProperty("登录源标记 ( 0:表示从app端注册和登陆  1:表示从pc端登陆和注册)")
    private Integer isAdmin;

    @ApiModelProperty("前台积分")
    private String frontScore;

    @ApiModelProperty("获取从前台输入的用户账号或者是手机号")
    private String loginAccount;


}
