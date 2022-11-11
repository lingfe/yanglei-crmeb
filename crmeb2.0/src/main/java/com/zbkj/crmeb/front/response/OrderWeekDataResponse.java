package com.zbkj.crmeb.front.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: crmeb
 * @description: 用户订单-本周数据-响应类
 * @author: 零风
 * @create: 2021-07-15 17:06
 **/
@Data
public class OrderWeekDataResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "日期字符串")
    private String dateStr;

    @ApiModelProperty(value = "订单量")
    private Integer orderNum;

    @ApiModelProperty(value = "总消费钱数")
    private BigDecimal sumPrice;

}
