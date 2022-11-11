package com.zbkj.crmeb.front.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 计算订单价格请求对象
 * @author: 零风
 * @CreateDate: 2022/6/14 13:45
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OrderComputedPriceRequest对象", description="计算订单价格请求对象")
public class OrderComputedPriceRequest {

    @ApiModelProperty(value = "预下单订单号")
    @NotBlank(message = "预下单订单号不能为空")
    private String preOrderNo;

    @ApiModelProperty(value = "地址id")
    private Integer addressId;

    @ApiModelProperty(value = "优惠券id")
    private Integer couponId;

    @ApiModelProperty(value = "配送方式: 1=快递、2=门店自提、3=无需配送")
    @NotNull(message = "快递类型不能为空")
    private Integer shippingType;

    @ApiModelProperty(value = "是否使用积分(已不支持)")
    @NotNull(message = "是否使用积分不能为空")
    private Boolean useIntegral;

}
