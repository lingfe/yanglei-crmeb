package com.zbkj.crmeb.retailer.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zbkj.crmeb.pub.model.PayProfitSharing;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 零售商表
 * @author: 零风
 * @CreateDate: 2021/11/22 14:17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_retailer")
@ApiModel(value="Retailer-对象", description="零售商表")
public class Retailer extends PayProfitSharing {

    @ApiModelProperty(value = "零售商表ID标识")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户uid(店铺管理者ID)")
    private Integer uid;

    @ApiModelProperty(value = "区域代理表ID标识")
    private Integer raId;

    @ApiModelProperty(value = "零售商名称")
    private String reName;

    @ApiModelProperty(value = "营业执照")
    private String reYyzz;

    @ApiModelProperty(value = "身份证正面")
    private String idZheng;

    @ApiModelProperty(value = "身份证反面")
    private String idFan;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "状态,0-审核中，1-审核通过，2-不通过")
    private Integer status;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "联系电话")
    private String rePhone;

    @ApiModelProperty(value = "联系人姓名")
    private String reContactName;

    @ApiModelProperty(value = "联系地址")
    private String reAddress;

    @ApiModelProperty(value = "联系详细地址")
    private String reAddressInfo;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "税号")
    private String reDutyParagraph;

}
