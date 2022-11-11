package com.zbkj.crmeb.store.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 供应商表-请求类
 * @author: 零风
 * @CreateDate: 2021/12/28 11:39
 */
@Data
public class SupplierRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "供应商表ID标识")
    private Integer id;
    @ApiModelProperty(value = "用户uid(供应商管理者ID)")
    private Integer uid;

    @ApiModelProperty(value = "供应商名称/商户简称")
    private String suppName;
    @ApiModelProperty(value = "供应商类型/商户类型：1=小微/个人、2=企业、3=其他")
    private Integer suppType;
    @ApiModelProperty(value = "法人姓名/商户负责人姓名/商户姓名")
    private String farenName;
    @ApiModelProperty(value = "法人电话/商户负责人电话/商户联系电话")
    private String phone;
    @ApiModelProperty(value = "所在地区/联系地址,省市区")
    private String address;
    @ApiModelProperty(value = "联系详细地址")
    private String addressInfo;
    @ApiModelProperty(value = "结算费率-微信")
    private BigDecimal settlementReteWeixin;
    @ApiModelProperty(value = "结算费率-支付宝")
    private BigDecimal settlementReteZfb;
    @ApiModelProperty(value = "结算费率-银联")
    private BigDecimal settlementReteYinlian;
    @ApiModelProperty(value = "身份证正面")
    private String idZmImg;
    @ApiModelProperty(value = "身份证反面")
    private String idFmImg;
    @ApiModelProperty(value = "身份证号")
    private String idNumber;

    @ApiModelProperty(value = "身份证有效开始时间")
    private String idStartTime;
    @ApiModelProperty(value = "身份证有效结束时间")
    private String idStopTime;

    @ApiModelProperty(value = "支付宝账号")
    private String zfb;
    @ApiModelProperty(value = "结算类型：1=对私-法人、2=对私-非法人、3=对公")
    private Integer settlementType;
    @ApiModelProperty(value = "银行卡照片-正面")
    private String yhkZmImg;
    @ApiModelProperty(value = "银行卡照片-反面")
    private String yhkFmImg;
    @ApiModelProperty(value = "银行卡号")
    private String yhk;
    @ApiModelProperty(value = "开户许可证照片")
    private String khxkzImg;
    @ApiModelProperty(value = "银行卡所在地")
    private String yhkSzdi;
    @ApiModelProperty(value = "银行卡开户行")
    private String yhkKaihuhang;
    @ApiModelProperty(value = "银行卡支行")
    private String yhkZhihang;
    @ApiModelProperty(value = "结算人账户名")
    private String settlementAccount;
    @ApiModelProperty(value = "结算人身份证号")
    private String settlementSfz;
    @ApiModelProperty(value = "结算人身份证照片-正面")
    private String settlementSfzZmImg;
    @ApiModelProperty(value = "结算人身份证照片-反面")
    private String settlementSfzFmImg;
    @ApiModelProperty(value = "结算人授权函照片")
    private String settlementShouquanhanImg;

    @ApiModelProperty(value = "经营类型")
    private String businessType;
    @ApiModelProperty(value = "经营类型表ID标识")
    private Integer businessTypeId;
    @ApiModelProperty(value = "店铺门头照片")
    private String shopMentouImg;
    @ApiModelProperty(value = "店铺内景照片")
    private String shopNeijingImg;
    @ApiModelProperty(value = "营业执照类型：1=三证合一、2=非三证合一")
    private Integer yyzzType;
    @ApiModelProperty(value = "营业执照照片")
    private String yyzzImg;
    @ApiModelProperty(value = "营业执照名称")
    private String yyzzName;
    @ApiModelProperty(value = "营业执照注册号")
    private String yyzzCode;
    @ApiModelProperty(value = "税务登记证照片")
    private String taxDengjizhengImg;
    @ApiModelProperty(value = "组织机构正式照片")
    private String zhuzhiImg;

    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "排序")
    private Integer sort;
    @ApiModelProperty(value = "状态: 0-审核中，1-审核通过，2-不通过")
    private Integer status;

    @ApiModelProperty(value = "分账费率比例(%)")
    private BigDecimal rate;
    @ApiModelProperty(value = "微信账号类型: MERCHANT_ID(商户号)、PERSONAL_OPENID(个人用户openid)")
    private String accountTypeWeixin;
    @ApiModelProperty(value = "微信帐号")
    private String accountWeixin;
    @ApiModelProperty(value = "微信账号实名")
    private String accountWeixinRealName;
    @ApiModelProperty(value = "支付宝账号类型: userId(唯一用户号)、cardAliasNo(支付宝绑定的卡编号)、loginName")
    private String accountTypeAlipay;
    @ApiModelProperty(value = "支付宝帐号")
    private String accountAlipay;
    @ApiModelProperty(value = "支付宝账号实名")
    private String accountAlipayRealName;
}
