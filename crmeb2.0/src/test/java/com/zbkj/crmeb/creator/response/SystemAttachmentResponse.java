package com.zbkj.crmeb.creator.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 附件表-响应类
 * @author: 零风
 * @CreateDate: 2022/7/19 15:01
 */
@Data
public class SystemAttachmentResponse {

    @ApiModelProperty(value = "附件ID")
    private Integer attid;

    @ApiModelProperty(value = "图片URL")
    private String url;

    @ApiModelProperty(value = "下载量")
    private Integer downloads;

}
