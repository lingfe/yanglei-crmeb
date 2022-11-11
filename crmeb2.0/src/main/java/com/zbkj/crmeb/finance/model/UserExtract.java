package com.zbkj.crmeb.finance.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* 用户提现表
* @author: 零风
* @Version: 1.0
* @CreateDate: 2021/8/12 15:01
* @return： UserExtract.java
**/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_extract")
@ApiModel(value="UserExtract-用户提现表", description="用户提现表")
public class UserExtract implements Serializable {

    //序列化
    private static final long serialVersionUID=1L;

    //公共字段
    @ApiModelProperty(value = "用户提现记录表ID标识")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty(value = "用户id")
    private Integer uid;
    @ApiModelProperty(value = "用户名称/收款人名称/银行卡实名/支付宝实名/微信号实名/等")
    private String realName;
    @ApiModelProperty(value = "提现金额")
    private BigDecimal extractPrice;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "余额")
    private BigDecimal balance;
    @ApiModelProperty(value = "无效原因")
    private String failMsg;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    @ApiModelProperty(value = "失败时间")
    private Date failTime;
    @ApiModelProperty(value = "状态: -1=未通过、0=申请中，1=成功/已提现，2=失败，3=挂单,4=退汇,5=取消")
    private Integer status;
    @ApiModelProperty(value = "提现方式: bank=银行卡、 alipay = 支付宝、 weixin=微信、other=其他")
    private String extractType;
    @ApiModelProperty(value = "商户订单号，由商户自定义，保持唯一性(必填)，64个英文字符以内")
    private String orderId;
    @ApiModelProperty(value = "手续费")
    private BigDecimal serviceFee;
    @ApiModelProperty(value = "关联类型：1=普通提现、2、申请信用卡还款资金、3=扫码向联盟商家转米")
    private Integer linkType;
    @ApiModelProperty(value = "提现是否成功：0=失败、1=成功")
    private Boolean isOk;
    @ApiModelProperty(value = "应用ID类型:0=无、1=微信服务号、2=微信小程序、3=安卓微信、4=ios微信、5=网页..")
    private Integer appidType;

    //新增字段
    @ApiModelProperty(value = "身份证号")
    private String idCard;
    @ApiModelProperty(value = "联系电话号码")
    private String phone;

    //银行卡-字段
    @ApiModelProperty(value = "银行卡号")
    private String bankCode;
    @ApiModelProperty(value = "银行名称")
    private String bankName;
    @ApiModelProperty(value = "开户地址")
    private String bankAddress;

    //支付宝-字段
    @ApiModelProperty(value = "支付宝账号")
    private String alipayCode;

    //微信-字段
    @ApiModelProperty(value = "微信号")
    private String wechat;
    @ApiModelProperty(value = "微信openid")
    private String openid;
    @ApiModelProperty(value = "微信收款二维码地址")
    private String qrcodeUrl;

    //不属于表字段，用于过渡
    @Transient
    @TableField(exist = false)
    private String nickName;
}
