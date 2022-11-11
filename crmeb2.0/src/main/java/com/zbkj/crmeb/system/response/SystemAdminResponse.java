package com.zbkj.crmeb.system.response;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* 系统管理员-Response对象
* @author: 零风
* @CreateDate: 2021/10/13 10:02
*/
@Data
public class SystemAdminResponse implements Serializable {

    private Integer id;

    private String account;

//    private String pwd;

    private String realName;

    private String roles;

    private String roleNames;

    private String lastIp;

    private Date lastTime;

    private Integer addTime;

    private Integer loginCount;

    private Integer level;

    private Boolean status;

//    private Boolean isDel;

    private String Token;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "是否接收短信")
    private Boolean isSms;

    @ApiModelProperty(value = "服务商表ID标识")
    private Integer spId;

    @ApiModelProperty(value = "服务商二级商户表ID标识")
    private Integer sptlId;

    @ApiModelProperty(value = "账户类型：0=普通、1=服务商、2=二级商户")
    private Integer type;
}
