package com.zbkj.crmeb.store.vo;

import com.utils.DateUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: crmeb
 * @description: 商品信息-Excel导入导出类
 * @author: 零风
 * @create: 2021-07-20 11:27
 **/
@Data
public class StoreProductExcel {

    @ApiModelProperty(value = "商户ID(选填)")
    private Integer merId=0;//(0为总后台管理员创建,不为0的时候是商户后台创建)
    @ApiModelProperty(value = "商品ID")
    private Integer id=0;

    //基本参数-start
    @ApiModelProperty(value = "商品图片(必填)")
    private String image="https://bing.ioliu.cn/v1/rand";
    @ApiModelProperty(value = "轮播图(必填)")
    private String sliderImage="[\"https://bing.ioliu.cn/v1/rand\",\"https://bing.ioliu.cn/v1/rand\"]";
    @ApiModelProperty(value = "商品详情(必填)")
    private String content="https://bing.ioliu.cn/v1/rand,https://bing.ioliu.cn/v1/rand";

    @ApiModelProperty(value = "商品名称(必填)")
    private String storeName="未设置-商品名称";
    @ApiModelProperty(value = "单位(必填)")
    private String unitName="*_*";
    @ApiModelProperty(value = "关键字(选填)")
    private String keyword=this.storeName;
    @ApiModelProperty(value = "商品简介(选填)")
    private String storeInfo="产品导入-未设置商品简介";
    @ApiModelProperty(value = "分类名称(必填)")
    private String cateName="默认分类名称";
    @ApiModelProperty(value = "品牌名称(必填)")
    private String brandName="默认品牌名称";
    @ApiModelProperty(value = "库存(必填)")
    private Integer stock=0;
    @ApiModelProperty(value = "商品价格(必填)")
    private BigDecimal price=BigDecimal.ZERO;
    @ApiModelProperty(value = "是否多规格(必填)")
    private Boolean specType=true;

    @ApiModelProperty(value = "销量(选填)")
    private Integer sales=0;
    @ApiModelProperty(value = "商品条形码(选填)")
    private String barCode="产品导入-未设置";

    @ApiModelProperty(value = "虚拟销量(选填)")
    private Integer ficti=0;
    @ApiModelProperty(value = "浏览量(选填)")
    private Integer browse=0;
    @ApiModelProperty(value = "运费模板ID(选填)")
    private Integer tempId=0;
    @ApiModelProperty(value = "会员价格(选填)")
    private BigDecimal vipPrice=BigDecimal.ZERO;
    @ApiModelProperty(value = "市场价(选填)")
    private BigDecimal otPrice=BigDecimal.ZERO;
    @ApiModelProperty(value = "邮费(选填)")
    private BigDecimal postage=BigDecimal.ZERO;
    @ApiModelProperty(value = "成本价(选填)")
    private BigDecimal cost=BigDecimal.ZERO;
    //end

    //营销相关-start
    @ApiModelProperty(value = "砍价状态是否开启(选填)")
    private Boolean isBargain=false;
    @ApiModelProperty(value = "秒杀状态是否开启(选填)")
    private Boolean isSeckill=false;
    @ApiModelProperty(value = "购买获得积分(选填)")
    private Integer giveIntegral=0;
    @ApiModelProperty(value = "商品二维码地址(选填)")
    private String codePath="无";//(用户小程序海报)
    @ApiModelProperty(value = "活动显示排序(选填)")
    private String activity="0";//0=默认，1=秒杀，2=砍价，3=拼团
    //end

    //布尔字段-start
    @ApiModelProperty(value = "是否显示(选填)")
    private Boolean isShow=false;
    @ApiModelProperty(value = "是否热卖(选填)")
    private Boolean isHot=false;
    @ApiModelProperty(value = "是否优惠(选填)")
    private Boolean isBenefit=false;
    @ApiModelProperty(value = "是否精品(选填)")
    private Boolean isBest=false;
    @ApiModelProperty(value = "是否包邮(选填)")
    private Boolean isPostage=true;
    @ApiModelProperty(value = "是否删除(选填)")
    private Boolean isDel=false;
    @ApiModelProperty(value = "是否新品(选填)")
    private Boolean isNew=false;
    @ApiModelProperty(value = "是否商户代理(选填)")
    private Boolean merUse=false;
    @ApiModelProperty(value = "是否优品推荐(选填)")
    private Boolean isGood=false;
    //end

    //商品返佣金相关-start
    @ApiModelProperty(value = "单独设置区域代理分佣(选填)")
    private Integer uga=0;//0=不设置，1=设置比例，2=设置金额
    @ApiModelProperty(value = "是否单独分佣(选填)")
    private Boolean isSub=false;
    //end

    //其他-start
    @ApiModelProperty(value = "淘宝京东1688类型(选填)")
    private String soureLink="";
    @ApiModelProperty(value = "主图视频链接(选填)")
    private String videoLink="";
    //end

    @ApiModelProperty(value = "分类ID(不填)")
    private String cateId="614";
    @ApiModelProperty(value = "排序(不填)")
    private Integer sort=0;
    @ApiModelProperty(value = "添加时间(不填)")
    private Integer addTime= DateUtil.getNowTime();
    @ApiModelProperty(value = "品牌ID(不填)")
    private Integer brandId=1;

}
