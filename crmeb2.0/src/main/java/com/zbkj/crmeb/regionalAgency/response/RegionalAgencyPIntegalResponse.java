package com.zbkj.crmeb.regionalAgency.response;

import com.zbkj.crmeb.integal.response.PublicIntegralLibraryResponse;
import com.zbkj.crmeb.regionalAgency.model.RegionalAgency;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 区域代理及公共积分库-响应类
 * @author: 零风
 * @CreateDate: 2021/11/12 9:54
 */
@Data
public class RegionalAgencyPIntegalResponse {

    @ApiModelProperty(value = "区域代理表")
    private RegionalAgency regionalAgency;

    @ApiModelProperty(value = "公共积分库")
    private PublicIntegralLibraryResponse pilr;

}
