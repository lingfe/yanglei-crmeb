package com.zbkj.crmeb.front.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
* 订单详情-Vo对象
* @author: 零风
* @CreateDate: 2021/10/21 15:26
*/
@Data
public class OrderInfoDetailVo {

    /** 商品id */
    private Integer productId;

    /** 商品名称 */
    private String productName;

    /** 规格属性id */
    private Integer attrValueId;

    /** 商品图片 */
    private String image;

    /** sku */
    private String sku;

    /** 单价 */
    private BigDecimal price;

    /** 购买数量 */
    private Integer payNum;

    /** 重量 */
    private BigDecimal weight;

    /** 体积 */
    private BigDecimal volume;

    /** 运费模板ID */
    private Integer tempId;

    /** 获得积分 */
    private Integer giveIntegral;

    /** 是否评价 */
    private Integer isReply;

    /** 是否单独分佣 */
    private Boolean isSub;

    /** 是否参与佣金分红 */
    private Boolean isBrokerageAbonus;

    /** 额度控制->Constants.java */
    private Integer quotaControl;

    /**
     * 商户用户ID
     */
    private Integer merId;

    //成本价
    private BigDecimal cost;


}
