package com.zbkj.crmeb.seckill.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* 秒杀商品信息表
* @author: 零风
* @CreateDate: 2021/10/21 15:35
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_seckill")
@ApiModel(value="StoreSeckill对象", description="秒杀商品信息表")
public class StoreSeckill implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "商品秒杀产品表id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商品id")
    private Integer productId;

    @ApiModelProperty(value = "推荐图")
    private String image;

    @ApiModelProperty(value = "轮播图")
    private String images;

    @ApiModelProperty(value = "活动标题")
    private String title;

    @ApiModelProperty(value = "简介")
    private String info;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "成本")
    private BigDecimal cost;

    @ApiModelProperty(value = "原价")
    private BigDecimal otPrice;

    @ApiModelProperty(value = "返多少积分")
    private Integer giveIntegral;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "单位名")
    private String unitName;

    @ApiModelProperty(value = "邮费")
    private BigDecimal postage;

    @ApiModelProperty(value = "内容")
    private String description;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date stopTime;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "秒杀状态 0=关闭 1=开启")
    private Integer status;

    @ApiModelProperty(value = "是否包邮")
    private Boolean isPostage;

    @ApiModelProperty(value = "删除 0未删除1已删除")
    private Boolean isDel;

    @ApiModelProperty(value = "最多秒杀几个")
    private Integer num;

    @ApiModelProperty(value = "显示")
    private Boolean isShow;

    @ApiModelProperty(value = "时间段ID")
    private Integer timeId;

    @ApiModelProperty(value = "运费模板ID")
    private Integer tempId;

    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    @ApiModelProperty(value = "体积")
    private BigDecimal volume;

    @ApiModelProperty(value = "限购总数 随销量递减")
    private Integer quota;

    @ApiModelProperty(value = "限购总数显示 不变")
    private Integer quotaShow;

    @ApiModelProperty(value = "规格 0单 1多")
    private Boolean specType;


}
