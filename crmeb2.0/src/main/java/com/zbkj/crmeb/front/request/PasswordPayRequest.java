package com.zbkj.crmeb.front.request;

import com.constants.RegularConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 支付密码-请求类
 * @author: 零风
 * @CreateDate: 2022/3/15 14:04
 */
@Data
public class PasswordPayRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "手机号", required = true)
    @Pattern(regexp = RegularConstants.PHONE, message = "手机号码格式错误")
    @NotBlank
    @JsonProperty(value = "account")
    private String phone;

    @ApiModelProperty(value = "支付密码", required = true)
    @Pattern(regexp = RegularConstants.VALIDATE_CODE_NUM_SIX, message = "密码格式错误，密码只能是数字!且只能是6位！")
    private String password;

    @ApiModelProperty(value = "验证码", required = true)
    @Pattern(regexp = RegularConstants.SMS_VALIDATE_CODE_NUM, message = "验证码格式错误，验证码必须为4位数字")
    @JsonProperty(value = "captcha")
    private String validateCode;


}
