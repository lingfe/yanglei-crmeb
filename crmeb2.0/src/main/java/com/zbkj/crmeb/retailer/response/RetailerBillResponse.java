package com.zbkj.crmeb.retailer.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 零售商账单表-响应类
 * @author: 零风
 * @CreateDate: 2021/12/16 9:23
 */
@Data
public class RetailerBillResponse {

    @ApiModelProperty(value = "零售商账单表ID标识")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "零售商表id标识")
    private Integer retailerId;
    @ApiModelProperty(value = "零售商名称")
    private String reName;

    @ApiModelProperty(value = "商品表ID标识")
    private Integer productId;
    @ApiModelProperty(value = "商品名称")
    private String storeName;
    @ApiModelProperty(value = "商品价格")
    private BigDecimal price;

    @ApiModelProperty(value = "账单金额")
    private BigDecimal billPrice;

    @ApiModelProperty(value = "状态: 0-无需结算、1-等待结算、2-已结算")
    private Integer status;

    @ApiModelProperty(value = "添加时间/账单生成时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间/结算时间")
    private Date updateTime;

}
