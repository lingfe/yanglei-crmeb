package com.zbkj.crmeb.front.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户佣金信息-响应类
 * @author: 零风
 * @CreateDate: 2022/1/5 15:57
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserCommissionResponse对象", description="推广佣金明细")
public class UserCommissionResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "昨天的佣金")
    private BigDecimal lastDayCount = BigDecimal.ZERO;

    @ApiModelProperty(value = "累计提现金额")
    private BigDecimal extractCount = BigDecimal.ZERO;

    @ApiModelProperty(value = "当前佣金")
    private BigDecimal commissionCount = BigDecimal.ZERO;

    @ApiModelProperty(value = "待结算佣金")
    private BigDecimal stayCettlementCommission;
}
