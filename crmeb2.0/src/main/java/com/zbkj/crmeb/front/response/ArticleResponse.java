package com.zbkj.crmeb.front.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 文章响应对象
 * @author: 零风
 * @CreateDate: 2022/1/7 11:32
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ArticleResponse对象", description="文章响应对象")
public class ArticleResponse implements Serializable {

    private static final long serialVersionUID = -4585094537501770138L;

    @ApiModelProperty(value = "文章管理ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "文章标题")
    private String title;

    @ApiModelProperty(value = "文章作者")
    private String author;

    @ApiModelProperty(value = "文章图片 前端用")
    private String imageInput;

    @ApiModelProperty(value = "文章简介")
    private String synopsis;

    @ApiModelProperty(value = "浏览次数")
    private String visit;

    @ApiModelProperty(value = "文章内容")
    private String content;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date createTime;
}
