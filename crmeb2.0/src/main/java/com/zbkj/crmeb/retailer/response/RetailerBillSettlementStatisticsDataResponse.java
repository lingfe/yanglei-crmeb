package com.zbkj.crmeb.retailer.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 零售商账单-结算统计数据-响应类
 * @author: 零风
 * @CreateDate: 2021/12/16 10:37
 */
@Data
public class RetailerBillSettlementStatisticsDataResponse {

    @ApiModelProperty(value = "无需结算数量")
    private Integer noNum;
    @ApiModelProperty(value = "待结算数量")
    private Integer daiNum;
    @ApiModelProperty(value = "已结算数量")
    private Integer okNum;

    @ApiModelProperty(value = "总金额")
    private BigDecimal totalAmount;
    @ApiModelProperty(value = "总待结算金额")
    private BigDecimal totalDaiAmount;
    @ApiModelProperty(value = "总已结算金额")
    private BigDecimal totalOkAmount;

}
