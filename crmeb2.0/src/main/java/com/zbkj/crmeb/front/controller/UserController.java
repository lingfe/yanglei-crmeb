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
 * ?????? -- ????????????
 */
@Slf4j
@RestController("FrontUserController")
@RequestMapping("api/front")
@Api(tags = "?????? -- ????????????")
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

    @ApiOperation(value = "??????-??????????????????")
    @RequestMapping(value = "/register/reset", method = RequestMethod.POST)
    public CommonResult<Boolean> password(@RequestBody @Validated PasswordRequest request){
        return CommonResult.success(userService.password(request));
    }

    @ApiOperation(value = "??????-??????????????????")
    @RequestMapping(value = "/passwordPay", method = RequestMethod.POST)
    public CommonResult<Boolean> passwordPay(@RequestBody @Validated PasswordPayRequest request) {
        return CommonResult.success(userService.passwordPay(request));
    }

    @ApiOperation(value = "??????-??????????????????")
    @RequestMapping(value = "/user/edit", method = RequestMethod.POST)
    public CommonResult<Boolean> personInfo(@RequestBody @Validated UserEditRequest request){
        User user = userService.getInfo();
        user.setAvatar(systemAttachmentService.clearPrefix(request.getAvatar()));
        user.setNickname(request.getNickname());
        return CommonResult.success(userService.updateById(user));
    }

    @ApiOperation(value = "??????-??????????????????????????????")
    @RequestMapping(value = "/isSetPayPwd", method = RequestMethod.GET)
    public CommonResult<Boolean> isSetPayPwd(){
        return CommonResult.success(userService.isSetPayPwd());
    }

    @ApiOperation(value = "??????-????????????????????????")
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public CommonResult<UserCenterResponse> getUserCenter(){
        return CommonResult.success(userService.getUserCenter());
    }

    @ApiOperation(value = "??????-?????????????????????")
    @RequestMapping(value = "update/binding/verify", method = RequestMethod.POST)
    public CommonResult<Boolean> updatePhoneVerify(@RequestBody @Validated UserBindingPhoneUpdateRequest request){
        return CommonResult.success(userService.updatePhoneVerify(request));
    }

    @ApiOperation(value = "??????-???????????????")
    @RequestMapping(value = "update/binding", method = RequestMethod.POST)
    public CommonResult<Boolean> updatePhone(@RequestBody @Validated UserBindingPhoneUpdateRequest request){
        return CommonResult.success(userService.updatePhone(request));
    }

    @ApiOperation(value = "??????-????????????????????????")
    @RequestMapping(value = "/menu/user", method = RequestMethod.GET)
    public CommonResult<HashMap<String, Object>> getMenuUser(){
        return CommonResult.success(systemGroupDataService.getMenuUser());
    }

    @ApiOperation(value = "??????-????????????????????????")
    @RequestMapping(value = "/user/level/grade", method = RequestMethod.GET)
    public CommonResult<List<SystemUserLevel>> getUserLevelList(){
        return CommonResult.success(userCenterService.getUserLevelList());
    }

    @ApiOperation(value = "??????-????????????")
    @RequestMapping(value = "/user/expList", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserBill>> getExperienceList(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(userCenterService.getUserExperienceList(pageParamRequest)));
    }

    @ApiOperation(value = "??????-??????????????????")
    @RequestMapping(value = "/user/balance", method = RequestMethod.GET)
    public CommonResult<UserBalanceResponse>  getUserBalance(){
        return CommonResult.success(userCenterService.getUserBalance());
    }

    @ApiOperation(value = "??????-??????????????????(??????ID)")
    @RequestMapping(value = "/getPayeeInfo", method = RequestMethod.GET)
    @ApiImplicitParam(name = "uid",value = "???????????????ID??????",required = true)
    public CommonResult<User> getPayeeInfo(@RequestParam("uid")Integer uid){
        return CommonResult.success(userService.getById(uid));
    }

    @ApiOperation(value = "??????-??????????????????(?????????????????????????????????????????????????????????????????????)")
    @RequestMapping(value = "/commission", method = RequestMethod.GET)
    public CommonResult<UserCommissionResponse> getCommission(){
        return CommonResult.success(userCenterService.getCommission());
    }

    @ApiOperation(value = "??????-??????????????????")
    @RequestMapping(value = "/spread/commission/detail", method = RequestMethod.GET)
    public CommonResult<CommonPage<SpreadCommissionDetailResponse>> getSpreadCommissionDetail(@Validated PageParamRequest pageParamRequest){
        PageInfo<SpreadCommissionDetailResponse> commissionDetail = userCenterService.getSpreadCommissionDetail(pageParamRequest);
        return CommonResult.success(CommonPage.restPage(commissionDetail));
    }

    @ApiOperation(value = "??????-????????????/????????????")
    @RequestMapping(value = "/spread/count/{type}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "?????? ????????????3=??????,4=??????", allowableValues = "range[3,4]", dataType = "int")
    public CommonResult<Map<String, BigDecimal>> getSpreadCountByType(@PathVariable int type){
        Map<String, BigDecimal> map = new HashMap<>();
        map.put("count", userCenterService.getSpreadCountByType(type));
        return CommonResult.success(map);
    }

    @ApiOperation(value = "??????-????????????")
    @RequestMapping(value = "/brokerage_rank", method = RequestMethod.GET)
    public CommonResult<List<User>> getTopBrokerageListByDate(@RequestParam String type, @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(userCenterService.getTopBrokerageListByDate(type, pageParamRequest));
    }

    @ApiOperation(value = "??????-????????????????????????????????????")
    @RequestMapping(value = "/user/brokerageRankNumber", method = RequestMethod.GET)
    public CommonResult<Integer> getNumberByTop(@RequestParam String type){
        return CommonResult.success(userCenterService.getNumberByTop(type));
    }

    @ApiOperation(value = "??????-???????????????")
    @RequestMapping(value = "/spread/people/count", method = RequestMethod.GET)
    public CommonResult<UserSpreadPeopleResponse>  getSpreadPeopleCount(){
        return CommonResult.success(userCenterService.getSpreadPeopleCount(null));
    }

    @ApiOperation(value = "??????-???????????????")
    @RequestMapping(value = "/spread/people", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserSpreadPeopleItemResponse>> getSpreadPeopleList(@Validated UserSpreadPeopleRequest request,
                                                                                      @Validated PageParamRequest pageParamRequest) {
        List<UserSpreadPeopleItemResponse> spreadPeopleList = userCenterService.getSpreadPeopleList(request, pageParamRequest,null);
        CommonPage<UserSpreadPeopleItemResponse> commonPage = CommonPage.restPage(spreadPeopleList);
        return CommonResult.success(commonPage);
    }

    @ApiOperation(value = "??????-???????????????")
    @RequestMapping(value = "/spread/order", method = RequestMethod.GET)
    public CommonResult<UserSpreadOrderResponse>  getSpreadOrder(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(userCenterService.getSpreadOrder(pageParamRequest));
    }

    @ApiOperation(value = "??????-???????????????")
    @RequestMapping(value = "/rank", method = RequestMethod.GET)
    public CommonResult<List<User>> getTopSpreadPeopleListByDate(@RequestParam(required = false) String type,
                                                                 @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(userCenterService.getTopSpreadPeopleListByDate(type, pageParamRequest));
    }

    @ApiOperation(value = "??????-???????????????")
    @RequestMapping(value = "/user/spread/banner", method = RequestMethod.GET)
    public CommonResult<List<UserSpreadBannerResponse>>  getSpreadBannerList(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(userCenterService.getSpreadBannerList(pageParamRequest));
    }

    @ApiOperation(value = "??????-????????????????????????????????????")
    @RequestMapping(value = "/user/bindSpread", method = RequestMethod.GET)
    public CommonResult<Boolean> bindsSpread(Integer spreadPid){
        userService.bindSpread(spreadPid);
        return CommonResult.success();
    }

    @ApiOperation(value = "???????????????-??????????????????")
    @RequestMapping(value = "/get/getUserGeneralAgentData", method = RequestMethod.GET)
    public CommonResult<UserGeneralAgentDataResponse> getUserGeneralAgentData(){
        return CommonResult.success(userService.getUserGeneralAgentData());
    }

    @ApiOperation(value = "???????????????-????????????-????????????")
    @RequestMapping(value = "/get/getUgaSubAllOrderList", method = RequestMethod.GET)
    @ApiImplicitParam(name = "status", value = "??????|0=?????????,1=?????????,2=?????????,3=?????????,4=?????????,-3=??????/??????", required = true)
    public CommonResult<CommonPage<OrderDetailResponse>> getUgaSubAllOrderList(
            @RequestParam(name = "status") Integer status,
            @ModelAttribute PageParamRequest pageRequest){
        return CommonResult.success(userService.getUgaSubAllOrderList(status, pageRequest));
    }

    @ApiOperation(value = "???????????????-????????????-????????????????????????")
    @RequestMapping(value = "/get/getUgaOrderDetails", method = RequestMethod.GET)
    @ApiImplicitParam(name = "dateType", value = "????????????(0=?????????1=?????????2=??????7??????3=?????????4=??????)", required = true)
    public CommonResult<GeneralAgentOrderDetailsResponse> getUgaOrderDetails(
            @RequestParam(name = "dateType") Integer dateType){
        return CommonResult.success(userService.getUgaOrderDetails(dateType));
    }

    @ApiOperation(value = "???????????????-??????????????????list")
    @RequestMapping(value = "/get/getUgaSupList", method = RequestMethod.GET)
    public CommonResult<List<User>> getUgaSupList(){
        //????????????????????????
        User user=userService.getInfoException();
        return CommonResult.success(userService.getUgaSupList(user));
    }

    @ApiOperation(value = "???????????????-??????????????????")
    @RequestMapping(value = "/get/getUgaCommission", method = RequestMethod.GET)
    public CommonResult<UserGeneralAgentCommissionDataResponse> getUgaCommission(@Validated PageParamRequest pageParamRequest){
        UserGeneralAgentCommissionDataResponse commissionDetail = userService.getUgaCommission(pageParamRequest);
        return CommonResult.success(commissionDetail);
    }

    @ApiOperation(value = "??????-??????????????????")
    @RequestMapping(value = "/extract/user", method = RequestMethod.GET)
    public CommonResult<UserExtractCashResponse> getExtractUser(){
        return CommonResult.success(userCenterService.getExtractUser());
    }

    @ApiOperation(value = "??????-??????????????????")
    @RequestMapping(value = "/extract/bank", method = RequestMethod.GET)
    public CommonResult<List<String>> getExtractBank(){
        return CommonResult.success(userCenterService.getExtractBank());
    }

    @ApiOperation(value = "??????-????????????")
    @RequestMapping(value = "/extract/record", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserExtractRecordResponse>> getExtractRecord(
            @Validated UserExtractSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(userExtractService.getPageInfo(request,null, pageParamRequest)));
    }

    @ApiOperation(value = "??????-??????????????????(?????????(????????????))")
    @RequestMapping(value = "/extract/cash", method = RequestMethod.POST)
    public CommonResult<Boolean> extractCash(@RequestBody @Validated UserExtractRequest request){
        return CommonResult.success(userCenterService.extractCash(request));
    }

    @ApiOperation(value = "??????-??????????????????(?????????(??????????????????))")
    @RequestMapping(value = "/applyWithdrawal", method = RequestMethod.POST)
    public CommonResult<Boolean> applyWithdrawal(@RequestBody @Validated UserExtractRequest userExtractRequest) throws Exception {
        return CommonResult.success(userService.applyWithdrawal(userExtractRequest));
    }

    @ApiOperation(value = "??????-????????????")
    @RequestMapping(value = "/retry", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="???????????????ID??????", required = true)
    public CommonResult<Map<String, Object>> retry(@RequestParam Integer id)  {
        return CommonResult.success(userExtractService.retry(id));
    }

    @ApiOperation(value = "??????-????????????")
    @RequestMapping(value = "/cancel", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="???????????????ID??????", required = true)
    public CommonResult<Boolean> cancel(@RequestParam Integer id)  {
         return CommonResult.success(userExtractService.cancel(id));
    }

    @ApiOperation(value = "??????-??????????????????")
    @RequestMapping(value = "/integral/user", method = RequestMethod.GET)
    public CommonResult<IntegralUserResponse> getIntegralUser(){
        return CommonResult.success(userCenterService.getIntegralUser(null));
    }

    @ApiOperation(value = "??????-????????????")
    @RequestMapping(value = "/integral/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserIntegralRecordResponse>> getIntegralList(@Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(userCenterService.getUserIntegralRecordList(pageParamRequest)));
    }

    @ApiOperation(value = "??????-???????????????????????????????????????")
    @RequestMapping(value = "/getNewestIncomeUserIntegralRecord", method = RequestMethod.GET)
    public CommonResult<UserIntegralRecord> getNewestIncomeUserIntegralRecord(){
        return CommonResult.success(userIntegralRecordService.getNewestIncomeUserIntegralRecord());
    }

    @ApiOperation(value = "??????-?????????????????????????????????,??????????????????10?????????")
    @RequestMapping(value = "/getUnreadUserIntegralRecordList", method = RequestMethod.GET)
    @ApiImplicitParam(name = "num",value = "??????",defaultValue = "10")
    public CommonResult<List<UserIntegralRecord>> getUnreadUserIntegralRecordList(@RequestParam("num")Integer num){
        return CommonResult.success(userIntegralRecordService.getUnreadUserIntegralRecordList(num));
    }

    @ApiOperation(value = "??????-???????????????????????????????????????")
    @RequestMapping(value = "/takeUnreadChangeRead", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "????????????ID??????")
    public CommonResult<Boolean> takeUnreadChangeRead(@RequestParam("id")Integer id){
        return CommonResult.success(userIntegralRecordService.takeUnreadChangeRead(id));
    }

    @ApiOperation(value = "??????-?????????????????????6")
    @RequestMapping(value = "/getUserCollectionCode", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> getUserCollectionCode(){
        return CommonResult.success(userService.getUserCollectionCode(null));
    }

    @ApiOperation(value = "??????-????????????")
    @RequestMapping(value = "/transferAccountsIntegral", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid",value = "???????????????ID??????",required = true),
            @ApiImplicitParam(name = "type",value = "????????????",defaultValue = "0"),
            @ApiImplicitParam(name = "value",value = "???????????????",required = true),
            @ApiImplicitParam(name = "pwd",value = "????????????",required = true)
    })
    public CommonResult<Boolean> transferAccountsIntegral(@RequestParam("uid")Integer uid,
                                                          @RequestParam(name = "type",defaultValue = "0",required = false)Integer type,
                                                          @RequestParam("value")BigDecimal value,
                                                          @RequestParam("pwd")String pwd){
        return CommonResult.success(userService.transferAccountsIntegral(uid,type,value,pwd));
    }

    @ApiOperation(value = "??????-??????????????????")
    @RequestMapping(value = "/transferAccountsIntegralList", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserIntegralRecordMonthResponse>> transferAccountsIntegralList(@Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(userService.transferAccountsIntegralList(pageParamRequest));
    }

    @ApiOperation(value = "??????-???????????????????????????",notes = Constants.INSERT)
    @RequestMapping(value = "/invoice/saveInvoiceRise", method = RequestMethod.POST)
    public CommonResult<Boolean> saveInvoiceRise(@RequestBody @Validated InvoiceRise invoiceRise){
        return CommonResult.success(invoiceRiseService.sou(invoiceRise));
    }

    @ApiOperation(value = "??????-??????????????????????????????",notes = Constants.SELECT)
    @RequestMapping(value = "/invoice/invoiceRiseInfo", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "????????????id??????",required = true)
    public CommonResult<InvoiceRiseResponse> invoiceRiseInfo(@RequestParam("id")Integer id){
        return CommonResult.success(invoiceRiseService.info(id));
    }

    @ApiOperation(value = "??????-????????????????????????",notes = Constants.SELECT) //??????swagger??????
    @RequestMapping(value = "/getRisePageList", method = RequestMethod.GET)
    public CommonResult<CommonPage<InvoiceRise>> getRiseList(
            @Validated InvoiceRiseSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(invoiceRiseService.getPageList(request, pageParamRequest)));
    }

    @ApiOperation(value = "??????-????????????????????????",notes = Constants.SELECT) //??????swagger??????
    @RequestMapping(value = "/getRecordPageList", method = RequestMethod.GET)
    public CommonResult<CommonPage<InvoiceRecordResponse>> getRecordList(
            @Validated InvoiceRecordSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(invoiceRecordService.getPageList(request, pageParamRequest)));
    }

    @ApiOperation(value = "??????-??????????????????",notes = Constants.DELETE)
    @RequestMapping(value = "/invoice/deleteRise", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "????????????id??????")
    public CommonResult<String> deleteRise(@RequestParam("id") Integer id) {
        if (invoiceRiseService.removeById(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "??????-??????????????????????????????",notes = Constants.SELECT)
    @RequestMapping(value = "/invoice/invoiceRecordInfo", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id",value = "???????????????id??????",required = true)
    public CommonResult<InvoiceRecordResponse> invoiceRecordInfo(@RequestParam("id")Integer id){
        return CommonResult.success(invoiceRecordService.info(id));
    }

    @ApiOperation(value = "????????????-????????????????????????(????????????)")
    @RequestMapping(value = "/transferIn", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "price",value = "????????????",required = true),
            @ApiImplicitParam(name = "type",value = "????????????????????????(1=?????????2=??????)",required = true)
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

    @ApiOperation(value = "????????????-????????????(????????????)")
    @RequestMapping(value = "/weixinPayPlatform", method = RequestMethod.POST)
    public CommonResult<UserExtract> weixinPayPlatform(@RequestBody @Validated UserExtractRequest userExtractRequest) {
        return CommonResult.success(userService.accountBalanceWithdrawal(userExtractRequest));
    }

}



