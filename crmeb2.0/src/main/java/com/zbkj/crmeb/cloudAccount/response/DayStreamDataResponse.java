package com.zbkj.crmeb.cloudAccount.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;

/**
 * @program: crmeb
 * @description: 云账户-日流水记录-响应类
 * @author: 零风
 * @create: 2021-08-19 15:14
 **/
@Builder
public class DayStreamDataResponse {

    /** 商户ID */
    @ApiModelProperty(value = "商户ID")
    private String dealer_id;
    /** 服务主体ID */
    @ApiModelProperty(value = "服务主体ID")
    private String broker_id;
    /** 商户订单号 */
    @ApiModelProperty(value = "商户订单号")
    private String order_id;
    /** 流水号 */
    @ApiModelProperty(value = "流水号")
    private String ref;
    /** 综合服务主体名称 */
    @ApiModelProperty(value = "综合服务主体名称")
    private String broker_product_name;
    /** 商户名称 */
    @ApiModelProperty(value = "商户名称")
    private String dealer_product_name;
    /** 业务订单流水号 */
    @ApiModelProperty(value = "业务订单流水号")
    private String biz_ref;
    /** 账户类型 */
    @ApiModelProperty(value = "账户类型")
    private String acct_type;
    /** 入账金额 */
    @ApiModelProperty(value = "入账金额")
    private String amount;
    /**  账户余额 */
    @ApiModelProperty(value = " 账户余额")
    private String balance;
    /** 业务分类 */
    @ApiModelProperty(value = "业务分类")
    private String business_category;
    /**  业务类型:打款、充值等 */
    @ApiModelProperty(value = " 业务类型：打款、充值等")
    private String business_type;
    /** 收支类型:收入、支出 */
    @ApiModelProperty(value = "收支类型:收入、支出")
    private String consumption_type;
    /**  订单接收时间:精确到秒 */
    @ApiModelProperty(value = " 订单接收时间:精确到秒")
    private String created_at;
    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remark;

}
