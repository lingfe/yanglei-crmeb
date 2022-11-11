package com.zbkj.crmeb.store.response;

import com.zbkj.crmeb.regionalAgency.model.RegionalAgency;
import com.zbkj.crmeb.store.vo.StoreOrderInfoOldVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单信息响应对象（pc列表用）
 * @author: 零风
 * @CreateDate: 2021/12/30 10:14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StoreOrderDetailResponse对象", description="订单信息响应对象（pc列表用）")
public class StoreOrderDetailResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "订单ID标识")
    private Integer id;

    @ApiModelProperty(value = "订单号")
    private String orderId;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "订单状态（0：待发货；1：待收货；2：已收货，待评价；3：已完成；）")
    private Integer status;

    @ApiModelProperty(value = "商品信息")
    private List<StoreOrderInfoOldVo> productList = new ArrayList<>();

    @ApiModelProperty(value = "订单状态字符串")
    private Map<String, String> statusStr;

    @ApiModelProperty(value = "支付方式")
    private String payTypeStr;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;

    @ApiModelProperty(value = "退款图片")
    private String refundReasonWapImg;

    @ApiModelProperty(value = "退款用户说明")
    private String refundReasonWapExplain;

    @ApiModelProperty(value = "退款时间")
    private Date refundReasonTime;

    @ApiModelProperty(value = "前台退款原因")
    private String refundReasonWap;

    @ApiModelProperty(value = "不退款的理由")
    private String refundReason;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundPrice;

    @ApiModelProperty(value = "0 未退款 1 申请中 2 已退款")
    private Integer refundStatus;

    @ApiModelProperty(value = "核销码")
    private String verifyCode;

    @ApiModelProperty(value = "订单类型")
    private String orderType;

    @ApiModelProperty(value = "订单管理员备注")
    private String remark;

    @ApiModelProperty(value = "用户姓名")
    private String realName;

    @ApiModelProperty(value = "支付状态")
    private Boolean paid;

    @ApiModelProperty(value = "订单类型:0-普通订单，1-视频号订单，2-区域代理商订单")
    private Integer type;

    @ApiModelProperty(value = "区域代理信息")
    private RegionalAgency regionalAgency;

    @ApiModelProperty(value = "商户名称")
    private String shopName;

    @ApiModelProperty(value = "是否需要给联盟商家发货")
    private Boolean isLmsjfahuo;

}
