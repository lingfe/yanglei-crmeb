package com.zbkj.crmeb.system.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统日志表-搜索请求类
 * @author: 零风
 * @CreateDate: 2022/4/13 11:21
 */
@Data
public class SystemLogsSearchRequest {

    @ApiModelProperty(value = "关键字")
    private String keywords;

    @ApiModelProperty(value = "日志类型= 1:正常操作日志 2:错误日志")
    private byte logType;

    @ApiModelProperty(value = "操作类型= 增删查改")
    private String operationType;

}
