package com.zbkj.crmeb.system.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * PC登录请求对象
 * @author: 零风
 * @CreateDate: 2022/4/13 9:43
 */
@Data
public class SystemAdminLoginRequest {
    @ApiModelProperty(value = "后台管理员账号", example = "userName")
    @NotEmpty(message = "账号 不能为空")
    private String account;

    @ApiModelProperty(value = "后台管理员密码", example = "userPassword")
    @NotEmpty(message = "密码 不能为空")
    @Length(min = 6, max = 30)
    private String pwd;

    @ApiModelProperty(value = "key", required = true)
    @NotEmpty(message = "验证码key 不能为空")
    private String key;

    @ApiModelProperty(value = "code", required = true)
    @NotEmpty(message = "验证码 不能为空")
    private String code;

    @ApiModelProperty(value = "微信授权code")
    private String wxCode;
}
