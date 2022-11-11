package com.zbkj.crmeb.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.model.UserExtract;
import com.zbkj.crmeb.finance.request.UserExtractRequest;
import com.zbkj.crmeb.front.request.PasswordPayRequest;
import com.zbkj.crmeb.front.request.PasswordRequest;
import com.zbkj.crmeb.front.request.UserBindingPhoneUpdateRequest;
import com.zbkj.crmeb.front.response.*;
import com.zbkj.crmeb.store.request.RetailShopStairUserRequest;
import com.zbkj.crmeb.store.response.SpreadOrderResponse;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.request.*;
import com.zbkj.crmeb.user.response.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户表-server层接口
 * @author: 零风
 * @CreateDate: 2022/3/14 14:13
 */
public interface UserService extends IService<User> {

    /**
     * 得到消费额度
     * @param user   用户信息
     * @Author 零风
     * @Date  2022/4/27
     * @return
     */
    BigDecimal getKeyonMiED2(User user,BigDecimal orderStatusSum);

    /**
     * 得到可用米额度
     * @param user   用户信息
     * @Author 零风
     * @Date  2022/4/27
     * @return
     */
    BigDecimal getKeyonMiED(User user);

    /**
     * 转入账户余额(公共接口)
     * -佣金、积分
     * @param price 转入金额
     * @param type 转入类型(1=佣金、2=积分)
     * @Author 零风
     * @Date  2021/12/24
     * @return 结果
     */
    Map<String,String> transferIn(BigDecimal price,Integer type,User user,BigDecimal fee);

    /**
     * 积分转账收款记录
     * @param pageParamRequest 分页
     * @Author 零风
     * @Date  2022/3/23
     * @return 按月份分页数据
     */
    CommonPage<UserIntegralRecordMonthResponse> transferAccountsIntegralList(PageParamRequest pageParamRequest);

    /**
     * 是否已设置支付密码
     * @Author 零风
     * @Date  2022/3/21
     * @return 结果
     */
    Boolean isSetPayPwd();

    /**
     * 修改密码
     * @param phone  手机号
     * @param code 验证码
     * @param password 密码
     * @param type  类型：1=修改登录密码，2=修改支付密码
     * @Author 零风
     * @Date  2022/3/15
     * @return
     */
    boolean passwordUpdate(String phone,String code,String password, Integer type);

    /**
     * 设置支付密码
     * @param request 参数
     * @Author 零风
     * @Date  2022/3/15
     * @return 结果
     */
    boolean passwordPay(PasswordPayRequest request);

    /**
     * 用户积分转账
     * @param uid   收款方用户ID标识
     * @param type  转账方式:0=账户ID转账、1=收款二维码转账
     * @param value 转账金额值
     * @param pwd 密码
     * @Author 零风
     * @Date  2022/3/14
     * @return 转账结果
     */
    Boolean transferAccountsIntegral(Integer uid,Integer type,BigDecimal value,String pwd);

    /**
     * 用户收款二维码
     * @param uid 用户表ID标识
     * @Author 零风
     * @Date  2022/3/14
     * @return 数据
     */
    Map<String,Object> getUserCollectionCode(Integer uid);

    /**
     * 申请提现-账户余额提现(公共接口)
     * @param request 请求参数
     * @Author 零风
     * @Date  2021/12/22
     * @return
     */
    UserExtract accountBalanceWithdrawal(UserExtractRequest request);

    /**
     * 执行提现
     * @param userExtract 提现记录
     * @Author 零风
     * @Date  2022/4/27
     * @return
     */
    boolean zhixintixin(UserExtract userExtract);

    /**
     * 酒米提现不通过或失败-提现退还(积分)
     * @param userExtract 提现记录
     * @param user 用户信息
     * @Author 零风
     * @Date  2022/5/16 14:14
     * @return
     */
    void tixintuihuanIntegral(UserExtract userExtract, User user);

    /**
     * 账户余额提现不通过或失败-退还余额
     * @param userExtract   提现记录
     * @param user          用户信息
     * @Author 零风
     * @Date  2022/6/14 9:39
     */
    void tixinTuihuanNowMoney(UserExtract userExtract, User user);

    /**
     * 账户余额提现审核
     * @Author 零风
     * @Date  2022/4/27
     * @return
     */
    Boolean accountBalanceWithdrawalIsExtract(Boolean isExtract, Integer id,String backMessage);

    /**
     * 申请提现-用户佣金余额提现(从云账户代付)
     * @param userExtractRequest  请求参数
     * @return
     */
    Boolean applyWithdrawal(UserExtractRequest userExtractRequest) throws Exception;

    /**
     * 分页显示用户表
     * @param request          搜索条件
     * @param pageParamRequest 分页参数
     * @Author 零风
     * @Date  2021/12/24
     * @return 用户数据
     */
    PageInfo<UserResponse> getList(UserSearchRequest request, PageParamRequest pageParamRequest);

    boolean updateIntegralMoney(UserOperateIntegralMoneyRequest request);

