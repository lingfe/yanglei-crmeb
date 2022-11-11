package com.zbkj.crmeb.store.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zbkj.crmeb.pub.model.PublicTableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 服务商二级商户表
 * @author: 零风
 * @CreateDate: 2022/5/9 14:31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_service_provider_twolevel")
@ApiModel(value="eb_service_provider_twolevel", description="服务商二级商户表")
public class ServiceProviderTwolevel extends PublicTableField {

    @ApiModelProperty(value = "自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "服务商表ID标识")
    private Integer spId;

    @ApiModelProperty(value = "佣金比例(%)")
    private BigDecimal rate;

    @ApiModelProperty(value = "二级商户名称")
    private String sptlName;

    @ApiModelProperty(value = "二级商户联系电话")
    private String sptlPhone;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;

}