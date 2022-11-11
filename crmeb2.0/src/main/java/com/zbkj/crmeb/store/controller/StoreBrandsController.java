package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.utils.DateUtil;
import com.zbkj.crmeb.store.model.StoreBrands;
import com.zbkj.crmeb.store.request.StoreBrandsSearchRequest;
import com.zbkj.crmeb.store.response.StoreBrandsPreferredRsponse;
import com.zbkj.crmeb.store.service.StoreBrandsService;
import com.zbkj.crmeb.store.vo.StoreBrandsVo;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @program: crmeb
 * @description: 品牌表控制层
 * @author: 零风
 * @create: 2021-06-23 11:56
 **/
@Slf4j
@RestController
@RequestMapping("api/admin/store/brands")
@Api(tags = "商品 -- 品牌信息")
public class StoreBrandsController {

    @Autowired
    private StoreBrandsService storeBrandsService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
     * 新增品牌信息
     * @param brands 新增参数
     * @author 零风
     * @since 2020-05-27
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated StoreBrands brands){
        brands.setCreateTime(DateUtil.nowDateTime());
        brands.setBrandImg(systemAttachmentService.clearPrefix(brands.getBrandImg()));
        if(storeBrandsService.save(brands)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }


    /**
     * 删除品牌信息
     * @param id  品牌id
     * @return
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public CommonResult<String> delete(@PathVariable Integer id){
        if(storeBrandsService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 分页查询品牌信息
     * @param pageParamRequest
     * @return
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreBrands>> getList(
            @Validated StoreBrandsSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        CommonResult<CommonPage<StoreBrands>> page=  CommonResult.success(CommonPage.restPage(storeBrandsService.getAdminList(request, pageParamRequest)));
        return  page;
    }

    /**
     * 修改品牌信息
     * @param storeBrands
     * @return
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated StoreBrands storeBrands){
        storeBrands.setBrandImg(systemAttachmentService.clearPrefix(storeBrands.getBrandImg()));
        if(storeBrandsService.updateById(storeBrands)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }


    /**
     * 修改是否显示
     * @param id 品牌ID标识
     * @return
     */
    @ApiOperation(value = "修改是否显示")
    @RequestMapping(value = "/updateIsDisplay/{id}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id", value="品牌id")
    public CommonResult<Object> updateIsDisplay(@Validated @PathVariable(name = "id") Integer id){
        if (storeBrandsService.updateIsDisplay(id)) {
            return CommonResult.success("修改成功");
        } else {
            return CommonResult.failed("修改失败");
        }
    }

    /**
     * 详情
     * @param id  品牌ID标识
     * @return
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/getInfoId/{id}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id", value="品牌id")
    public CommonResult<StoreBrandsVo> getInfoId(@Validated @PathVariable(name = "id") Integer id){
        CommonResult<StoreBrandsVo> ccsbVo= CommonResult.success(storeBrandsService.getInfoId(id));
        return ccsbVo;
    }

}
