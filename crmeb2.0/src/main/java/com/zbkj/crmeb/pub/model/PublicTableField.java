package com.zbkj.crmeb.pub.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 表实体-公共字段
 * @author: 零风
 * @CreateDate: 2022/2/25 10:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PublicTableField {

    @ApiModelProperty(value = "表ID标识")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @TableLogic
    @ApiModelProperty(value = "是否删除 （0：否，1：是）")
    private Boolean isDel;

    @ApiModelProperty(value = "是否显示 （0：否，1：是）")
    private Boolean isShow;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
