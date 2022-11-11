package com.zbkj.crmeb.store.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商城预约表-搜索请求类
 * @author: 零风
 * @CreateDate: 2022/7/21 15:10
 */
@Data
public class StoreMakeAnAppointmentSearchRequest {

    @ApiModelProperty(value = "预约结果是否成功")
    private Boolean isResult;
    @ApiModelProperty(value = "状态: -1=全部、0=已预约/成功、1=已结束")
    private Integer status;

}
