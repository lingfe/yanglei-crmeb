package com.zbkj.crmeb.user.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户更新信息-请求类
 * @author: 零风
 * @CreateDate: 2021/12/24 14:37
 */
@Data
@ApiModel(value="UserUpdateRequest-用户更新请求对象", description="用户更新请求对象")
public class UserUpdateRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "uid")
    private Integer uid;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "生日")
    private String birthday;

    @ApiModelProperty(value = "身份证号码")
    private String cardId;

    @ApiModelProperty(value = "用户备注")
    private String mark;

    @ApiModelProperty(value = "状态是否正常， 0 = 禁止， 1 = 正常")
    @NotNull
    private Boolean status;

    @ApiModelProperty(value = "详细地址")
    private String addres;

    @ApiModelProperty(value = "等级")
    private Integer level;

    @ApiModelProperty(value = "用户分组id")
    private String groupId;

    @ApiModelProperty(value = "用户标签id")
    private String tagId;

    @ApiModelProperty(value = "是否为推广员")
    @NotNull
    private Boolean isPromoter;

    @ApiModelProperty(value = "是否为区域代理")
    private Boolean isGeneralAgency;

}
