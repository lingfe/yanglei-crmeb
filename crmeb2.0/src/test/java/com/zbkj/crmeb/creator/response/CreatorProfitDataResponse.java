package com.zbkj.crmeb.creator.response;

import com.zbkj.crmeb.creator.model.UserProfit;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 创作者收益数据-响应类
 * @author: 零风
 * @CreateDate: 2022/7/29 10:07
 */
@Data
public class CreatorProfitDataResponse {

    @ApiModelProperty(value = "昨日收益")
    private BigDecimal yestProfit;
    @ApiModelProperty(value = "本月收益")
    private BigDecimal thisMonthProfit;
    @ApiModelProperty(value = "可提现余额")
    private BigDecimal nowMoney;
    @ApiModelProperty(value = "本周收益记录")
    List<UserProfit> userProfitList;


}
