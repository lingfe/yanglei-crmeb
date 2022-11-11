package com.zbkj.crmeb.front.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 个人资料-请求类
 * @author: 零风
 * @CreateDate: 2022/3/16 11:01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserEditRequest对象", description="修改个人资料")
public class UserEditRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户昵称")
    @NotBlank(message = "请填写用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    @NotBlank(message = "请上传用户头像")
    private String avatar;
}
