package com.zbkj.crmeb.system.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 组合数据表
 * @author: 零风
 * @CreateDate: 2022/6/9 10:41
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_system_group")
@ApiModel(value="SystemGroup-组合数据表", description="组合数据表")
public class SystemGroup implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "组合数据ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "数据组名称")
    private String name;

    @ApiModelProperty(value = "简介")
    private String info;

    @ApiModelProperty(value = "form 表单 id")
    private Integer formId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "是否显示 （0：否，1：是）")
    private Boolean isShow;
}
