package com.zbkj.crmeb.creator.response;

import com.zbkj.crmeb.system.model.SystemAttachment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 热门创作者-响应类
 * @author: 零风
 * @CreateDate: 2022/7/1 16:16
 */
@Data
public class RemenUserResponse {

    @ApiModelProperty(value = "创作者详细")
    private CreatorDataResponse creatorData;

    @ApiModelProperty(value = "作品集合")
    private List<SystemAttachment> worksList;

}
