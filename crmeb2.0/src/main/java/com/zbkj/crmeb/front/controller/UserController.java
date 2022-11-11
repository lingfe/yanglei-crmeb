package com.zbkj.crmeb.front.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.model.InvoiceRise;
import com.zbkj.crmeb.finance.model.UserExtract;
import com.zbkj.crmeb.finance.request.InvoiceRecordSearchRequest;
import com.zbkj.crmeb.finance.request.InvoiceRiseSearchRequest;
import com.zbkj.crmeb.finance.request.UserExtractRequest;
import com.zbkj.crmeb.finance.request.UserExtractSearchRequest;
import com.zbkj.crmeb.finance.response.InvoiceRecordResponse;
import com.zbkj.crmeb.finance.response.InvoiceRiseResponse;
import com.zbkj.crmeb.finance.service.InvoiceRecordService;
import com.zbkj.crmeb.finance.service.InvoiceRiseService;
import com.zbkj.crmeb.finance.service.UserExtractService;
import com.zbkj.crmeb.front.request.*;
import com.zbkj.crmeb.front.response.*;
import com.zbkj.crmeb.front.service.UserCenterService;
import com.zbkj.crmeb.system.model.SystemUserLevel;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.system.service.SystemGroupDataService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.model.UserIntegralRecord;
import com.zbkj.crmeb.user.response.UserGeneralAgentCommissionDataResponse;
import com.zbkj.crmeb.user.response.UserGeneralAgentDataResponse;
import com.zbkj.crmeb.user.response.UserIntegralRecordResponse;
import com.zbkj.crmeb.user.service.UserIntegralRecordService;
import com.zbkj.crmeb.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户 -- 用户中心
 */
