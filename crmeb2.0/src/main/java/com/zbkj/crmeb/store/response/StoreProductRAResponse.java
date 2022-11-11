package com.zbkj.crmeb.store.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zbkj.crmeb.store.model.StoreProduct;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 产品代理表-响应类
 * @author: 零风
 * @CreateDate: 2021/11/29 11:02
 */
@Data
public class StoreProductRAResponse {

    @ApiModelProperty(value = "产品代理表ID标识")
    private Integer id;

    @ApiModelProperty(value = "商品实体信息")
    private StoreProduct storeProduct;

    @ApiModelProperty(value = "区域代理ID标识")
    private Integer raId;
    @ApiModelProperty(value = "区域代理名称")
    private String raName;

}
