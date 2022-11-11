package com.zbkj.crmeb.user.response;

import com.zbkj.crmeb.front.response.OrderDataResponse;
import com.zbkj.crmeb.front.response.OrderWeekDataResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @program: crmeb
 * @description: (商户用户/商户/商铺/店铺/零售商..）-相关数据统计-响应类
 * @author: 零风
 * @create: 2021-07-15 10:48
 **/
@Data
public class UserMerIdDataResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "今日订单总数")
    private Integer dayOrderNum;
    @ApiModelProperty(value = "昨日订单总数")
    private Integer yesterdayOrderNum;
    @ApiModelProperty(value = "本月总订单总数")
    private Integer thisMonthOrderNum;

    @ApiModelProperty(value = "订单数量统计响应对象")
    private OrderDataResponse orderDataResponse;
    @ApiModelProperty(value = "本周订单数据统计响应对象")
    private List<OrderWeekDataResponse> orderWeekDataResponseList;

    @ApiModelProperty(value = "今日交易额")
    private BigDecimal dayGmv;
    @ApiModelProperty(value = "昨日交易额")
    private BigDecimal yesterdayGmv;
    @ApiModelProperty(value = "本月总交易额")
    private BigDecimal thisMonthGmv;

}
