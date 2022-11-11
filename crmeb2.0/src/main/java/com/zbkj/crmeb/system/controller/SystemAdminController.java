package com.zbkj.crmeb.system.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemAdmin;
import com.zbkj.crmeb.system.request.SystemAdminAddRequest;
import com.zbkj.crmeb.system.request.SystemAdminRequest;
import com.zbkj.crmeb.system.response.SystemAdminResponse;
import com.zbkj.crmeb.system.service.SystemAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * 后台管理员表 前端控制器
 * @author: 零风
 * @CreateDate: 2022/5/10 10:11
 */
@Slf4j
@RestController
@RequestMapping("api/admin/system/admin")
@Api(tags = "后台用户服务")
public class SystemAdminController {

    @Autowired
    private SystemAdminService systemAdminService;

    /**
     * 分页显示后台管理员表
     * @param systemAdminRequest 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<SystemAdminResponse>>  getList(@Validated SystemAdminRequest systemAdminRequest, PageParamRequest pageParamRequest){
        SystemAdmin systemAdmin = new SystemAdmin();
        BeanUtils.copyProperties(systemAdminRequest, systemAdmin);
        CommonPage<SystemAdminResponse> systemAdminCommonPage = CommonPage.restPage(systemAdminService.getList(systemAdminRequest, pageParamRequest));
        return CommonResult.success(systemAdminCommonPage);
    }

    /**
     * 新增后台管理员表
     * @param systemAdminAddRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<SystemAdminResponse> save(@ModelAttribute SystemAdminAddRequest systemAdminAddRequest) throws Exception {
        SystemAdminResponse systemAdminResponse = systemAdminService.saveAdmin(systemAdminAddRequest);
        if(null != systemAdminResponse){
            return CommonResult.success(systemAdminResponse,"添加管理员成功");
        }else{
            return CommonResult.failed("添加管理员失败");
        }
    }

    /**
     * 删除后台管理员表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(systemAdminService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改后台管理员表
     * @param id integer id
     * @param systemAdminRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id,
                                       @ModelAttribute SystemAdminRequest systemAdminRequest) throws Exception{
        SystemAdminResponse systemAdminResponse = systemAdminService.updateAdmin(systemAdminRequest);
        if(null != systemAdminResponse){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询后台管理员表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<SystemAdmin> info(@RequestParam(value = "id") @Valid Integer id){
        SystemAdmin systemAdmin = systemAdminService.getById(id);
        return CommonResult.success(systemAdmin);
    }

    /**
     * 修改后台管理员状态
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "修改后台管理员状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.GET)
    public CommonResult<Object> updateStatus(@RequestParam(value = "id") @Valid Integer id, @RequestParam(value = "status") @Valid Boolean status){
        if (systemAdminService.updateStatus(id, status)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }

    /**
     * 修改后台管理员是否接收状态
     */
    @ApiOperation(value = "修改后台管理员是否接收状态")
    @RequestMapping(value = "/update/isSms", method = RequestMethod.GET)
    public CommonResult<Object> updateIsSms(@RequestParam(value = "id") @Valid Integer id){
        if (systemAdminService.updateIsSms(id)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }
}



