package com.zbkj.crmeb.finance.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zbkj.crmeb.pub.model.PublicTableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 发票记录表
 * @author: 零风
 * @CreateDate: 2022/4/14 14:53
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_invoice_record")
@ApiModel(value="InvoiceRecord-发票记录表", description="发票记录表")
public class InvoiceRecord extends PublicTableField {

    @ApiModelProperty(value = "用户表ID标识")
    private Integer uid;

    @ApiModelProperty(value = "发票抬头表ID标识")
    private Integer riseId;

    @ApiModelProperty(value = "订单表id标识")
    private Integer orderId;

    @ApiModelProperty(value = "状态: 0=未开票、1=已开票")
    private Integer status;

    @ApiModelProperty(value = "发票号码")
    private String invoiceNumber;

    @ApiModelProperty(value = "备注")
    private String remark;

}
