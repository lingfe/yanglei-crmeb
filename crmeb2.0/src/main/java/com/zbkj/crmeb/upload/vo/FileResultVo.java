package com.zbkj.crmeb.upload.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文件信息-vo类
 * @author: 零风
 * @CreateDate: 2022/1/19 14:13
 */
@Data
public class FileResultVo{

    // 文件名
    private String fileName;

    // 扩展名
    private String extName;

    // 文件大小，字节
    private Long fileSize;

    // 文件存储在服务器的相对地址
    private String serverPath;

    //可供访问的url
    private String url;

    //类型
    private String type;

    @ApiModelProperty(value = "完整路径")
    private String path;
    @ApiModelProperty(value = "前缀")
    private String prefix;
    @ApiModelProperty(value = "创作者用户ID标识")
    private Integer uid;
}
