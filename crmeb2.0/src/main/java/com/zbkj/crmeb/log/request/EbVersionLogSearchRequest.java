package com.zbkj.crmeb.log.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 版本日志表-搜索请求类
 * @author: 零风
 * @CreateDate: 2021/9/27 15:39
 */
@Data
public class EbVersionLogSearchRequest {

    @ApiModelProperty(value = "类型: 1=客户端(ios、android)，2=后端系统(system)")
    private Integer ptype;

    @ApiModelProperty(value = "版本号")
    private String version;

}
