package com.zbkj.crmeb.system.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 系统日志
 * @author: 零风
 * @CreateDate: 2022/4/13 10:17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_system_logs")
@ApiModel(value="SystemLogs-系统日志表", description="系统日志表")
public class SystemLogs {

    @ApiModelProperty(value = "系统日志表ID标识")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户ID标识")
    private Integer uid;

    @ApiModelProperty(value = "管理员ID标识")
    private Integer adminId;

    @ApiModelProperty(value = "操作方法")
    private String operationMethod;

    @ApiModelProperty(value = "请求url(接口)")
    private String url;

    @ApiModelProperty(value = "操作简称")
    private String operationDesc;

    @ApiModelProperty(value = "参数")
    private String parameter;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "耗时")
    private Integer timeConsuming;

    @ApiModelProperty(value = "操作时间")
    private String operationTime;

    @ApiModelProperty(value = "日志类型= 1:正常操作日志 2:错误日志")
    private byte logType;

    @ApiModelProperty(value = "错误日志msg")
    private String errorLogMsg;

    @ApiModelProperty(value = "操作类型")
    private String operationType;

    @ApiModelProperty(value = "用户代理")
    private String userAgent;

    @ApiModelProperty(value = "操作系统")
    private String deviceName;

    @ApiModelProperty(value = "浏览器名称")
    private String browserName;

    @ApiModelProperty(value = "服务器地址")
    private String serverAddress;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "返回数据")
    private String returnData;

}
