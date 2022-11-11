package com.zbkj.crmeb.store.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单发货请求对象
 * @author: 零风
 * @CreateDate: 2021/12/29 10:18
 */
@Data
@ApiModel(value="StoreOrderSendRequest-订单发货请求对象", description="订单发货请求对象")
public class StoreOrderSendRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "订单id")
    private Integer id;

    @ApiModelProperty(value = "订单编号")
    @NotBlank(message = "订单编号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "类型， 1发货，2送货，3虚拟", allowableValues = "range[1,2,3]")
    @NotBlank(message = "请选择类型")
    private String type;

    @ApiModelProperty(value = "快递公司名,发货类型必传")
    private String expressName;

    @ApiModelProperty(value = "快递公司编码,发货类型必传")
    private String expressCode;

    @ApiModelProperty(value = "快递单号,发货类型必传")
    private String expressNumber;

    @ApiModelProperty(value = "发货记录类型，1正常、2电子面单,发货类型必传")
    private String expressRecordType;

    @ApiModelProperty(value = "电子面单模板,电子面单必传")
    private String expressTempId;

    @ApiModelProperty(value = "寄件人姓名,电子面单必传")
    private String toName;

    @ApiModelProperty(value = "寄件人电话,电子面单必传")
    private String toTel;

    @ApiModelProperty(value = "寄件人地址,电子面单必传")
    private String toAddr;

    @ApiModelProperty(value = "送货人姓名,送货类型必传")
    private String deliveryName;

    @ApiModelProperty(value = "送货人电话,送货类型必传")
    private String deliveryTel;
}
