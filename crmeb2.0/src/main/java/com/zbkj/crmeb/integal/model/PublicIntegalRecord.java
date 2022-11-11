package com.zbkj.crmeb.integal.model;

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
 * 公共积分记录表
 * @author: 零风
 * @CreateDate: 2021/10/18 11:56
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_public_integral_record")
@ApiModel(value="PublicIntegralRecord-公共积分记录表", description="公共积分记录表")
public class PublicIntegalRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "公共积分记录表ID标识")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "推广人id(表示这个公共积分是这个人推广的)")
    private Integer spreadUid;

    @ApiModelProperty(value = "关联id")
    private String linkId;

    @ApiModelProperty(value = "关联类型：（1=订单,2=系统随机奖励，3=推广奖励）")
    private Integer linkType;

    @ApiModelProperty(value = "积分类型：1-增加，2-扣减（提现）")
    private Integer type;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "积分")
    private BigDecimal integral;

    @ApiModelProperty(value = "积分余额")
    private BigDecimal integralBalance;

    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "状态：1-订单创建，2-冻结期，3-完成，4-失效（订单退款），5-提现申请,6-已分配，7-已存放(已放入公共积分库)")
    private Integer status;

    @ApiModelProperty(value = "冻结期时间（天）")
    private Integer frozenTime;

    @ApiModelProperty(value = "解冻时间")
    private Long thawTime;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "是否处理")
    private Boolean isHandle;

    @ApiModelProperty(value = "区域代理表ID标识,0=系统")
    private Integer raId;
}
