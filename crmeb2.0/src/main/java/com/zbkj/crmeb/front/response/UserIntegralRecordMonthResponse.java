package com.zbkj.crmeb.front.response;

import com.zbkj.crmeb.user.model.UserIntegralRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户积分记录(按年)-响应对象
 * @author: 零风
 * @CreateDate: 2022/3/25 10:17
 */
@Data
@ApiModel(value="UserIntegalRecordResponse-用户积分记录-响应对象", description="用户积分记录-响应对象")
public class UserIntegralRecordMonthResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "月份")
    private String date;

    @ApiModelProperty(value = "数据")
    private List<UserIntegralRecord> list;

}
