package com.zbkj.crmeb.system.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
* 后台登录页面-轮播图数据-响应类
* @author: 零风
* @CreateDate: 2021/10/13 9:40
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SystemGroupDataAdminLoginBannerResponse对象", description="后台登录页面轮播图")
public class SystemGroupDataAdminLoginBannerResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "图片")
    private String pic;

}
