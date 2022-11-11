package com.zbkj.crmeb.retailer.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
* 零售商-产品代理表
* @author: 零风
* @CreateDate: 2021/11/29 9:50
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_retailer_pra")
@ApiModel(value="RetailerPra-对象", description="零售商-产品代理表")
public class RetailerPra implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "零售商产品代理表ID标识")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "零售商表ID标识")
    private Integer retailerId;

    @ApiModelProperty(value = "产品代理表ID标识")
    private Integer praId;

    @ApiModelProperty(value = "是否销售，0=下架，1=上架")
    private Boolean isSale;

    @TableLogic
    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

}
