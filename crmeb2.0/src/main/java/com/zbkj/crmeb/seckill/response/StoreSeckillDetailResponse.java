package com.zbkj.crmeb.seckill.response;

import com.zbkj.crmeb.front.response.ProductAttrResponse;
import com.zbkj.crmeb.front.response.SecKillDetailH5Response;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 商品秒杀详情响应对象
 * @author: 零风
 * @CreateDate: 2022/7/21 18:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StoreSeckillDetailResponse对象", description="商品秒杀详情响应对象")
public class StoreSeckillDetailResponse implements Serializable {

    private static final long serialVersionUID = -4101548587444327191L;

    @ApiModelProperty(value = "产品属性")
    private List<ProductAttrResponse> productAttr;

    @ApiModelProperty(value = "商品属性详情")
    private HashMap<String,Object> productValue;

    @ApiModelProperty(value = "返佣金额区间")
    private String priceName;

    @ApiModelProperty(value = "收藏标识")
    private Boolean userCollect;

    @ApiModelProperty(value = "秒杀商品信息")
    private SecKillDetailH5Response storeSeckill;

    @ApiModelProperty(value = "是否预约")
    private Boolean isMaa;
    @ApiModelProperty(value = "预约结果是否成功")
    private Boolean isResult;
    @ApiModelProperty(value = "预约人数")
    private Integer maaNum;

}
