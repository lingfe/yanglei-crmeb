package com.zbkj.crmeb.store.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.zbkj.crmeb.front.vo.OrderInfoDetailVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 订单购物详情表
 * @author: 零风
 * @CreateDate: 2022/3/29 13:58
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StoreOrderInfoVo对象", description="订单购物详情表")
public class StoreOrderInfoOldVo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "订单id")
    private Integer orderId;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "购买东西的详细信息")
    private OrderInfoDetailVo info;

    @ApiModelProperty(value = "唯一id")
    @TableField(value = "`unique`")
    private String unique;



}
