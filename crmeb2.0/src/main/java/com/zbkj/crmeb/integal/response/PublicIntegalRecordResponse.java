package com.zbkj.crmeb.integal.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 公共积分记录-响应类
 * @author: 零风
 * @CreateDate: 2021/11/5 10:45
 */
@Data
public class PublicIntegalRecordResponse {

    @ApiModelProperty(value = "公共积分记录表ID标识")
    private Integer id;

    @ApiModelProperty(value = "用户uid")
    private Integer uid;
    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "关联id")
    private String linkId;

    @ApiModelProperty(value = "关联类型：（1=订单,2=提现，3=转余额）")
    private Integer linkType;

    @ApiModelProperty(value = "积分类型：1-增加，2-扣减（提现），3-扣减（消费）")
    private Integer type;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "积分")
    private BigDecimal integral;

    @ApiModelProperty(value = "积分余额")
    private BigDecimal integralBalance;

    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "状态：1-订单创建，2-冻结期，3-完成，4-失效（订单退款），5-提现申请，6-已分配，7-已存放(已放入公共积分库)")
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

}
