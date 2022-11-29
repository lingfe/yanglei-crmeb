package com.zbkj.crmeb.creator.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zbkj.crmeb.pub.model.PublicTableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 创作者用户收益记录表
 * @author: 零风
 * @CreateDate: 2022/7/29 10:50
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_profit")
@ApiModel(value="UserProfit-创作者用户收益记录表", description="创作者用户收益记录表")
public class UserProfit extends PublicTableField implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "创作者用户ID标识")
    private Integer uid;

    @ApiModelProperty(value = "日期字符串")
    private String dateStr;

    @ApiModelProperty(value = "曝光单价")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "下载量")
    private Integer downloadNum;

    @ApiModelProperty(value = "广告量")
    private Integer adNum;

    @ApiModelProperty(value = "收益")
    private BigDecimal profit;

}
