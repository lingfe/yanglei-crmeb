package com.zbkj.crmeb.system.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统附件表-搜索请求类
 * @author: 零风
 * @CreateDate: 2022/3/7 14:19
 */
@Data
public class SystemAttachmentSearchRequest {

    @ApiModelProperty(value = "上级ID")
    private Integer pid;
    @ApiModelProperty(value = "上级ID字符串，多个用逗号隔开")
    private String pidStr;

    @ApiModelProperty(value = "关键字")
    private String keywords;

}
