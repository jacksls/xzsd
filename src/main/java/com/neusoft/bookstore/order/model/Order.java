package com.neusoft.bookstore.order.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.neusoft.bookstore.util.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Liang
 * @date 2020/5/13 10:53
 */
@Data
public class Order extends BaseModel {

    @ApiModelProperty("下单人id")
    private Integer orderUserId;

    @ApiModelProperty("订单编码")
    private String orderCode;

    @ApiModelProperty("订单总金额")
    private BigDecimal orderAmount;

    @ApiModelProperty("订单状态(0:已下单  1:已发货  2:已完成未评价  3:已评价  4:已取消)")
    private Integer orderStatus;

    @ApiModelProperty("支付状态(0:未支付  1:已支付  2:退款中  3:已退款)")
    private Integer payStatus;

    @ApiModelProperty("商家编码")
    private String businessCode;



    @ApiModelProperty("订单列表模糊查询用户姓名（可以不传）")
    private String userName;

    @ApiModelProperty("订单列表模糊查询手机号（可以不传）")
    private String phone;

    @ApiModelProperty("订单状态名称")
    private String orderStatusName;

/*    @ApiModelProperty("下单开始时间")
    @JsonFormat(pattern="yyyy-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-dd HH:mm:ss")
    private Date orderStartTime;

    @ApiModelProperty("下单完成时间")
    @JsonFormat(pattern="yyyy-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-dd HH:mm:ss")
    private Date orderEndTime;*/

}
