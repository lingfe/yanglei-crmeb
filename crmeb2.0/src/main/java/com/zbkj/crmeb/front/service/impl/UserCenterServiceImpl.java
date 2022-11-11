package com.zbkj.crmeb.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.constants.*;
import com.exception.CrmebException;
import com.github.pagehelper.PageInfo;
import com.utils.*;
import com.zbkj.crmeb.finance.model.UserExtract;
import com.zbkj.crmeb.finance.model.UserRecharge;
import com.zbkj.crmeb.finance.request.UserExtractRequest;
import com.zbkj.crmeb.finance.service.UserExtractService;
import com.zbkj.crmeb.finance.service.UserRechargeService;
import com.zbkj.crmeb.front.request.UserRechargeRequest;
import com.zbkj.crmeb.front.request.UserSpreadPeopleRequest;
import com.zbkj.crmeb.front.request.WxBindingPhoneRequest;
import com.zbkj.crmeb.front.response.*;
import com.zbkj.crmeb.front.service.LoginService;
import com.zbkj.crmeb.front.service.UserCenterService;
import com.zbkj.crmeb.front.vo.WxPayJsResultVo;
import com.zbkj.crmeb.marketing.model.StoreCoupon;
import com.zbkj.crmeb.marketing.model.StoreCouponUser;
import com.zbkj.crmeb.marketing.service.StoreCouponService;
import com.zbkj.crmeb.marketing.service.StoreCouponUserService;
import com.zbkj.crmeb.payment.wechat.WeChatPayService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.system.model.SystemUserLevel;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.system.service.SystemGroupDataService;
import com.zbkj.crmeb.system.service.SystemUserLevelService;
import com.zbkj.crmeb.system.vo.SystemGroupDataRechargeConfigVo;
import com.zbkj.crmeb.user.dao.UserDao;
import com.zbkj.crmeb.user.model.*;
import com.zbkj.crmeb.user.request.RegisterThirdUserRequest;
import com.zbkj.crmeb.user.response.UserIntegralRecordResponse;
import com.zbkj.crmeb.user.service.*;
import com.zbkj.crmeb.wechat.response.WeChatAuthorizeLoginGetOpenIdResponse;
import com.zbkj.crmeb.wechat.response.WeChatAuthorizeLoginUserInfoResponse;
import com.zbkj.crmeb.wechat.response.WeChatProgramAuthorizeLoginGetOpenIdResponse;
import com.zbkj.crmeb.wechat.service.WeChatService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 用户中心 服务实现类
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Service
public class UserCenterServiceImpl extends ServiceImpl<UserDao, User> implements UserCenterService {

