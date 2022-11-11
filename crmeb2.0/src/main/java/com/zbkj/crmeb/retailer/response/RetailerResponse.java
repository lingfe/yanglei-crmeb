package com.zbkj.crmeb.retailer.response;

import com.zbkj.crmeb.pub.model.PayProfitSharing;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 零售商表-响应类
 * @author: 零风
 * @CreateDate: 2021/11/5 10:45
 */
@Data
public class RetailerResponse extends PayProfitSharing {

    @ApiModelProperty(value = "零售商表ID标识")
    private Integer id;

    @ApiModelProperty(value = "用户uid(店铺管理者ID)")
    private Integer uid;
    @ApiModelProperty(value = "用户昵称(店铺管理者名称)")
    private String nickname;

    @ApiModelProperty(value = "区域代理表ID标识")
    private Integer raId;
    @ApiModelProperty(value = "区域代理名称")
    private String raName;

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

    @ApiModelProperty(value = "状态")
    private Integer status;

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

    @ApiModelProperty("是否已注册为平台账户")
    private Boolean isRegister;

}
