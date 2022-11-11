package com.zbkj.crmeb.pub.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;

/**
 * 二维码公共vo类
 * @author: 零风
 * @CreateDate: 2022/3/14 16:34
 */
@Builder
public class PublicCodeVo {

    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "二维码类型：1=收款码")
    private Integer codeType;

}
