package com.zbkj.crmeb.store.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 类功能描述
 * @author: 零风
 * @CreateDate: 2022/2/24 15:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreBrandsTest {

    @ApiModelProperty(value = "分类id，多个用逗号隔开")
    @NotBlank(message = "分类id不能为空")
    private String cateId;

    @ApiModelProperty(value = "品牌名称")
    @NotBlank(message = "品牌名称不能为空")
    private String brandName;

    @ApiModelProperty(value = "品牌图标")
    @NotBlank(message = "请上传品牌图标")
    private String brandImg;

    @ApiModelProperty(value = "品牌介绍")
    private String brandDesc;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "是否显示")
    private Boolean isDisplay;

    @ApiModelProperty(value = "是否优选")
    private Boolean isPreferred;

}
