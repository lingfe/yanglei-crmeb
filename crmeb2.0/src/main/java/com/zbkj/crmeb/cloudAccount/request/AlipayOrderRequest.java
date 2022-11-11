package com.zbkj.crmeb.cloudAccount.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 云账户-支付宝-下单-请求实体
 */
@Data
@Builder
public class AlipayOrderRequest {

    @ApiModelProperty(value = "商户订单号，由商户保持唯一性(必填)，64个英文字符以内")
    private String order_id;
    @ApiModelProperty(value = "商户代码(必填)")
    private String dealer_id;
    @ApiModelProperty(value = "代征主体(必填)")
    private String broker_id;
    @ApiModelProperty(value = "姓名(必填)")
    private String real_name;
    @ApiModelProperty(value = "支付宝账号(必填)")
    private String card_no;
    @ApiModelProperty(value = "身份证号(必填)")
    private String id_card;
    @ApiModelProperty(value = "打款金额(单位为元, 必填)")
    private String pay;
    @ApiModelProperty(value = "打款备注(选填，最大20个字符，一个汉字占2个字符，不允许特殊字符：' \" & | @ % * ( ) - : # ￥)")
    private String pay_remark;
    @ApiModelProperty(value = "校验支付宝账户姓名，可填 Check=(校验)、NoCheck=(不校验)")
    private String check_name;

    @ApiModelProperty(value = "手机号(选填)")
    private String phone_no;
    @ApiModelProperty(value = "回调地址(选填，最大长度为200)")
    private String notify_url;

}