    private final Logger logger = LoggerFactory.getLogger(UserCenterServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserBillService userBillService;

    @Autowired
    private UserExtractService userExtractService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemUserLevelService systemUserLevelService;

    @Autowired
    private SystemGroupDataService systemGroupDataService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private UserRechargeService userRechargeService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private WeChatPayService weChatPayService;

    @Autowired
    private StoreCouponService storeCouponService;

    @Autowired
    private StoreCouponUserService storeCouponUserService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserIntegralRecordService userIntegralRecordService;

    @Autowired
    private UserIntegralRecordService integralRecordService;

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Override
    public LoginResponse weChatAppLogin(String data) {
        //实例化-登录响应对象
        LoginResponse loginResponse = new LoginResponse();
        try {
            //通过第一步获得的code获取微信授权信息
            JSONObject userInfo = JSONObject.parseObject(data);

            //成功获取授权,以下部分为业务逻辑处理了，根据个人业务需求写就可以了
            if (userInfo != null && userInfo.get("openId") != null) {
                //取出openid
                String openid =userInfo.get("openId").toString();

                //验证-分享人id
                Object spreadUidObject=userInfo.get("spreadUid");
                Integer spreadUid=0;
                if(spreadUidObject == null && "".equals(spreadUidObject))spreadUid=Integer.valueOf(spreadUidObject.toString());

                //验证-用户类型
                Integer type=0;
                switch (String.valueOf(userInfo.get("type"))){
                    case Constants.USER_LOGIN_TYPE_IOS_WX:
                        type=Constants.THIRD_LOGIN_TOKEN_TYPE_IOS_WX;
                        break;
                    case Constants.USER_LOGIN_TYPE_ANDROID_WX:
                        type=Constants.THIRD_LOGIN_TOKEN_TYPE_ANDROID_WX;
                        break;
                    default:
                        throw new CrmebException("用户类型错误！");
                }

                //检测-token是否存在
                UserToken userToken = userTokenService.getByOpenidAndType(openid, type);
                if (ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
                    User user = userService.getById(userToken.getUid());

                    //验证-状态
                    if (!user.getStatus()) {
                        throw new CrmebException("当前账户已禁用，请联系管理员！");
                    }

                    //记录最后一次登录时间
                    user.setLastLoginTime(DateUtil.nowDateTime());

                    //执行操作
                    Integer finalSpreadUid = spreadUid;
                    Boolean execute = transactionTemplate.execute(e -> {
                        // 分销绑定
                        if (userService.checkBingSpread(user, finalSpreadUid, "old")) {
                            user.setSpreadUid(finalSpreadUid);
                            user.setSpreadTime(DateUtil.nowDateTime());
                            // 处理新旧推广人数据
                            userService.updateSpreadCountByUid(finalSpreadUid, "add");
                        }

                        //更新用户信息
                        userService.updateById(user);
                        return Boolean.TRUE;
                    });

                    //验证-执行结果
                    if (!execute) {
                        logger.error(StrUtil.format("APP登录绑定分销关系失败，uid={},spreadUid={}", user.getUid(), spreadUid));
                    }

                    //生成token
                    try {
                        String token = userService.token(user);
                        loginResponse.setToken(token);
                    } catch (Exception e) {
                        logger.error(StrUtil.format("app登录生成token失败，uid={}", user.getUid()));
                        e.printStackTrace();
                    }

                    //赋值-并返回
                    loginResponse.setType("login");
                    loginResponse.setUid(user.getUid());
                    loginResponse.setNikeName(user.getNickname());
                    loginResponse.setPhone(user.getPhone());
                    return loginResponse;
                }else{
                    //没有用户-走创建用户流程
                    WeChatAuthorizeLoginUserInfoResponse weChatAuthorizeLoginUserInfoResponse = JSONObject.parseObject(userInfo.toJSONString(), WeChatAuthorizeLoginUserInfoResponse.class);
                    RegisterThirdUserRequest registerThirdUserRequest = new RegisterThirdUserRequest();
                    BeanUtils.copyProperties(weChatAuthorizeLoginUserInfoResponse, registerThirdUserRequest);

                    //其他参数赋值
                    registerThirdUserRequest.setSex(String.valueOf(userInfo.get("sex")==null?0:userInfo.get("sex")));
                    registerThirdUserRequest.setType(String.valueOf(userInfo.get("type")));
                    registerThirdUserRequest.setHeadimgurl(String.valueOf(userInfo.get("avatarUrl")));
                    registerThirdUserRequest.setSpreadPid(spreadUid);
                    registerThirdUserRequest.setOpenId(openid);

                    //生成key
                    String key = SecureUtil.md5(openid);
                    redisUtil.set(key, JSONObject.toJSONString(registerThirdUserRequest), (long) (60 * 2), TimeUnit.MINUTES);

                    //赋值并返回
                    loginResponse.setType("register");
                    loginResponse.setKey(key);
                    return loginResponse;
                }
            } else {
                throw new  CrmebException("app微信登录失败！data数据为空！");
            }
        } catch (Exception e) {
            throw new  CrmebException("app微信登录失败！"+e.getMessage());
        }
    }

    @Override
    public UserCommissionResponse getCommission() {
        // 得到当前登录用户
        User user = userService.getInfoException();

        // 当前佣金
        BigDecimal commissionCount=user.getBrokeragePrice();
        // 昨天得佣金
        BigDecimal yesterdayIncomes = userBrokerageRecordService.getYesterdayIncomes(user.getUid());
        // 累计已提取佣金
        BigDecimal totalMoney = userExtractService.getExtractTotalMoney(user.getUid());
        // 得到待结算佣金
        BigDecimal stayCettlementCommission= userBrokerageRecordService.getFreezePrice(user.getUid());

        // 实例化-用户佣金信息-响应对象
        UserCommissionResponse userCommissionResponse = new UserCommissionResponse();
        userCommissionResponse.setLastDayCount(yesterdayIncomes);
        userCommissionResponse.setExtractCount(totalMoney);
        userCommissionResponse.setCommissionCount(commissionCount);
        userCommissionResponse.setStayCettlementCommission(stayCettlementCommission);

        //返回
        return userCommissionResponse;
    }

    /**
     * 推广佣金/提现总和
     * @author Mr.Zhang
     * @since 2020-06-08
     * @return BigDecimal
     */
    @Override
    public BigDecimal getSpreadCountByType(int type) {
        //推广佣金/提现总和
        Integer userId = userService.getUserIdException();
        if(type == 3){
            BigDecimal count = userBillService.getSumBigDecimal(null, userId, Constants.USER_BILL_CATEGORY_MONEY, null, Constants.USER_BILL_TYPE_BROKERAGE);
            BigDecimal withdraw = userBillService.getSumBigDecimal(1, userId, Constants.USER_BILL_CATEGORY_MONEY, null, Constants.USER_BILL_TYPE_BROKERAGE); //提现
            return count.subtract(withdraw);
        }

        //累计提现
        if(type == 4){
            return userExtractService.getWithdrawn(null,null);
        }

        return BigDecimal.ZERO;
    }

    @Override
    public Boolean extractCash(UserExtractRequest request) {
        switch (request.getExtractType()){
            case "weixin":
                if(StringUtils.isBlank(request.getWechat())){
                    throw new  CrmebException("请填写微信号！");
                }
                request.setAlipayCode(null);
                request.setBankCode(null);
                request.setBankName(null);
                break;
            case "alipay":
                if(StringUtils.isBlank(request.getAlipayCode())){
                    throw new  CrmebException("请填写支付宝账号！");
                }
                request.setWechat(null);
                request.setBankCode(null);
                request.setBankName(null);
                break;
            case "bank":
                if(StringUtils.isBlank(request.getBankName())){
                    throw new  CrmebException("请填写银行名称！");
                }
                if(StringUtils.isBlank(request.getBankCode())){
                    throw new  CrmebException("请填写银行卡号！");
                }
                request.setWechat(null);
                request.setAlipayCode(null);
                break;
            default:
                throw new  CrmebException("请选择支付方式");
        }
        return userExtractService.extractApply(request);
        //return userExtractService.create(request,0);
    }

    /**
     * 提现银行/提现最低金额
     * @author Mr.Zhang
     * @since 2020-06-08
     * @return UserExtractCashResponse
     */
    @Override
    public List<String> getExtractBank() {
        // 获取提现银行
        String bank = systemConfigService.getValueByKeyException(Constants.CONFIG_BANK_LIST).replace("\r\n", "\n");
        List<String> bankArr = new ArrayList<>();
        if(bank.indexOf("\n") > 0){
            bankArr.addAll(Arrays.asList(bank.split("\n")));
        }else{
            bankArr.add(bank);
        }
        return bankArr;
    }

    /**
     * 会员等级列表
     * @author Mr.Zhang
     * @since 2020-06-19
     * @return List<UserLevel>
     */
    @Override
    public List<SystemUserLevel> getUserLevelList() {
        return systemUserLevelService.getH5LevelList();
    }

    @Override
    public List<UserSpreadPeopleItemResponse> getSpreadPeopleList(UserSpreadPeopleRequest request, PageParamRequest pageParamRequest,Integer uid) {
        //查询当前用户名下的一级推广员
        List<Integer> userIdList = new ArrayList<>();
        if(uid == null){
            Integer userId = userService.getUserIdException();
            userIdList.add(userId);
        }else{
            userIdList.add(uid);
        }
        userIdList = userService.getSpreadPeopleIdList(userIdList); //我推广的一级用户id集合
        if (CollUtil.isEmpty(userIdList)) {//如果没有一级推广人，直接返回
            return new ArrayList<>();
        }
        if (request.getGrade().equals(1)) {// 二级推广人
            //查询二级推广人
            List<Integer> secondSpreadIdList = userService.getSpreadPeopleIdList(userIdList);
            userIdList.clear();
            if(secondSpreadIdList.size()<=0){
                userIdList.add(0);
            }else{
                //二级推广人
                userIdList.addAll(secondSpreadIdList);
            }
        }
        List<UserSpreadPeopleItemResponse> list= userService.getSpreadPeopleList(userIdList, request.getKeyword(), request.getSortKey(), request.getIsAsc(), pageParamRequest);
        return list;
    }

    /**
     * 充值额度选择
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return UserRechargeResponse
     */
    @Override
    public UserRechargeResponse getRechargeConfig() {
        UserRechargeResponse userRechargeResponse = new UserRechargeResponse();
        userRechargeResponse.setRechargeQuota(systemGroupDataService.getListByGid(SysGroupDataConstants.GROUP_DATA_ID_RECHARGE_LIST, UserRechargeItemResponse.class));
        String rechargeAttention = systemConfigService.getValueByKey(Constants.CONFIG_RECHARGE_ATTENTION);
        List<String> rechargeAttentionList = new ArrayList<>();
        if(StringUtils.isNotBlank(rechargeAttention)){
            rechargeAttentionList = CrmebUtil.stringToArrayStrRegex(rechargeAttention, "\n");
        }
        userRechargeResponse.setRechargeAttention(rechargeAttentionList);
        return userRechargeResponse;
    }

    @Override
    public UserBalanceResponse getUserBalance() {
        //得到用户信息
        User user = userService.getInfo();

        //累计充值
        BigDecimal recharge = userBillService.getSumBigDecimal(
                1,
                user.getUid(),
                Constants.USER_BILL_CATEGORY_MONEY,
                null,
                Constants.USER_BILL_TYPE_PAY_RECHARGE);

        //联盟商家累计收款
        BigDecimal allianceMerchantsSum = userBillService.getSumBigDecimal(
                1,
                user.getUid(),
                Constants.USER_BILL_CATEGORY_MONEY,
                null,
                Constants.USER_BILL_TYPE_isAllianceMerchants);

        //累计提现
        LambdaQueryWrapper<UserExtract> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserExtract::getUid,user.getUid());
        queryWrapper.eq(UserExtract::getStatus,Constants.USER_EXTRACT_STATUS_SUCCESS);
        List<UserExtract> userExtractList = userExtractService.list(queryWrapper);
        BigDecimal extractSum = userExtractList.stream().map(UserExtract::getExtractPrice).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_DOWN);

