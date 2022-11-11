package com.zbkj.crmeb.data.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 经营类型表
 * @author: 零风
 * @CreateDate: 2021/12/30 14:50
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_business_type")
@ApiModel(value="StorePink-经营类型表", description="经营类型表")
public class BusinessType {

    @ApiModelProperty(value = "经营类型表ID标识")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "上级ID,0=第一级")
    private Integer pid;

    @ApiModelProperty(value = "标题名称")
    private String title;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "是否显示")
    private Boolean isShow;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

}
