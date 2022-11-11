package com.zbkj.crmeb.system.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.zbkj.crmeb.system.model.SystemLogs;
import com.zbkj.crmeb.system.request.SystemLogsSearchRequest;
import com.zbkj.crmeb.system.service.SystemLogsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统日志-控制层
 * @author: 零风
 * @CreateDate: 2022/4/13 11:34
 */
@Slf4j
@RestController
@RequestMapping("api/admin/system/logs")
@Api(tags = "设置 -- 系统日志")
public class SystemLogsController {

    @Autowired
    private SystemLogsService systemLogsService;

    @ApiOperation(value = "分页列表",notes = Constants.SELECT) //配合swagger使用
    @RequestMapping(value = "/getPageList", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemLogs>> getList(
            @Validated SystemLogsSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(systemLogsService.getPageList(request, pageParamRequest)));
    }

}
