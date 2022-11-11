package com.zbkj.crmeb.cloudAccount.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * 云账户-银行卡-下单打款-（用户）请求实体
 */
@Data
@Builder
public class BankCardOrderRequest {

    @ApiModelProperty(value = "银行开户姓名(必填)")
    private String realName;
    @ApiModelProperty(value = "银行开户卡号(必填)")
    private String cardNo;
    @ApiModelProperty(value = "用户或联系人手机号(选填)")
    private String phoneNo;
    @ApiModelProperty(value = "银行开户身份证号(必填)")
    private String idCard;
    @ApiModelProperty(value = "打款金额(单位为元, 必填)")
    private String pay;
    @ApiModelProperty(value = "打款备注(选填，最大20个字符，一个汉字占2个字符，不允许特殊字符：' \" & | @ % * ( ) - : # ￥)")
    private String payRemark;
    @ApiModelProperty(value = "银行名称")
    private String bankName;

}
