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
 * 用户佣金记录表
 * @author: 零风
 * @CreateDate: 2021/12/23 10:13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_brokerage_record")
@ApiModel(value="UserBrokerageRecord-用户佣金记录表", description="用户佣金记录表")
public class UserBrokerageRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "记录id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "关联id: 订单ID标识、提现记录ID标识")
    private String linkId;

    @ApiModelProperty(value = "关联类型: order=订单、extract=提现、yue=转余额,")
    private String linkType;

    @ApiModelProperty(value = "明细类型：1=订单佣金、2=提现申请、 3=提现拒绝退还佣金、4=佣金提现取消、5=佣金转余额")
    private Integer type;

    @ApiModelProperty(value = "收支类型：1-增加，2-扣减（提现）")
    private Integer pm;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "金额")
    private BigDecimal price;

    @ApiModelProperty(value = "余额")
    private BigDecimal balance;

    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "状态：1-订单创建，2-冻结期，3-完成，4-失效(订单退款)，5-提现申请")
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