    /**
     * 用户基本更新
     * @param user 用户参数
     * @return 更新结果
     */
    boolean updateBase(User user);

    boolean userPayCountPlus(User user);

    /**
     * 更新余额
     * @param user 用户
     * @param price 金额
     * @param type 增加add、扣减sub
     * @return 更新后的用户数据
     */
    Boolean updateNowMoney(User user, BigDecimal price, String type);

    /**
     * 会员分组
     * @param id String id
     * @param groupId Integer 分组Id
     */
    boolean group(String id, String groupId);

    /**
     * 修改登录密码
     * @param request 参数
     * @Author 零风
     * @Date  2022/3/15
     * @return 结果
     */
    boolean password(PasswordRequest request);

    void loginOut(String token);

    User getInfo();

    /**
     * 得到当前登录用户信息,不存在报异常
     * @Author 零风
     * @Date  2021/10/21
     * @return 用户信息
     */
    User getInfoException();

    Object getInfoByCondition(Integer userId,Integer type,PageParamRequest pageParamRequest);

    Integer getUserIdException();

    Integer getUserId();

    Integer getAddUserCountByDate(String date);

    Map<Object, Object> getAddUserCountGroupDate(String date);

    boolean bind(UserBindingPhoneUpdateRequest request);

    /**
     * 换绑手机号校验
     */
    Boolean updatePhoneVerify(UserBindingPhoneUpdateRequest request);

    /**
     * 换绑手机号
     */
    Boolean updatePhone(UserBindingPhoneUpdateRequest request);

    /**
     * 个人中心-用户信息
     * @return UserCenterResponse
     */
    UserCenterResponse getUserCenter();

    /**
     * 根据用户ID标识获取用户-map格式
     * -获取-订单对应的用户信息
     * @param uidList   用户ID标识list
     * @Author 零风
     * @Date  2022/1/20
     * @return map数据
     */
    HashMap<Integer, User> getMapListInUid(List<Integer> uidList);

    /**
     * 根据用户ID标识获取用户-list格式
     * @param uidList   用户ID标识list
     * @Author 零风
     * @Date  2022/1/20
     * @return list数据
     */
    List<User> getListInUid(List<Integer> uidList);

    void repeatSignNum(Integer userId);

    boolean tag(String id, String tagId);

    List<Integer> getSpreadPeopleIdList(List<Integer> userId);

    List<UserSpreadPeopleItemResponse> getSpreadPeopleList(List<Integer> userIdList, String keywords, String sortKey, String isAsc, PageParamRequest pageParamRequest);

    TopDetail getTopDetail(Integer userId);

    User registerByThird(RegisterThirdUserRequest thirdUserRequest);

    /**
     * 登录用户生成token
     * @author Mr.Zhang
     * @since 2020-04-29
     */
    String token(User user) throws Exception;

    String getValidateCodeRedisKey(String phone);

    boolean spread(Integer currentUserId, Integer spreadUserId);

    PageInfo<User> getUserListBySpreadLevel(RetailShopStairUserRequest request, PageParamRequest pageParamRequest);

    PageInfo<SpreadOrderResponse> getOrderListBySpreadLevel(RetailShopStairUserRequest request, PageParamRequest pageParamRequest);

    boolean clearSpread(Integer userId);

    List<User> getTopSpreadPeopleListByDate(String type, PageParamRequest pageParamRequest);

    Integer getCountByPayCount(int minPayCount, int maxPayCount);

    /**
     * 绑定推广关系（登录状态）
     * @param spreadUid 推广人id
     */
    void bindSpread(Integer spreadUid);

    boolean updateBrokeragePrice(User user, BigDecimal newBrokeragePrice);

    /**
     * 更新推广人
     * @param request 请求参数
     * @return Boolean
     */
    Boolean editSpread(UserUpdateSpreadRequest request);

    /**
     * 更新用户积分
     * @param user 用户
     * @param integral 积分
     * @param type 增加add、扣减sub
     * @return 更新后的用户对象
     */
    Boolean updateIntegral(User user, BigDecimal integral, String type);

    /**
     * 获取分销人员列表
     * @param keywords 搜索参数
     * @param dateLimit 时间参数
     * @param storeBrokerageStatus 分销状态：1-指定分销，2-人人分销
     */
    List<User> findDistributionList(String keywords, String dateLimit, String storeBrokerageStatus);

    /**
     * 获取发展会员人数
     * @param ids       推广人id集合
     * @param dateLimit 时间参数
     * @return Integer
     */
    Integer getDevelopDistributionPeopleNum(List<Integer> ids, String dateLimit);

    User getUserByAccount(String account);

    /**
     * 手机号注册用户
     * @param phone 手机号
     * @param spreadUid 推广人编号
     * @return User
     */
    User registerPhone(String phone, Integer spreadUid);

    /**
     * 检测能否绑定关系
     * @param user 当前用户
     * @param spreadUid 推广员Uid
     * @param type 用户类型:new-新用户，old—老用户
     * @return Boolean
     */
    Boolean checkBingSpread(User user, Integer spreadUid, String type);

