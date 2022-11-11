package com.zbkj.crmeb.log.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* 版本日誌表
* @author: 零风
* @CreateDate: 2021/9/27 15:26
*/
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_version_log")
@ApiModel(value="EbVersionLog对象", description="版本日誌表")
public class EbVersionLog implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "版本日誌表ID标识")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "类型: 1=客户端(ios、android)，2=后端系统(system)")
    private Integer ptype;

    @ApiModelProperty(value = "更新内容")
    private String updateContent;

    @ApiModelProperty(value = "版本号")
    private String version;

    @ApiModelProperty(value = "创建时间/更新时间")
    private Date createTime;

}