@Slf4j
@RestController("FrontUserController")
@RequestMapping("api/front")
@Api(tags = "用户 -- 用户中心")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SystemGroupDataService systemGroupDataService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private UserExtractService userExtractService;

    @Autowired
    private UserIntegralRecordService userIntegralRecordService;

    @Autowired
    private InvoiceRiseService invoiceRiseService;

    @Autowired
    private InvoiceRecordService invoiceRecordService;

    @ApiOperation(value = "密码-修改登录密码")
    @RequestMapping(value = "/register/reset", method = RequestMethod.POST)
    public CommonResult<Boolean> password(@RequestBody @Validated PasswordRequest request){
        return CommonResult.success(userService.password(request));
    }

    @ApiOperation(value = "密码-设置支付密码")
    @RequestMapping(value = "/passwordPay", method = RequestMethod.POST)
    public CommonResult<Boolean> passwordPay(@RequestBody @Validated PasswordPayRequest request) {
        return CommonResult.success(userService.passwordPay(request));
    }

    @ApiOperation(value = "个人-修改个人资料")
    @RequestMapping(value = "/user/edit", method = RequestMethod.POST)
    public CommonResult<Boolean> personInfo(@RequestBody @Validated UserEditRequest request){
        User user = userService.getInfo();
        user.setAvatar(systemAttachmentService.clearPrefix(request.getAvatar()));
        user.setNickname(request.getNickname());
        return CommonResult.success(userService.updateById(user));
    }

    @ApiOperation(value = "个人-是否已经设置支付密码")
    @RequestMapping(value = "/isSetPayPwd", method = RequestMethod.GET)
    public CommonResult<Boolean> isSetPayPwd(){
        return CommonResult.success(userService.isSetPayPwd());
    }

    @ApiOperation(value = "个人-个人中心用户信息")
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public CommonResult<UserCenterResponse> getUserCenter(){
        return CommonResult.success(userService.getUserCenter());
    }

    @ApiOperation(value = "个人-换绑手机号校验")
    @RequestMapping(value = "update/binding/verify", method = RequestMethod.POST)
    public CommonResult<Boolean> updatePhoneVerify(@RequestBody @Validated UserBindingPhoneUpdateRequest request){
        return CommonResult.success(userService.updatePhoneVerify(request));
    }

    @ApiOperation(value = "个人-换绑手机号")
    @RequestMapping(value = "update/binding", method = RequestMethod.POST)
    public CommonResult<Boolean> updatePhone(@RequestBody @Validated UserBindingPhoneUpdateRequest request){
        return CommonResult.success(userService.updatePhone(request));
    }

    @ApiOperation(value = "个人-获取个人中心菜单")
    @RequestMapping(value = "/menu/user", method = RequestMethod.GET)
    public CommonResult<HashMap<String, Object>> getMenuUser(){
        return CommonResult.success(systemGroupDataService.getMenuUser());
    }

    @ApiOperation(value = "个人-个人会员等级列表")
    @RequestMapping(value = "/user/level/grade", method = RequestMethod.GET)
    public CommonResult<List<SystemUserLevel>> getUserLevelList(){
        return CommonResult.success(userCenterService.getUserLevelList());
    }

    @ApiOperation(value = "个人-经验记录")
    @RequestMapping(value = "/user/expList", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserBill>> getExperienceList(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(userCenterService.getUserExperienceList(pageParamRequest)));
    }

    @ApiOperation(value = "个人-用户资金统计")
    @RequestMapping(value = "/user/balance", method = RequestMethod.GET)
    public CommonResult<UserBalanceResponse>  getUserBalance(){
        return CommonResult.success(userCenterService.getUserBalance());
    }

    @ApiOperation(value = "个人-获取用户信息(根据ID)")
    @RequestMapping(value = "/getPayeeInfo", method = RequestMethod.GET)
    @ApiImplicitParam(name = "uid",value = "收款方用户ID标识",required = true)
    public CommonResult<User> getPayeeInfo(@RequestParam("uid")Integer uid){
        return CommonResult.success(userService.getById(uid));
    }

    @ApiOperation(value = "佣金-推广数据接口(昨天的佣金、累计提现佣金、当前佣金、待结算佣金)")
    @RequestMapping(value = "/commission", method = RequestMethod.GET)
    public CommonResult<UserCommissionResponse> getCommission(){
        return CommonResult.success(userCenterService.getCommission());
    }

    @ApiOperation(value = "佣金-推广佣金明细")
    @RequestMapping(value = "/spread/commission/detail", method = RequestMethod.GET)
    public CommonResult<CommonPage<SpreadCommissionDetailResponse>> getSpreadCommissionDetail(@Validated PageParamRequest pageParamRequest){
        PageInfo<SpreadCommissionDetailResponse> commissionDetail = userCenterService.getSpreadCommissionDetail(pageParamRequest);
        return CommonResult.success(CommonPage.restPage(commissionDetail));
    }

    @ApiOperation(value = "佣金-推广佣金/提现总和")
    @RequestMapping(value = "/spread/count/{type}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "类型 佣金类型3=佣金,4=提现", allowableValues = "range[3,4]", dataType = "int")
    public CommonResult<Map<String, BigDecimal>> getSpreadCountByType(@PathVariable int type){
        Map<String, BigDecimal> map = new HashMap<>();
        map.put("count", userCenterService.getSpreadCountByType(type));
        return CommonResult.success(map);
    }

    @ApiOperation(value = "佣金-佣金排行")
    @RequestMapping(value = "/brokerage_rank", method = RequestMethod.GET)
    public CommonResult<List<User>> getTopBrokerageListByDate(@RequestParam String type, @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(userCenterService.getTopBrokerageListByDate(type, pageParamRequest));
    }

    @ApiOperation(value = "佣金-当前用户在佣金排行第几名")
    @RequestMapping(value = "/user/brokerageRankNumber", method = RequestMethod.GET)
    public CommonResult<Integer> getNumberByTop(@RequestParam String type){
        return CommonResult.success(userCenterService.getNumberByTop(type));
    }

    @ApiOperation(value = "推广-推广人统计")
    @RequestMapping(value = "/spread/people/count", method = RequestMethod.GET)
    public CommonResult<UserSpreadPeopleResponse>  getSpreadPeopleCount(){
        return CommonResult.success(userCenterService.getSpreadPeopleCount(null));
    }

    @ApiOperation(value = "推广-推广人列表")
    @RequestMapping(value = "/spread/people", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserSpreadPeopleItemResponse>> getSpreadPeopleList(@Validated UserSpreadPeopleRequest request,
                                                                                      @Validated PageParamRequest pageParamRequest) {
        List<UserSpreadPeopleItemResponse> spreadPeopleList = userCenterService.getSpreadPeopleList(request, pageParamRequest,null);
        CommonPage<UserSpreadPeopleItemResponse> commonPage = CommonPage.restPage(spreadPeopleList);
        return CommonResult.success(commonPage);
    }

    @ApiOperation(value = "推广-推广人订单")
    @RequestMapping(value = "/spread/order", method = RequestMethod.GET)
    public CommonResult<UserSpreadOrderResponse>  getSpreadOrder(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(userCenterService.getSpreadOrder(pageParamRequest));
    }

    @ApiOperation(value = "推广-推广人排行")
    @RequestMapping(value = "/rank", method = RequestMethod.GET)
    public CommonResult<List<User>> getTopSpreadPeopleListByDate(@RequestParam(required = false) String type,
                                                                 @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(userCenterService.getTopSpreadPeopleListByDate(type, pageParamRequest));
    }

    @ApiOperation(value = "推广-推广海报图")
    @RequestMapping(value = "/user/spread/banner", method = RequestMethod.GET)
    public CommonResult<List<UserSpreadBannerResponse>>  getSpreadBannerList(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(userCenterService.getSpreadBannerList(pageParamRequest));
    }

    @ApiOperation(value = "推广-绑定推广关系（登录状态）")
    @RequestMapping(value = "/user/bindSpread", method = RequestMethod.GET)
    public CommonResult<Boolean> bindsSpread(Integer spreadPid){
        userService.bindSpread(spreadPid);
        return CommonResult.success();
    }

    @ApiOperation(value = "总代理用户-相关数据统计")
    @RequestMapping(value = "/get/getUserGeneralAgentData", method = RequestMethod.GET)
    public CommonResult<UserGeneralAgentDataResponse> getUserGeneralAgentData(){
        return CommonResult.success(userService.getUserGeneralAgentData());
    }

    @ApiOperation(value = "总代理用户-下级所有-订单列表")
    @RequestMapping(value = "/get/getUgaSubAllOrderList", method = RequestMethod.GET)
    @ApiImplicitParam(name = "status", value = "状态|0=未支付,1=待发货,2=待收货,3=待评价,4=已完成,-3=售后/退款", required = true)
    public CommonResult<CommonPage<OrderDetailResponse>> getUgaSubAllOrderList(
            @RequestParam(name = "status") Integer status,
            @ModelAttribute PageParamRequest pageRequest){
        return CommonResult.success(userService.getUgaSubAllOrderList(status, pageRequest));
    }

    @ApiOperation(value = "总代理用户-下级所有-订单详情统计数据")
    @RequestMapping(value = "/get/getUgaOrderDetails", method = RequestMethod.GET)
    @ApiImplicitParam(name = "dateType", value = "日期类型(0=今天、1=昨天、2=最近7天、3=本月、4=本年)", required = true)
    public CommonResult<GeneralAgentOrderDetailsResponse> getUgaOrderDetails(
            @RequestParam(name = "dateType") Integer dateType){
        return CommonResult.success(userService.getUgaOrderDetails(dateType));
    }

    @ApiOperation(value = "总代理用户-下级所有用户list")
    @RequestMapping(value = "/get/getUgaSupList", method = RequestMethod.GET)
    public CommonResult<List<User>> getUgaSupList(){
        //得到当前登录用户
        User user=userService.getInfoException();
        return CommonResult.success(userService.getUgaSupList(user));
    }

    @ApiOperation(value = "总代理用户-推广佣金明细")
    @RequestMapping(value = "/get/getUgaCommission", method = RequestMethod.GET)
    public CommonResult<UserGeneralAgentCommissionDataResponse> getUgaCommission(@Validated PageParamRequest pageParamRequest){
        UserGeneralAgentCommissionDataResponse commissionDetail = userService.getUgaCommission(pageParamRequest);
        return CommonResult.success(commissionDetail);
    }

    @ApiOperation(value = "提现-提现用户信息")
    @RequestMapping(value = "/extract/user", method = RequestMethod.GET)
    public CommonResult<UserExtractCashResponse> getExtractUser(){
        return CommonResult.success(userCenterService.getExtractUser());
    }

    @ApiOperation(value = "提现-提现银行列表")
    @RequestMapping(value = "/extract/bank", method = RequestMethod.GET)
    public CommonResult<List<String>> getExtractBank(){
        return CommonResult.success(userCenterService.getExtractBank());
    }

    @ApiOperation(value = "提现-提现记录")
    @RequestMapping(value = "/extract/record", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserExtractRecordResponse>> getExtractRecord(
            @Validated UserExtractSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(userExtractService.getPageInfo(request,null, pageParamRequest)));
    }

    @ApiOperation(value = "提现-佣金余额提现(旧方式(手动打款))")
    @RequestMapping(value = "/extract/cash", method = RequestMethod.POST)
    public CommonResult<Boolean> extractCash(@RequestBody @Validated UserExtractRequest request){
        return CommonResult.success(userCenterService.extractCash(request));
    }

    @ApiOperation(value = "提现-佣金余额提现(新方式(从云账户代付))")
    @RequestMapping(value = "/applyWithdrawal", method = RequestMethod.POST)
    public CommonResult<Boolean> applyWithdrawal(@RequestBody @Validated UserExtractRequest userExtractRequest) throws Exception {
        return CommonResult.success(userService.applyWithdrawal(userExtractRequest));
    }

    @ApiOperation(value = "提现-申请重试")
    @RequestMapping(value = "/retry", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="提现记录表ID标识", required = true)
    public CommonResult<Map<String, Object>> retry(@RequestParam Integer id)  {
        return CommonResult.success(userExtractService.retry(id));
    }

    @ApiOperation(value = "提现-取消提现")
    @RequestMapping(value = "/cancel", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="提现记录表ID标识", required = true)
    public CommonResult<Boolean> cancel(@RequestParam Integer id)  {
         return CommonResult.success(userExtractService.cancel(id));
    }

    @ApiOperation(value = "积分-积分统计信息")
    @RequestMapping(value = "/integral/user", method = RequestMethod.GET)
    public CommonResult<IntegralUserResponse> getIntegralUser(){
        return CommonResult.success(userCenterService.getIntegralUser(null));
    }

    @ApiOperation(value = "积分-积分记录")
    @RequestMapping(value = "/integral/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserIntegralRecordResponse>> getIntegralList(@Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(userCenterService.getUserIntegralRecordList(pageParamRequest)));
    }

    @ApiOperation(value = "积分-获取用户最新的积分收入记录")
    @RequestMapping(value = "/getNewestIncomeUserIntegralRecord", method = RequestMethod.GET)
    public CommonResult<UserIntegralRecord> getNewestIncomeUserIntegralRecord(){
        return CommonResult.success(userIntegralRecordService.getNewestIncomeUserIntegralRecord());
    }

    @ApiOperation(value = "积分-获取用户未读的积分记录,默认返回最新10条记录")
    @RequestMapping(value = "/getUnreadUserIntegralRecordList", method = RequestMethod.GET)
    @ApiImplicitParam(name = "num",value = "数量",defaultValue = "10")
    public CommonResult<List<UserIntegralRecord>> getUnreadUserIntegralRecordList(@RequestParam("num")Integer num){
        return CommonResult.success(userIntegralRecordService.getUnreadUserIntegralRecordList(num));
    }

    @ApiOperation(value = "积分-将未读的积分记录改变为已读")
    @RequestMapping(value = "/takeUnreadChangeRead", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "积分记录ID标识")
    public CommonResult<Boolean> takeUnreadChangeRead(@RequestParam("id")Integer id){
        return CommonResult.success(userIntegralRecordService.takeUnreadChangeRead(id));
    }

    @ApiOperation(value = "积分-用户收款二维码6")
    @RequestMapping(value = "/getUserCollectionCode", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> getUserCollectionCode(){
        return CommonResult.success(userService.getUserCollectionCode(null));
    }

    @ApiOperation(value = "积分-用户转账")
    @RequestMapping(value = "/transferAccountsIntegral", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid",value = "收款方用户ID标识",required = true),
            @ApiImplicitParam(name = "type",value = "转账方式",defaultValue = "0"),
            @ApiImplicitParam(name = "value",value = "转账金额值",required = true),
            @ApiImplicitParam(name = "pwd",value = "支付密码",required = true)
    })
    public CommonResult<Boolean> transferAccountsIntegral(@RequestParam("uid")Integer uid,
                                                          @RequestParam(name = "type",defaultValue = "0",required = false)Integer type,
                                                          @RequestParam("value")BigDecimal value,
                                                          @RequestParam("pwd")String pwd){
        return CommonResult.success(userService.transferAccountsIntegral(uid,type,value,pwd));
    }

    @ApiOperation(value = "积分-转账收款记录")
    @RequestMapping(value = "/transferAccountsIntegralList", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserIntegralRecordMonthResponse>> transferAccountsIntegralList(@Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(userService.transferAccountsIntegralList(pageParamRequest));
    }

    @ApiOperation(value = "发票-添加或修改发票抬头",notes = Constants.INSERT)
    @RequestMapping(value = "/invoice/saveInvoiceRise", method = RequestMethod.POST)
    public CommonResult<Boolean> saveInvoiceRise(@RequestBody @Validated InvoiceRise invoiceRise){
        return CommonResult.success(invoiceRiseService.sou(invoiceRise));
    }

    @ApiOperation(value = "发票-发票抬头详细信息显示",notes = Constants.SELECT)
    @RequestMapping(value = "/invoice/invoiceRiseInfo", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "发票抬头id标识",required = true)
    public CommonResult<InvoiceRiseResponse> invoiceRiseInfo(@RequestParam("id")Integer id){
        return CommonResult.success(invoiceRiseService.info(id));
    }

    @ApiOperation(value = "发票-分页发票抬头列表",notes = Constants.SELECT) //配合swagger使用
    @RequestMapping(value = "/getRisePageList", method = RequestMethod.GET)
    public CommonResult<CommonPage<InvoiceRise>> getRiseList(
            @Validated InvoiceRiseSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(invoiceRiseService.getPageList(request, pageParamRequest)));
    }

    @ApiOperation(value = "发票-分页发票记录列表",notes = Constants.SELECT) //配合swagger使用
    @RequestMapping(value = "/getRecordPageList", method = RequestMethod.GET)
    public CommonResult<CommonPage<InvoiceRecordResponse>> getRecordList(
            @Validated InvoiceRecordSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(invoiceRecordService.getPageList(request, pageParamRequest)));
    }

    @ApiOperation(value = "发票-删除发票抬头",notes = Constants.DELETE)
    @RequestMapping(value = "/invoice/deleteRise", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "发票抬头id标识")
    public CommonResult<String> deleteRise(@RequestParam("id") Integer id) {
        if (invoiceRiseService.removeById(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "发票-发票记录详细信息显示",notes = Constants.SELECT)
    @RequestMapping(value = "/invoice/invoiceRecordInfo", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "发票记录表id标识",required = true)
    public CommonResult<InvoiceRecordResponse> invoiceRecordInfo(@RequestParam("id")Integer id){
        return CommonResult.success(invoiceRecordService.info(id));
    }

    @ApiOperation(value = "账户余额-其他转入账户余额(公共接口)")
    @RequestMapping(value = "/transferIn", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "price",value = "转入金额",required = true),
            @ApiImplicitParam(name = "type",value = "转入账户余额类型(1=佣金、2=积分)",required = true)
    })
    public CommonResult<Boolean> transferIn(@RequestParam(name = "price") BigDecimal price,
                                            @RequestParam(name = "type") Integer type){
        User user=userService.getInfoException();
        Map<String,String> map=userService.transferIn(price,type,user,BigDecimal.ZERO);
        if(Boolean.valueOf(map.get("result"))){
            return CommonResult.success(map.get("msg"));
        }else{
            return CommonResult.failed(map.get("msg"));
        }
    }

    @ApiOperation(value = "账户余额-申请提现(公共接口)")
    @RequestMapping(value = "/weixinPayPlatform", method = RequestMethod.POST)
    public CommonResult<UserExtract> weixinPayPlatform(@RequestBody @Validated UserExtractRequest userExtractRequest) {
        return CommonResult.success(userService.accountBalanceWithdrawal(userExtractRequest));
    }

}



