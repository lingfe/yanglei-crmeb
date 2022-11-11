package com.zbkj.crmeb.regionalAgency.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 区域代理表
 * @author: 零风
 * @CreateDate: 2021/11/6 10:24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_regional_agency")
@ApiModel(value="RegionalAgency-对象", description="区域代理表")
public class RegionalAgency  implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "区域代理表ID标识")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty(value = "管理者用户id标识")
    private Integer uid;
    @ApiModelProperty(value = "省")
    private String province;
    @ApiModelProperty(value = "市")
    private String city;
    @ApiModelProperty(value = "区,多个用逗号隔开")
    private String district;

    /**
     * https://blog.csdn.net/qq_38080370/article/details/102613865
     * @TableLogic(value="原值",delval="改值")
     */
    @TableLogic
    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;
    @ApiModelProperty(value = "创建时间")
    private Date updateTime;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "省级城市ID")
    private Integer cityIdProvince;
    @ApiModelProperty(value = "市级城市ID")
    private Integer cityIdCity;
    @ApiModelProperty(value = "区级城市ID,多个用逗号隔开")
    private String cityIdDistrict;
    @ApiModelProperty(value = "可分配积分")
    private BigDecimal distributableIntegral;

    @ApiModelProperty(value = "区域代理名称")
    private String raName;

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