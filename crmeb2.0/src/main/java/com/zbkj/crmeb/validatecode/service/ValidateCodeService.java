package com.zbkj.crmeb.validatecode.service;

import com.zbkj.crmeb.validatecode.model.ValidateCode;

/**
 * 验证码-service层接口
 * @author: 零风
 * @CreateDate: 2022/1/10 10:49
 */
public interface ValidateCodeService {

    /**
     * 获取验证码
     * @Author 零风
     * @Date  2021/10/13
     * @return 验证对象
     */
    ValidateCode get();

    /**
     * 检测验证码
     * @param validateCode 验证码类对象
     * @Author 零风
     * @Date  2022/1/10
     * @return 检测结果
     */
    boolean check(ValidateCode validateCode);
}