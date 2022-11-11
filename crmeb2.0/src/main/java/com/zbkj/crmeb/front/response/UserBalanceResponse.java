package com.zbkj.crmeb.front.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户资金余额-响应对象类
 * @author: 零风
 * @CreateDate: 2022/1/17 14:14
 */
@Data
public class UserBalanceResponse implements Serializable {

    private static final long serialVersionUID=1L;

    public UserBalanceResponse(){}
    public UserBalanceResponse(BigDecimal nowMoney, BigDecimal recharge, BigDecimal orderStatusSum) {
        this.nowMoney = nowMoney;
        this.recharge = recharge;
        this.orderStatusSum = orderStatusSum;
    }

    @ApiModelProperty(value = "当前总资金")
    private BigDecimal nowMoney;

    @ApiModelProperty(value = "累计充值")
    private BigDecimal recharge;

    @ApiModelProperty(value = "累计消费")
    private BigDecimal orderStatusSum;

    @ApiModelProperty(value = "联盟商家累计收款")
    private BigDecimal allianceMerchantsSum;

    @ApiModelProperty(value = "累计提现")
    private BigDecimal extractSum;
}
