package com.zbkj.crmeb.front.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 得到-总代理用户-订单详情统计数据-响应类
 * @author: 零风
 * @CreateDate: 2022/3/23 14:09
 */
@Data
public class GeneralAgentOrderDetailsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "支付订单总数")
    private Integer orderCount;

    @ApiModelProperty(value = "本周订单数据统计响应对象")
    private List<OrderWeekDataResponse> orderWeekDataResponseList;

    @ApiModelProperty(value = "日期字符串集合")
    private List<String> dateList;

    @ApiModelProperty(value = "日期对应的订单数值集合")
    private List<Integer> dateValueList;

    @ApiModelProperty(value = "日期对应的金额值集合")
    private List<BigDecimal> bigDecimalList;

    @ApiModelProperty(value = "总金额")
    private BigDecimal totalAmount;

}
