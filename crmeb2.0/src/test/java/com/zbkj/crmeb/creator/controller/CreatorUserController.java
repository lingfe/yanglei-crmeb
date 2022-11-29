package com.zbkj.crmeb.creator.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.creator.request.GetWorksListRequest;
import com.zbkj.crmeb.creator.response.CreatorDataResponse;
import com.zbkj.crmeb.creator.response.CreatorProfitDataResponse;
import com.zbkj.crmeb.creator.response.SystemAttachmentResponse;
import com.zbkj.crmeb.creator.service.CreatorUserService;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.upload.vo.FileResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 创作者用户-接口控制类
 * @author: 零风
 * @CreateDate: 2022/7/15 9:32
 */
@Slf4j
@RestController("UserCreatorController")
@RequestMapping("api/front/creator/userCreator")
@Api(tags = "创作者用户")
public class CreatorUserController {

    @Autowired
    private CreatorUserService userCreatorService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @ApiOperation(value = "作品分类")
    @RequestMapping(value = "/getWorksType", method = RequestMethod.GET)
    @ApiImplicitParam(name = "isNull",value = "是否只查询作品不为空的分类")
    public CommonResult<List<Map<String,Object>>> getWorksType(@RequestParam(value = "isNull")Boolean isNull) {
        return CommonResult.success(userCreatorService.getWorksType(isNull));
    }

    @ApiOperation(value = "作品列表")
    @RequestMapping(value = "/getWorksList", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemAttachmentResponse>> getWorksList(
            @Validated GetWorksListRequest request, @Validated PageParamRequest pageParamRequest) {
        CommonPage<SystemAttachmentResponse> systemAttachmentCommonPage = CommonPage.restPage(userCreatorService.getWorksList(request, pageParamRequest));
        return CommonResult.success(systemAttachmentCommonPage);
    }

    @ApiOperation(value = "删除作品")
    @RequestMapping(value = "/delete/{ids}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "ids",value = "多个用逗号隔开")
    public CommonResult<String> delete(@PathVariable String ids){
        if(systemAttachmentService.removeByIds(CrmebUtil.stringToArray(ids))){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "上传作品")
    @RequestMapping(value = "/image", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "multipart", value = "文件"),
            @ApiImplicitParam(name = "pid", value = "分类ID(必填)"),
            @ApiImplicitParam(name = "isUrl",value = "是否通过URL上传"),
            @ApiImplicitParam(name = "url",value = "图片url"),
    })
    public CommonResult<FileResultVo> uploadWorks(
            MultipartFile multipart,
            @RequestParam(value = "pid") Integer pid,
            @RequestParam(value = "isUrl") Boolean isUrl,
            @RequestParam(value = "url") String url) throws IOException {
        if(isUrl){
            return CommonResult.success(userCreatorService.UploadUrl(pid,url));
        }else{
            return CommonResult.success(userCreatorService.uploadWorks(multipart, "lingfe", pid));
        }
    }

    @ApiOperation(value = "设置邀请人")
    @RequestMapping(value = "/setSpreadUid", method = RequestMethod.GET)
    @ApiImplicitParam(name = "spreadUid", value = "推荐人用户ID标识")
    public CommonResult<Boolean> setSpreadUid(@RequestParam("spreadUid") Integer spreadUid){
        return CommonResult.success(userCreatorService.setSpreadUid(spreadUid));
    }

    @ApiOperation(value = "设置支付宝账号")
    @RequestMapping(value = "/setAlipay", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "realName", value = "真实姓名"),
            @ApiImplicitParam(name = "alipay", value = "支付宝账号")
    })
    public CommonResult<Boolean> setAlipay(
            @RequestParam("realName") String realName,
            @RequestParam("alipay")String alipay){
        return CommonResult.success(userCreatorService.setAlipay(realName,alipay));
    }

    @ApiOperation(value = "我的推荐")
    @RequestMapping(value = "/invitationMy", method = RequestMethod.GET)
    public CommonResult<CommonPage<CreatorDataResponse>> invitationMy(PageParamRequest pageParamRequest){
        CommonPage<CreatorDataResponse> userCommonPage = CommonPage.restPage(userCreatorService.invitationMy(pageParamRequest));
        return CommonResult.success(userCommonPage);
    }

    @ApiOperation(value = "我的资料")
    @RequestMapping(value = "/myInfo", method = RequestMethod.GET)
    public CommonResult<CreatorDataResponse> myInfo(){
        return CommonResult.success(userCreatorService.myInfo());
    }

    @ApiOperation(value = "获取收益数据")
    @RequestMapping(value = "/getCreatorProfitData", method = RequestMethod.GET)
    public CommonResult<CreatorProfitDataResponse> getCreatorProfitData(){
        return CommonResult.success(userCreatorService.getCreatorProfitData());
    }

}
