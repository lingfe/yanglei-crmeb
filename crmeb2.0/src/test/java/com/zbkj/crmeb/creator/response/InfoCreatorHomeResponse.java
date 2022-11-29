package com.zbkj.crmeb.creator.response;

import com.zbkj.crmeb.system.model.SystemAttachment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 创作者主页详细-响应类
 * @author: 零风
 * @CreateDate: 2022/7/1 16:09
 */
@Data
public class InfoCreatorHomeResponse {

    @ApiModelProperty(value = "创作者详细")
    private CreatorDataResponse creatorData;

    @ApiModelProperty(value = "分类集合")
    private List<Map<String,Object>> typeList;

    @ApiModelProperty(value = "默认作品集合")
    private List<SystemAttachment> defaultWorksList;

}
