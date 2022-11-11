package com.zbkj.crmeb.regionalAgency.model;

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
 * 区域代理用户表
 * @author: 零风
 * @CreateDate: 2021/11/9 14:53
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_regional_user")
@ApiModel(value="RegionalUser-对象", description="区域用户表")
public class RegionalUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "区域用户表ID标识")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id标识")
    private Integer uid;

    @ApiModelProperty(value = "区域代理表ID标识，等于0表示系统分配，大于0表示其他区域")
    private Integer raId;

    @ApiModelProperty(value = "创建时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
