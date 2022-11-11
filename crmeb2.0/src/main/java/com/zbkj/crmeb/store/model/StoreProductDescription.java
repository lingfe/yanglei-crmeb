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

/**
 * 商品描述表
 * @author: 零风
 * @CreateDate: 2022/3/1 10:28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_product_description")
@ApiModel(value="StoreProductDescription对象", description="商品描述表")
public class StoreProductDescription implements Serializable {

    private static final long serialVersionUID=1L;

    public StoreProductDescription() {
    }

    public StoreProductDescription(Integer productId, String description,Integer type) {
        this.productId = productId;
        this.description = description;
        this.type = type;
    }

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "商品详情")
    private String description;

    @ApiModelProperty(value = "商品类型，0=商品，1=秒杀，2=砍价，3=拼团，4=积分兑换商品")
    private Integer type;


}
