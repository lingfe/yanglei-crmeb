package com.zbkj.crmeb.pub.controller;

import com.common.CommonResult;
import com.exception.CrmebException;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @program: crmeb
 * @description: 用户登录
 * @author: 零风
 * @create: 2021-08-25 16:49
 **/
@Slf4j
@RestController()
@RequestMapping("api/public/fromData")
@Api(tags = "表单数据相关")
public class FromDataPublicController {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 获取-根据表单ID标识
     * @param typeOrFromID 表单ID标识
     * @Author lingfe
     * @Date  2021/8/12
     **/
    @ApiOperation(value = "获取-根据表单ID标识")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ApiImplicitParam(name = "typeOrFromID", value = "表单ID标识", required = true)
    public CommonResult<Map<String,String>> richTextData(@RequestParam(name = "typeOrFromID")Integer typeOrFromID){
        //验证ID非空
        if(typeOrFromID == null )throw new CrmebException("表单ID标识不能为空!");

        //得到表单数据-并验证非空
        Map<String,String> mapSysForm = systemConfigService.info(typeOrFromID);
        if(mapSysForm == null){
            throw new CrmebException("获取表单数据失败! 未对应表单id:"+typeOrFromID);
        }
        return CommonResult.success(mapSysForm);
    }

}
