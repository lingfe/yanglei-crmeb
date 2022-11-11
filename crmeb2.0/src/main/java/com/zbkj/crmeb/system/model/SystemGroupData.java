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
 * 组合数据详情数据表
 * @author: 零风
 * @CreateDate: 2022/6/9 10:37
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_system_group_data")
@ApiModel(value="SystemGroupData-组合数据详情数据表", description="组合数据详情数据表")
public class SystemGroupData implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "组合数据详情ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "对应的数据组id")
    private Integer gid;

    @ApiModelProperty(value = "数据组对应的数据值（json数据）")
    private String value;

    @ApiModelProperty(value = "数据排序")
    private Integer sort;

    @ApiModelProperty(value = "状态（1：开启；0：关闭；）")
    private Boolean status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
