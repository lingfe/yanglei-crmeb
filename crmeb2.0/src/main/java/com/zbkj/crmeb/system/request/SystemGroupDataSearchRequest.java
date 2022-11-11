package com.zbkj.crmeb.system.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 组合数据详情数据表-请求搜索类
 * @author: 零风
 * @CreateDate: 2022/6/9 10:39
 */
@Data
public class SystemGroupDataSearchRequest implements Serializable {

    private static final long serialVersionUID=1L;
    @ApiModelProperty(value = "关键字")
    private String keywords;

    @ApiModelProperty(value = "分组id")
    private Integer gid;

    @ApiModelProperty(value = "状态（1：开启；2：关闭；）")
    private Boolean status;

}
