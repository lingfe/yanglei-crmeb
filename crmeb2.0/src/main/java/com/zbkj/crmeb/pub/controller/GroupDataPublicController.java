package com.zbkj.crmeb.pub.controller;

import com.common.CommonResult;
import com.exception.CrmebException;
import com.zbkj.crmeb.system.service.SystemGroupDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * 组合数据-公共控制器
 * @author: 零风
 * @CreateDate: 2022/6/9 11:06
 */
@Slf4j
@RestController()
@RequestMapping("api/public/groupData")
@Api(tags = "组合数据相关")
public class GroupDataPublicController {

    @Autowired
    private SystemGroupDataService systemGroupDataService;

    @ApiOperation(value = "获取-根据组合数据ID标识")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ApiImplicitParam(name = "gid", value = "组合数据ID标识", required = true)
    public CommonResult<List<HashMap<String, Object>>> richTextData(@RequestParam(name = "gid")Integer gid){
        //验证ID非空
        if(gid == null )throw new CrmebException("组合数据ID标识不能为空!");

        //得到表单数据-并验证非空
        List<HashMap<String, Object>> list = systemGroupDataService.getListMapByGid(gid);
        if(list == null ) throw new CrmebException("获取组合数据失败! 未对应组合id:"+gid);
        return CommonResult.success(list);
    }


}
