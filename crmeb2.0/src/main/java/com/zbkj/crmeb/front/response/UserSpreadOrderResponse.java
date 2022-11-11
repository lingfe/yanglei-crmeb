package com.zbkj.crmeb.front.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 推广人订单-响应对象
 * @author: 零风
 * @CreateDate: 2022/1/20 14:47
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserSpreadOrderResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "累计推广订单")
    private Long count = 0L;

    @ApiModelProperty(value = "推广人列表")
    private List<UserSpreadOrderItemResponse> list;
}
