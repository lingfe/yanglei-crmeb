package com.zbkj.crmeb.user.response;

import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.front.response.SpreadCommissionDetailResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 总代理用户-佣金数据-响应类
 * @author: 零风
 * @CreateDate: 2022/3/23 14:10
 */
@Data
public class UserGeneralAgentCommissionDataResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "佣金明细-分页对象")
    PageInfo<SpreadCommissionDetailResponse> pageInfoCommissionDetail;

    @ApiModelProperty(value = "今日佣金总和")
    private BigDecimal dayCommission;
    @ApiModelProperty(value = "昨日佣金总和")
    private BigDecimal yesterCommission;
    @ApiModelProperty(value = "本月佣金总和")
    private BigDecimal thisMonthCommission;
    @ApiModelProperty(value = "我所有佣金总和")
    private BigDecimal myAllCommission;
    @ApiModelProperty(value = "累计收益佣金")
    private BigDecimal leijiShouyiCommissio;
    @ApiModelProperty(value = "累计提现佣金")
    private BigDecimal leijieTiXianCommissio;

}
