package com.zbkj.crmeb.front.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.zbkj.crmeb.front.response.IndexInfoResponse;
import com.zbkj.crmeb.front.response.IndexProductBannerResponse;
import com.zbkj.crmeb.front.response.IndexProductResponse;
import com.zbkj.crmeb.front.service.IndexService;
import com.zbkj.crmeb.marketing.response.StoreWearResponse;
import com.zbkj.crmeb.marketing.service.StoreWearService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * 用户 -- 用户中心
 * @author: 零风
 * @CreateDate: 2022/1/11 9:57
 */
@Slf4j
@RestController("IndexController")
@RequestMapping("api/front")
@Api(tags = "首页")
public class IndexController {

    @Autowired
    private IndexService indexService;

    @Autowired
    private StoreWearService storeWearService;

    @ApiOperation(value = "首页产品的轮播图和产品信息")
    @RequestMapping(value = "/groom/list/{type}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "类型 【1-精品推荐、 2-热门榜单、3-首发新品、4-促销单品、5-优品推荐】", dataType = "int", required = true)
    public CommonResult<IndexProductBannerResponse> getProductBanner(@PathVariable(value = "type") int type, PageParamRequest pageParamRequest) {
        if (type < Constants.INDEX_RECOMMEND_BANNER || type > Constants.INDEX_GOOD_BANNER) {
            return CommonResult.validateFailed();
        }
        return CommonResult.success(indexService.getProductBanner(type, pageParamRequest));
    }

    @ApiOperation(value = "首页数据")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public CommonResult<IndexInfoResponse> getIndexInfo() {
        return CommonResult.success(indexService.getIndexInfo());
    }

    /**
     * 首页商品列表
     */
    @ApiOperation(value = "首页商品列表")
    @RequestMapping(value = "/index/product/{type}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "类型 【1-精品推荐,2-热门榜单,3-首发新品, 4-促销单品,5-优品推荐】", dataType = "int", required = true)
    public CommonResult<CommonPage<IndexProductResponse>> getProductBanner(@PathVariable(value = "type") Integer type, PageParamRequest pageParamRequest) {
        if (type < Constants.INDEX_RECOMMEND_BANNER || type > Constants.INDEX_GOOD_BANNER) {
            return CommonResult.validateFailed();
        }
        return CommonResult.success(indexService.findIndexProductList(type, pageParamRequest));
    }

    /**
     * 热门搜索
     */
    @ApiOperation(value = "热门搜索")
    @RequestMapping(value = "/search/keyword", method = RequestMethod.GET)
    public CommonResult<List<HashMap<String, Object>>> hotKeywords() {
        return CommonResult.success(indexService.hotKeywords());
    }

    /**
     * 分享配置
     */
    @ApiOperation(value = "分享配置")
    @RequestMapping(value = "/share", method = RequestMethod.GET)
    public CommonResult<HashMap<String, String>> share() {
        return CommonResult.success(indexService.getShareConfig());
    }

    @ApiOperation(value = "首页-穿搭")
    @RequestMapping(value = "/index/wear", method = RequestMethod.GET)
    public CommonResult<StoreWearResponse> wear() {
        return CommonResult.success(storeWearService.getWearH5Index());
    }

}



