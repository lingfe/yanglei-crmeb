package com.zbkj.crmeb.store.response;

import com.zbkj.crmeb.store.model.ServiceProvider;
import com.zbkj.crmeb.store.model.ServiceProviderTwolevel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 服务商表-数据响应类
 * @author: 零风
 * @CreateDate: 2022/5/10 10:39
 */
@Data
public class ServiceProviderDataResponse {

    @ApiModelProperty(value = "服务商信息")
    private ServiceProvider serviceProvider;
    @ApiModelProperty(value = "服务商二级商户信息")
    private ServiceProviderTwolevel serviceProviderTwolevel;

    @ApiModelProperty(value = "会员总数")
    private Integer userTotalNum;

    @ApiModelProperty(value = "今日订单总数")
    private Integer dayOrderNum;
    @ApiModelProperty(value = "昨日订单总数")
    private Integer yesterdayOrderNum;
    @ApiModelProperty(value = "本月总订单总数")
    private Integer thisMonthOrderNum;
    @ApiModelProperty(value = "总订单数")
    private Integer orderTotalNum;

    @ApiModelProperty(value = "今日交易额")
    private BigDecimal dayGmv;
    @ApiModelProperty(value = "昨日交易额")
    private BigDecimal yesterdayGmv;
    @ApiModelProperty(value = "本月总交易额")
    private BigDecimal thisMonthGmv;
    @ApiModelProperty(value = "总交易额")
    private BigDecimal totalGmv;

}
