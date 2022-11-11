package com.zbkj.crmeb.front.response;

import com.zbkj.crmeb.user.model.UserBrokerageRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 推广佣金明细响应对象
 * @author: 零风
 * @CreateDate: 2021/12/24 14:06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SpreadCommissionDetailResponse对象", description = "推广佣金明细响应对象")
public class SpreadCommissionDetailResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    public SpreadCommissionDetailResponse() {}

    public SpreadCommissionDetailResponse(String date, List<UserBrokerageRecord> list) {
        this.date = date;
        this.list = list;
    }

    @ApiModelProperty(value = "月份")
    private String date;

    @ApiModelProperty(value = "数据")
    private List<UserBrokerageRecord> list;


}
