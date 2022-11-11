package com.zbkj.crmeb.finance.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户充值表
 * @author: 零风
 * @CreateDate: 2021/12/21 15:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_recharge")
@ApiModel(value="UserRecharge对象", description="用户充值表")
public class UserRecharge implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "充值用户UID")
    private Integer uid;

    @ApiModelProperty(value = "订单号")
    private String orderId;

    @ApiModelProperty(value = "充值金额")
    private BigDecimal price;

    @ApiModelProperty(value = "购买赠送金额")
    private BigDecimal givePrice;

    @ApiModelProperty(value = "充值类型")
    private String rechargeType;

    @ApiModelProperty(value = "是否充值")
    private Boolean paid;

    @ApiModelProperty(value = "充值支付时间")
    private Date payTime;

    @ApiModelProperty(value = "充值时间")
    private Date createTime;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundPrice;


}
