package com.zbkj.crmeb.integal.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *  公共积分记录-搜索请求类
 * @author: 零风
 * @CreateDate: 2021/10/18 14:06
 */
@Data
public class PublicIntegalRecordSearchRequest {

    @ApiModelProperty(value = "搜索关键字: 商品id或者名称")
    private String keywords;

    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "状态：0=全部，1-订单创建，2-冻结期，3-完成，4-失效（订单退款），5-提现申请，6-已分配，7-已存放(已放入公共积分库)")
    private Integer status;

    @ApiModelProperty(value = "关联类型：（1=订单,2=系统随机奖励，3=推广奖励）")
    private Integer linkType;

    @ApiModelProperty(value = "区域代理表ID标识,为空默认全部")
    private Integer raId;
}
