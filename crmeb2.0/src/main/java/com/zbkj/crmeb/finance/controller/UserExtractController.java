package com.zbkj.crmeb.finance.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.finance.model.UserExtract;
import com.zbkj.crmeb.finance.request.UserExtractRequest;
import com.zbkj.crmeb.finance.request.UserExtractSearchRequest;
import com.zbkj.crmeb.finance.response.BalanceResponse;
import com.zbkj.crmeb.finance.service.UserExtractService;
import com.zbkj.crmeb.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 用户提现表-前端控制器
 * @author: 零风
 * @CreateDate: 2022/4/27 16:16
 */
@Slf4j
@RestController
@RequestMapping("api/admin/finance/apply")
@Api(tags = "财务 -- 提现申请")
public class UserExtractController {

    @Autowired
    private UserExtractService userExtractService;

    @Autowired
    private UserService userService;

    /**
     * 分页显示用户提现记录
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-11
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserExtract>>  getList(@Validated UserExtractSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<UserExtract> userExtractCommonPage = CommonPage.restPage(userExtractService.getList(request, pageParamRequest));
        return CommonResult.success(userExtractCommonPage);
    }

    /**
     * 修改用户提现表
     * @param id integer id
     * @param userExtractRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-05-11
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated UserExtractRequest userExtractRequest){
        UserExtract userExtract = new UserExtract();    // 修改用户提现表
        BeanUtils.copyProperties(userExtractRequest, userExtract);
        userExtract.setId(id);
        if(userExtractService.updateById(userExtract)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 提现统计
     * @Param dateLimit 时间限制 today,yesterday,lately7,lately30,month,year,/yyyy-MM-dd hh:mm:ss,yyyy-MM-dd hh:mm:ss/
     * @author Mr.Zhang
     * @since 2020-05-11
     */
    @ApiOperation(value = "提现统计")
    @RequestMapping(value = "/balance", method = RequestMethod.POST)
    public CommonResult<BalanceResponse> balance(
            @RequestParam(value = "dateLimit", required = false,defaultValue = "")
                    String dateLimit){
        return CommonResult.success(userExtractService.getBalance(dateLimit));
    }

    @ApiOperation(value = "提现审核(旧)")
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    public CommonResult<String> updateStatus(@RequestParam(value = "id") Integer id,
                                             @RequestParam(value = "status",defaultValue = "审核状态： -1=未通过、 0=审核中、 1=已提现") Integer status,
                                             @RequestParam(value = "backMessage",defaultValue = "驳回原因", required = false) String backMessage){
        if(userExtractService.updateStatus(id, status, backMessage)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "提现审核(新)(云账户)")
    @RequestMapping(value = "/applyNew", method = RequestMethod.POST)
    public CommonResult<String> applyNew(
            @RequestParam(value = "id") Integer id,
            @RequestParam(value = "is",defaultValue = "是否通过") Boolean is,
            @RequestParam(value = "backMessage",defaultValue = "驳回原因", required = false) String backMessage){
        if(userExtractService.isExtract(is,id,backMessage)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "账户余额-提现审核(公共接口)")
    @RequestMapping(value = "/accountBalanceWithdrawalIsExtract", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "提现记录表ID标识"),
            @ApiImplicitParam(name = "is",value = "是否通过"),
            @ApiImplicitParam(name = "backMessage",value = "不通过原因")
    })
    public CommonResult<String> accountBalanceWithdrawalIsExtract(
            @RequestParam(value = "id") Integer id,
            @RequestParam(value = "is") Boolean is,
            @RequestParam(value = "backMessage", required = false) String backMessage){
        if(userService.accountBalanceWithdrawalIsExtract(is,id,backMessage)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }
}



