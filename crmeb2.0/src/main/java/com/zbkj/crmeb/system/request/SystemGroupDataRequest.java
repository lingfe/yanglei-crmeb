package com.zbkj.crmeb.system.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 组合数据详情数据表-请求类
 * @author: 零风
 * @CreateDate: 2022/6/9 10:37
 */
@Data
public class SystemGroupDataRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "对应的数据组id")
    private Integer gid;

    @ApiModelProperty(value = "表单数据")
    private SystemFormCheckRequest form;

}
