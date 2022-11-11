package com.zbkj.crmeb.pub.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.utils.ImageMergeUtil;
import com.utils.lingfe.images.ImageHttpGet;
import com.utils.vo.ImageMergeUtilVo;
import com.zbkj.crmeb.category.service.CategoryService;
import com.zbkj.crmeb.category.vo.CategoryTreeVo;
import com.zbkj.crmeb.system.model.SystemAttachment;
import com.zbkj.crmeb.system.request.SystemAttachmentSearchRequest;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.upload.vo.FileResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图片操作
 * @author: 零风
 * @CreateDate: 2022/1/19 14:10
 */
@Slf4j
@RestController
@RequestMapping("api/public/qrcode")
@Api(tags = "图片操作")
public class ImageMergeController {

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "图片分页列表") //配合swagger使用
    @RequestMapping(value = "/imgList", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemAttachment>>  getList(
            @Validated SystemAttachmentSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        CommonPage<SystemAttachment> systemAttachmentCommonPage = CommonPage.restPage(systemAttachmentService.getList(request, pageParamRequest));
        return CommonResult.success(systemAttachmentCommonPage);
    }

    @ApiOperation(value = "返回固定图片分类列表")
    @RequestMapping(value = "/imgTypeListTree", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="type", value="类型ID | 类型，1 产品分类，2 附件分类，3 文章分类， 4 设置分类， 5 菜单分类， 6 配置分类， 7 秒杀配置", example = "1"),
            @ApiImplicitParam(name="status", value="-1=全部，0=未生效，1=已生效", example = "1"),
            @ApiImplicitParam(name="name", value="模糊搜索", example = "电视")
    })
    public CommonResult<List<CategoryTreeVo>> getListTree(@RequestParam(name = "type") Integer type,
                                                          @RequestParam(name = "status") Integer status,
                                                          @RequestParam(name = "name", required = false) String name){
        List<CategoryTreeVo> listTree = categoryService.getListTree(type,status,name);
        return CommonResult.success(listTree);
    }

    @ApiOperation(value = "合并图片返回文件")
    @RequestMapping(value = "/mergeList", method = RequestMethod.POST)
    public CommonResult<Map<String, String>> mergeList(@RequestBody @Validated List<ImageMergeUtilVo> list){
        Map<String, String> map = new HashMap<>();
        map.put("base64Code", ImageMergeUtil.drawWordFile(list)); //需要云服务域名，如果需要存入数据库参照上传图片服务
        return CommonResult.success(map);
    }

    @ApiOperation(value = "通过图片url保存到服务器")
    @RequestMapping(value = "/imgUrlSaveFiles", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="imgUrl", value="图片url路径，多个用逗号隔开"),
            @ApiImplicitParam(name="folder", value="自定义文件夹,加在图片路径之中前面"),
            @ApiImplicitParam(name = "pid", value = "分类ID 0编辑器,1商品图片,2拼团图片,3砍价图片,4秒杀图片,5文章图片,6组合数据图,7前台用户,8微信系列 ", allowableValues = "range[0,1,2,3,4,5,6,7,8]")
    })
    public CommonResult<List<FileResultVo>> imgUrlSaveFiles(@RequestParam(value = "imgUrl")String imgUrl,
                                                            @RequestParam(value = "folder")String folder,
                                                            @RequestParam(value = "pid")Integer pid){
        //定义-图片上传-文件详情list
        List<FileResultVo> list=new ArrayList<>();

        //得到上传图片地址
        String uploadPath = systemConfigService.getValueByKey(Constants.UPLOAD_ROOT_PATH_CONFIG_KEY);
        //得到图片域名
        String imgPrefix = systemConfigService.getValueByKey(Constants.UPLOAD_LOCAL_URL);;

        //验证非空
        if(imgUrl==null||imgUrl.length()<=0){
            throw new CrmebException("图片url不能为空！");
        }

        //处理图片路径-并上传
        String[]  imgUrlArr=imgUrl.split(",");
        FileResultVo fileResultVo=null;
        for (String url: imgUrlArr) {
            //验证是否是本地url
            if(imgPrefix.indexOf(imgUrl)!=-1){
                fileResultVo=new FileResultVo();
                fileResultVo.setPrefix(imgPrefix);
                fileResultVo.setPath(uploadPath);
                fileResultVo.setUrl(imgUrl);
                fileResultVo.setFileName("最新下载");
                fileResultVo.setServerPath(imgPrefix);
            }else{
                //得到-文件详情对象
                fileResultVo=ImageHttpGet.getImages(url,folder, uploadPath,imgPrefix);
            }

            //执行保存文件记录
            systemAttachmentService.create(fileResultVo, pid);

            //添加到list
            list.add(fileResultVo);
        }

        //返回结果
        return CommonResult.success(list);
    }
}
