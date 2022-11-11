package com.zbkj.crmeb.integal.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 公共积分库-响应类
 * @author: 零风
 * @CreateDate: 2021/10/18 14:12
 */
@Data
@Builder
public class PublicIntegralLibraryResponse {

    @ApiModelProperty(value = "总积分")
    private BigDecimal totalIntegral;

    @ApiModelProperty(value = "冻结中积分")
    private BigDecimal freezingIntegral;

    @ApiModelProperty(value = "可分配积分")
    private BigDecimal distributableIntegral;

    @ApiModelProperty(value = "已分配积分")
    private BigDecimal alreadyDistributionIntegral;

    @ApiModelProperty(value = "其他积分")
    private BigDecimal otherIntegral;

    @ApiModelProperty(value = "已存放(已放入公共积分库)")
    private BigDecimal waitDistributionIntegral;

}
