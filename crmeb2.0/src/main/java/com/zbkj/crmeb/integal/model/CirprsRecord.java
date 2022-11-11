package com.zbkj.crmeb.integal.model;

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
 * 消费积分返推广人记录表
 * @author: 零风
 * @CreateDate: 2021/10/19 11:08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_cirprs_record")
@ApiModel(value="CirprsRecord-对象", description="消费积分返推广人记录表")
public class CirprsRecord {

    @ApiModelProperty(value = "公共积分记录表ID标识")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户ID")
    private Integer uid;

    @ApiModelProperty(value = "推广人ID")
    private Integer popularizeId;

    @ApiModelProperty(value = "公共积分记录ID标识")
    private Integer publicirId;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

}
