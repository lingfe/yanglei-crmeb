package com.zbkj.crmeb.front.request;

import com.constants.RegularConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 手机号注册
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="RegisterRequest对象", description="手机号注册")
public class RegisterRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "手机号", required = true)
    @Pattern(regexp = RegularConstants.PHONE, message = "手机号码格式错误")
    @NotBlank
    @JsonProperty(value = "account")
    private String phone;

    @ApiModelProperty(value = "密码", required = true)
    @Pattern(regexp = RegularConstants.PASSWORD, message = "密码格式错误，密码必须以字母开头，长度在6~18之间，只能包含字符、数字和下划线")
    private String password;

    @ApiModelProperty(value = "验证码", required = true)
    @Pattern(regexp = RegularConstants.SMS_VALIDATE_CODE_NUM, message = "验证码格式错误，验证码必须为4位数字")
    @JsonProperty(value = "captcha")
    private String validateCode;

    @ApiModelProperty(value = "推广人id")
    @JsonProperty(value = "spread_spid")
    private Integer spread = 0;



}
