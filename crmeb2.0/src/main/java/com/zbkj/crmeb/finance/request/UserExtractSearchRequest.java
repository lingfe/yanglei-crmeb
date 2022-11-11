package com.zbkj.crmeb.finance.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 用户提现表-搜索请求类
 * @author: 零风
 * @CreateDate: 2022/4/26 15:57
 */
@Data
public class UserExtractSearchRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "搜索关键字")
    private String keywords;

    @ApiModelProperty(value = "bank = 银行卡 alipay = 支付宝 weixin = 微信")
    private String extractType;

    @ApiModelProperty(value = "状态: 0=申请中，1=成功/已提现，2=失败，3=挂单,4=退汇,5=取消,6=审核中,-1=未通过")
    private Integer status;

    @ApiModelProperty(value = "today,yesterday,lately7,lately30,month,year,/yyyy-MM-dd hh:mm:ss,yyyy-MM-dd hh:mm:ss/")
    private String dateLimit;

    @ApiModelProperty(value = "关联类型：1=普通提现、2、申请信用卡还款资金")
    private Integer linkType;


}
