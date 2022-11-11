package com.zbkj.crmeb.store.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单九宫格数据
 * @author: 零风
 * @CreateDate: 2022/5/18 15:24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StoreOrderTopItemResponse对象", description="订单九宫格数据")
public class StoreOrderTopItemResponse implements Serializable {

    private static final long serialVersionUID = -7583407020447771557L;

    @ApiModelProperty(value = "订单数量")
    private Integer count;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "微信支付金额")
    private BigDecimal weChatAmount;

    @ApiModelProperty(value = "余额支付")
    private BigDecimal yueAmount;

    @ApiModelProperty(value = "酒米兑换")
    private BigDecimal integralAmount;

    @ApiModelProperty(value = "支付宝支付")
    private BigDecimal zfbAmount;

}
