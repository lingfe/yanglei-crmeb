package com.zbkj.crmeb.front.response;

import com.zbkj.crmeb.user.model.UserIntegralRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Login-Response
 * @author: 零风
 * @CreateDate: 2022/1/6 10:09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="LoginResponse", description="用户登录返回数据")
public class LoginResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户登录密钥")
    private String token;

    @ApiModelProperty(value = "状态:login-登录，register-注册,start-注册起始页，ios-苹果账号")
    private String type;

    @ApiModelProperty(value = "注册key")
    private String key;

    @ApiModelProperty(value = "登录用户Uid")
    private Integer uid;

    @ApiModelProperty(value = "登录用户昵称")
    private String nikeName;

    @ApiModelProperty(value = "登录用户手机号")
    private String phone;

    @ApiModelProperty(value = "用户积分记录")
    private UserIntegralRecord userIntegralRecord;
}
