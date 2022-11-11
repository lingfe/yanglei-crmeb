package com.zbkj.crmeb.validatecode.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
* 验证码-code类
* @author: 零风
* @CreateDate: 2021/10/13 9:48
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ValidateCode对象", description="验证码类")
public class ValidateCode implements Serializable {

    public ValidateCode(String key, String code) {
        this.key = key;
        this.code = code;
    }

    @ApiModelProperty(value = "key", required = true)
    private String key;

    @ApiModelProperty(value = "code", required = true)
    private String code;

}
