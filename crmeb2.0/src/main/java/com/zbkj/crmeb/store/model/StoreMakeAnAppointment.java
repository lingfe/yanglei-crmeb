package com.zbkj.crmeb.store.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zbkj.crmeb.pub.model.PublicTableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 商城预约表
 * @author: 零风
 * @CreateDate: 2022/7/21 14:42
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_maa")
@ApiModel(value="StoreMakeAnAppointment-商城预约表", description="商城预约表")
public class StoreMakeAnAppointment extends PublicTableField implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户ID")
    private Integer uid;
    @ApiModelProperty(value = "关联id")
    private Integer linkId;
    @ApiModelProperty(value = "关联类型:-1=全部、0=商品、1=其他")
    private Integer linkType;

    @ApiModelProperty(value = "预约结果是否成功")
    private Boolean isResult;
    @ApiModelProperty(value = "状态: -1=全部、0=待确认、1=已成功、2=未成功、3=已结束")
    private Integer status;

}
