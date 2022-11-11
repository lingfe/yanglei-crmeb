package com.zbkj.crmeb.user.response;

import com.zbkj.crmeb.front.response.OrderWeekDataResponse;
import com.zbkj.crmeb.front.response.GeneralAgentOrderStateResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 用户总代理-相关统计数据-响应类
 * @author: 零风
 * @CreateDate: 2022/3/23 14:09
 */
@Data
public class UserGeneralAgentDataResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "今日交易额")
    private BigDecimal dayGmv;
    @ApiModelProperty(value = "昨日交易额")
    private BigDecimal yesterdayGmv;
    @ApiModelProperty(value = "本月总交易额")
    private BigDecimal thisMonthGmv;

    @ApiModelProperty(value = "今日订单总数")
    private Integer dayOrderNum;
    @ApiModelProperty(value = "昨日订单总数")
    private Integer yesterdayOrderNum;
    @ApiModelProperty(value = "本月总订单总数")
    private Integer thisMonthOrderNum;
    @ApiModelProperty(value = "订单数量统计响应对象")
    private GeneralAgentOrderStateResponse generalAgentOrderStateResponse;
    @ApiModelProperty(value = "本周订单数据统计响应对象")
    private List<OrderWeekDataResponse> orderWeekDataResponseList;

    @ApiModelProperty(value = "今日推广总人数")
    private Integer daySpreadNum;
    @ApiModelProperty(value = "昨日推广总人数")
    private Integer yesterSpreadNum;
    @ApiModelProperty(value = "本月推广总人数")
    private Integer thisMonthSpreadNum;
    @ApiModelProperty(value = "我所有推广总人数")
    private Integer myAllSpreadNum;

    @ApiModelProperty(value = "今日佣金总和")
    private BigDecimal dayCommission;
    @ApiModelProperty(value = "昨日佣金总和")
    private BigDecimal yesterCommission;
    @ApiModelProperty(value = "本月佣金总和")
    private BigDecimal thisMonthCommission;
    @ApiModelProperty(value = "我所有佣金总和")
    private BigDecimal myAllCommission;

}
