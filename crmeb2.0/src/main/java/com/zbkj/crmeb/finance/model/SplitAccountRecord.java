package com.zbkj.crmeb.finance.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zbkj.crmeb.pub.model.PublicTableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 分账记录表
 * @author: 零风
 * @CreateDate: 2022/1/21 10:13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_spilt_account_record")
@ApiModel(value="SplitAccount-分账记录表", description="分账记录表")
public class SplitAccountRecord extends PublicTableField implements Serializable {

    //序列化
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "订单ID标识")
    private Integer orderid;

    @ApiModelProperty(value = "分账类型：1=微信支付订单分账、2=支付宝支付订单分账、3=积分支付订单分账")
    private Integer type;

    @ApiModelProperty(value = "状态:0=分账失败、1=分账成功、2=不再继续分账")
    private Integer status=0;

    @ApiModelProperty(value = "分账描述:成功描述、失败描述")
    private String description;

    public SplitAccountRecord(){}

    public SplitAccountRecord(Integer orderid, Integer type, String description, Integer status){
        this.orderid=orderid;
        this.type=type;
        this.description=description;
        this.status=status;
    }

}
