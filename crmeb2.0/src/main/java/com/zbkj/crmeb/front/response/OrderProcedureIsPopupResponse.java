package com.zbkj.crmeb.front.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单流程是否需要弹窗-响应类
 * @author: 零风
 * @CreateDate: 2022/6/15 10:33
 */
@Data
public class OrderProcedureIsPopupResponse {

    @ApiModelProperty(value = "是否弹窗")
    private Boolean isPopup;

    @ApiModelProperty(value = "是否针对联盟商家弹窗")
    private Boolean isAllianceMerchants=Boolean.FALSE;

    @ApiModelProperty(value = "弹窗标题")
    private String titile;

    @ApiModelProperty(value = "弹窗内容")
    private String content;

    @ApiModelProperty(value = "用户id")
    private Integer uid;
    @ApiModelProperty(value = "用户昵称")
    private String nickname;
    @ApiModelProperty(value = "用户头像")
    private String avatar;
    @ApiModelProperty(value = "手机号码")
    private String phone;

}
