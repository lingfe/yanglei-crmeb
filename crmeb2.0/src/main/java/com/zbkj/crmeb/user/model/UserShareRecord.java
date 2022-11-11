package com.zbkj.crmeb.user.model;

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
 * @program: crmeb
 * @description: 用户分享记录表
 * @author: 零风
 * @create: 2021-06-29 11:04
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_share_record")
@ApiModel(value="UserShareRecord-对象", description="用户分享记录表")
public class UserShareRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "分享记录ID标识")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "分享时间")
    private Date shareDatetime;

    @ApiModelProperty(value = "分享类型，1=朋友圈，2=好友")
    private Integer shareType;
}
