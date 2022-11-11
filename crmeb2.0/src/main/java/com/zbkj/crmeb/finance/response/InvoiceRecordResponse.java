package com.zbkj.crmeb.finance.response;

import com.zbkj.crmeb.store.vo.StoreOrderInfoOldVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 发票记录表-响应类
 * @author: 零风
 * @CreateDate: 2022/4/15 10:07
 */
@Data
public class InvoiceRecordResponse {

    @ApiModelProperty(value = "发票记录表ID标识")
    private Integer id;
    @ApiModelProperty(value = "发票记录状态-数值(查看:InvoiceRecord.java")
    private Integer status;
    @ApiModelProperty(value = "发票记录状态-中文字符串(查看常量类:com.constants.Constants.java")
    private String statusStr;
    @ApiModelProperty(value = "记录时间")
    private String recordTime;
    @ApiModelProperty(value = "发票号码")
    private String invoiceNumber;
    @ApiModelProperty(value = "发票备注")
    private String remark;

    @ApiModelProperty(value = "商品信息")
    private List<StoreOrderInfoOldVo> productList;

    @ApiModelProperty(value = "订单号")
    private String orderId;
    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payPrice;
    @ApiModelProperty(value = "支付方式(查看常量类:com.constants.PayConstants.java)")
    private String payType;
    @ApiModelProperty(value = "支付时间")
    private Date payTime;
    @ApiModelProperty(value = "订单状态-数值(查看常量类:com.constants.Constants.java")
    private Integer orderStatus;
    @ApiModelProperty(value = "订单状态-字符串(查看常量类:com.constants.Constants.java")
    private String orderStatusStr;
    @ApiModelProperty(value = "订单备注")
    private String mark;

    @ApiModelProperty(value = "发票抬头信息")
    private InvoiceRiseResponse invoiceRiseResponse;

}
