package com.zbkj.crmeb.system.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
* PC登录请求对象,无需验证码验证，图片滑块登录请求
* @author: 零风
* @Version: 1.0
* @CreateDate: 2021/6/21 14:46
* @return： com\zbkj\crmeb\system\request\SystemAdminLoginNoYzmRequest.java
**/
@Data
public class SystemAdminLoginNoYzmRequest {

    @ApiModelProperty(value = "后台管理员账号", example = "userName")
    @NotEmpty(message = "账号 不能为空")
    private String account;

    @ApiModelProperty(value = "后台管理员密码", example = "userPassword")
    @NotEmpty(message = "密码 不能为空")
    @Length(min = 6, max = 30)
    private String pwd;

    @ApiModelProperty(value = "微信授权code")
    private String wxCode;
}
