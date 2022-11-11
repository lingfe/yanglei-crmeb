package com.zbkj.crmeb.front.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.request.UserExtractRequest;
import com.zbkj.crmeb.front.request.UserRechargeRequest;
import com.zbkj.crmeb.front.request.UserSpreadPeopleRequest;
import com.zbkj.crmeb.front.request.WxBindingPhoneRequest;
import com.zbkj.crmeb.front.response.*;
import com.zbkj.crmeb.system.model.SystemUserLevel;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.request.RegisterThirdUserRequest;
import com.zbkj.crmeb.user.response.UserIntegralRecordResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户中心-服务层接口
 * @author: 零风
 * @CreateDate: 2021/12/22 14:28
 */
public interface UserCenterService extends IService<User> {

    /**
     * 微信授权app登录
     * @Author lingfe
     * @Date  2021/9/13
     * @param data      微信用户信息
     **/
    LoginResponse weChatAppLogin(String data);

    /**
     * 佣金-推广数据接口(昨天的佣金、累计提现佣金、当前佣金、待结算佣金)
     * @Author 零风
     * @Date  2022/3/21
     * @return 响应对象
     */
    UserCommissionResponse getCommission();

    BigDecimal getSpreadCountByType(int type);

    /**
     * 申请提现-佣金余额提现(旧方式(手动打款))-(公共)
     * @param request   参数
     * @Author 零风
     * @Date  2021/12/23
     * @return
     */
    Boolean extractCash(UserExtractRequest request);

    /**
     * 获取提现银行列表
     * @return List<String>
     */
    List<String> getExtractBank();

    List<SystemUserLevel> getUserLevelList();

    /**
     * 获取推广人列表
     * -推广用户， 我自己推广了哪些用户
     * -前后端共用接口
     * @param request 查询参数
     * @param pageParamRequest 分页
     * @Author 零风
     * @Date  2022/4/12
     * @return List<UserSpreadPeopleItemResponse>
     */
    List<UserSpreadPeopleItemResponse> getSpreadPeopleList(UserSpreadPeopleRequest request, PageParamRequest pageParamRequest,Integer uid);

    UserRechargeResponse getRechargeConfig();

    /**
     * 用户资金余额详情
     * @Author 零风
     * @Date  2022/1/17
     * @return 资金余额响应对象
     */
    UserBalanceResponse getUserBalance();

    /**
     * 推广人订单
     * @param pageParamRequest 分页对象
     * @Author 零风
     * @Date  2022/1/20
     * @return 推广人订单响应对象
     */
    UserSpreadOrderResponse getSpreadOrder(PageParamRequest pageParamRequest);

    OrderPayResultResponse recharge(UserRechargeRequest request);

    /**
     * 微信-公众号授权登录
     * @param code          微信code
     * @param spreadUid     推开人ID
     * @return
     */
    LoginResponse weChatAuthorizeLogin(String code, Integer spreadUid);

    String getLogo();

    /**
     * 微信小程序授权登录
     * @param code 前端临时授权code
     * @param request 用户参数
     * @Author 零风
     * @Date  2022/6/28 14:00
     * @return 结果
     */
    LoginResponse weChatAuthorizeProgramLogin(String code, RegisterThirdUserRequest request);

    List<User> getTopSpreadPeopleListByDate(String type, PageParamRequest pageParamRequest);

    List<User> getTopBrokerageListByDate(String type, PageParamRequest pageParamRequest);

    List<UserSpreadBannerResponse> getSpreadBannerList(PageParamRequest pageParamRequest);

    Integer getNumberByTop(String type);

    /**
     * 推广佣金明细
     * @param pageParamRequest 分页参数
     */
    PageInfo<SpreadCommissionDetailResponse> getSpreadCommissionDetail(PageParamRequest pageParamRequest);

    /**
     * 用户账单记录（现金）
     * @param type 记录类型：all-全部，expenditure-支出，income-收入
     * @return CommonPage
     */
    CommonPage<UserRechargeBillRecordResponse> nowMoneyBillRecord(String type, PageParamRequest pageRequest);

    /**
     * 注册绑定手机号(公共)
     * @param request 请求参数
     * @return 登录信息
     */
    LoginResponse registerBindingPhone(WxBindingPhoneRequest request);

    /**
     * 用户积分记录列表
     * @param pageParamRequest 分页参数
     * @return List<UserIntegralRecord>
     */
    List<UserIntegralRecordResponse> getUserIntegralRecordList(PageParamRequest pageParamRequest);

    /**
     * 获取用户积分信息
     * @param uid 用户ID标识
     * @Author 零风
     * @Date  2021/12/27
     * @return 用户积分响应对象
     */
    IntegralUserResponse getIntegralUser(Integer uid);

    /**
     * 获取用户经验记录
     * @param pageParamRequest 分页参数
     * @return List<UserBill>
     */
    List<UserBill> getUserExperienceList(PageParamRequest pageParamRequest);

    /**
     * 提现用户信息
     * @return UserExtractCashResponse
     */
    UserExtractCashResponse getExtractUser();

    /**
     * 推广人列表统计
     * -前后端公用接口
     * @param uid   用户ID标识
     * @Author 零风
     * @Date  2022/4/7
     * @return 统计数据
     */
    UserSpreadPeopleResponse getSpreadPeopleCount(Integer uid);
}
