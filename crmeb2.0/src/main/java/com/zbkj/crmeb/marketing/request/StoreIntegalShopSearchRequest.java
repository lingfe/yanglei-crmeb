package com.zbkj.crmeb.marketing.request;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @program: crmeb
 * @description: 商城-营销-积分兑换商品表-（请求-搜索参数class类
 * @author: 零风
 * @create: 2021-07-01 16:50
 **/
@Data
public class StoreIntegalShopSearchRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "搜索关键字 商品id或者名称")
    private String keywords;

    @ApiModelProperty(value = "状态，0=全部、1=未开始、2=进行中、3=已结束、4=已失效")
    private Integer state;

    @ApiModelProperty(value = "是否显示，0=全部，1=显示，2=关闭")
    private Integer isShow;
}
