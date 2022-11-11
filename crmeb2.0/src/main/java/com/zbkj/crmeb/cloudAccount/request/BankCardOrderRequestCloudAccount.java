package com.zbkj.crmeb.cloudAccount.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 云账户-银行卡-下单打款-请求(云账户)实体
 */
@Data
@Builder
public class BankCardOrderRequestCloudAccount {

    @ApiModelProperty(value = "商户订单号，由商户自定义，保持唯一性(必填)，64个英文字符以内")
    private String order_id;

    @ApiModelProperty(value = "商户代码(必填)")
    private String dealer_id;

    @ApiModelProperty(value = "代征主体(必填)")
    private String broker_id;

    @ApiModelProperty(value = "银行开户姓名(必填)")
    private String real_name;

    @ApiModelProperty(value = "银行开户卡号(必填)")
    private String card_no;

    @ApiModelProperty(value = "用户或联系人手机号(选填)")
    private String phone_no;

    @ApiModelProperty(value = "银行开户身份证号(必填)")
    private String id_card;

    @ApiModelProperty(value = "打款金额(单位为元, 必填)")
    private String pay;

    @ApiModelProperty(value = "打款备注(选填，最大20个字符，一个汉字占2个字符，不允许特殊字符：' \" & | @ % * ( ) - : # ￥)")
    private String pay_remark;

    @ApiModelProperty(value = "回调地址(选填，最大长度为200)")
    private String notify_url;

}
