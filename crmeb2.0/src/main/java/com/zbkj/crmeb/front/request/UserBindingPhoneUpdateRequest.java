package com.zbkj.crmeb.front.request;

import com.constants.RegularConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 换绑手机号请求对象
 * @author: 零风
 * @CreateDate: 2022/3/23 14:06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserBindingPhoneUpdateRequest", description="换绑手机号请求对象")
public class UserBindingPhoneUpdateRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "手机号", required = true)
    @Pattern(regexp = RegularConstants.PHONE, message = "手机号码格式错误")
    private String phone;

    @ApiModelProperty(value = "验证码", required = true)
    @Pattern(regexp = RegularConstants.SMS_VALIDATE_CODE_NUM, message = "验证码格式错误，验证码必须为6位数字")
    private String captcha;
}
