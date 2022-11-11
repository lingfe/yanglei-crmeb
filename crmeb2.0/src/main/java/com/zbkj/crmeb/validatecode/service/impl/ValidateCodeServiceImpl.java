package com.zbkj.crmeb.validatecode.service.impl;

import com.constants.Constants;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.utils.RedisUtil;
import com.utils.ValidateCodeUtil;
import com.zbkj.crmeb.validatecode.model.ValidateCode;
import com.zbkj.crmeb.validatecode.service.ValidateCodeService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * ValidateCodeService-实现类
 * @author: 零风
 * @CreateDate: 2022/1/10 10:51
 */
@Service
public class ValidateCodeServiceImpl implements ValidateCodeService {

    @Resource
    private RedisUtil redisUtil;

    @Override
    public ValidateCode get() {
        //生成-验证码对象
        ValidateCodeUtil.Validate randomCode = ValidateCodeUtil.getRandomCode();//直接调用静态方法，返回验证码对象
        if(randomCode == null){
            return null;
        }

        //绘制验证码bs64格式-定义变量
        String value = randomCode.getValue().toLowerCase(); //验证码
        String md5Key = DigestUtils.md5Hex(value);          //加密后的-验证码
        String redisKey = this.getRedisKey(md5Key);         //拼接redis-key
        String base64Str = randomCode.getBase64Str();       //验证码-Base64格式值

        //放入缓存
        redisUtil.set(redisKey, value, 5L, TimeUnit.MINUTES);   //5分钟过期

        //返回-验证码code类
        return new ValidateCode(md5Key, CrmebUtil.getBase64Image(base64Str));
    }

    /**
     * 获取redis-key
     * @param md5Key value的md5加密值
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    public String getRedisKey(String md5Key){
        return Constants.VALIDATE_REDIS_KEY_PREFIX + md5Key;
    }

    @Override
    public boolean check(ValidateCode validateCode){
        if(!redisUtil.exists(getRedisKey(validateCode.getKey()))) throw new CrmebException("验证码错误");
        Object redisValue = redisUtil.get(getRedisKey(validateCode.getKey()));
        if(!redisValue.equals(validateCode.getCode().toLowerCase())){
            return false;
        }
        return true;
    }

    public static String st = "fmZc8AgVI3jwYdL+RRLyL5e9Yl6SzD92";
    public static String sk = "0b7…）*#~Nel4MGKdoEaRagoxQ";
}

