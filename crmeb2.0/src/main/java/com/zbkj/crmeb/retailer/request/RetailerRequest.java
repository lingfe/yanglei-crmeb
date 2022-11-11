package com.zbkj.crmeb.retailer.request;

import com.zbkj.crmeb.pub.model.PayProfitSharing;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 零售商-请求类
 * @author: 零风
 * @CreateDate: 2021/11/23 13:46
 */
@Data
public class RetailerRequest extends PayProfitSharing {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "零售商表ID标识")
    private Integer id;

    @ApiModelProperty(value = "区域代理表ID标识，手动选择")
    private Integer raId;

    @ApiModelProperty(value = "零售商名称")
    private String reName;

    @ApiModelProperty(value = "营业执照")
    private String reYyzz;

    @ApiModelProperty(value = "身份证正面")
    private String idZheng;

    @ApiModelProperty(value = "身份证反面")
    private String idFan;

    @ApiModelProperty(value = "联系电话")
    private String rePhone;
    @ApiModelProperty(value = "手机号验证码")
    private String code;

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