    /**
     * 更新推广员推广数
     * @param uid uid
     * @param type add or sub
     * @return Boolean
     */
    Boolean updateSpreadCountByUid(Integer uid, String type);

    /**
     * 添加/扣减佣金
     * @param uid 用户id
     * @param price 金额
     * @param brokeragePrice 历史金额
     * @param type 类型：add—添加，sub—扣减
     */
    Boolean operationBrokerage(Integer uid, BigDecimal price, BigDecimal brokeragePrice, String type);

    /**
     * 添加/扣减余额
     * @param uid 用户id
     * @param price 金额
     * @param nowMoney 历史金额
     * @param type 类型：add—添加，sub—扣减
     */
    Boolean operationNowMoney(Integer uid, BigDecimal price, BigDecimal nowMoney, String type);

    /**
     * 添加/扣减积分
     * @param uid 用户id
     * @param integral 积分
     * @param nowIntegral 历史积分
     * @param type 类型：add—添加，sub—扣减
     */
    Boolean operationIntegral(Integer uid, BigDecimal integral, BigDecimal nowIntegral, String type);

    /**
     * PC后台分销员列表
     * @param storeBrokerageStatus 分销模式 1-指定分销，2-人人分销
     * @param keywords 搜索参数
     * @param dateLimit 时间参数
     * @param pageRequest 分页参数
     * @return
     */
    PageInfo<User> getAdminSpreadPeopleList(String storeBrokerageStatus, String keywords, String dateLimit, PageParamRequest pageRequest);

    /**
     * 清除User Group id
     * @param groupId 待清除的GroupId
     */
    void clearGroupByGroupId(String groupId);

    /**
     * 更新用户
     * @param userRequest 用户参数
     */
    Boolean updateUser(UserUpdateRequest userRequest);

    /**
     * 根据手机号查询用户
     * @param phone 用户手机号
     * @return 用户信息
     */
    User getByPhone(String phone);

    /**
     * 后台修改用户手机号
     * @param id 用户uid
     * @param phone 手机号
     * @return Boolean
     */
    Boolean updateUserPhone(Integer id, String phone);

    /**
     * 根据昵称匹配用户，返回id集合
     * @param nikeName 需要匹配得昵称
     * @return List
     */
    List<Integer> findIdListLikeName(String nikeName);

    /**
     * 得到-总代理用户-相关统计数据
     * @return  数据
     */
    UserGeneralAgentDataResponse getUserGeneralAgentData();

    /**
     * 得到-总代理-下级所有-订单列表
     * @param status 订单状态
     * @param pageRequest 分页
     * @return 订单集合
     */
    CommonPage<OrderDetailResponse> getUgaSubAllOrderList(Integer status, PageParamRequest pageRequest);

    /**
     * 得到-总代理用户-订单详情统计数据
     * @param dateType  日期类型(0=今天、1=昨天、2=最近7天、3=本月、4=本年)
     * @return
     */
    GeneralAgentOrderDetailsResponse getUgaOrderDetails(Integer dateType);

    /**
     * 得到-总代理-下级所有用户list
     * @param user  当前登录用户
     * @return
     */
    List<User> getUgaSupList(User user);

    /**
     * 得到-总代理用户-推广佣金明细
     * @param pageParamRequest
     * @return
     */
    UserGeneralAgentCommissionDataResponse getUgaCommission(PageParamRequest pageParamRequest);

    /**
     * 得到-(商户用户/商户/商铺/店铺/零售商..）-相关数据统计-响应对象
     * (公共接口)
     * @param merId
     * @return  (商户用户/商户/商铺/店铺/零售商..）
     */
    UserMerIdDataResponse getUserMerIdDataResponse(Integer merId);

    /**
     * 区域代理-得到-相关统计数据
     * @param raId 区域代理表ID标识
     * @return  数据
     */
    UserMerIdDataResponse getUserMerIdData(Integer raId);

    /**
     * 得到-(商户用户/商户/商铺/店铺/零售商..）-订单详细统计数据-响应对象
     * (公共接口)
     * @param dateType  日期类型(0=今天、1=昨天、2=最近7天、3=本月、4=本年)
     * @param merId     (商户用户/商户/商铺/店铺/零售商..）
     * @author lingfe
     * @return
     */
    UserMerIdOrderDetailsResponse getMerIdOrderInfoStatisticsData(Integer merId,Integer dateType);

    /**
     * 区域代理-得到-订单详细统计数据
     * @param raId 区域代理表ID标识
     * @param dateType  日期类型(0=今天、1=昨天、2=最近7天、3=本月、4=本年)
     * @author lingfe
     * @return
     */
    UserMerIdOrderDetailsResponse getUserMerIdOrderInfoStatisticsData(Integer raId,Integer dateType);

    /**
     * 短信营销
     * @Author 零风
     * @Date  2022/7/28 15:53
     * @return 结果
     */
    Map<String,Object> sms(UserSmsRequest request);

}
