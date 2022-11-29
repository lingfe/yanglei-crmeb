package com.zbkj.crmeb.creator.controller;


import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.creator.request.IndexDataRequest;
import com.zbkj.crmeb.creator.request.IndexSearchRequest;
import com.zbkj.crmeb.creator.request.InfoCreatorSearchRequest;
import com.zbkj.crmeb.creator.response.IndexDataResponse;
import com.zbkj.crmeb.creator.response.IndexSearchResponse;
import com.zbkj.crmeb.creator.response.InfoCreatorHomeResponse;
import com.zbkj.crmeb.creator.service.CreatorIndexService;
import com.zbkj.crmeb.front.request.PublicUserLoginRequest;
import com.zbkj.crmeb.front.request.UserBindingPhoneUpdateRequest;
import com.zbkj.crmeb.front.response.LoginResponse;
import com.zbkj.crmeb.system.model.SystemAttachment;
import com.zbkj.crmeb.upload.vo.FileResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 首页-接口控制
 * @author: 零风
 * @CreateDate: 2022/1/11 9:57
 */
@Slf4j
@RestController("IndexController")
@RequestMapping("api/front/creator/index/")
@Api(tags = "首页")
public class CreatorIndexController {

    @Autowired
    private CreatorIndexService indexService;

    /**
     * 方法描述
     * @Author 李小杰
     * @Date  2022/8/11 10:12
     */
    public static void main(String[] args) {
        String name= "邹洁";
        System.out.print("\n\t\t\t\t\t\t");
        Thread obj = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                for (int i = 0; i < name.length(); i++) {
                    System.out.print(name.substring(i));
                    Thread.sleep(1000);
                }

                for (float y=1.5f;y>-1.5f;y-=0.1f){
                    Thread.sleep(300);
                    for (float x=-1.5f;x<1.5f;x+=0.05f){
                        float a=x*x+y*y-1;
                        if ((a*a*a-x*x*y*y*y)<0.0f){
                            System.out.print("*");
                            //Thread.sleep(300);
                        } else {
                            System.out.print(" ");
                        }
                    }
                    System.out.print("\n");
                }
            }
        });
        obj.start();
    }

    //ai
    private static void ai() {
        for (float y=1.5f;y>-1.5f;y-=0.1f){
            for (float x=-1.5f;x<1.5f;x+=0.05f){
                float a=x*x+y*y-1;
                if ((a*a*a-x*x*y*y*y)<0.0f){
                    System.out.print("*");
                }else {
                    System.out.print(" ");
                }
            }
            System.out.print("\n");
        }
    }

    @ApiOperation(value = "首页数据")
    @RequestMapping(value = "/indexData", method = RequestMethod.GET)
    public CommonResult<IndexDataResponse> indexData(@Validated IndexDataRequest request) {
        return CommonResult.success(indexService.indexData(request));
    }

    @ApiOperation(value = "首页搜索")
    @RequestMapping(value = "/indexSearch", method = RequestMethod.GET)
    public CommonResult<IndexSearchResponse> indexSearch(@Validated IndexSearchRequest request) {
        return CommonResult.success(indexService.indexSearch(request));
    }

    @ApiOperation(value = "创作者详细主页")
    @RequestMapping(value = "/infoCreatorHome", method = RequestMethod.GET)
    public CommonResult<InfoCreatorHomeResponse> infoCreatorHome(@Validated InfoCreatorSearchRequest request) {
        return CommonResult.success(indexService.infoCreatorHome(request));
    }

    @ApiOperation(value = "根据分类筛选创作者作品(分页)")
    @RequestMapping(value = "/whereCategoryScreenCreatorWorksList", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemAttachment>>  whereCategoryScreenCreatorWorksList(
            @Validated InfoCreatorSearchRequest request,
            @Validated PageParamRequest pageParamRequest) {
        CommonPage<SystemAttachment> systemAttachmentCommonPage = CommonPage.restPage(indexService.whereCategoryScreenCreatorWorksList(request, pageParamRequest));
        return CommonResult.success(systemAttachmentCommonPage);
    }

    @ApiOperation(value = "通过图片url保存到服务器")
    @RequestMapping(value = "/imgUrlSaveFiles", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="imgUrl", value="图片https-url路径，多个用逗号隔开"),
            @ApiImplicitParam(name="folder", value="自定义文件夹,加在图片路径之中前面"),
            @ApiImplicitParam(name = "pid", value = "分类ID", allowableValues = "range[0,1,2,3,4,5,6,7,8]")
    })
    public CommonResult<FileResultVo> imgUrlSaveFiles(
            @RequestParam(value = "imgUrl")String imgUrl,
            @RequestParam(value = "folder")String folder,
            @RequestParam(value = "pid")Integer pid){
        return CommonResult.success(indexService.imgUrlSaveFiles(imgUrl,folder,pid));
    }

    @ApiOperation(value = "字节小程序授权登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public CommonResult<LoginResponse> programLogin(@RequestParam("code") String code, @RequestBody @Validated PublicUserLoginRequest request){
        return CommonResult.success(indexService.zijieAuthorizeProgramLogin(code, request));
    }

    @ApiOperation(value = "绑定手机号")
    @RequestMapping(value = "/bindPhone", method = RequestMethod.GET)
    public CommonResult<Boolean> bindPhone(@Validated UserBindingPhoneUpdateRequest request) {
        return CommonResult.success(indexService.bindPhone(request));
    }

    @ApiOperation(value = "附件详细信息")
    @RequestMapping(value = "/attInfo", method = RequestMethod.GET)
    @ApiImplicitParam(name="attid",value = "附件ID标识")
    public CommonResult<Object> attInfo(@RequestParam(value = "attid")Integer attid) {
        return CommonResult.success(indexService.attInfo(attid));
    }

    @ApiOperation(value = "下载")
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ApiImplicitParam(name="attid",value = "附件ID标识")
    public CommonResult<Boolean> download(@RequestParam(value = "attid")Integer attid) {
        return CommonResult.success(indexService.download(attid));
    }

    @ApiOperation(value = "点赞")
    @RequestMapping(value = "/like", method = RequestMethod.GET)
    @ApiImplicitParam(name="attid",value = "附件ID标识")
    public CommonResult<Boolean> like(@RequestParam(value = "attid")Integer attid) {
        return CommonResult.success(indexService.like(attid));
    }

    @ApiOperation(value = "我的点赞")
    @RequestMapping(value = "/likeMy", method = RequestMethod.GET)
    public CommonResult<List<SystemAttachment>> likeMy() {
        return CommonResult.success(indexService.likeMy());
    }

//    @ApiOperation(value = "用户信息")
//    @RequestMapping(value = "/user", method = RequestMethod.GET)
//    public CommonResult<UserCenterResponse> getUserCenter(){
//        return CommonResult.success(userService.getUserCenter());
//    }

}



