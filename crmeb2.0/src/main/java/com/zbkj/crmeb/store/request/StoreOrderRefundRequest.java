package com.zbkj.crmeb.store.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * 订单退款-请求类
 * @author: 零风
 * @CreateDate: 2022/1/17 9:25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreOrderRefundRequest {

    @ApiModelProperty(value = "订单编号")
    @NotBlank(message = "订单编号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "退款金额")
    @DecimalMin(value = "0.00", message = "退款金额不能少于0.00")
    private BigDecimal amount;

    @ApiModelProperty(value = "订单ID标识")
    private Integer orderId;
}
