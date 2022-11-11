package com.zbkj.crmeb.front.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 总代理-订单分别状态数量-响应类
 * @author: 零风
 * @CreateDate: 2022/4/11 15:35
 */
@Data
public class GeneralAgentOrderStateResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "待支付订单数量")
    private Integer unPaidCount;

    @ApiModelProperty(value = "待发货订单数量")
    private Integer unShippedCount;

    @ApiModelProperty(value = "待收货订单数量")
    private Integer receivedCount;

    @ApiModelProperty(value = "待评价订单数量")
    private Integer evaluatedCount;

    @ApiModelProperty(value = "已完成订单数量")
    private Integer completeCount;

    @ApiModelProperty(value = "退款订单数量(包含已退款、退款中)")
    private Integer refundCount;

    @ApiModelProperty(value = "支付订单总数")
    private Integer orderCount;

    @ApiModelProperty(value = "总消费钱数")
    private BigDecimal sumPrice;

}
