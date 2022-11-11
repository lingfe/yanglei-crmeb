package com.zbkj.crmeb.combination.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 拼团响应体
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
public class StorePinkResponse {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "拼团ID")
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "订单id 生成")
    private String orderId;

    @ApiModelProperty(value = "订单id  数据库")
    private Integer orderIdKey;

    @ApiModelProperty(value = "购买商品个数")
    private Integer totalNum;

    @ApiModelProperty(value = "购买总金额")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "拼团商品id")
    private Integer cid;

    @ApiModelProperty(value = "商品id")
    private Integer pid;

    @ApiModelProperty(value = "拼图总人数")
    private Integer people;

    @ApiModelProperty(value = "拼团商品单价")
    private BigDecimal price;

    @ApiModelProperty(value = "开始时间")
    private Long addTime;

    @ApiModelProperty(value = "结束时间")
    private Long stopTime;

    @ApiModelProperty(value = "团长id 0为团长")
    private Integer kId;

    @ApiModelProperty(value = "是否发送模板消息0未发送1已发送")
    private Boolean isTpl;

    @ApiModelProperty(value = "是否退款 0未退款 1已退款")
    private Boolean isRefund;

    @ApiModelProperty(value = "状态1进行中2已完成3未完成")
    private Integer status;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "是否虚拟拼团")
    private Boolean is_virtual;

    @ApiModelProperty(value = "几人参团")
    private Integer countPeople;

    @ApiModelProperty(value = "还剩几人成团")
    private Integer count;
}
