package com.zbkj.crmeb.finance.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zbkj.crmeb.pub.model.PublicTableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 发票抬头表
 * @author: 零风
 * @CreateDate: 2022/4/14 14:39
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_invoice_rise")
@ApiModel(value="InvoiceRise-发票抬头表", description="发票抬头表")
public class InvoiceRise extends PublicTableField {

    @ApiModelProperty(value = "用户表ID标识")
    private Integer uid;

    @ApiModelProperty(value = "抬头类型:1=个人，2=企业")
    private Integer riseType;

    @ApiModelProperty(value = "发票类型:1=增值税电子普通发票,2=增值税电子专用发票")
    private Integer invoiceType;

    @ApiModelProperty(value = "发票抬头")
    private String rise;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱号")
    private String mailbox;

    @ApiModelProperty(value = "是否默认：0=否、1=是")
    private Boolean isDefault;

    @ApiModelProperty(value = "税号")
    private String dutyParagraph;

    @ApiModelProperty(value = "开户银行")
    private String yhkKaihuhang;

    @ApiModelProperty(value = "银行卡号")
    private String yhkNumber;

    @ApiModelProperty(value = "详细地址")
    private String addressInfo;

    @ApiModelProperty(value = "企业电话")
    private String telephone;

}
