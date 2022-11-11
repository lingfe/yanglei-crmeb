package com.zbkj.crmeb.user.controller;


import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.front.request.UserSpreadPeopleRequest;
import com.zbkj.crmeb.front.response.IntegralUserResponse;
import com.zbkj.crmeb.front.response.UserSpreadPeopleItemResponse;
import com.zbkj.crmeb.front.service.UserCenterService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.request.*;
import com.zbkj.crmeb.user.response.TopDetail;
import com.zbkj.crmeb.user.response.UserResponse;
import com.zbkj.crmeb.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * 用户表-前端控制器
 * @author: 零风
 * @CreateDate: 2021/12/24 14:30
 */
@Slf4j
@RestController
@RequestMapping("api/admin/user")
@Api(tags = "会员管理")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserCenterService userCenterService;

    @ApiOperation(value = "用户积分统计信息")
    @RequestMapping(value = "/integral", method = RequestMethod.GET)
    @ApiImplicitParam(name = "uid",value = "用户表ID标识")
    public CommonResult<IntegralUserResponse> getIntegralUser(@RequestParam("uid")Integer uid){
        return CommonResult.success(userCenterService.getIntegralUser(uid));
    }

    @ApiOperation(value = "分页显示用户表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserResponse>> getList(
            @ModelAttribute UserSearchRequest request,
            @ModelAttribute PageParamRequest pageParamRequest){
        CommonPage<UserResponse> userCommonPage = CommonPage.restPage(userService.getList(request, pageParamRequest));
        return CommonResult.success(userCommonPage);
    }

    @ApiOperation(value = "用户推广人列表")
    @RequestMapping(value = "/spread/people", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserSpreadPeopleItemResponse>> getSpreadPeopleList(
            @Validated UserSpreadPeopleRequest request,
            @RequestParam("uid")Integer uid,
            @Validated PageParamRequest pageParamRequest) {
        List<UserSpreadPeopleItemResponse> spreadPeopleList = userCenterService.getSpreadPeopleList(request, pageParamRequest,uid);
        CommonPage<UserSpreadPeopleItemResponse> commonPage = CommonPage.restPage(spreadPeopleList);
        return CommonResult.success(commonPage);
    }

    @ApiOperation(value = "删除用户")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "用户表ID标识")
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(userService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "修改用户")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiImplicitParam(name = "id",value = "用户表ID标识")
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated UserUpdateRequest userRequest){
        userRequest.setUid(id);
        if(userService.updateUser(userRequest)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "修改用户手机号")
    @RequestMapping(value = "/update/phone", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户表ID标识"),
            @ApiImplicitParam(name = "phone",value = "新手机号")
    })
    public CommonResult<String> updatePhone(@RequestParam(name = "id") Integer id, @RequestParam(name = "phone") String phone){
        if(userService.updateUserPhone(id, phone)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询用户表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-10
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<User> info(@RequestParam(value = "id") Integer id){
        //根据ID得到数据
        User user = userService.getById(id);

        //是否为区域代理
        if(user.getIsGeneralAgency()==null){
            user.setIsGeneralAgency(false);
        }

        //返回
        return CommonResult.success(user);
    }

    /**
     * 根据参数类型查询会员对应的信息
     * @param userId Integer 会员id
     * @param type int 类型 0=消费记录，1=积分明细，2=签到记录，3=持有优惠券，4=余额变动，5=好友关系
     * @param pageParamRequest PageParamRequest 分页
     * @author stivepeim
     * @since 2020-04-10
     */
    @ApiOperation(value="会员详情")
    @RequestMapping(value = "/infobycondition", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",example = "1", required = true),
            @ApiImplicitParam(name = "type", value="0=消费记录，1=积分明细，2=签到记录，3=持有优惠券，4=余额变动，5=好友关系", example = "0"
                    , required = true)
    })
    public CommonResult<CommonPage<T>> infoByCondition(@RequestParam(name = "userId") @Valid Integer userId,
                                                       @RequestParam(name = "type") @Valid @Max(5) @Min(0) int type,
                                                       @ModelAttribute PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage((List<T>)userService.getInfoByCondition(userId,type,pageParamRequest)));
    }

    /**
     * 获取会员详情对应数据
     * @param userId
     * @return
     */
    @ApiOperation(value = "会员详情页Top数据")
    @RequestMapping(value = "topdetail", method = RequestMethod.GET)
    public CommonResult<TopDetail> topDetail (@RequestParam @Valid Integer userId){
        return CommonResult.success(userService.getTopDetail(userId));
    }

    /**
     * 操作积分
     */
    @ApiOperation(value = "积分余额")
    @RequestMapping(value = "/operate/founds", method = RequestMethod.GET)
    public CommonResult<Object> founds(@Validated UserOperateIntegralMoneyRequest request){
        if(userService.updateIntegralMoney(request)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "分组")
    @RequestMapping(value = "/group", method = RequestMethod.POST)
    public CommonResult<String> group(@RequestParam String id,
                                       @RequestParam String groupId){
        if(userService.group(id, groupId)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "标签")
    @RequestMapping(value = "/tag", method = RequestMethod.POST)
    public CommonResult<String> tag(@RequestParam String id,
                                       @RequestParam String tagId){
        if(userService.tag(id, tagId)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "修改上级推广人")
    @RequestMapping(value = "/update/spread", method = RequestMethod.POST)
    public CommonResult<String> editSpread(@Validated @RequestBody UserUpdateSpreadRequest request){
        if(userService.editSpread(request)){
            return CommonResult.success("修改成功");
        }else{
            return CommonResult.failed("修改失败");
        }
    }

    @ApiOperation(value = "发送营销短信")
    @RequestMapping(value = "/sms", method = RequestMethod.GET)
    public CommonResult<Object> sms(@Validated UserSmsRequest request){
        return CommonResult.success(userService.sms(request));
    }

}



