package com.zbkj.crmeb.store.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: crmeb
 * @description: 品牌接口请求参数
 * @author: 零风
 * @create: 2021-06-23 10:42
 **/
@Data
public class StoreBrandsSearchRequest {

    @ApiModelProperty(value = "分类ID,多个用逗号隔开")
    private String cateId;

    @ApiModelProperty(value = "是否显示,0=显示全部，1=显示，2=不显示")
    private Integer isDisplay;

    @ApiModelProperty(value = "关键字")
    private String keywords;

    @ApiModelProperty(value = "是否优选")
    private Boolean isPreferred;

    @ApiModelProperty(value = "品牌ID")
    private Integer brandId;
}
