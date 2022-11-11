package com.zbkj.crmeb.retailer.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 零售商账单表
 * @author: 零风
 * @CreateDate: 2021/12/15 16:27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_retailer_bill")
@ApiModel(value="RetailerPra-对象", description="零售商-账单表")
public class RetailerBill {

    @ApiModelProperty(value = "零售商账单表ID标识")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "零售商表id标识")
    private Integer retailerId;

    @ApiModelProperty(value = "商品表ID标识")
    private Integer productId;

    @ApiModelProperty(value = "账单金额")
    private BigDecimal billPrice;

    @ApiModelProperty(value = "状态: 0-无需结算、1-等待结算、2-已结算")
    private Integer status;

    @ApiModelProperty(value = "添加时间/账单生成时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间/结算时间")
    private Date updateTime;

}
