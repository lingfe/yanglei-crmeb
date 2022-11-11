package com.zbkj.crmeb.user.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 向用户发送营销短信-请求类
 * @author: 零风
 * @CreateDate: 2022/8/4 10:05
 */
@Data
public class UserSmsRequest {

    @ApiModelProperty(value = "短信类型: 0=预约通知短信、、")
    private Integer type;
    @ApiModelProperty(value = "用户类型：0=手机号、1=系统用户ID")
    private Integer userType;
    @ApiModelProperty(value = "用户ID或者手机号,多个用逗号隔开")
    private String uidsOrPhons;
    @ApiModelProperty(value = "短信模版ID")
    private Integer modelId;
    @ApiModelProperty(value = "内容参数，多个用逗号隔开")
    private String contentParam;


}
