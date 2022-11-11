package com.zbkj.crmeb.store.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zbkj.crmeb.front.response.OrderDataResponse;
import com.zbkj.crmeb.front.response.OrderWeekDataResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 产品订单数据-响应类
 * @author: 零风
 * @CreateDate: 2021/12/15 11:04
 */
@Data
public class ProductOrderDataResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "商品表ID标识")
    private Integer productId;
    @ApiModelProperty(value = "商品名称")
    private String storeName;
    @ApiModelProperty(value = "商品价格")
    private BigDecimal price;

    @ApiModelProperty(value = "今日订单总数")
    private Integer dayOrderNum;
    @ApiModelProperty(value = "昨日订单总数")
    private Integer yesterdayOrderNum;
    @ApiModelProperty(value = "本月总订单总数")
    private Integer thisMonthOrderNum;
    @ApiModelProperty(value = "总订单数")
    private Integer totalOrderNum;

    @ApiModelProperty(value = "今日交易额")
    private BigDecimal dayGmv;
    @ApiModelProperty(value = "昨日交易额")
    private BigDecimal yesterdayGmv;
    @ApiModelProperty(value = "本月总交易额")
    private BigDecimal thisMonthGmv;
    @ApiModelProperty(value = "总交易额")
    private BigDecimal totalGmv;

}
