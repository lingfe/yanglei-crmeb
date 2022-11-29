package com.zbkj.crmeb.creator.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 创作者用户-获取作品列表请求类
 * @author: 零风
 * @CreateDate: 2022/7/15 9:47
 */
@Data
public class GetWorksListRequest {

    @ApiModelProperty(value = "状态:-1=全部、0=审核中、1=通过、2=不通过")
    private Integer status;

    @ApiModelProperty(value = "分类ID")
    private Integer type;

}
