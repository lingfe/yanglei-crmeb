package com.zbkj.crmeb.user.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zbkj.crmeb.pub.model.PublicTableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 附件业务表
 * @author: 零风
 * @CreateDate: 2022/7/12 10:48
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_system_attachment_yw")
@ApiModel(value="SystemAttachmentYw-附件业务表", description="附件业务表")
public class SystemAttachmentYw extends PublicTableField implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户表ID标识")
    private Integer uid;

    @ApiModelProperty(value = "附件表ID标识")
    private Integer attid;

    @ApiModelProperty(value = "是否点赞")
    private Boolean isLike;

    @ApiModelProperty(value = "业务类型：-1=全部、0=点赞、1=下载")
    private Integer ywType;

}
