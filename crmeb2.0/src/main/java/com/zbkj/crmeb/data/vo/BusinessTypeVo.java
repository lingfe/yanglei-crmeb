package com.zbkj.crmeb.data.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.zbkj.crmeb.system.vo.SystemCityTreeVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 经营类型表-vo类
 * @author: 零风
 * @CreateDate: 2021/12/30 16:20
 */
@Data
public class BusinessTypeVo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "经营类型表ID标识")
    private Integer id;

    @ApiModelProperty(value = "上级ID,0=第一级")
    private Integer pid;

    @ApiModelProperty(value = "标题名称")
    private String title;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "是否显示")
    private Boolean isShow;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @JsonInclude(JsonInclude.Include.NON_EMPTY) //属性为 空（""）[] 或者为 NULL 都不序列化
    private List<BusinessTypeVo> child = new ArrayList<>();
}
