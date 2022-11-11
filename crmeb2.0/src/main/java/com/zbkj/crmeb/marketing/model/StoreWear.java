package com.zbkj.crmeb.marketing.model;

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
 * 穿搭表
 * @author: 零风
 * @CreateDate: 2021/10/8 10:43
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_wear")
@ApiModel(value="StoreWear-对象", description="穿搭表")
public class StoreWear implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "穿搭表ID标识")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "主商品ID")
    private Integer productId;

    @ApiModelProperty(value = "主图")
    private String img;

    @ApiModelProperty(value = "穿搭名称")
    private String wearName;

    @ApiModelProperty(value = "穿搭商品ID，用英文逗号隔开")
    private String wearProductIds;

    @ApiModelProperty(value = "是否删除： 状态（0：否，1：是）")
    private Boolean isDel;

    @ApiModelProperty(value = "是否显示： 状态（0：否，1：是）")
    private Boolean isShow;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "是否展示在首页： 状态（0：否，1：是）")
    private Boolean isIndex;
}
