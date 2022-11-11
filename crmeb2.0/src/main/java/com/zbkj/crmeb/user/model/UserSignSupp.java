package com.zbkj.crmeb.user.model;

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
 * @program: crmeb
 * @description: 用户-签到-补签表
 * @author: 零风
 * @create: 2021-06-28 10:57
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_sign_supp")
@ApiModel(value="UserSignSupp-对象", description="补签记录表")
public class UserSignSupp {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "补签积分(补签获得的积分)")
    private Integer suppIntegral;

    @ApiModelProperty(value = "添加时间")
    private Date suppDate;
}
