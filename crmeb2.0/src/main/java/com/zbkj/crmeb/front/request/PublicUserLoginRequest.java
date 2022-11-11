package com.zbkj.crmeb.front.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户注册登录（公共请求类）
 * @author: 零风
 * @CreateDate: 2022/4/18 10:51
 */
@Data
public class PublicUserLoginRequest {

    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "用户密码")
    private String pwd;
    @ApiModelProperty(value = "性别")
    private Integer sex;
    @ApiModelProperty(value = "头像")
    private String avatarUrl;
    @ApiModelProperty(value = "用户昵称")
    private String nickName;
    @ApiModelProperty(value = "用户个人资料填写的省份")
    private String province;
    @ApiModelProperty(value = "普通用户个人资料填写的城市")
    private String city;
    @ApiModelProperty(value = "国家，如中国为CN")
    private String country;

    @ApiModelProperty(value = "唯一用户标识(可为空,有时候注册时会返回)")
    private String openid;

    @ApiModelProperty(value = "用户账号(当第三方进入时，填写平台内的用户ID标识)")
    private String account;
    @ApiModelProperty(value = "推荐人用户ID标识(当第三方进入时，不需要传值)")
    private Integer spreadPid;
    @ApiModelProperty(value = "登录账号token类型，静态值(当第三方进入时，必填)(与二级服务商表关联一一对应)：1-公众号、2-小程序、、、8=卡卡乐账号", required = true)
    private Integer tokenType;
    @ApiModelProperty(value = "服务商表ID标识(当第三方进入时，必填)")
    private Integer spId;
    @ApiModelProperty(value = "服务商二级商户表ID标识(当第三方进入时，必填)")
    private Integer sptlId;
    @ApiModelProperty(value = "推荐人信息(第三方进入专属，没有推荐人时不需要传)")
    private PublicUserLoginRequest sprRequest;
}