        //累计消费金额
        BigDecimal orderStatusSum = storeOrderService.getSumBigDecimal(user.getUid(), null,new Integer[]{3},true);//已完成的实际消费订单统计

        //实例化响应对象
        UserBalanceResponse userBalanceResponse =  new UserBalanceResponse(user.getNowMoney(), recharge, orderStatusSum);
        userBalanceResponse.setNowMoney(user.getNowMoney());
        userBalanceResponse.setRecharge(recharge);
        userBalanceResponse.setOrderStatusSum(orderStatusSum);
        userBalanceResponse.setExtractSum(extractSum);
        userBalanceResponse.setAllianceMerchantsSum(allianceMerchantsSum);
        return userBalanceResponse;
    }

    @Override
    public UserSpreadOrderResponse getSpreadOrder(PageParamRequest pageParamRequest) {
        // 得到当前用户
        User user = userService.getInfoException();

        // 实例化-推广人订单-响应对象
        UserSpreadOrderResponse spreadOrderResponse = new UserSpreadOrderResponse();

        // 获取-用户佣金记录条数
        Integer spreadCount = userBrokerageRecordService.getSpreadCountByUid(user.getUid());
        spreadOrderResponse.setCount(spreadCount.longValue());
        if (spreadCount.equals(0)) {
            return spreadOrderResponse;
        }

        // 获取-佣金记录，分页
        List<UserBrokerageRecord> recordList = userBrokerageRecordService.findSpreadListByUid(user.getUid(), pageParamRequest);

        // 获取-佣金记录对应的订单信息
        List<String> orderNoList = recordList.stream().map(UserBrokerageRecord::getLinkId).collect(Collectors.toList()); //取出订单ID标识
        Map<String, StoreOrder> orderMap = storeOrderService.getMapInOrderNo(orderNoList); // 订单ID标识key-订单信息value
        List<StoreOrder> storeOrderList = new ArrayList<>(orderMap.values()); // 取出订单信息

        // 获取-订单对应的用户信息
        List<Integer> uidList = storeOrderList.stream().map(StoreOrder::getUid).distinct().collect(Collectors.toList()); //用户ID标识
        HashMap<Integer, User> userMap = userService.getMapListInUid(uidList);// 用户ID标识key-用户信息value

        // 定义集合-推广订单响应list
        List<UserSpreadOrderItemResponse> userSpreadOrderItemResponseList = new ArrayList<>();
        List<String> monthList = CollUtil.newArrayList(); //储存月份list

        // 处理佣金记录-转成已月份对应推广订单形式响应给前端
        recordList.forEach(record -> {
            // 实例化-推广订单信息子集-响应对象
            UserSpreadOrderItemChildResponse userSpreadOrderItemChildResponse = new UserSpreadOrderItemChildResponse();
            //赋值
            userSpreadOrderItemChildResponse.setOrderId(orderMap.get(record.getLinkId()).getOrderId());
            userSpreadOrderItemChildResponse.setTime(record.getUpdateTime());
            userSpreadOrderItemChildResponse.setNumber(record.getPrice());

            //先拿订单用户ID标识，再度该订单用户信息
            Integer orderUid = orderMap.get(record.getLinkId()).getUid();
            userSpreadOrderItemChildResponse.setAvatar(userMap.get(orderUid).getAvatar());
            userSpreadOrderItemChildResponse.setNickname(userMap.get(orderUid).getNickname());
            userSpreadOrderItemChildResponse.setType("返佣");

            // 得到月份
            String month = DateUtil.dateToStr(record.getUpdateTime(), Constants.DATE_FORMAT_MONTH);
            if (monthList.contains(month)) {
                // 如果在已有的数据中找到当前月份数据则追加
                for (UserSpreadOrderItemResponse userSpreadOrderItemResponse : userSpreadOrderItemResponseList) {
                    if(userSpreadOrderItemResponse.getTime().equals(month)){
                        userSpreadOrderItemResponse.getChild().add(userSpreadOrderItemChildResponse);
                        break;
                    }
                }
            } else {
                // 不包含此月份，创建一个
                UserSpreadOrderItemResponse userSpreadOrderItemResponse = new UserSpreadOrderItemResponse();
                userSpreadOrderItemResponse.setTime(month);
                userSpreadOrderItemResponse.getChild().add(userSpreadOrderItemChildResponse);
                userSpreadOrderItemResponseList.add(userSpreadOrderItemResponse);
                monthList.add(month);
            }
        });

        // 获取月份总订单数
        Map<String, Integer> countMap = userBrokerageRecordService.getSpreadCountByUidAndMonth(user.getUid(), monthList);
        for (UserSpreadOrderItemResponse userSpreadOrderItemResponse: userSpreadOrderItemResponseList) {
            userSpreadOrderItemResponse.setCount(countMap.get(userSpreadOrderItemResponse.getTime()));
        }

        // 更新并返回
        spreadOrderResponse.setList(userSpreadOrderItemResponseList);
        return spreadOrderResponse;
    }

    /**
     * 充值
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return UserSpreadOrderResponse;
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
    public OrderPayResultResponse recharge(UserRechargeRequest request) {
        request.setPayType(PayConstants.PAY_TYPE_WE_CHAT);

        //验证金额是否为最低金额
        String rechargeMinAmountStr = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_RECHARGE_MIN_AMOUNT);
        BigDecimal rechargeMinAmount = new BigDecimal(rechargeMinAmountStr);
        int compareResult = rechargeMinAmount.compareTo(request.getPrice());
        if(compareResult > 0){
            throw new CrmebException("充值金额不能低于" + rechargeMinAmountStr);
        }

        request.setGivePrice(BigDecimal.ZERO);

        if(request.getGroupDataId() > 0){
            SystemGroupDataRechargeConfigVo systemGroupData = systemGroupDataService.getNormalInfo(request.getGroupDataId(), SystemGroupDataRechargeConfigVo.class);
            if(null == systemGroupData){
                throw new CrmebException("您选择的充值方式已下架");
            }

            //售价和赠送
            request.setPrice(systemGroupData.getPrice());
            request.setGivePrice(systemGroupData.getGiveMoney());

        }
        User currentUser = userService.getInfoException();
        //生成系统订单

        UserRecharge userRecharge = new UserRecharge();
        userRecharge.setUid(currentUser.getUid());
        userRecharge.setOrderId(CrmebUtil.getOrderNo("recharge"));
        userRecharge.setPrice(request.getPrice());
        userRecharge.setGivePrice(request.getGivePrice());
        userRecharge.setRechargeType(request.getFromType());
        boolean save = userRechargeService.save(userRecharge);
        if (!save) {
            throw new CrmebException("生成充值订单失败!");
        }

        OrderPayResultResponse response = new OrderPayResultResponse();
        MyRecord record = new MyRecord();
        Map<String, String> unifiedorder = weChatPayService.unifiedRecharge(userRecharge, request.getClientIp());
        record.set("status", true);
        response.setStatus(true);
        WxPayJsResultVo vo = new WxPayJsResultVo();
        vo.setAppId(unifiedorder.get("appId"));
        vo.setNonceStr(unifiedorder.get("nonceStr"));
        vo.setPackages(unifiedorder.get("package"));
        vo.setSignType(unifiedorder.get("signType"));
        vo.setTimeStamp(unifiedorder.get("timeStamp"));
        vo.setPaySign(unifiedorder.get("paySign"));
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_H5)) {
            vo.setMwebUrl(unifiedorder.get("mweb_url"));
            response.setPayType(PayConstants.PAY_CHANNEL_WE_CHAT_H5);
        }
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_IOS) || userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_ANDROID)) {//
            vo.setPartnerid(unifiedorder.get("partnerid"));
        }
        response.setJsConfig(vo);
        response.setOrderNo(userRecharge.getOrderId());
        return response;
    }

    @Override
    public LoginResponse weChatAuthorizeLogin(String code, Integer spreadUid) {
        // 通过code获取获取公众号授权信息
        WeChatAuthorizeLoginGetOpenIdResponse response = weChatService.authorizeLogin(code);
        //检测是否存在
        UserToken userToken = userTokenService.getByOpenidAndType(response.getOpenId(),  Constants.THIRD_LOGIN_TOKEN_TYPE_PUBLIC);
        LoginResponse loginResponse = new LoginResponse();
        if (ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
            User user = userService.getById(userToken.getUid());
            if (!user.getStatus()) {
                throw new CrmebException("当前账户已禁用，请联系管理员！");
            }

            // 记录最后一次登录时间
            user.setLastLoginTime(DateUtil.nowDateTime());
            Boolean execute = transactionTemplate.execute(e -> {
                // 分销绑定
                if (userService.checkBingSpread(user, spreadUid, "old")) {
                    user.setSpreadUid(spreadUid);
                    user.setSpreadTime(DateUtil.nowDateTime());
                    // 处理新旧推广人数据
                    userService.updateSpreadCountByUid(spreadUid, "add");
                }
                userService.updateById(user);
                return Boolean.TRUE;
            });
            if (!execute) {
                logger.error(StrUtil.format("公众号登录绑定分销关系失败，uid={},spreadUid={}", user.getUid(), spreadUid));
            }
            try {
                String token = userService.token(user);
                loginResponse.setToken(token);
            } catch (Exception e) {
                logger.error(StrUtil.format("公众号登录生成token失败，uid={}", user.getUid()));
                e.printStackTrace();
            }
            loginResponse.setType("login");
            loginResponse.setUid(user.getUid());
            loginResponse.setNikeName(user.getNickname());
            loginResponse.setPhone(user.getPhone());
            return loginResponse;
        }
        // 没有用户，走创建用户流程
        // 从微信获取用户信息，存入Redis中，将key返回给前端，前端在下一步绑定手机号的时候下发
        WeChatAuthorizeLoginUserInfoResponse userInfo = weChatService.getUserInfo(response.getOpenId(), response.getAccessToken());
        RegisterThirdUserRequest registerThirdUserRequest = new RegisterThirdUserRequest();
        BeanUtils.copyProperties(userInfo, registerThirdUserRequest);
        registerThirdUserRequest.setSpreadPid(spreadUid);
        registerThirdUserRequest.setType(Constants.USER_LOGIN_TYPE_PUBLIC);
        registerThirdUserRequest.setOpenId(response.getOpenId());
        String key = SecureUtil.md5(response.getOpenId());
        redisUtil.set(key, JSONObject.toJSONString(registerThirdUserRequest), (long) (60 * 2), TimeUnit.MINUTES);

        loginResponse.setType("register");
        loginResponse.setKey(key);
        return loginResponse;
    }

    /**
     * 获取微信授权logo
     * @author Mr.Zhang
     * @since 2020-06-29
     * @return String;
     */
    @Override
    public String getLogo() {
        return systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MOBILE_LOGIN_LOGO);
    }

    @Override
    public LoginResponse weChatAuthorizeProgramLogin(String code, RegisterThirdUserRequest request) {
        WeChatProgramAuthorizeLoginGetOpenIdResponse response = weChatService.programAuthorizeLogin(code);
        System.out.println("小程序登陆成功 = " + JSON.toJSONString(response));

        //检测是否存在
        UserToken userToken = userTokenService.getByOpenidAndType(response.getOpenId(), Constants.THIRD_LOGIN_TOKEN_TYPE_PROGRAM);
        LoginResponse loginResponse = new LoginResponse();
        if(ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
            User user = userService.getById(userToken.getUid());
            if (!user.getStatus()) {
                throw new CrmebException("当前账户已禁用，请联系管理员！");
            }

            // 记录最后一次登录时间
            user.setLastLoginTime(DateUtil.nowDateTime());
            Boolean execute = transactionTemplate.execute(e -> {
                // 分销绑定
                if (userService.checkBingSpread(user, request.getSpreadPid(), "old")) {
                    user.setSpreadUid(request.getSpreadPid());
                    user.setSpreadTime(DateUtil.nowDateTime());
                    // 处理新旧推广人数据
                    userService.updateSpreadCountByUid(request.getSpreadPid(), "add");
                }
                userService.updateById(user);
                return Boolean.TRUE;
            });
            if (!execute) {
                logger.error(StrUtil.format("小程序登录绑定分销关系失败，uid={},spreadUid={}", user.getUid(), request.getSpreadPid()));
            }

            try {
                String token = userService.token(user);
                loginResponse.setToken(token);
            } catch (Exception e) {
                logger.error(StrUtil.format("小程序登录生成token失败，uid={}", user.getUid()));
                e.printStackTrace();
            }
            loginResponse.setType("login");
            loginResponse.setUid(user.getUid());
            loginResponse.setNikeName(user.getNickname());
            loginResponse.setPhone(user.getPhone());
            return loginResponse;
        }

        if (StrUtil.isBlank(request.getNickName()) && StrUtil.isBlank(request.getAvatar()) && StrUtil.isBlank(request.getHeadimgurl())) {
            // 返回后，前端去走注册起始页
            loginResponse.setType("start");
            return loginResponse;
        }

        request.setType(Constants.USER_LOGIN_TYPE_PROGRAM);
        request.setOpenId(response.getOpenId());
        String key = SecureUtil.md5(response.getOpenId());
        redisUtil.set(key, JSONObject.toJSONString(request), (long) (60 * 2), TimeUnit.MINUTES);
        loginResponse.setType("register");
        loginResponse.setKey(key);

        //返回
        return loginResponse;
    }

    /**
     * 推广人排行榜
     * @param type  String 时间范围(week-周，month-月)
     * @param pageParamRequest PageParamRequest 分页
     * @return List<LoginResponse>
     */
    @Override
    public List<User> getTopSpreadPeopleListByDate(String type, PageParamRequest pageParamRequest) {
        return userService.getTopSpreadPeopleListByDate(type, pageParamRequest);
    }

    /**
     * 佣金排行榜
     * @param type  String 时间范围
     * @param pageParamRequest PageParamRequest 分页
     * @return List<User>
     */
    @Override
    public List<User> getTopBrokerageListByDate(String type, PageParamRequest pageParamRequest) {
        // 获取佣金排行榜（周、月）
        List<UserBrokerageRecord> recordList = userBrokerageRecordService.getBrokerageTopByDate(type, pageParamRequest);
        if (CollUtil.isEmpty(recordList)) {
            return null;
        }

        List<Integer> uidList = recordList.stream().map(UserBrokerageRecord::getUid).collect(Collectors.toList());
        //查询用户
        HashMap<Integer, User> userVoList = userService.getMapListInUid(uidList);

        //解决排序问题
        List<User> userList = CollUtil.newArrayList();
        for (UserBrokerageRecord record: recordList) {
            User user = new User();
            User userVo = userVoList.get(record.getUid());

            user.setUid(record.getUid());
            user.setAvatar(userVo.getAvatar());
            user.setBrokeragePrice(record.getPrice());
            if(StrUtil.isBlank(userVo.getNickname())){
                user.setNickname(userVo.getPhone().substring(0, 2) + "****" + userVo.getPhone().substring(7));
            }else{
                user.setNickname(userVo.getNickname());
            }
            userList.add(user);
        }
        return userList;
    }

    /**
     * 推广海报图
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return List<SystemGroupData>
     */
    @Override
    public List<UserSpreadBannerResponse> getSpreadBannerList(PageParamRequest pageParamRequest) {
        return systemGroupDataService.getListByGid(Constants.GROUP_DATA_ID_SPREAD_BANNER_LIST, UserSpreadBannerResponse.class);
    }

    /**
     * 当前用户在佣金排行第几名
     * @param type  String 时间范围
     * @return 优惠券集合
     */
    @Override
    public Integer getNumberByTop(String type) {
        int number = 0;
        Integer userId = userService.getUserIdException();
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setLimit(100);

        List<UserBrokerageRecord> recordList = userBrokerageRecordService.getBrokerageTopByDate(type, pageParamRequest);
        if (CollUtil.isEmpty(recordList)) {
            return number;
        }

        for (int i = 0; i < recordList.size(); i++) {
            if (recordList.get(i).getUid().equals(userId)) {
                number = i + 1;
                break ;
            }
        }
        return number;
    }

    /**
     * 推广佣金明细
     * @param pageParamRequest 分页参数
     */
    @Override
    public PageInfo<SpreadCommissionDetailResponse> getSpreadCommissionDetail(PageParamRequest pageParamRequest) {
        User user = userService.getInfoException();
        return userBrokerageRecordService.findDetailListByUid(user.getUid(), pageParamRequest);
    }

    /**
     * 用户账单记录（现金）
     * @param type 记录类型：all-全部，expenditure-支出，income-收入
     * @return CommonPage
     */
    @Override
    public CommonPage<UserRechargeBillRecordResponse> nowMoneyBillRecord(String type, PageParamRequest pageRequest) {
        User user = userService.getInfo();
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("用户数据异常");
        }
        PageInfo<UserBill> billPageInfo = userBillService.nowMoneyBillRecord(user.getUid(), type, pageRequest);
        List<UserBill> list = billPageInfo.getList();

        // 获取年-月
        Map<String, List<UserBill>> map = CollUtil.newHashMap();
        list.forEach(i -> {
            String month = StrUtil.subPre(DateUtil.dateToStr(i.getCreateTime(), Constants.DATE_FORMAT), 7);
            if (map.containsKey(month)) {
                map.get(month).add(i);
            } else {
                List<UserBill> billList = CollUtil.newArrayList();
                billList.add(i);
                map.put(month, billList);
            }
        });
        List<UserRechargeBillRecordResponse> responseList = CollUtil.newArrayList();
        map.forEach((key, value) -> {
            UserRechargeBillRecordResponse response = new UserRechargeBillRecordResponse();
            response.setDate(key);
            response.setList(value);
            responseList.add(response);
        });

        PageInfo<UserRechargeBillRecordResponse> pageInfo = CommonPage.copyPageInfo(billPageInfo, responseList);
        return CommonPage.restPage(pageInfo);
    }

    /**
     * 注册绑定手机号(公共)
     * @param request 请求参数
     * @return 登录信息
     */
    @Override
    public LoginResponse registerBindingPhone(WxBindingPhoneRequest request) {
        // 验证用户类型-并设置对应token类型
        int tokenType;
        switch (request.getType()) {
            case "public":
                tokenType = Constants.THIRD_LOGIN_TOKEN_TYPE_PUBLIC;
                break;
            case "routine":
                tokenType = Constants.THIRD_LOGIN_TOKEN_TYPE_PROGRAM;
                break;
            case "iosWx":
                tokenType = Constants.THIRD_LOGIN_TOKEN_TYPE_IOS_WX;
                break;
            case "androidWx":
                tokenType = Constants.THIRD_LOGIN_TOKEN_TYPE_ANDROID_WX;
                break;
            case "ios-ios":
                tokenType = Constants.THIRD_LOGIN_TOKEN_TYPE_IOS;
                break;
            case "zijie_routine":
                tokenType = 9;
                break;
            default:
                throw new CrmebException("未知的用户类型");
        }

        // 校验-手机号以及-用户类型的相关参数
        this.checkBindingPhone(request);

        // 根据key(新用户登录时返回的key)-读取缓存
        Object o = redisUtil.get(request.getKey());
        if (ObjectUtil.isNull(o)) {
            throw new CrmebException("用户缓存已过期，请清除缓存重新登录");
        }
        // 将值转成对象
        RegisterThirdUserRequest registerThirdUserRequest = JSONObject.parseObject(o.toString(), RegisterThirdUserRequest.class);

        // 是否创建
        boolean isNew = true;

        // 验证-手机号是否存在
        User user = userService.getByPhone(request.getPhone());
        if (ObjectUtil.isNull(user)) {
            // 不存在，通过用户信息注册
            user = userService.registerByThird(registerThirdUserRequest);
            user.setPhone(request.getPhone());
            user.setAccount(request.getPhone());
            user.setSpreadUid(0);
            user.setPwd(CommonUtil.createPwd(request.getPhone()));
        } else {
            // 已有账户，关联到之前得账户即可
            UserToken userToken = userTokenService.getTokenByUserId(user.getUid(), tokenType);
            if (ObjectUtil.isNotNull(userToken)) {
                throw new CrmebException("该手机号已被注册!");
            }

            //修改为否：不创建
            isNew = false;
        }

        //执行操作
        User finalUser = user;
        boolean finalIsNew = isNew;
        int finalTokenType = tokenType;
        Boolean execute = transactionTemplate.execute(e -> {
            //验证-是否创建-新用户
            if (finalIsNew) {
                // 分销绑定
                if (userService.checkBingSpread(finalUser, registerThirdUserRequest.getSpreadPid(), "new")) {
                    finalUser.setSpreadUid(registerThirdUserRequest.getSpreadPid());
                    finalUser.setSpreadTime(DateUtil.nowDateTime());
                    userService.updateSpreadCountByUid(registerThirdUserRequest.getSpreadPid(), "add");
                }

                // 更新用户信息
                userService.save(finalUser);

                // 赠送新人券
                this.giveNewPeopleCoupon(finalUser.getUid());
            }

            // 根据用户类型-绑定token
            userTokenService.bind(registerThirdUserRequest.getOpenId(), finalTokenType, finalUser.getUid());

            //返回执行结果
            return Boolean.TRUE;
        });

        //验证-执行结果
        if (!execute) {
            logger.error("用户注册生成失败，nickName = " + registerThirdUserRequest.getNickName());
        } else if (!isNew){
            // 老用户绑定推广人
            if (ObjectUtil.isNotNull(registerThirdUserRequest.getSpreadPid()) && registerThirdUserRequest.getSpreadPid() > 0) {
                loginService.bindSpread(finalUser, registerThirdUserRequest.getSpreadPid());
            }
        }

        //实例化-登录响应对象
        LoginResponse loginResponse = new LoginResponse();

        //生成token
        try {
            String token = userService.token(finalUser);
            loginResponse.setToken(token);
        } catch (Exception e) {
            logger.error(StrUtil.format("绑定手机号，自动登录生成token失败，uid={}", finalUser.getUid()));
            e.printStackTrace();
        }

        //赋值其他参数
        loginResponse.setType("login");
        loginResponse.setUid(user.getUid());
        loginResponse.setNikeName(user.getNickname());
        loginResponse.setPhone(user.getPhone());

        //返回
        return loginResponse;
    }

    /**
     * 新用户其他操作
     * 1、新用户注册加积分
     * 2、验证有无上级、给上级加推广积分
     * @return
     */
    private UserIntegralRecord newUserOther(User user) {
        //验证是不是新用户，以及是否由推广员推广加入的,给推广员加积分
        if(user.getLastLoginTime()==null && user.getSpreadUid()!=0){
            //给分享人加分享好友积分
            //integralRecordService.shareFriendsPoints(user.getSpreadUid());
        }

        //新用户没有最后一次登录时间，设置加积分。
        if(user.getLastLoginTime() == null){
            //得到登录注册配置表单
            Map<String,String> mapSysForm= systemConfigService.info(ConstantsFromID.INT_NEE_USER_CONFIG_FORM);
            if(mapSysForm == null){
                throw new CrmebException("失败! 未对应登录注册配置表单id:"+ConstantsFromID.INT_NEE_USER_CONFIG_FORM);
            }
            //新用户获得积分值
            Integer newUserIntegal=Integer.valueOf(mapSysForm.get("newUserIntegal"));

            //给新用户加积分
            user.setIntegral(new BigDecimal(newUserIntegal));
            userService.updateById(user);

            //生成用户积分记录
            UserIntegralRecord integralRecord = new UserIntegralRecord();
            integralRecord.setUid(user.getUid());
            integralRecord.setLinkType("newUser");
            integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
            integralRecord.setTitle(mapSysForm.get("newUserIntegalRecordTitle"));
            integralRecord.setIntegral(new BigDecimal(newUserIntegal));
            integralRecord.setBalance(user.getIntegral());
            integralRecord.setMark(StrUtil.format("新用户注册登录积分奖励增加了{}积分", newUserIntegal));
            integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
            userIntegralRecordService.save(integralRecord);

            //返回积分记录
            return integralRecord;
        }
        return null;
    }

    /**
     * 用户积分记录列表
     * @param pageParamRequest 分页参数
     * @return List<UserIntegralRecord>
     */
    @Override
    public List<UserIntegralRecordResponse> getUserIntegralRecordList(PageParamRequest pageParamRequest) {
        Integer uid = userService.getUserIdException();
        return userIntegralRecordService.findUserIntegralRecordList(uid, pageParamRequest);
    }

    @Override
    public IntegralUserResponse getIntegralUser(Integer uid) {
        //得到-用户信息
        User user;
        if(uid!=null&&uid>0){
            user = userService.getById(uid);
            if(user == null )throw new CrmebException("该用户已不存在！");
        }else{
            user = userService.getInfoException();
        }

        //实例化-响应对象
        IntegralUserResponse userSignInfoResponse = new IntegralUserResponse();

        //积分余额
        BigDecimal integral=user.getIntegral();
        userSignInfoResponse.setIntegral(integral);

        //累计总收入积分
        BigDecimal sumIntegral = userIntegralRecordService.getSumIntegral(
                user.getUid(),
                IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD, "",
                null,
                IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE
        );
        userSignInfoResponse.setSumIntegral(sumIntegral);

        //累计消费积分
        BigDecimal deductionIntegral = userIntegralRecordService.getSumIntegral(
                user.getUid(),
                IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB,
                "",
                IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER,
                IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE
        );
        userSignInfoResponse.setDeductionIntegral(deductionIntegral);

        //待结算积分
        BigDecimal djsIntegral = userIntegralRecordService.getSumIntegral(
                user.getUid(),
                IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD,
                "",
                IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_PUBLIC,
                IntegralRecordConstants.INTEGRAL_RECORD_STATUS_DJS
        );
        userSignInfoResponse.setDjsIntegral(djsIntegral);

        //计算-可用积分额度
        BigDecimal availableIntegral = userService.getKeyonMiED(user);
        userSignInfoResponse.setAvailableIntegral(availableIntegral);

        //计算-消费额度
        BigDecimal orderStatusSum = storeOrderService.getSumBigDecimal(user.getUid(), null, null,false);// 用户订单支付的累计消费(包含未完成订单)
        BigDecimal quota = userService.getKeyonMiED2(user,orderStatusSum);
        userSignInfoResponse.setQuota(quota);

        //计算-冻结积分
        BigDecimal frozenIntegralBig;
        if(quota.compareTo(integral) == 1){ //计算消费冻结,如果消费额度大于积分余额
            frozenIntegralBig = quota.subtract(availableIntegral);
        }else if(availableIntegral.compareTo(integral) < 1){ //计算余额冻结，如果可用积分小于等于积分余额
            frozenIntegralBig = integral.subtract(availableIntegral);
        }else {
            frozenIntegralBig = availableIntegral.subtract(integral);
        }
        userSignInfoResponse.setFrozenIntegral(frozenIntegralBig);

        //得到积分规则
        Map<String,String> mapSysForm=systemConfigService.info(ConstantsFromID.INT_INTEGAL_CONFIG_FORM);
        if(mapSysForm != null){
            //积分规则
            String integralRule= mapSysForm.get("integralRule");
            userSignInfoResponse.setIntegralRule(integralRule);
        }else{
            throw new CrmebException("获取积分规则失败! 未对应表单id:"+ConstantsFromID.INT_INTEGAL_CONFIG_FORM);
        }

        //返回
        return userSignInfoResponse;
    }

    /**
     * 获取用户经验记录
     * @param pageParamRequest 分页参数
     * @return List<UserBill>
     */
    @Override
    public List<UserBill> getUserExperienceList(PageParamRequest pageParamRequest) {
        Integer userId = userService.getUserIdException();
        return userBillService.getH5List(userId, Constants.USER_BILL_CATEGORY_EXPERIENCE, pageParamRequest);
    }

    /**
     * 提现用户信息
     * @return UserExtractCashResponse
     */
    @Override
    public UserExtractCashResponse getExtractUser() {
        User user = userService.getInfoException();
        // 提现最低金额
        String minPrice = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_EXTRACT_MIN_PRICE);
        // 冻结天数
        String extractTime = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_EXTRACT_FREEZING_TIME);
        // 可提现佣金
        BigDecimal brokeragePrice = user.getBrokeragePrice();
        // 冻结佣金
        BigDecimal freeze = userBrokerageRecordService.getFreezePrice(user.getUid());
        return new UserExtractCashResponse(minPrice, brokeragePrice, freeze, extractTime);
    }

    @Override
    public UserSpreadPeopleResponse getSpreadPeopleCount(Integer uid) {
        //查询当前用户名下的一级推广员
        UserSpreadPeopleResponse userSpreadPeopleResponse = new UserSpreadPeopleResponse();
        List<Integer> userIdList = new ArrayList<>();
        if(uid == null){
            Integer userId = userService.getUserIdException();
            userIdList.add(userId);
        }else{
            userIdList.add(uid);
        }
        userIdList = userService.getSpreadPeopleIdList(userIdList); //我推广的一级用户id集合
        if (CollUtil.isEmpty(userIdList)) {//如果没有一级推广人，直接返回
            userSpreadPeopleResponse.setCount(0);
            userSpreadPeopleResponse.setTotal(0);
            userSpreadPeopleResponse.setTotalLevel(0);
            return userSpreadPeopleResponse;
        }

        userSpreadPeopleResponse.setTotal(userIdList.size()); //一级推广人

        //查询二级推广人
        List<Integer> secondSpreadIdList = userService.getSpreadPeopleIdList(userIdList);
        if (CollUtil.isEmpty(secondSpreadIdList)) {
            userSpreadPeopleResponse.setTotalLevel(0);
            userSpreadPeopleResponse.setCount(userSpreadPeopleResponse.getTotal());
            return userSpreadPeopleResponse;
        }
        userSpreadPeopleResponse.setTotalLevel(secondSpreadIdList.size());
        userSpreadPeopleResponse.setCount(userIdList.size() + secondSpreadIdList.size());
        return userSpreadPeopleResponse;
    }

    /**
     * 绑定手机号数据校验
     */
    private void checkBindingPhone(WxBindingPhoneRequest request) {
        //公众号、安卓微信、ios微信用户、苹果账号
        if (request.getType().equals("public")||
                request.getType().equals("androidWx")||
                request.getType().equals("iosWx") ||
                request.getType().equals("ios-ios")) {
            //验证-验证码非空
            if (StrUtil.isBlank(request.getCaptcha())) {
                throw new CrmebException("验证码不能为空");
            }

            //验证-手机号格式
            boolean matchPhone = ReUtil.isMatch(RegularConstants.PHONE, request.getPhone());
            if (!matchPhone) {
                throw new CrmebException("手机号格式错误，请输入正确得手机号");
            }

            //校验-验证码
            boolean match = ReUtil.isMatch(RegularConstants.SMS_VALIDATE_CODE_NUM, request.getCaptcha());
            if (!match) {
                throw new CrmebException("验证码格式错误，验证码必须为6位数字");
            }

            //检测-验证码
            checkValidateCode(request.getPhone(), request.getCaptcha());
        }

        //小程序用户
        else if(request.getType().equals("routine")) {
            // 参数校验
            if (StrUtil.isBlank(request.getCode())) {
                throw new CrmebException("小程序获取手机号code不能为空");
            }
            if (StrUtil.isBlank(request.getEncryptedData())) {
                throw new CrmebException("小程序获取手机号加密数据不能为空");
            }
            if (StrUtil.isBlank(request.getIv())) {
                throw new CrmebException("小程序获取手机号加密算法的初始向量不能为空");
            }

            // 获取appid
            String programAppId = systemConfigService.getValueByKey("routine_appid");
            if(StringUtils.isBlank(programAppId)){
                throw new CrmebException("微信小程序appId未设置");
            }

            //得到-微信小程序用户授权返回数据
            WeChatProgramAuthorizeLoginGetOpenIdResponse response = weChatService.programAuthorizeLogin(request.getCode());
            System.out.println("小程序登陆成功 = " + JSON.toJSONString(response));
            String decrypt = WxUtil.decrypt(programAppId, request.getEncryptedData(), response.getSessionKey(), request.getIv());
            if (StrUtil.isBlank(decrypt)) {
                throw new CrmebException("微信小程序获取手机号解密失败");
            }
            JSONObject jsonObject = JSONObject.parseObject(decrypt);
            if (StrUtil.isBlank(jsonObject.getString("phoneNumber"))) {
                throw new CrmebException("微信小程序获取手机号没有有效的手机号");
            }
            request.setPhone(jsonObject.getString("phoneNumber"));
        }

        //其他用户
        else if(request.getType().equals("zijie_routine")){
            // 参数校验
            if (StrUtil.isBlank(request.getCode())) {
                throw new CrmebException("字节小程序获取手机号code不能为空");
            }
            if (StrUtil.isBlank(request.getEncryptedData())) {
                throw new CrmebException("字节小程序获取手机号加密数据不能为空");
            }
            if (StrUtil.isBlank(request.getIv())) {
                throw new CrmebException("字节小程序获取手机号加密算法的初始向量不能为空");
            }

            //通过code获取access_token
            String programAppId = systemConfigService.getValueByKey("zhijie_appid");
            if(StringUtils.isBlank(programAppId)){
                throw new CrmebException("字节小程序appId未设置");
            }
            //验证zhijie_appsecret
            String programAppSecret = systemConfigService.getValueByKey("zhijie_appsecret");
            if(StringUtils.isBlank(programAppSecret)){
                throw new CrmebException("字节小程序secret未设置");
            }

            //拼接请求https://developer.toutiao.com/api/apps/v2/jscode2session
            String url = new StringBuffer(Constants.ZIJIE_APl_URL).append(Constants.ZIJIE_API_GET_ACCESS_TOKEN).toString();
            Map<String,Object> map=new HashMap<>();
            map.put("appid",programAppId);
            map.put("secret",programAppSecret);
            map.put("code",request.getCode());
            String result = restTemplateUtil.postMapData(url,map);
            JSONObject jsonObject=JSONObject.parseObject(result);
            JSONObject data=jsonObject.getJSONObject("data");
            String decrypt = WxUtil.decrypt(programAppId, request.getEncryptedData(), data.getString("session_key"), request.getIv());
            if (StrUtil.isBlank(decrypt)) {
                throw new CrmebException("字节小程序获取手机号解密失败");
            }
            JSONObject decryptData = JSONObject.parseObject(decrypt);
            if (StrUtil.isBlank(decryptData.getString("phoneNumber"))) {
                throw new CrmebException("字节小程序获取手机号没有有效的手机号");
            }
            request.setPhone(decryptData.getString("phoneNumber"));
        }
    }

    /**
     * 赠送新人券
     * @param uid 用户uid
     */
    private void giveNewPeopleCoupon(Integer uid) {
        // 查询是否有新人注册赠送优惠券
        List<StoreCouponUser> couponUserList = CollUtil.newArrayList();
        List<StoreCoupon> couponList = storeCouponService.findRegisterList();
        if (CollUtil.isNotEmpty(couponList)) {
            couponList.forEach(storeCoupon -> {
                //是否有固定的使用时间
                if(!storeCoupon.getIsFixedTime()){
                    String endTime = DateUtil.addDay(DateUtil.nowDate(Constants.DATE_FORMAT), storeCoupon.getDay(), Constants.DATE_FORMAT);
                    storeCoupon.setUseEndTime(DateUtil.strToDate(endTime, Constants.DATE_FORMAT));
                    storeCoupon.setUseStartTime(DateUtil.nowDateTimeReturnDate(Constants.DATE_FORMAT));
                }

                StoreCouponUser storeCouponUser = new StoreCouponUser();
                storeCouponUser.setCouponId(storeCoupon.getId());
                storeCouponUser.setName(storeCoupon.getName());
                storeCouponUser.setMoney(storeCoupon.getMoney());
                storeCouponUser.setMinPrice(storeCoupon.getMinPrice());
                storeCouponUser.setUseType(storeCoupon.getUseType());
                if (storeCoupon.getIsFixedTime()) {// 使用固定时间
                    storeCouponUser.setStartTime(storeCoupon.getUseStartTime());
                    storeCouponUser.setEndTime(storeCoupon.getUseEndTime());
                } else {// 没有固定使用时间
                    Date nowDate = DateUtil.nowDateTime();
                    storeCouponUser.setStartTime(nowDate);
                    DateTime dateTime = cn.hutool.core.date.DateUtil.offsetDay(nowDate, storeCoupon.getDay());
                    storeCouponUser.setEndTime(dateTime);
                }
                storeCouponUser.setType("register");
                if (storeCoupon.getUseType() > 1) {
                    storeCouponUser.setPrimaryKey(storeCoupon.getPrimaryKey());
                }
                storeCouponUser.setType(CouponConstants.STORE_COUPON_USER_TYPE_REGISTER);
                couponUserList.add(storeCouponUser);
            });
        }

        // 赠送客户优惠券
        if (CollUtil.isNotEmpty(couponUserList)) {
            couponUserList.forEach(couponUser -> couponUser.setUid(uid));
            storeCouponUserService.saveBatch(couponUserList);
            couponList.forEach(coupon -> storeCouponService.deduction(coupon.getId(), 1, coupon.getIsLimited()));
        }
    }

    /**
     * 检测手机验证码
     * @param phone 手机号
     * @param code 验证码
     */
    private void checkValidateCode(String phone, String code) {
        //从缓存取出验证码
        Object validateCode = redisUtil.get(SmsConstants.SMS_VALIDATE_PHONE + phone);
        if(validateCode == null){
            throw new CrmebException("验证码已过期");
        }
        if(!validateCode.toString().equals(code)){
            throw new CrmebException("验证码错误");
        }

        //删除验证码
        redisUtil.remove(SmsConstants.SMS_VALIDATE_PHONE + phone);
    }
}
