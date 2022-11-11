package com.zbkj.crmeb.store.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 商品属性详情表
 * @author: 零风
 * @CreateDate: 2022/3/29 13:48
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_product_attr_result")
@ApiModel(value="StoreProductAttrResult对象", description="商品属性详情表")
public class StoreProductAttrResult implements Serializable {

    private static final long serialVersionUID=1L;

    public StoreProductAttrResult() {
    }

    public StoreProductAttrResult(Integer id,Integer productId, String result, Integer changeTime, Integer type) {
        this.id = id;
        this.productId = productId;
        this.result = result;
        this.changeTime = changeTime;
        this.type = type;
    }

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "商品属性参数")
    private String result;

    @ApiModelProperty(value = "上次修改时间")
    private Integer changeTime;

    @ApiModelProperty(value = "活动类型 0=商品，1=秒杀，2=砍价，3=拼团，4=积分兑换商品")
    private Integer type;


}
