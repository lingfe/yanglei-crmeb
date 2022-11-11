package com.zbkj.crmeb.store.request;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
* 订单列表请求对象
* @author: 零风
* @CreateDate: 2021/11/15 15:45
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_order")
@ApiModel(value="StoreOrderSearchRequest对象", description="订单列表请求对象")
public class StoreOrderSearchRequest implements Serializable {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "创建时间区间")
    private String dateLimit;

    @ApiModelProperty(value = "订单状态（all 总数； 未支付 unPaid； 未发货 notShipped；待收货 spike；待评价 bargain；已完成 complete；待核销 toBeWrittenOff；退款中:refunding；已退款:refunded；已删除:deleted")
    private String status;

    @ApiModelProperty(value = "订单类型: 0-普通订单、1-视频号订单、2-区域代理订单、3-零售商订单、4-供应商订单、-1=全部")
    private Integer type;

    @ApiModelProperty(value = "商户ID-(供应商ID标识、区域代理ID标识、零售商ID标识)(前端可传不传),等于(负1)-1表示查询全部区域代理订单")
    private Integer merId;

}
