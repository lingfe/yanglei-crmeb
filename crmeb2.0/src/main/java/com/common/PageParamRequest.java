package com.common;

import com.constants.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页公共请求对象
 * @author: 零风
 * @CreateDate: 2022/1/10 10:09
 */
@Data
public class PageParamRequest {

    @ApiModelProperty(value = "页码", example= Constants.DEFAULT_PAGE + "")
    private int page = Constants.DEFAULT_PAGE;

    @ApiModelProperty(value = "每页数量", example = Constants.DEFAULT_LIMIT + "")
    private int limit = Constants.DEFAULT_LIMIT;

}
