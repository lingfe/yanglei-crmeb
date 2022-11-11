package com.zbkj.crmeb.sms.request;

import lombok.Data;

/**
 * 短信发送api第三方参数实体类
 * @author: 零风
 * @CreateDate: 2022/6/10 9:26
 */
@Data
public class SendSmsVo {
    private String uid;
    private String token;

    // 待发送短信手机号
    private String mobile;

    // 模版id
    private Integer template;

    // 发送参数
    private String param;

    private String content;

}
