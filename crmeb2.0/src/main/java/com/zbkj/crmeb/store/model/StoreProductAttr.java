package com.zbkj.crmeb.store.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品属性表
 * @author: 零风
 * @CreateDate: 2022/3/29 13:44
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_product_attr")
@ApiModel(value="StoreProductAttr对象", description="商品属性表")
public class StoreProductAttr implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "attrId")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "属性名")
    private String attrName;

    @ApiModelProperty(value = "属性值")
    private String attrValues;

    @ApiModelProperty(value = "活动类型 0=商品，1=秒杀商品，2=砍价商品，3=拼团商品，4=积分兑换商品")
    private Integer type;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
