package com.zbkj.crmeb.front.controller;

import com.common.CommonResult;
import com.zbkj.crmeb.system.vo.SystemGroupDataSignConfigVo;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserShareRecord;
import com.zbkj.crmeb.user.service.UserIntegralRecordService;
import com.zbkj.crmeb.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: crmeb
 * @description: 用户分享控制类-用户端
 * @author: 零风
 * @create: 2021-06-29 11:58
 **/
@Slf4j
@RestController("UserShareController")
@RequestMapping("api/front/user/share")
@Api(tags = "用户 -- 分享")
public class UserShareController {

    @Autowired
    private UserIntegralRecordService integralRecordService;

    @Autowired
    private UserService userService;

    /**
     * 分享朋友圈得积分
     * @return
     */
    @ApiOperation(value = "分享朋友圈得积分")
    @RequestMapping(value = "/sharePYQPoints", method = RequestMethod.POST)
    public CommonResult<SystemGroupDataSignConfigVo> sharePYQPoints(){
        SystemGroupDataSignConfigVo restPage = integralRecordService.sharePYQPoints();
        return CommonResult.success(restPage);
    }

    /**
     * 分享好友得积分
     * @return
     */
    @ApiOperation(value = "分享好友得积分")
    @RequestMapping(value = "/shareFriendsPoints/{shareUserId}", method = RequestMethod.GET)
    public CommonResult<SystemGroupDataSignConfigVo> shareFriendsPoints(){
        User user=userService.getInfoException();
        SystemGroupDataSignConfigVo restPage = integralRecordService.shareFriendsPoints(user.getUid());
        return CommonResult.success(restPage);
    }

}
