package com.zbkj.crmeb.front.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
* 预下单请求对象
* @author: 零风
* @CreateDate: 2021/12/2 15:31
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="PreOrderRequest对象", description="预下单请求对象")
public class PreOrderRequest {

    @ApiModelProperty(value = "预下单类型（“shoppingCart”：购物车下单，“buyNow”：立即购买，”again“： 再次购买，”video“: 视频号商品下单）")
    @NotBlank(message = "预下单类型不能为空")
    private String preOrderType;

    @ApiModelProperty(value = "订单详情列表")
    private List<PreOrderDetailRequest> orderDetails;

    @ApiModelProperty(value = "是否为零售商订单")
    private Boolean isRetailer=Boolean.FALSE;
    @ApiModelProperty(value = "零售商ID标识")
    private Integer retailerId;

}
