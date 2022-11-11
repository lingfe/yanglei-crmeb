package com.zbkj.crmeb.marketing.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: crmeb
 * @description: 商城-营销-积分兑换商品表
 * @author: 零风
 * @create: 2021-07-01 13:58
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_marketing_integal_exchange_product")
@ApiModel(value="StoreIntegalShop-对象", description="营销-积分-兑换商品表")
public class StoreIntegalShop implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "积分兑换商品ID")
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

    @ApiModelProperty(value = "活动属性")
    private String attr;

    @ApiModelProperty(value = "简介")
    private String info;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "添加时间")
    private Long addTime;

    @ApiModelProperty(value = "推荐")
    private Boolean isHost;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;

    @ApiModelProperty(value = "是否包邮1是0否")
    private Boolean isPostage;

    @ApiModelProperty(value = "邮费")
    private BigDecimal postage;

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long stopTime;

    @ApiModelProperty(value = "订单有效时间(小时)")
    private Integer effectiveTime;

    @ApiModelProperty(value = "商品成本")
    private BigDecimal cost;

    @ApiModelProperty(value = "浏览量")
    private Integer browse;

    @ApiModelProperty(value = "单位名称")
    private String unitName;

    @ApiModelProperty(value = "运费模板ID")
    private Integer tempId;

    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    @ApiModelProperty(value = "体积")
    private BigDecimal volume;

    @ApiModelProperty(value = "原价")
    private BigDecimal otPrice;


    @ApiModelProperty(value = "是否开启日期，否=无时间限制")
    private Boolean isDate;//             `is_date` bigint(20) NULL DEFAULT NULL COMMENT '是否开启日期',

    @ApiModelProperty(value = "付款类型，1=积分+现金，2=纯积分")
    private Integer payType;//      `pay_type` int(11) NULL DEFAULT NULL COMMENT '付款类型，1=积分+现金，2=纯积分',

    @ApiModelProperty(value = "兑换积分")
    private Integer  integal;//     `exchange_integal` int(10) NULL DEFAULT NULL COMMENT '兑换积分',

    @ApiModelProperty(value = "兑换金额/兑换价格")
    private BigDecimal  price;//     `exchange_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '兑换金额',

    @ApiModelProperty(value = "兑换价设置,1=统一设置，2=独立设置")
    private Integer  method;//     `exchange_method` int(255) NULL DEFAULT NULL COMMENT '兑换价设置，1=统一设置，2=独立设置',

    @ApiModelProperty(value = "是否开启原价购买")
    private Boolean   isPurchase;//  `is_purchase` bigint(255) NULL DEFAULT NULL COMMENT '是否开启原价购买',

    @ApiModelProperty(value = "是否限购")
    private Boolean   isXiangou;//     `is_xiangou` bigint(255) NULL DEFAULT NULL COMMENT '是否限购',

    @ApiModelProperty(value = "每人限购数量")
    private Integer    xiangouNum;//    `xiangou_num` int(11) NULL DEFAULT NULL COMMENT '每人限购数量',

    @ApiModelProperty(value = "创建时间")
    private Date    crtDatetime;//    `crt_datetime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',

    @ApiModelProperty(value = "状态，1=未开始、2=进行中、3=已结束、4=已失效")
    private Integer   state;//     `state` int(255) NULL DEFAULT 1 COMMENT '状态，1=未开始、2=进行中、3=已结束、4=已失效',

    @ApiModelProperty(value = "是否显示")
    private Boolean isShow;

}
