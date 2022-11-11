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
 * @program: crmeb
 * @description: 商品品牌信息表
 * @author: 零风
 * @create: 2021-06-23 10:42
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_brands")
@ApiModel(value="StoreBrands对象", description="品牌表")
public class StoreBrands extends StoreBrandsTest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

}
