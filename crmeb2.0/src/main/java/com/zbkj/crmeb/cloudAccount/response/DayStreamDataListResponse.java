package com.zbkj.crmeb.cloudAccount.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @program: crmeb
 * @description: 云账户-日流水记录-list响应类
 * @author: 零风
 * @create: 2021-08-19 15:14
 **/
@Getter
@Setter
@Builder
public class DayStreamDataListResponse {

    /** 总条数 */
    @ApiModelProperty(value = "总条数")
    private Integer total_num;

    /** 日流水记录集合 */
    @ApiModelProperty(value = "日流水记录集合")
    private List<DayStreamDataResponse> list;

}
