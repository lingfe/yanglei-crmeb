package com.zbkj.crmeb.front.controller;

import com.common.CommonResult;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.zbkj.crmeb.category.model.Category;
import com.zbkj.crmeb.category.service.CategoryService;
import com.zbkj.crmeb.category.vo.CategoryTreeVo;
import com.zbkj.crmeb.store.model.StoreBrands;
import com.zbkj.crmeb.store.request.StoreBrandsSearchRequest;
import com.zbkj.crmeb.store.response.StoreBrandsPreferredRsponse;
import com.zbkj.crmeb.store.service.StoreBrandsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: crmeb
 * @description: 商品-品牌信息，H5用户端使用
 * @author: 零风
 * @create: 2021-06-24 17:31
 **/
@Slf4j
@RestController("BrandsController")
@RequestMapping("api/front/brands")
@Api(tags = "商品 -- 品牌信息")
public class BrandsController {

    @Autowired
    private StoreBrandsService storeBrandsService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据品牌ID标识-得到下级分类list
     * @param brandId 品牌主分类ID
     * @return
     */
    @ApiOperation(value = "根据品牌ID标识-得到下级分类list-H5") //配合swagger使用
    @RequestMapping(value = "/getWhereBransIdCateList", method = RequestMethod.GET)
    @ApiImplicitParam(name="brandId", value="品牌ID标识")
    public CommonResult<List<CategoryTreeVo>> getWhereBransIdCateList(@RequestParam(name = "brandId") String brandId){
        //验证非空
        if(brandId == null || "".equals(brandId)){
            throw new CrmebException("品牌ID标识不能为空！");
        }

        //得到品牌信息
        StoreBrands storeBrands = storeBrandsService.getById(brandId);
        if(storeBrands == null) throw new CrmebException("品牌不存在！");
        if(storeBrands.getCateId()==null)throw new CrmebException("该品牌未绑定主分类！");

        //转成list
        List<Category> list= categoryService.getChildVoListByPid(Integer.valueOf(storeBrands.getCateId()));
        List<Integer> ids= list.stream().map(Category::getId).collect(Collectors.toList());

        //得到数据并返回
        List<CategoryTreeVo> listTree = categoryService.getListTree(Constants.CATEGORY_TYPE_PRODUCT ,1,ids);
        return CommonResult.success(listTree);
    }

    /**
     * 根据品牌主分类ID-得到下级分类list
     * @param bCateIds 品牌主分类ID
     * @return
     */
    @ApiOperation(value = "根据品牌主分类ID-得到下级分类list-H5") //配合swagger使用
    @RequestMapping(value = "/getWhereBCateList", method = RequestMethod.GET)
    @ApiImplicitParam(name="bCateIds", value="品牌主分类ID")
    public CommonResult<List<CategoryTreeVo>> getWhereBCateList(@RequestParam(name = "bCateIds") String bCateIds){
        //验证非空
        if(bCateIds == null || "".equals(bCateIds)){
            throw new CrmebException("品牌主分类ID不能为空！");
        }

        //转成list
        List<Integer> ids= Arrays.stream(bCateIds.split(",")).map(s -> Integer.valueOf(s.trim())).collect(Collectors.toList());

        //得到数据并返回
        List<CategoryTreeVo> listTree = categoryService.getListTree(Constants.CATEGORY_TYPE_PRODUCT ,1,ids);
        return CommonResult.success(listTree);
    }


    /**
     * 根据分类ID查询品牌list-h5使用,
     * @param request 请求参数
     * @return
     */
    @ApiOperation(value = "分类ID查询品牌list-H5") //配合swagger使用
    @RequestMapping(value = "/getCateIdList", method = RequestMethod.GET)
    public CommonResult<List<StoreBrands>> getCateIdList(
            @Validated StoreBrandsSearchRequest request){
        CommonResult<List<StoreBrands>> list=  CommonResult.success(storeBrandsService.getCateIdList(request));
        return  list;
    }

    /**
     * 获取-品牌优选-详情
     * @param pageParamRequest  分页参数
     * @return
     */
    @ApiOperation(value = "获取-品牌优选-详情") //配合swagger使用
    @RequestMapping(value = "/get/getBrandsPreferredInfo", method = RequestMethod.GET)
    public CommonResult<StoreBrandsPreferredRsponse> getBrandsPreferredInfo(
            @Validated StoreBrandsSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(storeBrandsService.getBrandsPreferredInfo(request,pageParamRequest));
    }

}
