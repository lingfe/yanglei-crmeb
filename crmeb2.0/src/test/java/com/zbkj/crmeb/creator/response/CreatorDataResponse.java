package com.zbkj.crmeb.creator.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创作者详细-响应类
 * @author: 零风
 * @CreateDate: 2022/7/1 16:09
 */
@Data
public class CreatorDataResponse {

    @ApiModelProperty(value = "口令")
    private Integer code;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "作品统计")
    private Integer worksCount;

    @ApiModelProperty(value = "点赞统计")
    private Integer giveCount;

    @ApiModelProperty(value = "用户余额")
    private BigDecimal nowMoney;

    @ApiModelProperty(value = "是否为创作者")
    private Boolean isCreator;

}
