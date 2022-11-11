package com.zbkj.crmeb.user.model;

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
 * 用户积分记录表
 * @author: 零风
 * @CreateDate: 2022/3/14 14:16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_integral_record")
@ApiModel(value="UserIntegralRecord-用户积分记录表", description="用户积分记录表")
public class UserIntegralRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "记录id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "关联id-orderNo,(sign,system默认为0）")
    private String linkId;

    @ApiModelProperty(value = "关联类型（order=订单,sign=签到,system=系统）、collection=积分收款、transfer=转账 " +
            "shareFriends=分享好友,sharePYQ=分享朋友圈,newUser=新用户注册,signSupp=补签," +
            "retailerRa=零售商返代理积分")
    private String linkType;

    @ApiModelProperty(value = "类型：1-增加，2-扣减")
    private Integer type;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "积分")
    private BigDecimal integral;

    @ApiModelProperty(value = "剩余")
    private BigDecimal balance;

    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "是否已读:0=未读,1=已读")
    private Integer isRead;

    @ApiModelProperty(value = "状态：1-订单创建、2-冻结期、3-完成、4-失效（订单退款）、5=待结算")
    private Integer status;

    @ApiModelProperty(value = "冻结期时间（天）")
    private Integer frozenTime;

    @ApiModelProperty(value = "解冻时间")
    private Long thawTime;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

}
