package com.zbkj.crmeb.store.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: crmeb
 * @description: 商品-属性值-excel导入导出类
 * @author: 零风
 * @create: 2021-07-23 11:37
 **/
@Data
public class StoreProductAttrValueExcel implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "属性值ID(不填)")
    private Integer id=0;

    @ApiModelProperty(value = "商品ID(必填)")
    private Integer productId=0;

    @ApiModelProperty(value = "属性sku(必填)")
    private String suk="(必填),属性sku,这是测试,多个用逗号隔开";//商品属性sku,用逗号隔开

    @ApiModelProperty(value = "产品属性值和属性名对应关系(选填)")
    private String attrValue ="{\"选项\":\"(必填)\",\"说明\":\"属性sku\",\"这是\":\"这是测试\",\"规则\":\"多个用逗号隔开\"}";

    @ApiModelProperty(value = "图片(选填)")
    private String image="https://bing.ioliu.cn/v1/rand";

    @ApiModelProperty(value = "库存(必填)")
    private Integer stock=0;

    @ApiModelProperty(value = "销量(选填)")
    private Integer sales=0;

    @ApiModelProperty(value = "金额(必填)")
    private BigDecimal price=BigDecimal.ZERO;

    @ApiModelProperty(value = "唯一值(选填)")
    private String unique="(选填)";

    @ApiModelProperty(value = "成本价(选填)")
    private BigDecimal cost=BigDecimal.ZERO;;

    @ApiModelProperty(value = "商品条码(选填)")
    private String barCode="(选填)";

    @ApiModelProperty(value = "原价(选填)")
    private BigDecimal otPrice=BigDecimal.ZERO;

    @ApiModelProperty(value = "重量(选填)")
    private BigDecimal weight=BigDecimal.ZERO;

    @ApiModelProperty(value = "体积(选填)")
    private BigDecimal volume=BigDecimal.ZERO;

    @ApiModelProperty(value = "一级返佣(选填)")
    private BigDecimal brokerage=BigDecimal.ZERO;

    @ApiModelProperty(value = "二级返佣(选填)")
    private BigDecimal brokerageTwo=BigDecimal.ZERO;

    @ApiModelProperty(value = "类型(选填)")
    private Integer type=0;//0=商品，1=秒杀，2=砍价，3=拼团，4=积分兑换商品

    @ApiModelProperty(value = "活动限购数量(选填)")
    private Integer quota=0;

    @ApiModelProperty(value = "活动限购数量显示(选填)")
    private Integer quotaShow=0;

    @ApiModelProperty(value = "区域代理返佣比例%(选填)")
    private BigDecimal ugaBrokerage=BigDecimal.ZERO;

    @ApiModelProperty(value = "区域代理返佣金额￥(选填)")
    private BigDecimal ugaPrice=BigDecimal.ZERO;

}
