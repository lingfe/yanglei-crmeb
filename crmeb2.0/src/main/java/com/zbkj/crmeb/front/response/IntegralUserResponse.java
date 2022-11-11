package com.zbkj.crmeb.front.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户积分响应对象
 * @author: 零风
 * @CreateDate: 2021/12/27 10:12
 */
@Data
public class IntegralUserResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户剩余积分")
    private BigDecimal integral;

    @ApiModelProperty(value = "累计总收入积分")
    private BigDecimal sumIntegral;

    @ApiModelProperty(value = "累计总消费积分")
    private BigDecimal deductionIntegral;

    @ApiModelProperty(value = "冻结中积分")
    private BigDecimal frozenIntegral;

    @ApiModelProperty(value = "积分规则")
    private String integralRule;

    @ApiModelProperty(value = "可用积分")
    private BigDecimal availableIntegral;//2021-10-21

    @ApiModelProperty(value = "待结算积分")
    private BigDecimal djsIntegral;//2021-12-27

    @ApiModelProperty(value = "消费额度")
    private BigDecimal quota;

}
