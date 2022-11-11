package com.zbkj.crmeb.front.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 推广订单信息-响应类
 * @author: 零风
 * @CreateDate: 2022/1/20 15:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserSpreadOrderItemResponse对象", description="推广订单信息")
public class UserSpreadOrderItemResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "推广条数")
    private Integer count = 0;

    @ApiModelProperty(value = "推广年月")
    private String time;

    @ApiModelProperty(value = "推广订单信息")
    private List<UserSpreadOrderItemChildResponse> child = new ArrayList<>();
}
