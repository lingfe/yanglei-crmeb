package com.zbkj.crmeb.data.controller;

import com.common.CommonResult;
import com.zbkj.crmeb.data.service.BusinessTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 经营类型表-控制类
 * @author: 零风
 * @CreateDate: 2021/12/30 16:31
 */
@Slf4j
@RestController()
@RequestMapping("api/admin/businessType")
@Api(tags = "经营类型")
public class BusinessTypeController {

    @Autowired
    private BusinessTypeService businessTypeService;

    @ApiOperation(value = "树形结构")
    @RequestMapping(value = "/getListTree", method = RequestMethod.GET)
    public CommonResult<Object> getListTree(){
        return CommonResult.success(businessTypeService.getListTree());
    }

}
