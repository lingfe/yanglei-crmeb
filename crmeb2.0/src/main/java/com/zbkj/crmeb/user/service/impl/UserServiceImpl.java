package com.zbkj.crmeb.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.*;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qiniu.util.Json;
import com.utils.*;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.authorization.manager.TokenManager;
import com.zbkj.crmeb.authorization.model.TokenModel;
import com.zbkj.crmeb.finance.model.UserExtract;
import com.zbkj.crmeb.finance.request.FundsMonitorSearchRequest;
import com.zbkj.crmeb.finance.request.UserExtractRequest;
import com.zbkj.crmeb.finance.service.UserExtractService;
import com.zbkj.crmeb.front.request.PasswordPayRequest;
import com.zbkj.crmeb.front.request.PasswordRequest;
import com.zbkj.crmeb.front.request.UserBindingPhoneUpdateRequest;
import com.zbkj.crmeb.front.response.*;
import com.zbkj.crmeb.front.service.LoginService;
import com.zbkj.crmeb.front.service.QrCodeService;
import com.zbkj.crmeb.front.service.UserCenterService;
import com.zbkj.crmeb.front.service.impl.OrderServiceImpl;
import com.zbkj.crmeb.marketing.model.StoreCoupon;
import com.zbkj.crmeb.marketing.model.StoreCouponUser;
import com.zbkj.crmeb.marketing.request.StoreCouponUserSearchRequest;
import com.zbkj.crmeb.marketing.service.StoreCouponService;
import com.zbkj.crmeb.marketing.service.StoreCouponUserService;
import com.zbkj.crmeb.pub.vo.PublicCodeVo;
import com.zbkj.crmeb.regionalAgency.model.RegionalAgency;
import com.zbkj.crmeb.regionalAgency.service.RegionalAgencyService;
import com.zbkj.crmeb.sms.service.SmsService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.request.RetailShopStairUserRequest;
import com.zbkj.crmeb.store.response.SpreadOrderResponse;
import com.zbkj.crmeb.store.service.StoreOrderInfoService;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.store.service.StoreProductRelationService;
import com.zbkj.crmeb.store.vo.StoreOrderInfoOldVo;
import com.zbkj.crmeb.system.model.SystemUserLevel;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.system.service.SystemUserLevelService;
import com.zbkj.crmeb.user.dao.UserDao;
import com.zbkj.crmeb.user.model.*;
import com.zbkj.crmeb.user.request.*;
import com.zbkj.crmeb.user.response.*;
import com.zbkj.crmeb.user.service.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;

/**
 * 用户表 服务实现类
 *
 * 2021.07.15
 * 1、新增了推广等级路径path
 * @author 零风
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserDao userDao;

    @Autowired
    private UserBillService userBillService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemUserLevelService systemUserLevelService;

    @Autowired
    private UserLevelService userLevelService;

    @Autowired
    private UserTagService userTagService;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private UserSignService userSignService;

    @Autowired
    private StoreCouponUserService storeCouponUserService;

    @Autowired
    private StoreCouponService storeCouponService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserIntegralRecordService userIntegralRecordService;

    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private StoreProductRelationService storeProductRelationService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private UserExtractService userExtractService;

    @Autowired
    private RegionalAgencyService regionalAgencyService;

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private SmsService smsService;

    @Override
    public Map<String,String> transferIn(BigDecimal price,Integer type,User user,BigDecimal fee) {
        //定义变量
        String userbillType="";
        UserBrokerageRecord brokerageRecord=null;
        List<UserIntegralRecord> integralRecordList=new ArrayList<>();
        BigDecimal subtract;
        Map<String,String> map=new HashMap<>();
        map.put("result",Boolean.FALSE.toString());

        try{
            //验证类型
            switch (type){
                case Constants.USER_BILL_transferIn_TYPE_1:
                    //佣金转入账户余额
                    subtract = user.getBrokeragePrice();
                    userbillType=Constants.USER_BILL_TYPE_TRANSFER_IN;
                    brokerageRecord = userBrokerageRecordService.getUserBrokerageRecord(    // 佣金转入账户余额-佣金记录
                            user.getUid(),"0",
                            BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_YUE,
                            BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_5,
                            BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_SUB,
                            price,user.getBrokeragePrice().subtract(price),
                            0,0L
                    );
                    break;
                case Constants.USER_BILL_transferIn_TYPE_2:
                    //验证服务费是否为空
                    userbillType=Constants.USER_BILL_TYPE_integralTransferIn;
                    if(fee == null || fee.compareTo(ZERO) < 1){
                        //验证和计算服务费
                        Map<String,BigDecimal> mapBig=this.extracted(user,price);
                        price = mapBig.get("price");
                        fee = mapBig.get("fee");

                        //得到积分记录s
                        subtract = user.getIntegral();
                        integralRecordList = this.getUserIntegralRecords(user,fee,price,IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_integralTransferIn);
                        price = price.add(fee);
                    }else{
                        price = price.add(fee);
                        subtract = price;
                    }
                    break;
                default:
                    map.put("msg","转入账户余额类型错误！");
                    return map;
            }

            //验证-转入金额
            if (price.compareTo(BigDecimal.ZERO) < 1) {
                map.put("msg","转入金额不能为0!");
                return map;
            }

            //验证-可用余额
            if(subtract.compareTo(price) == -1){
                map.put("msg","您当前可用余额为 " + subtract + "元");
                return map;
            }

            // 转账户余额公共接口-账单记录
            UserBill userBill = userBillService.getUserBill(    // 转账户余额公共接口-账单记录
                    user.getUid(),
                    "0",
                    Constants.USER_BILL_PM_1,
                    Constants.USER_BILL_CATEGORY_MONEY,
                    userbillType,
                    price,user.getNowMoney().add(price),
                    ""
            );

            //执行事务
            UserBrokerageRecord finalBrokerageRecord = brokerageRecord;
            BigDecimal finalPrice = price;
            List<UserIntegralRecord> finalIntegralRecordList = integralRecordList;
            Boolean execute = transactionTemplate.execute(e -> {
                //验证-佣金记录是否为空
                if(finalBrokerageRecord != null){
                    this.operationBrokerage(user.getUid(), finalPrice, user.getBrokeragePrice(), "sub");//佣金转入账户余额-减佣金
                    userBrokerageRecordService.save(finalBrokerageRecord);
                }

                //验证-积分记录是否为空
                else if(finalIntegralRecordList != null && finalIntegralRecordList.size()>0){
                    this.operationIntegral(user.getUid(), finalPrice, user.getIntegral(), "sub");//积分转入账户余额-减积分
                    userIntegralRecordService.saveBatch(finalIntegralRecordList);
                }

                // 加余额
                this.operationNowMoney(user.getUid(), finalPrice, user.getNowMoney(), "add");

                // 保存账单记录
                userBillService.save(userBill);
                return Boolean.TRUE;
            });

            //验证结果
            if(execute){
                map.put("result",Boolean.TRUE.toString());
                map.put("msg","成功！");
            }else{
                map.put("msg","失败！");
            }

            //执行结果
            return map;
        }catch (Exception e){
            e.printStackTrace();
            map.put("msg",new StringBuffer("发生错误: ").append(e.getMessage()).toString());
            return map;
        }
    }

    private HashMap<String, String> getStringStringHashMap(BigDecimal price, User user) {
        //验证-积分余额是否充足
        if(user.getIntegral().compareTo(price) == -1){
            throw new CrmebException("积分余额不足！无法提现！");
        }

        //积分转入账户余额配置
        HashMap<String, String> formMap = systemConfigService.info(ConstantsFromID.INT_CONFIG_FORM_ID_167);
        if(formMap == null )throw new CrmebException("积分转入账户余额配置不存在！");

        //验证是否开启
        if(!Boolean.valueOf(formMap.get("integralTransferIn_is_open"))){
            throw new CrmebException("积分转入账户余额暂未开放！");
        }

        //得到可用米额度
        BigDecimal keyonEd = this.getKeyonMiED(user);
        if(keyonEd.compareTo(price) == -1){
            throw new CrmebException("可用米额度不足！快去消费吧！");
        }
        return formMap;
    }

    @Override
    public BigDecimal getKeyonMiED(User user) {
        //得到-用户累计消费金额-//得到-用户累计已提可用积分额度-//得到可用提现额度
        BigDecimal orderStatusSum = storeOrderService.getSumBigDecimal(user.getUid(), null, new Integer[]{3},false);// 用于验证可用额度
        BigDecimal orderStatusSum2 = this.getKeyonMiED2(user,orderStatusSum);
        if(orderStatusSum2.compareTo(user.getIntegral()) > -1){//如果累计消费金额 - 已提现金额 >= 积分余额
            return user.getIntegral();
        }else{
            return orderStatusSum2;
        }
    }

    @Override
    public BigDecimal getKeyonMiED2(User user,BigDecimal orderStatusSum) {
        LambdaQueryWrapper<UserExtract> userExtractLambdaQueryWrapper=new LambdaQueryWrapper<>();
        userExtractLambdaQueryWrapper.eq(UserExtract::getUid, user.getUid());
        userExtractLambdaQueryWrapper.in(UserExtract::getLinkType,
                Constants.USER_BALANCE_TIXIN_LINKTYPE_2,
                Constants.USER_BALANCE_TIXIN_LINKTYPE_3,
                Constants.USER_BALANCE_TIXIN_LINKTYPE_JFDHSUBQUOTA);
        userExtractLambdaQueryWrapper.in(UserExtract::getStatus,
                Constants.USER_EXTRACT_STATUS_SQZ,
                Constants.USER_EXTRACT_STATUS_SUCCESS);
        List<UserExtract> userExtractList = userExtractService.list(userExtractLambdaQueryWrapper);
        BigDecimal userExtractPrice=userExtractList.stream().map(UserExtract::getExtractPrice).reduce(ZERO,BigDecimal::add);
        BigDecimal fee=userExtractList.stream().map(UserExtract::getServiceFee).reduce(ZERO,BigDecimal::add);
        userExtractPrice=userExtractPrice.add(fee);
        if(orderStatusSum.compareTo(userExtractPrice) > -1){ //如果累计消费金额大于等于已提现
            orderStatusSum = orderStatusSum.subtract(userExtractPrice);
            if(orderStatusSum.compareTo(ZERO) > -1){
                return orderStatusSum;
            }else{
                return ZERO;
            }
        }else{
            return ZERO;
        }
    }

    @Override
    public CommonPage<UserIntegralRecordMonthResponse> transferAccountsIntegralList(PageParamRequest pageParamRequest) {
        User user=this.getInfoException();
        Page<UserIntegralRecord> userIntegralRecordPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserIntegralRecord> lqw = Wrappers.lambdaQuery();
        lqw.select(UserIntegralRecord::getId,
                UserIntegralRecord::getTitle,
                UserIntegralRecord::getType,
                UserIntegralRecord::getLinkType,
                UserIntegralRecord::getIntegral,
                UserIntegralRecord::getCreateTime);
        lqw.eq(UserIntegralRecord::getUid, user.getUid());
        lqw.in(UserIntegralRecord::getLinkType,
                IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_Collection,
                IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_transfer);
        lqw.orderByDesc(UserIntegralRecord::getCreateTime);
        List<UserIntegralRecord> billList = userIntegralRecordService.list(lqw);
        PageInfo<UserIntegralRecord> page = CommonPage.copyPageInfo(userIntegralRecordPage, billList);
        List<UserIntegralRecord> list = page.getList();

        // 获取年-月
        Map<String, List<UserIntegralRecord>> map = CollUtil.newHashMap();
        list.forEach(i -> {
            String month = StrUtil.subPre(DateUtil.dateToStr(i.getCreateTime(), Constants.DATE_FORMAT), 7);
            if (map.containsKey(month)) {
                map.get(month).add(i);
            } else {
                List<UserIntegralRecord> userIntegralRecordList = CollUtil.newArrayList();
                userIntegralRecordList.add(i);
                map.put(month, userIntegralRecordList);
            }
        });
        List<UserIntegralRecordMonthResponse> responseList = CollUtil.newArrayList();
        map.forEach((key, value) -> {
            UserIntegralRecordMonthResponse response = new UserIntegralRecordMonthResponse();
            response.setDate(key);
            response.setList(value);
            responseList.add(response);
        });

        PageInfo<UserIntegralRecordMonthResponse> pageInfo = CommonPage.copyPageInfo(page, responseList);
        return CommonPage.restPage(pageInfo);
    }

    @Override
    public Boolean isSetPayPwd() {
        User user=this.getInfoException();
        if(StringUtils.isNotBlank(user.getPayPwd()))return true;
        return false;
    }

    @Override
    public boolean passwordUpdate(String phone,String code,String password, Integer type) {
        //检测验证码
        this.checkValidateCode(phone, code);

        //查询用户
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getPhone, phone);
        User user = userDao.selectOne(lambdaQueryWrapper);

        //业务类型
        switch (type){
            case 1://修改登录密码
                String pwd= CrmebUtil.encryptPassword(password, user.getAccount());
                user.setPwd(pwd);//新登录密码
                break;
            case 2://修改支付密码
                String payPwd= CrmebUtil.encryptPassword(password, user.getUid().toString());
                user.setPayPwd(payPwd);//新支付密码
                break;
            default:
                throw new CrmebException("不支持该操作类型！");
        }

        //执行修改
        return update(user, lambdaQueryWrapper);
    }

    @Override
    public boolean passwordPay(PasswordPayRequest request) {
        return this.passwordUpdate(request.getPhone(),request.getValidateCode(),request.getPassword(),2);
    }

    @Override
    public Boolean transferAccountsIntegral(Integer uid,Integer type,BigDecimal value, String pwd) {
        try{
            //得到收款方用户信息
            User shouUser= userDao.selectById(uid);
            if(shouUser == null)throw new CrmebException("积分转账失败！收款人异常！");

            //得到当前登录用户信息
            User user=this.getInfoException();

            //验证-收款人和转账人是不是相同
            if(shouUser.getUid().equals(user.getUid())){
                throw new CrmebException("收款人和转账人不能相同！");
            }

            //验证-转账金额值非空大于零
            if(value == null || value.compareTo(BigDecimal.ZERO) < -1){
                throw new CrmebException("转账积分金额错误！");
            }

            //验证-积分余额是否充足
            if(user.getIntegral().compareTo(value) == -1){
                throw new CrmebException("积分余额不足！");
            }

            //验证-支付密码
            String payPwd = CrmebUtil.encryptPassword(pwd,user.getUid().toString());
            if(!user.getPayPwd().equals(payPwd)){
                throw new CrmebException("密码错误！");
            }

            //验证-转账方式
            switch (type){
                case 0: //账号ID转账
                    //积分转账-并增加积分记录
                    this.operationIntegral(shouUser.getUid(), value, shouUser.getIntegral(), "add");
                    UserIntegralRecord integralRecordZhuanzhang = userIntegralRecordService.getUserIntegralRecord( // 积分转账记录
                            shouUser.getUid(),
                            shouUser.getIntegral(),
                            shouUser.getUid().toString(),
                            IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_Collection,
                            IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD,
                            IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE,
                            value,user.getNickname());
                    userIntegralRecordService.save(integralRecordZhuanzhang);

                    //积分收款-并增加积分记录
                    this.operationIntegral(user.getUid(), value, user.getIntegral(), "sub");
                    UserIntegralRecord integralRecordShoukuan = userIntegralRecordService.getUserIntegralRecord( // 积分收款记录
                            user.getUid(),
                            user.getIntegral(),
                            user.getUid().toString(),
                            IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_transfer,
                            IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB,
                            IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE,
                            value,shouUser.getNickname());
                    userIntegralRecordService.save(integralRecordShoukuan);
                    break;
                case 1: //二维码转账
                case Constants.USER_BILL_transferIn_TYPE_3:
                    //验证-可用米额度
                    BigDecimal keIntegral=this.getKeyonMiED(user);
                    if(keIntegral.compareTo(value) == -1){
                        throw new CrmebException("用户可用米额度不足!");
                    }

                    //联盟商家收到用户扫码转米转入余额-账单记录
                    UserBill userBill = userBillService.getUserBill(    // 联盟商家收到用户扫码转米转入余额-账单记录
                            shouUser.getUid(),
                            user.getUid().toString(),
                            Constants.USER_BILL_PM_1,
                            Constants.USER_BILL_CATEGORY_MONEY,
                            Constants.USER_BILL_TYPE_isAllianceMerchants,
                            value,
                            shouUser.getNowMoney().add(value),
                            new StringBuffer(user.getUid()).append("【").append(user.getNickname()).toString()
                    );
                    userBillService.save(userBill);
                    this.operationNowMoney(shouUser.getUid(), value, shouUser.getNowMoney(), Constants.ADD_STR);

                    //用户积分记录
                    List<UserIntegralRecord> integralRecordList = this.getUserIntegralRecords(  // 扫码向联盟商家转米-积分记录
                            user,
                            ZERO,
                            value,
                            IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_isAllianceMerchants);
                    userIntegralRecordService.saveBatch(integralRecordList);
                    this.operationIntegral(user.getUid(), value, user.getIntegral().subtract(value), Constants.SUB_STR);

                    //增加-提现记录
                    UserExtract userExtract=new UserExtract();
                    userExtract.setLinkType(Constants.USER_BALANCE_TIXIN_LINKTYPE_3);
                    userExtract.setStatus(Constants.USER_EXTRACT_STATUS_SUCCESS);
                    userExtract.setUid(user.getUid());
                    userExtract.setExtractPrice(value);
                    userExtract.setServiceFee(ZERO);
                    userExtract.setBalance(user.getIntegral().subtract(value));
                    userExtract.setIsOk(Boolean.TRUE);
                    userExtract.setNickName(user.getNickname());
                    userExtract.setRealName(user.getRealName());
                    userExtract.setExtractType("other");
                    userExtract.setRemark("用户酒米二维码消费转账");
                    userExtract.setCreateTime(DateUtil.nowDateTime());
                    userExtractService.save(userExtract);
                    break;
                default:throw new CrmebException("转账类型错误！");
            }
            return Boolean.TRUE;
        }catch (Exception e){
            throw new CrmebException("错误:"+e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getUserCollectionCode(Integer uid) {
        User user;
        if(uid == null || uid <= 0){
            user=this.getInfoException();
        }else{
            user = userDao.selectById(uid);
            if(user == null)throw new CrmebException("用户ID不存在！");
        }
        PublicCodeVo vo=PublicCodeVo.builder().uid(user.getUid()).codeType(1).build();
        return qrCodeService.base64String(Json.encode(vo), 200,200);
    }

    @Override
    public Boolean accountBalanceWithdrawalIsExtract(Boolean isExtract, Integer id, String backMessage) {
        //根据ID标识读取提现记录
        UserExtract userExtract = userExtractService.getById(id);
        if(userExtract==null)throw new CrmebException("提现记录为：NULL!");

        //得到申请用户
        User user=this.getById(userExtract.getUid());
        if(user == null) throw new CrmebException("用户已不存在！");

        //是否
        Boolean result;
        if(isExtract){
            switch (userExtract.getLinkType()){
                case 1:
                    result = this.zhixintixin(userExtract);
                    if(!result){
                        this.tixinTuihuanNowMoney(userExtract,user);//账户余额提现失败，退还余额
                        throw new CrmebException(userExtract.getFailMsg());
                    } break;
                case 2:
                    Map<String,String> map = this.transferIn(userExtract.getExtractPrice(), Constants.USER_BILL_transferIn_TYPE_2, user, userExtract.getServiceFee());
                    result = Boolean.valueOf(map.get("result"));
                    if(!result){
                        userExtract.setStatus(Constants.USER_EXTRACT_STATUS_FAIL);
                        userExtract.setFailMsg(map.get("msg"));
                        userExtract.setRemark(userExtract.getFailMsg());
                        userExtract.setFailTime(DateUtil.nowDateTime());
                        userExtract.setIsOk(Boolean.FALSE);
                        this.tixintuihuanIntegral(userExtract,user);//酒米提现失败，退还酒米
                        userExtractService.saveOrUpdate(userExtract);
                        return Boolean.FALSE;
                    }else{
                        userExtract.setStatus(Constants.USER_EXTRACT_STATUS_SUCCESS);
                        userExtract.setRemark(map.get("msg"));
                        userExtract.setIsOk(Boolean.TRUE);
                    } break;
                default: throw new CrmebException("关联类型错误！");
            }

            //返回
            return Boolean.TRUE;
        }else{
            switch (userExtract.getLinkType()){
                case 1: this.tixinTuihuanNowMoney(userExtract, user);break;//账户余额提现不通过，退还余额
                case 2: this.tixintuihuanIntegral(userExtract, user);break;//酒米提现不通过，退还积分
                default: throw new CrmebException("关联类型错误！");
            }

            //no-不通过
            userExtract.setFailMsg(backMessage);
            userExtract.setStatus(Constants.USER_EXTRACT_STATUS_NO);
            userExtract.setIsOk(Boolean.FALSE);

            //返回结果
            return userExtractService.saveOrUpdate(userExtract);
        }
    }

    @Override
    public void tixinTuihuanNowMoney(UserExtract userExtract, User user) {
        //退还余额
        BigDecimal price= userExtract.getExtractPrice().add(userExtract.getServiceFee());
        UserBill userBill = userBillService.getUserBill(    // 提现不通过-账单记录
                user.getUid(),
                userExtract.getId().toString(),
                Constants.USER_BILL_PM_1,
                Constants.USER_BILL_CATEGORY_MONEY,
                Constants.USER_BILL_TYPE_EXTRACT_NO,
                price,
                user.getNowMoney(),
                ""
        );
        userBillService.save(userBill);
        this.operationNowMoney(user.getUid(),price, user.getNowMoney(),Constants.ADD_STR);//提现不通过-退还余额
    }

    @Override
    public void tixintuihuanIntegral(UserExtract userExtract, User user) {
        //统计退还积分，加上服务费
        BigDecimal integralSub = userExtract.getExtractPrice().add(userExtract.getServiceFee());
        List<UserIntegralRecord> integralRecordList=new ArrayList<>();
        integralRecordList.add(userIntegralRecordService.getUserIntegralRecord( // 酒米提现失败或不通过退还-积分记录
                user.getUid(),
                user.getIntegral(),
                user.getUid().toString(),
                IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_tixintuihuan,
                IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD,
                IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE,integralSub ,null));

        //保存记录并更新
        userIntegralRecordService.saveBatch(integralRecordList);
        this.operationIntegral(user.getUid(), integralSub, user.getIntegral(), Constants.ADD_STR);//酒米提现失败或不通过-退还积分
    }

    @Override
    public UserExtract accountBalanceWithdrawal(UserExtractRequest request) {
        //当前登录用户
        User user=this.getInfoException();

        //提现记录
        UserExtract userExtract=new UserExtract();
        BeanUtils.copyProperties(request,userExtract);//转换
        userExtract.setUid(user.getUid());
        userExtract.setRealName(user.getRealName());

        //验证关联类型-默认普通提现
        if(userExtract.getLinkType() == null || Constants.USER_BALANCE_TIXIN_LINKTYPE_1 == userExtract.getLinkType()){
            //账户余额提现配置
            HashMap<String, String> formMap = systemConfigService.info(ConstantsFromID.INT_CONFIG_FORM_ID_168);
            if(formMap == null )throw new CrmebException("账户余额提现配置,不存在！");

            //验证-是否开启
            if(!Boolean.valueOf(formMap.get("accountBalance_tixin_isOpen"))){
                throw new CrmebException("账户余额提现,暂未开放！");
            }

            //验证-账户是否充足
            BigDecimal extractPrice=userExtract.getExtractPrice();
            if(user.getNowMoney().compareTo(extractPrice) == -1){
                throw new CrmebException("账户余额不足！无法提现！");
            }

            //计算服务费
            //验证提现服务费
            List<UserBill> userBillList=new ArrayList<>();
            BigDecimal service_fee=new BigDecimal(formMap.get("accountBalance_tixin_rate"));
            BigDecimal fee=service_fee.divide(new BigDecimal(100)).multiply(extractPrice);
            if(fee.compareTo(ZERO) == 1){
                UserBill userBillFee = userBillService.getUserBill(    // 账户余额提现服务费-账单记录
                        user.getUid(),
                        String.valueOf(userExtract.getId()),
                        Constants.USER_BILL_PM_0,
                        Constants.USER_BILL_CATEGORY_MONEY,
                        Constants.USER_BILL_TYPE_EXTRACT_FEE,
                        fee,
                        user.getNowMoney(),
                        ""
                );
                userBillList.add(userBillFee);

                //更新值
                userExtract.setServiceFee(fee);
                userExtract.setExtractPrice(userExtract.getExtractPrice().subtract(fee));
            }

            //保存账单记录
            UserBill userBill = userBillService.getUserBill(    // 账户余额提现-账单记录
                    user.getUid(),
                    String.valueOf(userExtract.getId()),
                    Constants.USER_BILL_PM_0,
                    Constants.USER_BILL_CATEGORY_MONEY,
                    Constants.USER_BILL_TYPE_EXTRACT,
                    userExtract.getExtractPrice(),
                    user.getNowMoney(),
                    ""
            );
            userBillList.add(userBill);
            userBillService.saveBatch(userBillList);
            this.operationNowMoney(user.getUid(), extractPrice, user.getNowMoney(), Constants.SUB_STR);// 扣除账户余额

            //验证-提现金额是否需要审核
            BigDecimal vPrice=new BigDecimal(formMap.get("accountBalance_tixin_auditAmount"));
            if(vPrice.compareTo(extractPrice) == 1){
                //返回,并验证是否成功！
                Boolean bl = this.zhixintixin(userExtract);
                if(bl){
                    return userExtract;
                }else{
                    this.tixinTuihuanNowMoney(userExtract,user);//提现失败，退还余额
                    throw new CrmebException(userExtract.getFailMsg());
                }
            }
        }

        //申请信用卡还款资金
        else if(userExtract.getLinkType().equals(Constants.USER_BALANCE_TIXIN_LINKTYPE_2)){
            Map<String,BigDecimal>  mapBig = this.extracted(user, userExtract.getExtractPrice());
            userExtract.setExtractPrice(mapBig.get("price"));
            userExtract.setServiceFee(mapBig.get("fee"));

            //统计
            List<UserIntegralRecord> integralRecordList = this.getUserIntegralRecords(user, userExtract.getServiceFee(),userExtract.getExtractPrice(),IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_integralTransferIn);
            BigDecimal integralSub= integralRecordList.stream().map(UserIntegralRecord::getIntegral).reduce(ZERO, BigDecimal::add);
            this.operationIntegral(user.getUid(), integralSub, user.getIntegral(), Constants.SUB_STR);//积分转入账户余额-减积分
            userIntegralRecordService.saveBatch(integralRecordList);
        }

        //关联类型错误
        else{
            throw new CrmebException("关联类型错误！");
        }

        //更新或保存提现记录
        userExtract.setStatus(Constants.USER_EXTRACT_STATUS_SQZ);
        userExtract.setIsOk(Boolean.FALSE);
        userExtract.setRemark("已提交!审核中...");
        userExtractService.saveOrUpdate(userExtract);
        return userExtract;
    }

    /**
     * 积分转入账户余额记录s
     * @param user  用户信息
     * @param fee   服务费
     * @param price 转入值
     * @return
     */
    private List<UserIntegralRecord> getUserIntegralRecords(User user, BigDecimal fee,BigDecimal price,String linkType) {
        //服务费
        List<UserIntegralRecord> integralRecordList=new ArrayList<>();
        if(fee.compareTo(ZERO) == 1){
            integralRecordList.add(userIntegralRecordService.getUserIntegralRecord( // 服务费-积分记录
                    user.getUid(),
                    user.getIntegral(),
                    user.getUid().toString(),
                    IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_SERVICE_FEE,
                    IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB,
                    IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE, fee,null));
        }

        //积分转入账户余额-参数设置
        integralRecordList.add(userIntegralRecordService.getUserIntegralRecord( // 积分转入账户余额-积分记录
                user.getUid(),
                user.getIntegral(),
                user.getUid().toString(),
                linkType,
                IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB,
                IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE, price,null));
        return integralRecordList;
    }

    //验证和计算服务费
    private Map<String,BigDecimal> extracted(User user, BigDecimal price) {
        //得到并验证积分提现配置
        HashMap<String, String> formMap2 = this.getStringStringHashMap(price, user); // 转入账户余额(公共接口)
        //计算服务费
        BigDecimal service_fee=new BigDecimal(formMap2.get("integralTransferIn_service_fee"));
        BigDecimal fee=service_fee.divide(new BigDecimal(100)).multiply(price);
        //更新值
        Map<String,BigDecimal> map=new HashMap<>();
        map.put("fee",fee);
        map.put("price",price.subtract(fee));
        return map;
    }

    @Override
    public boolean zhixintixin(UserExtract userExtract) {
        HashMap<String, Object>  map=new HashMap<>();
        String failMsg=null;

        //验证-提现方式
        switch (userExtract.getExtractType()){
            case PayConstants.PAY_TYPE_WE_CHAT: //提现-到微信零钱
                map = userExtractService.weixinPayToChange(
                        userExtract.getExtractPrice(),
                        userExtract.getAppidType(),
                        userExtract.getUid());
                break;
            case PayConstants.PAY_TYPE_BANK:   //提现-到银行卡
            case PayConstants.PAY_TYPE_ALI_PAY: //提现到-支付宝
            default:
                map.put("msg","暂不支持该提现方式！");
                break;
        }

        //验证-提现结果
        Boolean result=Boolean.FALSE;
        if(map!=null){
            result=Boolean.valueOf(String.valueOf(map.get("result")));
            if(result){
                userExtract.setStatus(Constants.USER_EXTRACT_STATUS_SUCCESS);
                userExtract.setIsOk(Boolean.TRUE);
                userExtract.setRemark(String.valueOf(map.get("msg")));
            }else{
                failMsg=String.valueOf(map.get("msg"));
            }
        }else{
            failMsg="map为空！提现失败！";
        }

        //验证结果
        if(!result){
            //更新提现记录
            userExtract.setStatus(Constants.USER_EXTRACT_STATUS_FAIL);
            userExtract.setFailMsg(failMsg);
            userExtract.setRemark(failMsg);
            userExtract.setFailTime(DateUtil.nowDateTime());
            userExtractService.saveOrUpdate(userExtract);
            return Boolean.FALSE;
        }else{
            //返回结果
            //更新提现记录
            return userExtractService.saveOrUpdate(userExtract);
        }
    }

    @Override
    public Boolean applyWithdrawal(UserExtractRequest userExtractRequest) {
        switch (userExtractRequest.getExtractType()){
            case PayConstants.PAY_TYPE_BANK:   //提现-到银行卡
                return userExtractService.applyWithdrawalToBankCard(userExtractRequest);
            case PayConstants.PAY_TYPE_ALI_PAY:    //提现-到支付宝
                return userExtractService.applyWithdrawalToAlipay(userExtractRequest);
            case PayConstants.PAY_TYPE_WE_CHAT: //提现-到微信零钱
                return userExtractService.applyWithdrawalToWeixin(userExtractRequest);
            default:
                throw new  CrmebException("请选择提现方式!");
        }
    }

    @Override
    public PageInfo<UserResponse> getList(UserSearchRequest request, PageParamRequest pageParamRequest) {
        //得到分页参数
        Page<User> pageUser = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //定义查询条件
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        Map<String, Object> map = CollUtil.newHashMap();

        //条件-是否为推广员
        if (request.getIsPromoter() != null) {
            map.put("isPromoter", request.getIsPromoter() ? 1 : 0);
            lambdaQueryWrapper.eq(User::getIsPromoter,request.getIsPromoter() ? 1 : 0);
        }
        //条件-用户分组id
        if (!StringUtils.isBlank(request.getGroupId())) {
            map.put("groupId", request.getGroupId());
            lambdaQueryWrapper.eq(User::getGroupId,request.getGroupId());
        }
        //条件-用户标签id
        if (!StringUtils.isBlank(request.getLabelId())) {
            String tagIdSql = CrmebUtil.getFindInSetSql("u.tag_id", request.getLabelId());
            map.put("tagIdSql", tagIdSql);
            lambdaQueryWrapper.eq(User::getTagId,tagIdSql);
        }
        //条件-等级
        if (!StringUtils.isBlank(request.getLevel())) {
            map.put("level", request.getLevel());
            lambdaQueryWrapper.in(User::getLevel,request.getLevel());
        }
        //条件-用户类型
        if (StringUtils.isNotBlank(request.getUserType())) {
            map.put("userType", request.getUserType());
            lambdaQueryWrapper.eq(User::getUserType,request.getUserType());
        }
        //条件-性别
        if (StringUtils.isNotBlank(request.getSex())) {
            map.put("sex", Integer.valueOf(request.getSex()));
            lambdaQueryWrapper.eq(User::getSex, request.getSex());
        }
        //条件-国家，住址
        if (StringUtils.isNotBlank(request.getCountry())) {
            map.put("country", request.getCountry());
            lambdaQueryWrapper.eq(User::getCountry,request.getCountry());
            // 根据省市查询
            if (StrUtil.isNotBlank(request.getCity())) {
                request.setProvince(request.getProvince().replace("省", ""));
                request.setCity(request.getCity().replace("市", ""));
                map.put("addres", request.getProvince() + "," + request.getCity());
                lambdaQueryWrapper.like(User::getAddres,request.getProvince() + "," + request.getCity());
            }
        }
        //条件-消费情况
        if (StrUtil.isNotBlank(request.getPayCount())) {
            map.put("payCount", Integer.valueOf(request.getPayCount()));
            lambdaQueryWrapper.eq(User::getPayCount,request.getPayCount());
        }
        //条件-状态
        if (request.getStatus() != null) {
            map.put("status", request.getStatus() ? 1 : 0);
            lambdaQueryWrapper.eq(User::getStatus,request.getStatus() ? 1 : 0);
        }
        //条件-是否为区域代理
        if(request.getIsGeneralAgency()!=null&&request.getIsGeneralAgency()){
            map.put("isGeneralAgency",request.getIsGeneralAgency());
            lambdaQueryWrapper.eq(User::getIsGeneralAgency,request.getIsGeneralAgency());
        }

        //得到时间Vo类-时间范围//条件-注册时间范围
        dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
        if (!StringUtils.isBlank(dateLimit.getStartTime())) {
            map.put("startTime", dateLimit.getStartTime());
            map.put("endTime", dateLimit.getEndTime());
            map.put("accessType", request.getAccessType());
            switch (request.getAccessType()){
                default:
                case 1:
                    lambdaQueryWrapper.between(User::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
                    break;
                case 2:
                    lambdaQueryWrapper.between(User::getLastLoginTime, dateLimit.getStartTime(), dateLimit.getEndTime());
                    break;
                case 3:
                    lambdaQueryWrapper.notBetween(User::getLastLoginTime, dateLimit.getStartTime(), dateLimit.getEndTime());
                    break;
            }
        }

        //条件-关键字
        if (StringUtils.isNotBlank(request.getKeywords())) {
            map.put("keywords", request.getKeywords());
            lambdaQueryWrapper.like(User::getNickname,request.getKeywords());
            lambdaQueryWrapper.or().like(User::getPhone,request.getKeywords());
        }

        //排序
        lambdaQueryWrapper.orderByDesc(User::getUid);

        //得到数据
        //List<User> userList = userDao.findAdminList(map);
        List<User> userList = userDao.selectList(lambdaQueryWrapper);

        //循环处理数据
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : userList) {
            //实例化用户响应对象
            UserResponse userResponse = new UserResponse();
            BeanUtils.copyProperties(user, userResponse);

            // 获取分组信息
            if (!StringUtils.isBlank(user.getGroupId())) {
                userResponse.setGroupName(userGroupService.getGroupNameInId(user.getGroupId()));
                userResponse.setGroupId(user.getGroupId());
            }

            // 获取标签信息
            if (!StringUtils.isBlank(user.getTagId())) {
                userResponse.setTagName(userTagService.getGroupNameInId(user.getTagId()));
                userResponse.setTagId(user.getTagId());
            }

            //获取推广人信息
            if (null == user.getSpreadUid() || user.getSpreadUid() == 0) {
                userResponse.setSpreadNickname("无");
            } else {
                logger.info(user.getSpreadUid().toString());
                User userp = userDao.selectById(user.getSpreadUid());
                if(userp !=null){
                    userResponse.setSpreadNickname(userp.getNickname());
                }else{
                    userResponse.setSpreadNickname("推荐人已不存在");
                }
            }

            //获取推广团队人数
            userResponse.setUserSPR(userCenterService.getSpreadPeopleCount(user.getUid()));

            //添加到响应
            userResponses.add(userResponse);
        }

        //返回
        return CommonPage.copyPageInfo(pageUser, userResponses);
    }

    /**
     * 操作积分、余额
     */
    @Override
    public boolean updateIntegralMoney(UserOperateIntegralMoneyRequest request) {
        if (null == request.getMoneyValue() || null == request.getIntegralValue()) {
            throw new CrmebException("至少输入一个金额");
        }

        if (request.getMoneyValue().compareTo(BigDecimal.ZERO) < 1 && request.getIntegralValue() <= 0) {
            throw new CrmebException("修改值不能等小于等于0");
        }

        User user = getById(request.getUid());
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("用户不存在");
        }
        // 减少时要判断小于0的情况,添加时判断是否超过数据限制
        if (request.getMoneyType().equals(2) && request.getMoneyValue().compareTo(BigDecimal.ZERO) != 0) {
            if (user.getNowMoney().subtract(request.getMoneyValue()).compareTo(BigDecimal.ZERO) < 0) {
                throw new CrmebException("余额扣减后不能小于0");
            }
        }
        if (request.getMoneyType().equals(1) && request.getMoneyValue().compareTo(BigDecimal.ZERO) != 0) {
            if (user.getNowMoney().add(request.getMoneyValue()).compareTo(new BigDecimal("99999999.99")) > 0) {
                throw new CrmebException("余额添加后后不能大于99999999.99");
            }
        }

        if (request.getIntegralType().equals(2) && request.getIntegralValue() != 0) {
            if (user.getIntegral().intValue() - request.getIntegralValue() < 0) {
                throw new CrmebException("积分扣减后不能小于0");
            }
        }
        if (request.getIntegralType().equals(1) && request.getIntegralValue() != 0) {
            if ((user.getIntegral().intValue() + request.getIntegralValue()) > 99999999) {
                throw new CrmebException("积分添加后不能大于99999999");
            }
        }

        //执行
        Boolean execute = transactionTemplate.execute(e -> {
            // 处理余额
            if (request.getMoneyValue().compareTo(BigDecimal.ZERO) > 0) {
                // 验证增加或减少
                UserBill userBill = null; // 余额变动账单记录
                if (request.getMoneyType() == 1) {// 增加
                    userBill = userBillService.getUserBill( // 系统增加-余额
                            user.getUid(),
                            "0",
                            1,
                            Constants.USER_BILL_CATEGORY_MONEY,
                            Constants.USER_BILL_TYPE_SYSTEM_ADD,
                            request.getMoneyValue(),
                            user.getNowMoney().add(request.getMoneyValue()),
                            ""
                    );
                    userBillService.save(userBill);

                    //增加-用户余额
                    this.operationNowMoney(user.getUid(), request.getMoneyValue(), user.getNowMoney(), "add");
                } else { // 减少
                    userBill = userBillService.getUserBill( // 系统减少-余额
                            user.getUid(),
                            "0",
                            0,
                            Constants.USER_BILL_CATEGORY_MONEY,
                            Constants.USER_BILL_TYPE_SYSTEM_SUB,
                            request.getMoneyValue(),
                            user.getNowMoney().subtract(request.getMoneyValue()),
                            ""
                    );
                    userBillService.save(userBill);

                    //减少-用户余额
                    this.operationNowMoney(user.getUid(), request.getMoneyValue(), user.getNowMoney(), "sub");
                }
            }

            // 处理积分
            if (request.getIntegralValue() > 0) {
                // 生成记录
                UserIntegralRecord integralRecord = new UserIntegralRecord();
                integralRecord.setUid(user.getUid());
                integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_SIGN);
                integralRecord.setTitle(IntegralRecordConstants.BROKERAGE_RECORD_TITLE_SYSTEM);
                integralRecord.setIntegral(new BigDecimal(request.getIntegralValue()));
                integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
                if (request.getIntegralType() == 1) {// 增加
                    integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
                    integralRecord.setBalance(user.getIntegral().subtract(new BigDecimal(request.getIntegralValue())));
                    integralRecord.setMark(StrUtil.format("后台操作增加了{}积分", request.getIntegralValue()));
                    operationIntegral(user.getUid(), new BigDecimal(request.getIntegralValue()), user.getIntegral(), "add");
                } else {
                    integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB);
                    integralRecord.setBalance(user.getIntegral().subtract(new BigDecimal(request.getIntegralValue())));
                    integralRecord.setMark(StrUtil.format("后台操作减少了{}积分", request.getIntegralValue()));
                    operationIntegral(user.getUid(), new BigDecimal(request.getIntegralValue()), user.getIntegral(), "sub");
                }
                userIntegralRecordService.save(integralRecord);
            }
            return Boolean.TRUE;
        });

        if (!execute) {
            throw new CrmebException("修改积分/余额失败");
        }
        return execute;
    }

    /**
     * 更新用户信息
     *
     * @param user 用户参数
     * @return 更新结果
     */
    @Override
    public boolean updateBase(User user) {
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        if (null == user.getUid()) return false;
        lambdaUpdateWrapper.eq(User::getUid, user.getUid());
        if (StringUtils.isNotBlank(user.getNickname())) {
            lambdaUpdateWrapper.set(User::getNickname, user.getNickname());
        }
        if (StringUtils.isNotBlank(user.getAccount())) {
            lambdaUpdateWrapper.set(User::getAccount, user.getAccount());
        }
        if (StringUtils.isNotBlank(user.getPwd())) {
            lambdaUpdateWrapper.set(User::getPwd, user.getPwd());
        }
        if (StringUtils.isNotBlank(user.getRealName())) {
            lambdaUpdateWrapper.set(User::getRealName, user.getRealName());
        }
        if (StringUtils.isNotBlank(user.getBirthday())) {
            lambdaUpdateWrapper.set(User::getBirthday, user.getBirthday());
        }
        if (StringUtils.isNotBlank(user.getCardId())) {
            lambdaUpdateWrapper.set(User::getCardId, user.getCardId());
        }
        if (StringUtils.isNotBlank(user.getMark())) {
            lambdaUpdateWrapper.set(User::getMark, user.getMark());
        }
        if (null != user.getPartnerId()) {
            lambdaUpdateWrapper.set(User::getPartnerId, user.getPartnerId());
        }
        if (StringUtils.isNotBlank(user.getGroupId())) {
            lambdaUpdateWrapper.set(User::getGroupId, user.getGroupId());
        }
        if (StringUtils.isNotBlank(user.getTagId())) {
            lambdaUpdateWrapper.set(User::getTagId, user.getTagId());
        }
        if (StringUtils.isNotBlank(user.getAvatar())) {
            lambdaUpdateWrapper.set(User::getAvatar, user.getAvatar());
        }
        if (StringUtils.isNotBlank(user.getPhone())) {
            lambdaUpdateWrapper.set(User::getPhone, user.getPhone());
        }
        if (StringUtils.isNotBlank(user.getAddIp())) {
            lambdaUpdateWrapper.set(User::getAddIp, user.getAddIp());
        }
        if (StringUtils.isNotBlank(user.getLastIp())) {
            lambdaUpdateWrapper.set(User::getLastIp, user.getLastIp());
        }
        if (null != user.getNowMoney() && user.getNowMoney().compareTo(BigDecimal.ZERO) > 0) {
            lambdaUpdateWrapper.set(User::getNowMoney, user.getNowMoney());
        }
        if (null != user.getBrokeragePrice() && user.getBrokeragePrice().compareTo(BigDecimal.ZERO) > 0) {
            lambdaUpdateWrapper.set(User::getBrokeragePrice, user.getBrokeragePrice());
        }
        if (null != user.getIntegral() && user.getIntegral().intValue() >= 0) {
            lambdaUpdateWrapper.set(User::getIntegral, user.getIntegral());
        }
        if (null != user.getExperience() && user.getExperience() > 0) {
            lambdaUpdateWrapper.set(User::getExperience, user.getExperience());
        }
        if (null != user.getSignNum() && user.getSignNum() > 0) {
            lambdaUpdateWrapper.set(User::getSignNum, user.getSignNum());
        }
        if (null != user.getStatus()) {
            lambdaUpdateWrapper.set(User::getStatus, user.getStatus());
        }
        if (null != user.getLevel() && user.getLevel() > 0) {
            lambdaUpdateWrapper.set(User::getLevel, user.getLevel());
        }
        if (null != user.getSpreadUid() && user.getSpreadUid() > 0) {
            lambdaUpdateWrapper.set(User::getSpreadUid, user.getSpreadUid());
        }
        if (null != user.getSpreadTime()) {
            lambdaUpdateWrapper.set(User::getSpreadTime, user.getSpreadTime());
        }
        if (StringUtils.isNotBlank(user.getUserType())) {
            lambdaUpdateWrapper.set(User::getUserType, user.getUserType());
        }
        if (null != user.getIsPromoter()) {
            lambdaUpdateWrapper.set(User::getIsPromoter, user.getIsPromoter());
        }
        if (null != user.getPayCount()) {
            lambdaUpdateWrapper.set(User::getPayCount, user.getPayCount());
        }
        if (null != user.getSpreadCount()) {
            lambdaUpdateWrapper.set(User::getSpreadCount, user.getSpreadCount());
        }
        if (StringUtils.isNotBlank(user.getAddres())) {
            lambdaUpdateWrapper.set(User::getAddres, user.getAddres());
        }
        return update(lambdaUpdateWrapper);
    }

    @Override
    public boolean userPayCountPlus(User user) {
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(User::getUid, user.getUid());
        lambdaUpdateWrapper.set(User::getPayCount, user.getPayCount() + 1);
        return update(lambdaUpdateWrapper);
    }

    /**
     * 更新用户金额
     *
     * @param user  用户
     * @param price 金额
     * @param type  增加add、扣减sub
     * @return 更新后的用户对象
     */
    @Override
    public Boolean updateNowMoney(User user, BigDecimal price, String type) {
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        if (type.equals("add")) {
            lambdaUpdateWrapper.set(User::getNowMoney, user.getNowMoney().add(price));
        } else {
            lambdaUpdateWrapper.set(User::getNowMoney, user.getNowMoney().subtract(price));
        }
        lambdaUpdateWrapper.eq(User::getUid, user.getUid());
        if (type.equals("sub")) {
            lambdaUpdateWrapper.apply(StrUtil.format(" now_money - {} >= 0", price));
        }
        return update(lambdaUpdateWrapper);
    }

    /**
     * 会员分组
     *
     * @param id           String id
     * @param groupIdValue Integer 分组Id
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @Override
    public boolean group(String id, String groupIdValue) {
        if (StrUtil.isBlank(id)) throw new CrmebException("会员编号不能为空");
        if (StrUtil.isBlank(groupIdValue)) throw new CrmebException("分组id不能为空");

        //循环id处理
        List<Integer> idList = CrmebUtil.stringToArray(id);
        idList = idList.stream().distinct().collect(Collectors.toList());
        List<User> list = getListInUid(idList);
        if (CollUtil.isEmpty(list)) throw new CrmebException("没有找到用户信息");
        if (list.size() < idList.size()) {
            throw new CrmebException("没有找到用户信息");
        }
        for (User user : list) {
            user.setGroupId(groupIdValue);
        }
        return updateBatchById(list);
    }

    @Override
    public  List<User> getListInUid(List<Integer> uidList) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(User::getUid, uidList);
        return userDao.selectList(lambdaQueryWrapper);
    }

    @Override
    public boolean password(PasswordRequest request) {
        return this.passwordUpdate(request.getPhone(),request.getValidateCode(),request.getPassword(),1);
    }

    /**
     * 退出
     *
     * @param token String token
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @Override
    public void loginOut(String token) {
        tokenManager.deleteToken(token, Constants.USER_TOKEN_REDIS_KEY_PREFIX);
        ThreadLocalUtil.remove("id");
    }

    /**
     * 获取个人资料
     *
     * @return User
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @Override
    public User getInfo() {
        if (getUserId() == 0) {
            return null;
        }
        return getById(getUserId());
    }

    @Override
    public User getInfoException() {
        User user = getInfo();
        if (user == null) {
            throw new CrmebException("用户信息不存在！");
        }

        if (!user.getStatus()) {
            throw new CrmebException("用户已经被禁用！");
        }
        return user;
    }

    /**
     * 获取当前用户id
     *
     * @return Integer
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @Override
    public Integer getUserIdException() {
        return Integer.parseInt(tokenManager.getLocalInfoException("id"));
    }

    /**
     * 获取当前用户id
     *
     * @return Integer
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @Override
    public Integer getUserId() {
        Object id = tokenManager.getLocalInfo("id");
        if (null == id) {
            return 0;
        }
        return Integer.parseInt(id.toString());
    }

    /**
     * 按开始结束时间查询新增用户数量
     *
     * @param date String 时间范围
     * @return HashMap<String, Object>
     * @author Mr.Zhang
     * @since 2020-05-16
     */
    @Override
    public Integer getAddUserCountByDate(String date) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(date)) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            lambdaQueryWrapper.between(User::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        return userDao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 按开始结束时间查询每日新增用户数量
     *
     * @param date String 时间范围
     * @return HashMap<String, Object>
     */
    @Override
    public Map<Object, Object> getAddUserCountGroupDate(String date) {
        Map<Object, Object> map = new HashMap<>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("count(uid) as uid", "left(create_time, 10) as create_time");
        if (StringUtils.isNotBlank(date)) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            queryWrapper.between("create_time", dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        queryWrapper.groupBy("left(create_time, 10)").orderByAsc("create_time");
        List<User> list = userDao.selectList(queryWrapper);
        if (list.size() < 1) {
            return map;
        }

        for (User user : list) {
            map.put(DateUtil.dateToStr(user.getCreateTime(), Constants.DATE_FORMAT_DATE), user.getUid());
        }
        return map;
    }

    /**
     * 绑定手机号
     *
     * @return boolean
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @Override
    public boolean bind(UserBindingPhoneUpdateRequest request) {
        //检测验证码
        checkValidateCode(request.getPhone(), request.getCaptcha());

        //删除验证码
        redisUtil.remove(getValidateCodeRedisKey(request.getPhone()));

        //检测当前手机号是否已经是账号
        User user = getUserByAccount(request.getPhone());
        if (null != user) {
            throw new CrmebException("此手机号码已被注册");
        }

        //查询手机号信息
        User bindUser = getInfoException();
        bindUser.setAccount(request.getPhone());
        bindUser.setPhone(request.getPhone());
        return updateById(bindUser);
    }

    /**
     * 换绑手机号校验
     */
    @Override
    public Boolean updatePhoneVerify(UserBindingPhoneUpdateRequest request) {
        //检测验证码
        checkValidateCode(request.getPhone(), request.getCaptcha());

        //删除验证码
        redisUtil.remove(getValidateCodeRedisKey(request.getPhone()));

        User user = getInfoException();

        if (!user.getPhone().equals(request.getPhone())) {
            throw new CrmebException("手机号不是当前用户手机号");
        }

        return Boolean.TRUE;
    }

    /**
     * 换绑手机号
     */
    @Override
    public Boolean updatePhone(UserBindingPhoneUpdateRequest request) {
        //检测验证码
        checkValidateCode(request.getPhone(), request.getCaptcha());

        //删除验证码
        redisUtil.remove(getValidateCodeRedisKey(request.getPhone()));

        //检测当前手机号是否已经是账号
        User user = getByPhone(request.getPhone());
        if (null != user) {
            throw new CrmebException("此手机号码已被注册");
        }

        //查询手机号信息
        User bindUser = getInfoException();
        bindUser.setAccount(request.getPhone());
        bindUser.setPhone(request.getPhone());
        return updateById(bindUser);
    }

    /**
     * 用户中心
     * @return UserCenterResponse
     */
    @Override
    public UserCenterResponse getUserCenter() {
        //得到用户信息
        User currentUser = getInfo();
        if (ObjectUtil.isNull(currentUser)) {
            throw new CrmebException("您的登录已过期，请先登录");
        }

        //实例化-个人中心-响应对象
        UserCenterResponse userCenterResponse = new UserCenterResponse();
        BeanUtils.copyProperties(currentUser, userCenterResponse);

        //验证联盟商家
        String tagid=currentUser.getTagId();
        if(Constants.TARGID_LMSJ.equals(tagid) ||
                (tagid.indexOf(",10")!=-1&&tagid.indexOf(",1010") == -1)||
                (tagid.indexOf("10,")!=-1&&tagid.indexOf("1010,") == -1)){
            userCenterResponse.setIsAllianceMerchants(Boolean.TRUE);
        }

        // 优惠券数量
        userCenterResponse.setCouponCount(storeCouponUserService.getUseCount(currentUser.getUid()));

        // 收藏数量
        userCenterResponse.setCollectCount(storeProductRelationService.getCollectCountByUid(currentUser.getUid()));

        // 判断是否开启会员功能
        Integer vipOpen = Integer.valueOf(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_VIP_OPEN));
        if (vipOpen.equals(0)) {
            userCenterResponse.setVip(false);
        } else {// 开启
            //是否会员
            userCenterResponse.setVip(userCenterResponse.getLevel() > 0);

            //得到等级信息
            UserLevel userLevel = userLevelService.getUserLevelByUserId(currentUser.getUid());
            if (ObjectUtil.isNotNull(userLevel)) {
                //得到等级设置信息
                SystemUserLevel systemUserLevel = systemUserLevelService.getByLevelId(userLevel.getLevelId());
                if (ObjectUtil.isNotNull(systemUserLevel)) {
                    userCenterResponse.setVipIcon(systemUserLevel.getIcon());
                    userCenterResponse.setVipName(systemUserLevel.getName());
                } else {
                    userCenterResponse.setVip(false);
                }
            } else {
                userCenterResponse.setVip(false);
            }
        }

        // 充值开关
        String rechargeSwitch = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_RECHARGE_SWITCH);
        if (StrUtil.isNotBlank(rechargeSwitch)) {
            userCenterResponse.setRechargeSwitch(Boolean.valueOf(rechargeSwitch));
        }

        // 判断是否展示我的推广，1.分销模式是否开启，2.如果是人人分销，所有人都是推广员
        String funcStatus = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_BROKERAGE_FUNC_STATUS);
        if (funcStatus.equals("1")) {
            //得到分销模式
            String brokerageStatus = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_STORE_BROKERAGE_STATUS);
            if (brokerageStatus.equals("2")) {// 人人分销
                userCenterResponse.setIsPromoter(true);
            }
        } else {
            userCenterResponse.setIsPromoter(false);
        }

        //是否为区域代理用户
        if(currentUser.getIsGeneralAgency() == null){
            userCenterResponse.setIsGeneralAgency(false);
        }

        //是否为零售商管理用户
        if(currentUser.getIsRetailer() == null){
            userCenterResponse.setIsRetailer(Boolean.FALSE);
        }

        //是否为供应商管理用户
        if(currentUser.getIsSupplier() == null) currentUser.setIsSupplier(Boolean.FALSE);

        //返回数据
        return userCenterResponse;
    }

    @Override
    public HashMap<Integer, User> getMapListInUid(List<Integer> uidList) {
        List<User> userList = getListInUid(uidList);
        HashMap<Integer, User> map = new HashMap<>();
        if (null == userList || userList.size() < 1) {
            return map;
        }
        for (User user : userList) {
            map.put(user.getUid(), user);
        }
        return map;
    }

    /**
     * 重置连续签到天数
     *
     * @param userId Integer 用户id
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @Override
    public void repeatSignNum(Integer userId) {
        User user = new User();
        user.setUid(userId);
        user.setSignNum(0);
        updateById(user);
    }

    /**
     * 会员标签
     * @param id         String id
     * @param tagIdValue Integer 标签Id
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @Override
    public boolean tag(String id, String tagIdValue) {
        if (StrUtil.isBlank(id)) throw new CrmebException("会员编号不能为空");
        if (StrUtil.isBlank(tagIdValue)) throw new CrmebException("标签id不能为空");

        //循环id处理
        List<Integer> idList = CrmebUtil.stringToArray(id);
        idList = idList.stream().distinct().collect(Collectors.toList());
        List<User> list = getListInUid(idList);
        if (CollUtil.isEmpty(list)) throw new CrmebException("没有找到用户信息");
        if (list.size() < 1) {
            throw new CrmebException("没有找到用户信息");
        }
        for (User user : list) {
            user.setTagId(tagIdValue);
        }
        return updateBatchById(list);
    }

    /**
     * 根据用户id获取自己本身的推广用户
     *
     * @param userIdList List<Integer> 用户id集合
     * @return List<User>
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    @Override
    public List<Integer> getSpreadPeopleIdList(List<Integer> userIdList) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(User::getUid); //查询用户id
        lambdaQueryWrapper.in(User::getSpreadUid, userIdList); //xx的下线集合
        List<User> list = userDao.selectList(lambdaQueryWrapper);
        if (null == list || list.size() < 1) {
            return new ArrayList<>();
        }
        return list.stream().map(User::getUid).distinct().collect(Collectors.toList());
    }

    /**
     * 根据用户id获取自己本身的推广用户
     */
    @Override
    public List<UserSpreadPeopleItemResponse> getSpreadPeopleList(List<Integer> userIdList, String keywords, String sortKey, String isAsc, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        Map<String, Object> map = new HashMap<>();
        map.put("userIdList", userIdList.stream().map(String::valueOf).distinct().collect(Collectors.joining(",")));
        if (StringUtils.isNotBlank(keywords)) {
            map.put("keywords", "%" + keywords + "%");
        }
        map.put("sortKey", "create_time");
        if (StringUtils.isNotBlank(sortKey)) {
            map.put("sortKey", sortKey);
        }
        map.put("sortValue", Constants.SORT_DESC);
        if (isAsc.toLowerCase().equals(Constants.SORT_ASC)) {
            map.put("sortValue", Constants.SORT_ASC);
        }
        List<UserSpreadPeopleItemResponse> list= userDao.getSpreadPeopleList(map);
        return list;
    }

    @Override
    public String token(User user) throws Exception {
        TokenModel token = tokenManager.createToken(user.getAccount(), user.getUid().toString(), Constants.USER_TOKEN_REDIS_KEY_PREFIX);
        return token.getToken();
    }

    /**
     * 检测手机验证码
     * @author Mr.Zhang
     * @since 2020-04-29
     */
    private void checkValidateCode(String phone, String value) {
        Object validateCode = redisUtil.get(getValidateCodeRedisKey(phone));
        if (validateCode == null) {
            throw new CrmebException("验证码已过期");
        }

        if (!validateCode.toString().equals(value)) {
            throw new CrmebException("验证码错误");
        }
    }

    /**
     * 检测手机验证码
     *
     * @param phone String 手机号
     * @return String
     * @author Mr.Zhang
     * @since 2020-04-29
     */
    @Override
    public String getValidateCodeRedisKey(String phone) {
        return SmsConstants.SMS_VALIDATE_PHONE + phone;
    }


    @Override
    public User getUserByAccount(String account) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getAccount, account);
        return userDao.selectOne(lambdaQueryWrapper);
    }

    /**
     * 手机号注册用户
     * @param phone     手机号
     * @param spreadUid 推广人编号
     * @return User
     */
    @Override
    public User registerPhone(String phone, Integer spreadUid) {
        //实例化-用户-对象
        User user = new User();
        //赋值
        user.setAccount(phone);
        user.setPwd(CommonUtil.createPwd(phone));
        user.setPhone(phone);
        user.setUserType(Constants.USER_LOGIN_TYPE_H5);
        user.setNickname(CommonUtil.createNickName(phone));
        user.setAvatar(systemConfigService.getValueByKey(Constants.USER_DEFAULT_AVATAR_CONFIG_KEY));
        Date nowDate = DateUtil.nowDateTime();
        user.setCreateTime(nowDate);
        user.setLastLoginTime(nowDate);
        user.setNowMoney(BigDecimal.ZERO);
        user.setIntegral(BigDecimal.ZERO);
        user.setBrokeragePrice(BigDecimal.ZERO);
        user.setExperience(0);

        // 推广人
        user.setSpreadUid(0);
        Boolean check = checkBingSpread(user, spreadUid, "new");
        if (check) {
            user.setSpreadUid(spreadUid);
            user.setSpreadTime(nowDate);
            //2021.7.15-新增path-推广等级路径
            User spUser=userDao.selectById(spreadUid);
            if(spUser!=null){
                //设置path
                user.setPath(new StringBuffer(spUser.getPath()).append(spreadUid).append("/").toString());
            }else{
                throw new CrmebException("创建用户失败!推广人ID无效！");
            }
        }

        // 查询是否有新人注册赠送优惠券
        List<StoreCouponUser> couponUserList = CollUtil.newArrayList();
        List<StoreCoupon> couponList = storeCouponService.findRegisterList();
        if (CollUtil.isNotEmpty(couponList)) {
            couponList.forEach(storeCoupon -> {
                //是否有固定的使用时间
                if (!storeCoupon.getIsFixedTime()) {
                    String endTime = DateUtil.addDay(DateUtil.nowDate(Constants.DATE_FORMAT), storeCoupon.getDay(), Constants.DATE_FORMAT);
                    storeCoupon.setUseEndTime(DateUtil.strToDate(endTime, Constants.DATE_FORMAT));
                    storeCoupon.setUseStartTime(DateUtil.nowDateTimeReturnDate(Constants.DATE_FORMAT));
                }

                //实例化用户优惠卷记录-对象
                StoreCouponUser storeCouponUser = new StoreCouponUser();
                storeCouponUser.setCouponId(storeCoupon.getId());
                storeCouponUser.setName(storeCoupon.getName());
                storeCouponUser.setMoney(storeCoupon.getMoney());
                storeCouponUser.setMinPrice(storeCoupon.getMinPrice());
                storeCouponUser.setStartTime(storeCoupon.getUseStartTime());
                storeCouponUser.setEndTime(storeCoupon.getUseEndTime());
                storeCouponUser.setUseType(storeCoupon.getUseType());
                storeCouponUser.setType(CouponConstants.STORE_COUPON_USER_TYPE_REGISTER);
                if (storeCoupon.getUseType() > 1) {
                    storeCouponUser.setPrimaryKey(storeCoupon.getPrimaryKey());
                }
                couponUserList.add(storeCouponUser);
            });
        }

        //执行结果
        Boolean execute = transactionTemplate.execute(e -> {
            save(user);
            // 推广人处理
            if (check) {
                updateSpreadCountByUid(spreadUid, "add");
            }
            // 赠送客户优惠券
            if (CollUtil.isNotEmpty(couponUserList)) {
                couponUserList.forEach(couponUser -> couponUser.setUid(user.getUid()));
                storeCouponUserService.saveBatch(couponUserList);
                couponList.forEach(coupon -> storeCouponService.deduction(coupon.getId(), 1, coupon.getIsLimited()));
            }
            return Boolean.TRUE;
        });

        //验证并返回结果
        if (!execute) {
            throw new CrmebException("创建用户失败!");
        }
        return user;
    }

    /**
     * 更新推广员推广数
     *
     * @param uid uid
     * @param type add or sub
     */
    public Boolean updateSpreadCountByUid(Integer uid, String type) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        if (type.equals("add")) {
            updateWrapper.setSql("spread_count = spread_count + 1");
        } else {
            updateWrapper.setSql("spread_count = spread_count - 1");
        }
        updateWrapper.eq("uid", uid);
        return update(updateWrapper);
    }

    /**
     * 添加/扣减佣金
     * @param uid            用户id
     * @param price          金额
     * @param brokeragePrice 历史金额
     * @param type           类型：add—添加，sub—扣减
     * @return Boolean
     */
    @Override
    public Boolean operationBrokerage(Integer uid, BigDecimal price, BigDecimal brokeragePrice, String type) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        if (type.equals("add")) {
            updateWrapper.setSql(StrUtil.format("brokerage_price = brokerage_price + {}", price));
        } else {
            updateWrapper.setSql(StrUtil.format("brokerage_price = brokerage_price - {}", price));
            updateWrapper.last(StrUtil.format(" and (brokerage_price - {} >= 0)", price));
        }
        updateWrapper.eq("uid", uid);
        updateWrapper.eq("brokerage_price", brokeragePrice);
        return update(updateWrapper);
    }

    /**
     * 添加/扣减余额
     * @param uid      用户id
     * @param price    金额
     * @param nowMoney 历史金额
     * @param type     类型：add—添加，sub—扣减
     */
    @Override
    public Boolean operationNowMoney(Integer uid, BigDecimal price, BigDecimal nowMoney, String type) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        if (type.equals("add")) {
            updateWrapper.setSql(StrUtil.format("now_money = now_money + {}", price));
        } else {
            updateWrapper.setSql(StrUtil.format("now_money = now_money - {}", price));
            updateWrapper.last(StrUtil.format(" and (now_money - {} >= 0)", price));
        }
        updateWrapper.eq("uid", uid);
        updateWrapper.eq("now_money", nowMoney);
        return update(updateWrapper);
    }

    /**
     * 添加/扣减积分
     *
     * @param uid         用户id
     * @param integral    积分
     * @param nowIntegral 历史积分
     * @param type        类型：add—添加，sub—扣减
     * @return Boolean
     */
    @Override
    public Boolean operationIntegral(Integer uid, BigDecimal integral, BigDecimal nowIntegral, String type) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        if (type.equals("add")) {
            updateWrapper.setSql(StrUtil.format("integral = integral + {}", integral));
        } else {
            updateWrapper.setSql(StrUtil.format("integral = integral - {}", integral));
            updateWrapper.last(StrUtil.format(" and (integral - {} >= 0)", integral));
        }
        updateWrapper.eq("uid", uid);
        updateWrapper.eq("integral", nowIntegral);
        return update(updateWrapper);
    }

    /**
     * PC后台分销员列表
     *
     * @param storeBrokerageStatus 分销模式 1-指定分销，2-人人分销
     * @param keywords             搜索参数
     * @param dateLimit            时间参数
     * @param pageRequest          分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<User> getAdminSpreadPeopleList(String storeBrokerageStatus, String keywords, String dateLimit, PageParamRequest pageRequest) {
        Page<User> pageUser = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        // id,头像，昵称，姓名，电话，推广用户数，推广订单数，推广订单额，佣金总金额，已提现金额，提现次数，未提现金额，上级推广人
        lqw.select(User::getUid, User::getNickname, User::getRealName, User::getPhone, User::getAvatar, User::getSpreadCount, User::getBrokeragePrice, User::getSpreadUid);
        if (storeBrokerageStatus.equals("1")) {
            lqw.eq(User::getIsPromoter, true);
        }
        lqw.apply("1 = 1");
        if (StrUtil.isNotBlank(keywords)) {
            lqw.and(i -> i.eq(User::getUid, keywords) //用户账号
                    .or().like(User::getNickname, keywords) //昵称
                    .or().like(User::getPhone, keywords)); //手机号码
        }
        lqw.orderByDesc(User::getUid);
        List<User> userList = userDao.selectList(lqw);
        return CommonPage.copyPageInfo(pageUser, userList);
    }

    /**
     * 检测能否绑定关系
     *
     * @param user      当前用户
     * @param spreadUid 推广员Uid
     * @param type      用户类型:new-新用户，old—老用户
     * @return Boolean
     * 1.判断分销功能是否启用
     * 2.判断分销模式
     * 3.根据不同的分销模式校验
     * 4.指定分销，只有分销员才可以分销，需要spreadUid是推广员才可以绑定
     * 5.人人分销，可以直接绑定
     * *推广关系绑定，下级不能绑定自己的上级为下级，A->B->A(❌)
     */
    public Boolean checkBingSpread(User user, Integer spreadUid, String type) {
        //验证推广员ID是否可用
        if (spreadUid ==null || spreadUid <= 0 || user.getSpreadUid() > 0) {
            return false;
        }

        //验证非空并验证推广员ID是不是自己
        if (ObjectUtil.isNotNull(user.getUid()) && user.getUid().equals(spreadUid)) {
            return false;
        }

        // 判断分销功能是否启用
        String isOpen = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_BROKERAGE_IS_OPEN);
        if (StrUtil.isBlank(isOpen) || isOpen.equals("0")) {
            return false;
        }

        //验证用户类型，type=new-新用户，old—老用户
        if (type.equals("old")) {
            // 判断分销关系绑定类型（所有、新用户）
            String bindType = systemConfigService.getValueByKey(Constants.CONFIG_KEY_DISTRIBUTION_TYPE);
            if (StrUtil.isBlank(bindType) || bindType.equals("1")) {
                return false;
            }

            //验证是否已绑定
            if (user.getSpreadUid().equals(spreadUid)) {
                return false;
            }
        }

        // 判断分销模式
        String model = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_BROKERAGE_MODEL);
        if (StrUtil.isBlank(model)) {
            return false;
        }

        // 查询推广员，是否存在
        User spreadUser = getById(spreadUid);
        if (ObjectUtil.isNull(spreadUser) || !spreadUser.getStatus()) {
            return false;
        }

        // 指定分销不是推广员不绑定
        if (model.equals("1") && !spreadUser.getIsPromoter()) {
            return false;
        }

        // 下级不能绑定自己的上级为自己的下级
        if (ObjectUtil.isNotNull(user.getUid()) && spreadUser.getSpreadUid().equals(user.getUid())) {
            return false;
        }

        //返回检测结果
        return true;
    }

    /**
     * 获取用户好友关系，spread_uid往下两级的用户信息
     *
     * @return List<User>
     */
    private List<User> getUserRelation(Integer userId) {
        List<User> userList = new ArrayList<>();
        User currUser = userDao.selectById(userId);
        if (currUser.getSpreadUid() > 0) {
            User spUser1 = userDao.selectById(currUser.getSpreadUid());
            if (null != spUser1) {
                userList.add(spUser1);
                if (spUser1.getSpreadUid() > 0) {
                    User spUser2 = userDao.selectById(spUser1.getSpreadUid());
                    if (null != spUser2) {
                        userList.add(spUser2);
                    }
                }
            }
        }
        return userList;
    }

    /**
     * 根据条件获取会员对应信息列表
     *
     * @param userId 用户id
     * @param type             0=消费记录，1=积分明细，2=签到记录，3=持有优惠券，4=余额变动，5=好友关系
     * @param pageParamRequest 分页参数
     * @return Object
     */
    @Override
    public Object getInfoByCondition(Integer userId, Integer type, PageParamRequest pageParamRequest) {
        switch (type) {
            case 0:
                return storeOrderService.findPaidListByUid(userId, pageParamRequest);
            case 1:
                AdminIntegralSearchRequest fmsq = new AdminIntegralSearchRequest();
                fmsq.setUid(userId);
                return userIntegralRecordService.findAdminList(fmsq, pageParamRequest);
            case 2:
                UserSign userSign = new UserSign();
                userSign.setUid(userId);
                return userSignService.getListByCondition(userSign, pageParamRequest);
            case 3:
                StoreCouponUserSearchRequest scur = new StoreCouponUserSearchRequest();
                scur.setUid(userId);
                return storeCouponUserService.findListByUid(userId, pageParamRequest);
            case 4:
                FundsMonitorSearchRequest fmsqq = new FundsMonitorSearchRequest();
                fmsqq.setUid(userId);
                fmsqq.setCategory(Constants.USER_BILL_CATEGORY_MONEY);
                return userBillService.getList(fmsqq, pageParamRequest);
            case 5:
                return getUserRelation(userId);
        }

        return new ArrayList<>();
    }

    /**
     * 会员详情顶部数据
     *
     * @param userId Integer 用户id
     * @return Object
     */
    @Override
    public TopDetail getTopDetail(Integer userId) {
        TopDetail topDetail = new TopDetail();
        User currentUser = userDao.selectById(userId);
        topDetail.setUser(currentUser);
        topDetail.setBalance(currentUser.getNowMoney());
        topDetail.setIntegralCount(currentUser.getIntegral().intValue());
        topDetail.setMothConsumeCount(storeOrderService.getSumPayPriceByUidAndDate(1, Constants.SEARCH_DATE_MONTH,userId));
        topDetail.setAllConsumeCount(storeOrderService.getSumPayPriceByUid(userId,null));
        topDetail.setMothOrderCount(storeOrderService.getOrderCountByUidAndDate(userId, Constants.SEARCH_DATE_MONTH));
        topDetail.setAllOrderCount(storeOrderService.getOrderCountByUid(userId,null));
        return topDetail;
    }

    /**
     * 通过微信信息注册用户
     * @param thirdUserRequest RegisterThirdUser 三方用户登录注册信息
     * @return User
     */
    @Override
    public User registerByThird(RegisterThirdUserRequest thirdUserRequest) {
        User user = new User(); // 通过微信信息注册用户
        user.setAccount(DigestUtils.md5Hex(CrmebUtil.getUuid() + DateUtil.getNowTime()));
        user.setUserType(thirdUserRequest.getType());
        user.setNickname(thirdUserRequest.getNickName());
        user.setNowMoney(BigDecimal.ZERO);
        user.setIntegral(BigDecimal.ZERO);
        user.setBrokeragePrice(BigDecimal.ZERO);
        user.setExperience(0);
        String avatar = null;
        switch (thirdUserRequest.getType()) {
            case Constants.USER_LOGIN_TYPE_PUBLIC:
            case Constants.USER_LOGIN_TYPE_IOS_WX:
            case Constants.USER_LOGIN_TYPE_ANDROID_WX:
                avatar = thirdUserRequest.getHeadimgurl();
                break;
            case Constants.USER_LOGIN_TYPE_PROGRAM:
            case Constants.USER_LOGIN_TYPE_H5:
                avatar = thirdUserRequest.getAvatar();
                break;
        }
        user.setAvatar(avatar);
        user.setSpreadTime(DateUtil.nowDateTime());
        user.setSex(Integer.parseInt(thirdUserRequest.getSex()));
        user.setAddres(thirdUserRequest.getCountry() + "," + thirdUserRequest.getProvince() + "," + thirdUserRequest.getCity());
        return user;
    }

    /**
     * 添加推广关系
     *
     * @param currentUserId 当前用户id 被推广人
     * @param spreadUserId  推广人id
     * @return 添加推广关系是否成功
     */
    @Override
    public boolean spread(Integer currentUserId, Integer spreadUserId) {
        // 检查用户是否存在
        User currentUser = userDao.selectById(currentUserId);
        if (null == currentUser) throw new CrmebException("用户id:" + currentUserId + "不存在");
        User spreadUser = userDao.selectById(spreadUserId);
        if (null == spreadUser) throw new CrmebException("用户id:" + spreadUserId + "不存在");
        // 检查是否是推广员
        if (!spreadUser.getIsPromoter()) throw new CrmebException("用户id:" + spreadUserId + "不是推广员身份");
        // 检查是否已经有推广关系
        LambdaQueryWrapper<User> lmq = new LambdaQueryWrapper<>();
        lmq.like(User::getPath, spreadUserId);
        lmq.eq(User::getUid, currentUserId);
        List<User> spreadUsers = userDao.selectList(lmq);
        if (spreadUsers.size() > 0) {
            throw new CrmebException("推广关系已经存在");
        }
        currentUser.setPath(currentUser.getPath() + spreadUser.getUid() + "/");
        currentUser.setSpreadUid(spreadUserId);
        currentUser.setSpreadTime(new Date());
        currentUser.setSpreadCount(currentUser.getSpreadCount() + 1);
        return userDao.updateById(currentUser) >= 0;
    }

    /**
     * 根据推广级别和其他参数当前用户下的推广列表
     *
     * @param request 推广列表参数
     * @return 当前用户的推广人列表
     */
    @Override
    public PageInfo<User> getUserListBySpreadLevel(RetailShopStairUserRequest request, PageParamRequest pageParamRequest) {
        if (request.getType().equals(1)) {// 一级推广人
            return getFirstSpreadUserListPage(request, pageParamRequest);
        }
        if (request.getType().equals(2)) {// 二级推广人
            return getSecondSpreadUserListPage(request, pageParamRequest);
        }
        return getAllSpreadUserListPage(request, pageParamRequest);
    }

    // 分页获取一级推广员
    private PageInfo<User> getFirstSpreadUserListPage(RetailShopStairUserRequest request, PageParamRequest pageParamRequest) {
        Page<User> userPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getUid, User::getAvatar, User::getNickname, User::getIsPromoter, User::getSpreadCount, User::getPayCount);
        queryWrapper.eq(User::getSpreadUid, request.getUid());
        if (StrUtil.isNotBlank(request.getNickName())) {
            queryWrapper.and(e -> e.like(User::getNickname, request.getNickName()).or().eq(User::getUid, request.getNickName())
                    .or().eq(User::getPhone, request.getNickName()));
        }
        List<User> userList = userDao.selectList(queryWrapper);
        return CommonPage.copyPageInfo(userPage, userList);
    }

    // 分页获取二级推广员
    private PageInfo<User> getSecondSpreadUserListPage(RetailShopStairUserRequest request, PageParamRequest pageParamRequest) {
        // 先获取一级推广员
        List<User> firstUserList = getSpreadListBySpreadIdAndType(request.getUid(), 1);
        if (CollUtil.isEmpty(firstUserList)) {
            return new PageInfo<>(CollUtil.newArrayList());
        }
        List<Integer> userIds = firstUserList.stream().map(User::getUid).distinct().collect(Collectors.toList());
        Page<User> userPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getUid, User::getAvatar, User::getNickname, User::getIsPromoter, User::getSpreadCount, User::getPayCount);
        queryWrapper.in(User::getSpreadUid, userIds);
        if (StrUtil.isNotBlank(request.getNickName())) {
            queryWrapper.and(e -> e.like(User::getNickname, request.getNickName()).or().eq(User::getUid, request.getNickName())
                    .or().eq(User::getPhone, request.getNickName()));
        }
        List<User> userList = userDao.selectList(queryWrapper);
        return CommonPage.copyPageInfo(userPage, userList);
    }

    // 分页获取所有推广员
    private PageInfo<User> getAllSpreadUserListPage(RetailShopStairUserRequest request, PageParamRequest pageParamRequest) {
        // 先所有一级推广员
        List<User> firstUserList = getSpreadListBySpreadIdAndType(request.getUid(), 0);
        if (CollUtil.isEmpty(firstUserList)) {
            return new PageInfo<>(CollUtil.newArrayList());
        }
        List<Integer> userIds = firstUserList.stream().map(User::getUid).distinct().collect(Collectors.toList());
        Page<User> userPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getUid, User::getAvatar, User::getNickname, User::getIsPromoter, User::getSpreadCount, User::getPayCount);
        queryWrapper.in(User::getUid, userIds);
        if (StrUtil.isNotBlank(request.getNickName())) {
            queryWrapper.and(e -> e.like(User::getNickname, request.getNickName()).or().eq(User::getUid, request.getNickName())
                    .or().eq(User::getPhone, request.getNickName()));
        }
        List<User> userList = userDao.selectList(queryWrapper);
        return CommonPage.copyPageInfo(userPage, userList);
    }

    /**
     * 根据推广级别和其他参数获取推广列表
     *
     * @param request 推广层级和推广时间参数
     * @return 推广订单列表
     */
    @Override
    public PageInfo<SpreadOrderResponse> getOrderListBySpreadLevel(RetailShopStairUserRequest request, PageParamRequest pageParamRequest) {
        // 获取推广人列表
        if (ObjectUtil.isNull(request.getType())) {
            request.setType(0);
        }
        List<User> userList = getSpreadListBySpreadIdAndType(request.getUid(), request.getType());
        if (CollUtil.isEmpty(userList)) {
            return new PageInfo<>();
        }

        List<Integer> userIds = userList.stream().map(User::getUid).distinct().collect(Collectors.toList());
        // 获取推广人订单号集合
        List<StoreOrder> orderList = storeOrderService.getOrderListStrByUids(userIds, request);
        if (CollUtil.isEmpty(orderList)) {
            return new PageInfo<>();
        }
        List<String> orderNoList = CollUtil.newArrayList();
        Map<String, StoreOrder> orderMap = CollUtil.newHashMap();
        orderList.forEach(e -> {
            orderNoList.add(e.getOrderId());
            orderMap.put(e.getOrderId(), e);
        });
        // 获取用户佣金记录
        PageInfo<UserBrokerageRecord> recordPageInfo = userBrokerageRecordService.findListByLinkIdsAndLinkTypeAndUid(orderNoList, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER, request.getUid(), pageParamRequest);
        List<SpreadOrderResponse> responseList = recordPageInfo.getList().stream().map(e -> {
            SpreadOrderResponse response = new SpreadOrderResponse();
            StoreOrder storeOrder = orderMap.get(e.getLinkId());
            response.setId(storeOrder.getId());
            response.setOrderId(storeOrder.getOrderId());
            response.setRealName(storeOrder.getRealName());
            response.setUserPhone(storeOrder.getUserPhone());
            response.setPrice(e.getPrice());
            response.setUpdateTime(e.getUpdateTime());
            return response;
        }).collect(Collectors.toList());

        return CommonPage.copyPageInfo(recordPageInfo, responseList);
    }

    /**
     * 获取推广人列表
     *
     * @param spreadUid 父Uid
     * @param type      类型 0 = 全部 1=一级推广人 2=二级推广人
     */
    private List<User> getSpreadListBySpreadIdAndType(Integer spreadUid, Integer type) {
        // 获取一级推广人
        List<User> userList = getSpreadListBySpreadId(spreadUid);
        if (CollUtil.isEmpty(userList)) return userList;
        if (type.equals(1)) return userList;
        // 获取二级推广人
        List<User> userSecondList = CollUtil.newArrayList();
        userList.forEach(user -> {
            List<User> childUserList = getSpreadListBySpreadId(user.getUid());
            if (CollUtil.isNotEmpty(childUserList)) {
                userSecondList.addAll(childUserList);
            }
        });
        if (type.equals(2)) {
            return userSecondList;
        }
        userList.addAll(userSecondList);
        return userList;
    }

    /**
     * 获取推广人列表
     *
     * @param spreadUid 父Uid
     */
    private List<User> getSpreadListBySpreadId(Integer spreadUid) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getSpreadUid, spreadUid);
        return userDao.selectList(queryWrapper);
    }

    /**
     * 根据用户id清除用户当前推广人
     *
     * @param userId 当前推广人id
     * @return 清除推广结果
     */
    @Override
    public boolean clearSpread(Integer userId) {
        User teamUser = getById(userId);
        User user = new User();
        user.setUid(userId);
        user.setPath("/0/");
        user.setSpreadUid(0);
        user.setSpreadTime(null);
        Boolean execute = transactionTemplate.execute(e -> {
            userDao.updateById(user);
            if (teamUser.getSpreadUid() > 0) {
                updateSpreadCountByUid(teamUser.getSpreadUid(), "sub");
            }
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 推广人排行
     *
     * @param type             String 类型
     * @param pageParamRequest PageParamRequest 分页
     * @return List<User>
     */
    @Override
    public List<User> getTopSpreadPeopleListByDate(String type, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("count(spread_count) as spread_count, spread_uid")
                .gt("spread_uid", 0)
                .eq("status", true);
        if (StrUtil.isNotBlank(type)) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(type);
            queryWrapper.between("create_time", dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        queryWrapper.groupBy("spread_uid").orderByDesc("spread_count");
        List<User> spreadVoList = userDao.selectList(queryWrapper);
        if (spreadVoList.size() < 1) {
            return null;
        }

        List<Integer> spreadIdList = spreadVoList.stream().map(User::getSpreadUid).collect(Collectors.toList());
        if (spreadIdList.size() < 1) {
            return null;
        }

        ArrayList<User> userList = new ArrayList<>();
        //查询用户
        HashMap<Integer, User> userVoList = getMapListInUid(spreadIdList);

        //解决排序问题
        for (User spreadVo : spreadVoList) {
            User user = new User();
            User userVo = userVoList.get(spreadVo.getSpreadUid());
            user.setUid(spreadVo.getSpreadUid());
            user.setAvatar(userVo.getAvatar());
            user.setSpreadCount(spreadVo.getSpreadCount());
            if (StringUtils.isBlank(userVo.getNickname())) {
                user.setNickname(userVo.getPhone().substring(0, 2) + "****" + userVo.getPhone().substring(7));
            } else {
                user.setNickname(userVo.getNickname());
            }

            userList.add(user);
        }

        return userList;
    }

    /**
     * 推广人排行
     *
     * @param minPayCount int 最小消费次数
     * @param maxPayCount int 最大消费次数
     * @return Integer
     */
    @Override
    public Integer getCountByPayCount(int minPayCount, int maxPayCount) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.between(User::getPayCount, minPayCount, maxPayCount);
        return userDao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 绑定推广关系（登录状态）
     * @param spreadUid 推广人id
     */
    @Override
    public void bindSpread(Integer spreadUid) {
        //新用户会在注册的时候单独绑定，此处只处理登录用户
        if (ObjectUtil.isNull(spreadUid) || spreadUid == 0) {
            return;
        }
        User user = getInfo();
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("当前用户未登录,请先登录");
        }

        loginService.bindSpread(user, spreadUid);
    }

    @Override
    public boolean updateBrokeragePrice(User user, BigDecimal newBrokeragePrice) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("brokerage_price", newBrokeragePrice)
                .eq("uid", user.getUid()).eq("brokerage_price", user.getBrokeragePrice());
        return userDao.update(user, updateWrapper) > 0;
    }

    /**
     * 更新推广人
     *
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean editSpread(UserUpdateSpreadRequest request) {
        Integer userId = request.getUserId();
        Integer spreadUid = request.getSpreadUid();
        if (userId.equals(spreadUid)) {
            throw new CrmebException("上级推广人不能为自己");
        }
        User user = getById(userId);
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("用户不存在");
        }
        if (user.getSpreadUid().equals(spreadUid)) {
            throw new CrmebException("当前推广人已经是所选人");
        }
        Integer oldSprUid = user.getSpreadUid();

        User spreadUser = getById(spreadUid);
        if (ObjectUtil.isNull(spreadUser)) {
            throw new CrmebException("上级用户不存在");
        }
        if (spreadUser.getSpreadUid().equals(userId)) {
            throw new CrmebException("当前用户已是推广人的上级");
        }

        User tempUser = new User();
        tempUser.setUid(userId);
        tempUser.setSpreadUid(spreadUid);
        tempUser.setSpreadTime(DateUtil.nowDateTime());
        Boolean execute = transactionTemplate.execute(e -> {
            updateById(tempUser);
            updateSpreadCountByUid(spreadUid, "add");
            if (oldSprUid > 0) {
                updateSpreadCountByUid(oldSprUid, "sub");
            }
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 更新用户积分
     * @param user     用户
     * @param integral 积分
     * @param type     增加add、扣减sub
     * @return 更新后的用户对象
     */
    @Override
    public Boolean updateIntegral(User user, BigDecimal integral, String type) {
        //创建修改对象
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = Wrappers.lambdaUpdate();

        //验证-积分变更类型
        if (type.equals("add")) {
            lambdaUpdateWrapper.set(User::getIntegral, user.getIntegral().add(integral));
        } else {
            lambdaUpdateWrapper.set(User::getIntegral, user.getIntegral().subtract(integral));
        }

        //修改条件-用户id
        lambdaUpdateWrapper.eq(User::getUid, user.getUid());

        //验证-减扣
        if (type.equals("sub")) {
            lambdaUpdateWrapper.apply(StrUtil.format(" integral - {} >= 0", integral));
        }

        //执行修改
        return update(lambdaUpdateWrapper);
    }

    /**
     * 获取分销人员列表
     *
     * @param keywords             搜索参数
     * @param dateLimit            时间参数
     * @param storeBrokerageStatus 分销状态：1-指定分销，2-人人分销
     * @return List<User>
     */
    @Override
    public List<User> findDistributionList(String keywords, String dateLimit, String storeBrokerageStatus) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        if (storeBrokerageStatus.equals("1")) {
            lqw.eq(User::getIsPromoter, true);
        }
        if (StrUtil.isNotBlank(dateLimit)) {
            dateLimitUtilVo dateLimitVo = DateUtil.getDateLimit(dateLimit);
            lqw.between(User::getCreateTime, dateLimitVo.getStartTime(), dateLimitVo.getEndTime());
        }
        if (StrUtil.isNotBlank(keywords)) {
            lqw.and(i -> i.like(User::getRealName, keywords) //真实姓名
                    .or().like(User::getPhone, keywords) //手机号码
                    .or().like(User::getNickname, keywords) //用户昵称
                    .or().like(User::getUid, keywords)); //uid
        }
        return userDao.selectList(lqw);
    }

    /**
     * 获取发展会员人数
     *
     * @param ids       推广人id集合
     * @param dateLimit 时间参数
     * @return Integer
     */
    @Override
    public Integer getDevelopDistributionPeopleNum(List<Integer> ids, String dateLimit) {
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.in(User::getSpreadUid, ids);
        if (StrUtil.isNotBlank(dateLimit)) {
            dateLimitUtilVo dateLimitVo = DateUtil.getDateLimit(dateLimit);
            lqw.between(User::getCreateTime, dateLimitVo.getStartTime(), dateLimitVo.getEndTime());
        }
        return userDao.selectCount(lqw);
    }

    /**
     * 清除User Group id
     *
     * @param groupId 待清除的GroupId
     */
    @Override
    public void clearGroupByGroupId(String groupId) {
        LambdaUpdateWrapper<User> upw = Wrappers.lambdaUpdate();
        upw.set(User::getGroupId, "").eq(User::getGroupId, groupId);
        update(upw);
    }

    /**
     * 更新用户
     *
     * @param userRequest 用户参数
     * @return Boolean
     */
    @Override
    public Boolean updateUser(UserUpdateRequest userRequest) {
        //根据ID得到用户信息
        User tempUser = getById(userRequest.getUid());

        //实例化用户对象
        User user = new User();
        BeanUtils.copyProperties(userRequest, user);

        //非空验证-等级
        if (ObjectUtil.isNull(userRequest.getLevel())) {
            user.setLevel(0);
        }

        //执行结果
        Boolean execute = transactionTemplate.execute(e -> {
            //执行修改
            updateById(user);

            //验证等级
            if (ObjectUtil.isNotNull(userRequest.getLevel()) && !tempUser.getLevel().equals(userRequest.getLevel())) {
                //得到-用户等级
                UserLevel userLevel = userLevelService.getUserLevelByUserId(tempUser.getUid());
                //验证非空并删除-修改
                if (ObjectUtil.isNotNull(userLevel)) {
                    userLevel.setIsDel(true);
                    userLevelService.updateById(userLevel);
                }

                //得到-设置用户等级表-对象
                SystemUserLevel systemUserLevel = systemUserLevelService.getByLevelId(userRequest.getLevel());

                //实例化-用户等级-对象
                UserLevel newLevel = new UserLevel();
                //赋值
                newLevel.setUid(tempUser.getUid());
                newLevel.setLevelId(systemUserLevel.getId());
                newLevel.setGrade(systemUserLevel.getGrade());
                newLevel.setStatus(true);
                newLevel.setMark(StrUtil.format("尊敬的用户 {},在{}管理员调整会员等级成为{}", tempUser.getNickname(), DateUtil.nowDateTimeStr(), systemUserLevel.getName()));
                newLevel.setDiscount(systemUserLevel.getDiscount());
                newLevel.setCreateTime(DateUtil.nowDateTime());

                //执行保存
                userLevelService.save(newLevel);
            }

            //验证等级
            if (ObjectUtil.isNull(userRequest.getLevel()) && tempUser.getLevel() > 0) {
                //得到-用户等级
                UserLevel userLevel = userLevelService.getUserLevelByUserId(tempUser.getUid());

                //删除
                userLevel.setIsDel(true);
                userLevelService.updateById(userLevel);
            }

            //返回执行结果
            return Boolean.TRUE;
        });

        //返回结果
        return execute;
    }

    /**
     * 根据手机号查询用户
     * @param phone 用户手机号
     * @return 用户信息
     */
    @Override
    public User getByPhone(String phone) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getPhone, phone);
        return userDao.selectOne(lqw);
    }

    /**
     * 后台修改用户手机号
     * @param id 用户uid
     * @param phone 手机号
     * @return Boolean
     */
    @Override
    public Boolean updateUserPhone(Integer id, String phone) {
        boolean matchPhone = ReUtil.isMatch(RegularConstants.PHONE, phone);
        if (!matchPhone) {
            throw new CrmebException("手机号格式错误，请输入正确得手机号");
        }
        User user = getById(id);
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("对应用户不存在");
        }
        if (phone.equals(user.getPhone())) {
            throw new CrmebException("手机号与之前一致");
        }

        //检测当前手机号是否已经是账号
        User tempUser = getByPhone(phone);
        if (ObjectUtil.isNotNull(tempUser)) {
            throw new CrmebException("此手机号码已被注册");
        }

        //执行更新
        User newUser = new User();
        newUser.setUid(id);
        newUser.setPhone(phone);
        newUser.setAccount(phone);
        return userDao.updateById(newUser) > 0;
    }

    /**
     * 根据昵称匹配用户，返回id集合
     * @param nikeName 需要匹配得昵称
     * @return List
     */
    @Override
    public List<Integer> findIdListLikeName(String nikeName) {
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.select(User::getUid);
        lqw.like(User::getNickname, nikeName);
        List<User> userList = userDao.selectList(lqw);
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(User::getUid).collect(Collectors.toList());
    }

    @Override
    public UserGeneralAgentDataResponse getUserGeneralAgentData() {
        //得到当前用户
        User thisLoginUser=this.getInfo();

        //得到下级用户,定义查询条件
        QueryWrapper<User> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.like("path", "/"+thisLoginUser.getUid()+"/");
        List<User> listSubUser = this.getUgaSupList(thisLoginUser);

        //定义变量-过渡变量
        BigDecimal gmv= BigDecimal.ZERO;//接收值
        Integer count=0;                //接收整型值

        //定义变量-日期相关
        String date =null;          //日期范围
        String startTime = null;    //开始日期
        String endTime = DateUtil.nowDateTime(Constants.DATE_FORMAT);           //结束日期
        StringBuffer dateSB=new StringBuffer("%s").append(",").append(endTime); //日期字符串拼接
        Date lastMonthEndDay=DateUtil.strToDate(DateUtil.getLastMonthEndDay(),Constants.DATE_FORMAT);//上个月最后一天
        Date weekStartDay=DateUtil.strToDate(DateUtil.getWeekStartDay(),Constants.DATE_FORMAT);//本周第一天
        Integer distanceThisDay=DateUtil.nowDateTime().getDay()-weekStartDay.getDay();//距离今天=当前本周第几天-距离本周第一天有多少天

        //定义变量-交易相关
        BigDecimal dayGmv = BigDecimal.ZERO;           //今日交易额
        BigDecimal yesterdayGmv = BigDecimal.ZERO;     //昨日交易额
        BigDecimal thisMonthGmv = BigDecimal.ZERO;     //本月交易额

        //定义变量-佣金相关
        BigDecimal dayCommission=BigDecimal.ZERO;           //今日佣金总和
        BigDecimal yesterCommission=BigDecimal.ZERO;        //昨日佣金总和
        BigDecimal thisMonthCommission=BigDecimal.ZERO;     //本月佣金总和
        BigDecimal myAllCommissio=BigDecimal.ZERO;          //所有佣金总和

        //定义变量-推广数量
        Integer daySpreadNum=0;           //今日推广总人数
        Integer yesterSpreadNum=0;        //昨日推广总人数
        Integer thisMonthSpreadNum=0;     //本月推广总人数
        Integer myAllSpreadNum=0;         //我所有推广的总人数

        //定义变量-订单统计
        LambdaQueryWrapper<StoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        Integer dayOrderNum=0;          //今日订单总数
        Integer yesterdayOrderNum=0;    //昨日订单总数
        Integer thisMonthOrderNum=0;    //本月总订单总数
        GeneralAgentOrderStateResponse generalAgentOrderStateResponse = new GeneralAgentOrderStateResponse();//订单分别状态数量-响应对象-并初始化赋值
        generalAgentOrderStateResponse.setUnPaidCount(0);
        generalAgentOrderStateResponse.setUnShippedCount(0);
        generalAgentOrderStateResponse.setReceivedCount(0);
        generalAgentOrderStateResponse.setEvaluatedCount(0);
        generalAgentOrderStateResponse.setCompleteCount(0);
        generalAgentOrderStateResponse.setRefundCount(0);
        generalAgentOrderStateResponse.setOrderCount(0);
        generalAgentOrderStateResponse.setSumPrice(BigDecimal.ZERO);
        List<OrderWeekDataResponse> orderWeekDataResponseList=new ArrayList<>();//本周订单-统计每天数量

        //循环处理
        for (User subUser: listSubUser) {
            //今日交易额
            startTime=DateUtil.addDay(DateUtil.nowDateTime(), 0, Constants.DATE_FORMAT_DATE);
            date=String.format(dateSB.toString(),startTime);
            gmv = userBillService.getSumBigDecimal(0, subUser.getUid(), Constants.USER_BILL_CATEGORY_MONEY, date, null);
            dayGmv=dayGmv.add(gmv);
            //今日总佣金
            gmv=userBillService.getSumBigDecimal(0, subUser.getUid(), Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE, date, null);
            dayCommission=dayCommission.add(gmv);
            //得到-今日推广总人数
            objectQueryWrapper.between("create_time", startTime, endTime);
            count=userDao.selectCount(objectQueryWrapper);
            daySpreadNum=daySpreadNum+count;
            //得到-今日订单总数
            lambdaQueryWrapper.eq(StoreOrder::getUid,subUser.getUid());
            lambdaQueryWrapper.between(StoreOrder::getCreateTime,startTime,endTime);
            count = storeOrderService.count(lambdaQueryWrapper);
            dayOrderNum=dayOrderNum+count;

            //昨日交易额
            startTime = DateUtil.addDay(DateUtil.nowDateTime(), -1, Constants.DATE_FORMAT_DATE);
            date=String.format(dateSB.toString(),startTime);
            gmv = userBillService.getSumBigDecimal(0, subUser.getUid(), Constants.USER_BILL_CATEGORY_MONEY, date, null);
            yesterdayGmv=yesterdayGmv.add(gmv);
            //得到-昨日佣金
            gmv=userBillService.getSumBigDecimal(0, subUser.getUid(), Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE, date, null);
            yesterCommission=yesterCommission.add(gmv);
            //得到-昨日推广总人数
            objectQueryWrapper.between("create_time", startTime, endTime);
            count=userDao.selectCount(objectQueryWrapper);
            yesterSpreadNum=yesterSpreadNum+count;
            //得到-昨日订单总数
            lambdaQueryWrapper.eq(StoreOrder::getUid,subUser.getUid());
            lambdaQueryWrapper.between(StoreOrder::getCreateTime,startTime,endTime);
            count = storeOrderService.count(lambdaQueryWrapper);
            yesterdayOrderNum=yesterdayOrderNum+count;

            //得到-本月交易额
            startTime = DateUtil.addDay(lastMonthEndDay, 0, Constants.DATE_FORMAT);
            date=String.format(dateSB.toString(),startTime);
            gmv = userBillService.getSumBigDecimal(0, subUser.getUid(), Constants.USER_BILL_CATEGORY_MONEY, date, null);
            thisMonthGmv=thisMonthGmv.add(gmv);
            //得到-本月佣金
            gmv = userBillService.getSumBigDecimal(0, subUser.getUid(), Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE, date, null);
            thisMonthCommission=thisMonthCommission.add(gmv);
            //得到-本月推广总人数
            objectQueryWrapper.between("create_time", startTime, endTime);
            count=userDao.selectCount(objectQueryWrapper);
            thisMonthSpreadNum=thisMonthSpreadNum+count;
            //得到-本月订单总数
            lambdaQueryWrapper.eq(StoreOrder::getUid,subUser.getUid());
            lambdaQueryWrapper.between(StoreOrder::getCreateTime,startTime,endTime);
            count = storeOrderService.count(lambdaQueryWrapper);
            thisMonthOrderNum=thisMonthOrderNum+count;

            //得到-下级用户-佣金总和-并累计
            gmv=userBillService.getSumBigDecimal(0, subUser.getUid(), Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE, null, null);
            myAllCommissio=myAllCommissio.add(gmv);

            //得到-每个用户的订单分别状态的数量-并累计
            count = storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_UNPAID, subUser.getUid(),null);
            generalAgentOrderStateResponse.setUnPaidCount(generalAgentOrderStateResponse.getUnPaidCount()+count);//待支付
            count = storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_NOT_SHIPPED, subUser.getUid(),null);
            generalAgentOrderStateResponse.setUnShippedCount(generalAgentOrderStateResponse.getUnShippedCount()+count);//待发货
            count=storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_SPIKE, subUser.getUid(),null);
            generalAgentOrderStateResponse.setReceivedCount(generalAgentOrderStateResponse.getReceivedCount()+count);//待收货
            count=storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_JUDGE, subUser.getUid(),null);
            generalAgentOrderStateResponse.setEvaluatedCount(generalAgentOrderStateResponse.getEvaluatedCount()+count);//待评价
            count=storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_COMPLETE, subUser.getUid(),null);
            generalAgentOrderStateResponse.setCompleteCount(generalAgentOrderStateResponse.getCompleteCount()+count);//已完成
            count=storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_REFUND, subUser.getUid(),null);
            generalAgentOrderStateResponse.setRefundCount(generalAgentOrderStateResponse.getRefundCount()+count);//退款中和已退款-累计

            //得到-下级用户-订单总数量-并累计
            count=storeOrderService.getOrderCountByUid(subUser.getUid(),null);
            generalAgentOrderStateResponse.setOrderCount(generalAgentOrderStateResponse.getOrderCount()+count);

            //得到-下级用户-总消费金额-并累计
            gmv = storeOrderService.getSumPayPriceByUid(subUser.getUid(),null);
            generalAgentOrderStateResponse.setSumPrice(generalAgentOrderStateResponse.getSumPrice().add(gmv));
        }

        //得到-下级用户-本周订单-每天的交易额
        for (;distanceThisDay>=0;distanceThisDay--){
            //实例化-本周订单-统计数据-响应对象
            OrderWeekDataResponse orderWeekDataResponse=new OrderWeekDataResponse();
            orderWeekDataResponse.setOrderNum(0);
            orderWeekDataResponse.setSumPrice(BigDecimal.ZERO);

            //日期
            startTime = DateUtil.addDay(DateUtil.nowDateTime(), -distanceThisDay, Constants.DATE_FORMAT_DATE);
            date = startTime+","+startTime; //查当天的数据
            orderWeekDataResponse.setDateStr(startTime);

            //循环处理
            for (User subUser: listSubUser) {
                gmv = storeOrderService.getSumPayPriceByUidAndDate(1, date,subUser.getUid());
                count = storeOrderService.getOrderCount(1 ,subUser.getUid(),date);

                //累加
                orderWeekDataResponse.setSumPrice(orderWeekDataResponse.getSumPrice().add(gmv));
                orderWeekDataResponse.setOrderNum(orderWeekDataResponse.getOrderNum()+count);
            }
            //添加到-本周订单统计数据-list
            orderWeekDataResponseList.add(orderWeekDataResponse);
        }

        //实例化-总代理用户-响应对象
        UserGeneralAgentDataResponse ugadr=new UserGeneralAgentDataResponse();
        //赋值-交易相关
        ugadr.setDayGmv(dayGmv);
        ugadr.setYesterdayGmv(yesterdayGmv);
        ugadr.setThisMonthGmv(thisMonthGmv);

        //赋值-佣金相关
        ugadr.setDayCommission(dayCommission);
        ugadr.setYesterCommission(yesterCommission);
        ugadr.setThisMonthCommission(thisMonthCommission);
        ugadr.setMyAllCommission(myAllCommissio);

        //赋值-推广相关
        ugadr.setDaySpreadNum(daySpreadNum);
        ugadr.setYesterSpreadNum(yesterSpreadNum);
        ugadr.setThisMonthSpreadNum(thisMonthSpreadNum);
        myAllSpreadNum=listSubUser.size();
        ugadr.setMyAllSpreadNum(myAllSpreadNum);

        //赋值-订单相关
        ugadr.setDayOrderNum(dayOrderNum);
        ugadr.setYesterdayOrderNum(yesterdayOrderNum);
        ugadr.setThisMonthOrderNum(thisMonthOrderNum);
        ugadr.setGeneralAgentOrderStateResponse(generalAgentOrderStateResponse);
        ugadr.setOrderWeekDataResponseList(orderWeekDataResponseList);

        //返回
        return ugadr;
    }

    @Override
    public CommonPage<OrderDetailResponse> getUgaSubAllOrderList(Integer status, PageParamRequest pageRequest) {
        //得到当前用户
        User user=this.getInfo();

        //得到-总代理-下级用户list
        List<User> subUserList=this.getUgaSupList(user);

        //定义集合变量-订单相关集合
        List<StoreOrder> orderList = null;
        CommonPage<StoreOrder> storeOrderCommonPage = new CommonPage<>();
        List<OrderDetailResponse> responseList = CollUtil.newArrayList();

        //循环处理-得到下级订单列表
        for (User subUser: subUserList) {
            //得到-下级用户-订单列表
            orderList = storeOrderService.getUserOrderList(subUser.getUid(), status, pageRequest);
            storeOrderCommonPage = CommonPage.restPage(orderList);

            //循环处理-下级用户订单数据
            for (StoreOrder storeOrder : orderList) {
                //实例化-订单列表-响应对象
                OrderDetailResponse infoResponse = new OrderDetailResponse();
                BeanUtils.copyProperties(storeOrder, infoResponse);

                //订单状态
                infoResponse.setOrderStatus(OrderServiceImpl.getH5OrderStatus(storeOrder));

                //活动类型
                infoResponse.setActivityType(OrderServiceImpl.getOrderActivityType(storeOrder));

                //得到-订单订单购物详情-list对象
                List<StoreOrderInfoOldVo> infoVoList = storeOrderInfoService.getOrderListByOrderId(storeOrder.getId());

                //循环处理，定义订单购物详情-响应对象-集合
                List<OrderInfoResponse> infoResponseList = CollUtil.newArrayList();
                infoVoList.forEach(e -> {
                    //实例化-订单购物详情-响应对象
                    OrderInfoResponse orderInfoResponse = new OrderInfoResponse();
                    orderInfoResponse.setStoreName(e.getInfo().getProductName());
                    orderInfoResponse.setImage(e.getInfo().getImage());
                    orderInfoResponse.setCartNum(e.getInfo().getPayNum());
                    orderInfoResponse.setPrice(e.getInfo().getPrice());
                    orderInfoResponse.setProductId(e.getProductId());

                    //添加到-订单购物详-响应对象-集合
                    infoResponseList.add(orderInfoResponse);
                });

                //得到-订单详情对象列表
                infoResponse.setOrderInfoList(infoResponseList);

                //添加到-订单列表-响应集合对象
                responseList.add(infoResponse);
            }
        }

        //得到-订单详情响应-分页对象
        CommonPage<OrderDetailResponse> detailPage = CommonPage.restPage(responseList);
        BeanUtils.copyProperties(storeOrderCommonPage, detailPage, "list");

        //返回
        return detailPage;
    }

    @Override
    public GeneralAgentOrderDetailsResponse getUgaOrderDetails(Integer dateType) {
        //得到当前用户
        User user=this.getInfo();

        //得到-总代理-下级用户list
        List<User> subUserList=this.getUgaSupList(user);

        //定义变量-过渡变量
        BigDecimal gmv= BigDecimal.ZERO;//接收值
        Integer count=0;                //接收整型值

        //定义变量-日期相关
        String date =null;          //日期范围
        String startTime = null;    //开始日期
        String endTime = DateUtil.nowDateTime(Constants.DATE_FORMAT);           //结束日期
        StringBuffer dateSB=new StringBuffer("%s").append(",").append(endTime); //日期字符串拼接
        Date lastMonthEndDay=DateUtil.strToDate(DateUtil.getLastMonthEndDay(),Constants.DATE_FORMAT);   //上个月最后一天
        Date weekStartDay=DateUtil.strToDate(DateUtil.getWeekStartDay(),Constants.DATE_FORMAT);         //本周第一天
        Integer distanceThisDay=DateUtil.nowDateTime().getDay()-weekStartDay.getDay();                  //距离今天=当前本周第几天-距离本周第一天有多少天
        Date getLastYearEndDay=DateUtil.strToDate(DateUtil.getLastYearEndDay(),Constants.DATE_FORMAT);  //上一年最后一天

        //定义变量-订单相关
        Integer orderCount=0;//订单数量
        List<OrderWeekDataResponse> orderWeekDataResponseList=new ArrayList<>();//本周订单-统计每天数量
        BigDecimal totalAmount=BigDecimal.ZERO;//总金额

        //定义变量-报表相关
        List<String> dateList=new ArrayList<>();    //日期字符串集合
        List<Integer> dateValueList=new ArrayList<>();  //日期对于的-订单数值-数组
        List<BigDecimal> bigDecimalList=new ArrayList<>();

        //switch验证查询日期类型-拼接日期范围
        switch (dateType){
            case 0:
                //今天
                startTime =DateUtil.nowDateTime(Constants.DATE_FORMAT_DATE);
                date=String.format(dateSB.toString(),startTime);
                break;
            case 1:
                //昨天
                startTime = DateUtil.addDay(DateUtil.nowDateTime(), -1, Constants.DATE_FORMAT_DATE);
                date=String.format(dateSB.toString(),startTime);
                break;
            case 2:
                //最近7天
                startTime = DateUtil.addDay(DateUtil.nowDateTime(), -7, Constants.DATE_FORMAT_DATE);
                date=String.format(dateSB.toString(),startTime);
                break;
            case 3:
                //本月
                startTime = DateUtil.addDay(lastMonthEndDay, 0, Constants.DATE_FORMAT);
                date=String.format(dateSB.toString(),startTime);
                break;
            case 4:
                //本年
                startTime = DateUtil.addDay(getLastYearEndDay, 0, Constants.DATE_FORMAT);
                date=String.format(dateSB.toString(),startTime);
                break;
            default:
                throw new CrmebException("日期类型传值错误！");
        }

        //得到-下级用户-本周订单-每天的交易额
        int i=0;//只在-subUserList集合-遍历一遍
        Calendar calendar = Calendar.getInstance();//日期处理
        for (;distanceThisDay>=0;distanceThisDay--){
            //实例化-本周订单-统计数据-响应对象
            OrderWeekDataResponse orderWeekDataResponse=new OrderWeekDataResponse();
            orderWeekDataResponse.setOrderNum(0);
            orderWeekDataResponse.setSumPrice(BigDecimal.ZERO);

            //本周日期范围
            String startTimeWeek = DateUtil.addDay(DateUtil.nowDateTime(), -distanceThisDay, Constants.DATE_FORMAT_DATE);
            String dateWeek = startTimeWeek+","+startTimeWeek; //查当天的数据
            orderWeekDataResponse.setDateStr(startTimeWeek);

            //根据本周日期范围-开始日期-得到年月日
            Date dateDay = DateUtil.strToDate(startTimeWeek,Constants.DATE_FORMAT_DATE);
            calendar.setTime(dateDay);
            dateList.add(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));

            //循环处理-得到下级订单列表
            for (User subUser: subUserList) {
                //只在这个循环-遍历一遍
                if(i<subUserList.size()){
                    //得到-代理商-下级用户-某段日期-订单总数量-并累计
                    count = storeOrderService.getOrderCount(1,subUser.getUid(), date);
                    orderCount=orderCount+count;
                    gmv = storeOrderService.getSumPayPriceByUidAndDate(1, date,subUser.getUid());
                    totalAmount.add(gmv);
                    i++;
                }

                //得到-该用户-本周-某一天总订单量、总消费
                gmv = storeOrderService.getSumPayPriceByUidAndDate(1, dateWeek,subUser.getUid());
                count = storeOrderService.getOrderCount(1,subUser.getUid(), dateWeek);

                //累加
                orderWeekDataResponse.setSumPrice(orderWeekDataResponse.getSumPrice().add(gmv));
                orderWeekDataResponse.setOrderNum(orderWeekDataResponse.getOrderNum()+count);
            }

            //得到-日期-对应的订单数
            dateValueList.add(orderWeekDataResponse.getOrderNum());
            bigDecimalList.add(orderWeekDataResponse.getSumPrice());

            //添加到-本周订单统计数据-list
            orderWeekDataResponseList.add(orderWeekDataResponse);
        }

        //实例化-总代理用户-订单详情统计数据-响应对象
        GeneralAgentOrderDetailsResponse gaodr=new GeneralAgentOrderDetailsResponse();
        gaodr.setOrderCount(orderCount);
        gaodr.setOrderWeekDataResponseList(orderWeekDataResponseList);
        gaodr.setDateList(dateList);
        gaodr.setDateValueList(dateValueList);
        gaodr.setTotalAmount(totalAmount);
        gaodr.setBigDecimalList(bigDecimalList);

        //返回
        return gaodr;
    }

    @Override
    public List<User> getUgaSupList(User user){
        //验证非空
        if(user == null){
            throw new CrmebException("当前登录用户不存在！");
        }

        //验证是否为区域代理
        if(user.getIsGeneralAgency()==null||!user.getIsGeneralAgency()){
            throw new CrmebException("不是区域代理用户！无法查看!");
        }

        //定义查询条件-得到下级用户
        QueryWrapper<User> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.like("path", "/"+user.getUid()+"/");
        List<User> subUserList = userDao.selectList(objectQueryWrapper);

        //验证非空
        if(subUserList == null ){
            subUserList=new ArrayList<>();
        }

        //返回
        return subUserList;
    }

    @Override
    public UserGeneralAgentCommissionDataResponse getUgaCommission(PageParamRequest pageParamRequest) {
        //得到当前用户
        User user=this.getInfo();

        //定义变量-佣金相关
        BigDecimal dayCommission=BigDecimal.ZERO;           //今日佣金总和
        BigDecimal yesterCommission=BigDecimal.ZERO;        //昨日佣金总和
        BigDecimal thisMonthCommission=BigDecimal.ZERO;     //本月佣金总和
        BigDecimal myAllCommissio=BigDecimal.ZERO;          //所有佣金总和
        BigDecimal leijiShouyiCommissio=BigDecimal.ZERO;    //累计收益佣金
        BigDecimal leijieTiXianCommissio=BigDecimal.ZERO;   //累计提现佣金

        //定义变量-日期相关
        String date =null;          //日期范围
        String startTime = null;    //开始日期
        String endTime = DateUtil.nowDateTime(Constants.DATE_FORMAT);           //结束日期
        StringBuffer dateSB=new StringBuffer("%s").append(",").append(endTime); //日期字符串拼接
        Date lastMonthEndDay=DateUtil.strToDate(DateUtil.getLastMonthEndDay(),Constants.DATE_FORMAT);//上个月最后一天
        Date weekStartDay=DateUtil.strToDate(DateUtil.getWeekStartDay(),Constants.DATE_FORMAT);//本周第一天

        //得到-今日总佣金
        startTime=DateUtil.addDay(DateUtil.nowDateTime(), 0, Constants.DATE_FORMAT_DATE);
        date=String.format(dateSB.toString(),startTime);
        dayCommission=userBillService.getSumBigDecimal(0, user.getUid(), Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE, date, null);

        //得到-昨日佣金
        startTime = DateUtil.addDay(DateUtil.nowDateTime(), -1, Constants.DATE_FORMAT_DATE);
        date=String.format(dateSB.toString(),startTime);
        yesterCommission=userBillService.getSumBigDecimal(0, user.getUid(), Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE, date, null);

        //得到-本月佣金
        startTime = DateUtil.addDay(lastMonthEndDay, 0, Constants.DATE_FORMAT);
        date=String.format(dateSB.toString(),startTime);
        thisMonthCommission = userBillService.getSumBigDecimal(0, user.getUid(), Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE, date, null);

        //得到-佣金总和
        myAllCommissio=userBillService.getSumBigDecimal(0, user.getUid(), Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE, null, null);

        //得到-累计收益
        leijiShouyiCommissio = userBillService.getSumBigDecimal(null, user.getUid(), Constants.USER_BILL_CATEGORY_MONEY, null, Constants.USER_BILL_TYPE_BROKERAGE);
        leijieTiXianCommissio = userBillService.getSumBigDecimal(1, user.getUid(), Constants.USER_BILL_CATEGORY_MONEY, null, Constants.USER_BILL_TYPE_BROKERAGE); //提现
        leijiShouyiCommissio.subtract(leijieTiXianCommissio);

        //得到-累计提现
        leijieTiXianCommissio=userExtractService.getWithdrawn(null,null);

        //实例化-总代理用户-佣金数据-响应对象
        UserGeneralAgentCommissionDataResponse ugacdr=new UserGeneralAgentCommissionDataResponse();
        ugacdr.setDayCommission(dayCommission);
        ugacdr.setMyAllCommission(myAllCommissio);
        ugacdr.setLeijieTiXianCommissio(leijieTiXianCommissio);
        ugacdr.setLeijiShouyiCommissio(leijiShouyiCommissio);
        ugacdr.setYesterCommission(yesterCommission);
        ugacdr.setThisMonthCommission(thisMonthCommission);

        //佣金明细-分页对象
        PageInfo<SpreadCommissionDetailResponse> pageInfoCommissionDetail = userBrokerageRecordService.findDetailListByUid(user.getUid(), pageParamRequest);
        ugacdr.setPageInfoCommissionDetail(pageInfoCommissionDetail);

        //返回数据
        return ugacdr;
    }

    @Override
    public UserMerIdDataResponse getUserMerIdDataResponse(Integer merIds) {
        //得到ID
        Integer merId = merIds;   //商户ID、商户用户ID、零售商ID标识

        //定义变量-过渡变量
        BigDecimal gmv= BigDecimal.ZERO;//接收值
        Integer count=0;                //接收整型值

        //定义变量-日期相关
        String date =null;          //日期范围
        String startTime = null;    //开始日期
        String endTime = DateUtil.nowDateTime(Constants.DATE_FORMAT);           //结束日期
        StringBuffer dateSB=new StringBuffer("%s").append(",").append(endTime); //日期字符串拼接
        Date lastMonthEndDay=DateUtil.strToDate(DateUtil.getLastMonthEndDay(),Constants.DATE_FORMAT);//上个月最后一天
        Date weekStartDay=DateUtil.strToDate(DateUtil.getWeekStartDay(),Constants.DATE_FORMAT);//本周第一天
        Integer distanceThisDay=DateUtil.nowDateTime().getDay()-weekStartDay.getDay();//距离今天=当前本周第几天-距离本周第一天有多少天

        //定义变量-交易相关
        BigDecimal dayGmv = BigDecimal.ZERO;           //今日交易额
        BigDecimal yesterdayGmv = BigDecimal.ZERO;     //昨日交易额
        BigDecimal thisMonthGmv = BigDecimal.ZERO;     //本月交易额

        //定义变量-订单统计
        Integer dayOrderNum=0;          //今日订单总数
        Integer yesterdayOrderNum=0;    //昨日订单总数
        Integer thisMonthOrderNum=0;    //本月总订单总数
        List<OrderWeekDataResponse> orderWeekDataResponseList=new ArrayList<>();//本周订单-统计每天数量

        //订单分别状态数量-响应对象-并初始化赋值
        OrderDataResponse orderDataResponse = new OrderDataResponse();
        orderDataResponse.setUnPaidCount(0);
        orderDataResponse.setUnShippedCount(0);
        orderDataResponse.setReceivedCount(0);
        orderDataResponse.setEvaluatedCount(0);
        orderDataResponse.setCompleteCount(0);
        orderDataResponse.setRefundCount(0);
        orderDataResponse.setOrderCount(0);
        orderDataResponse.setSumPrice(BigDecimal.ZERO);

        //得到-今日订单总数 + 总金额
        startTime=DateUtil.addDay(DateUtil.nowDateTime(), 0, Constants.DATE_FORMAT_DATE);
        date=String.format(dateSB.toString(),startTime);
        dayOrderNum = storeOrderService.getOrderCount(2,merId, date);
        dayGmv = storeOrderService.getSumPayPriceByUidAndDate(2,date,merId);

        //得到-昨日订单总数 + 总金额
        startTime = DateUtil.addDay(DateUtil.nowDateTime(), -1, Constants.DATE_FORMAT_DATE);
        date=String.format("%s,%s",startTime,startTime);
        yesterdayOrderNum = storeOrderService.getOrderCount(2,merId, date);
        yesterdayGmv = storeOrderService.getSumPayPriceByUidAndDate(2,date,merId);

        //得到-本月订单总数 + 总金额
        startTime = DateUtil.addDay(lastMonthEndDay, 0, Constants.DATE_FORMAT);
        date=String.format(dateSB.toString(),startTime);
        thisMonthOrderNum=storeOrderService.getOrderCount(2,merId, date);
        thisMonthGmv = storeOrderService.getSumPayPriceByUidAndDate(2,date,merId);

        //得到-下级用户-本周订单-每天的交易额
        for (;distanceThisDay>=0;distanceThisDay--){
            //实例化-本周订单-统计数据-响应对象
            OrderWeekDataResponse orderWeekDataResponse=new OrderWeekDataResponse();
            orderWeekDataResponse.setOrderNum(0);
            orderWeekDataResponse.setSumPrice(BigDecimal.ZERO);

            //日期
            startTime = DateUtil.addDay(DateUtil.nowDateTime(), -distanceThisDay, Constants.DATE_FORMAT_DATE);
            date = startTime+","+startTime; //查当天的数据
            orderWeekDataResponse.setDateStr(startTime);

            //数据
            gmv = storeOrderService.getSumPayPriceByUidAndDate(2, date,merId);
            count = storeOrderService.getOrderCount(2,merId, date);

            //赋值
            orderWeekDataResponse.setSumPrice(gmv);
            orderWeekDataResponse.setOrderNum(count);

            //添加到-本周订单统计数据-list
            orderWeekDataResponseList.add(orderWeekDataResponse);
        }

        //得到-商户用户的订单分别状态的数量
        count = storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_UNPAID,null, merId);
        orderDataResponse.setUnPaidCount(count);//待支付
        count = storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_NOT_SHIPPED, null,merId);
        orderDataResponse.setUnShippedCount(count);//待发货
        count=storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_SPIKE,null, merId);
        orderDataResponse.setReceivedCount(count);//待收货
        count=storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_JUDGE,null, merId);
        orderDataResponse.setEvaluatedCount(count);//待评价
        count=storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_COMPLETE, null,merId);
        orderDataResponse.setCompleteCount(count);//已完成
        count=storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_REFUNDING, null,merId);
        orderDataResponse.setRefundCount(count);//只统计-退款中

        //得到-订单总数量-并累计
        count=storeOrderService.getOrderCountByUid(null,merId);
        orderDataResponse.setOrderCount(count);

        //得到-总消费金额-并累计
        gmv = storeOrderService.getSumPayPriceByUid(null,merId);
        orderDataResponse.setSumPrice(gmv);

        //定义响应对象
        UserMerIdDataResponse userMerIdDataResponse=new UserMerIdDataResponse();

        //赋值-每周数据
        userMerIdDataResponse.setOrderWeekDataResponseList(orderWeekDataResponseList);
        userMerIdDataResponse.setOrderDataResponse(orderDataResponse);

        //赋值-交易相关
        userMerIdDataResponse.setDayGmv(dayGmv);
        userMerIdDataResponse.setYesterdayGmv(yesterdayGmv);
        userMerIdDataResponse.setThisMonthGmv(thisMonthGmv);

        //赋值-订单数量
        userMerIdDataResponse.setDayOrderNum(dayOrderNum);
        userMerIdDataResponse.setThisMonthOrderNum(thisMonthOrderNum);
        userMerIdDataResponse.setYesterdayOrderNum(yesterdayOrderNum);

        //返回
        return userMerIdDataResponse;
    }

    @Override
    public UserMerIdDataResponse getUserMerIdData(Integer raId) {
        //定义变量
        RegionalAgency regionalAgency =null;

        //验证区域代理ID标识非空-得到-区域代理信息
        if(raId == null || raId.equals(0)){
            //得到-当前登录用户（查当前用户绑定的-区域代理）
            User thisLoginUser=this.getInfoException();
            LambdaQueryWrapper<RegionalAgency> regionalAgencyLambdaQueryWrapper=new LambdaQueryWrapper<>();
            regionalAgencyLambdaQueryWrapper.eq(RegionalAgency::getUid,thisLoginUser.getUid());
            regionalAgencyLambdaQueryWrapper.last("LIMIT 1");//只取一条
            regionalAgency = regionalAgencyService.getOne(regionalAgencyLambdaQueryWrapper);
        }else{
            //根据ID标识-得到区域代理信息
            regionalAgency = regionalAgencyService.getById(raId);
        }

        //验证-区域代理信息
        if(regionalAgency == null) return new UserMerIdDataResponse();

        //得到数据-并返回
        return this.getUserMerIdDataResponse(regionalAgency.getId());
    }

    @Override
    public UserMerIdOrderDetailsResponse getMerIdOrderInfoStatisticsData(Integer merIds, Integer dateType) {
        //得到ID
        Integer merId = merIds;   //商户ID、商户用户ID、零售商ID标识

        //定义变量-过渡变量
        BigDecimal gmv= BigDecimal.ZERO;//接收值
        Integer count=0;                //接收整型值

        //定义变量-日期相关
        String date =null;          //日期范围
        String startTime = null;    //开始日期
        String endTime = DateUtil.nowDateTime(Constants.DATE_FORMAT);           //结束日期
        StringBuffer dateSB=new StringBuffer("%s").append(",").append(endTime); //日期字符串拼接
        Date lastMonthEndDay=DateUtil.strToDate(DateUtil.getLastMonthEndDay(),Constants.DATE_FORMAT);   //上个月最后一天
        Date weekStartDay=DateUtil.strToDate(DateUtil.getWeekStartDay(),Constants.DATE_FORMAT);         //本周第一天
        Integer distanceThisDay=DateUtil.nowDateTime().getDay()-weekStartDay.getDay();                  //距离今天=当前本周第几天-距离本周第一天有多少天
        Date getLastYearEndDay=DateUtil.strToDate(DateUtil.getLastYearEndDay(),Constants.DATE_FORMAT);  //上一年最后一天

        //定义变量-订单相关
        Integer orderCount=0;                                                       //总订单数量
        List<OrderWeekDataResponse> orderWeekDataResponseList=new ArrayList<>();    //本周订单-统计每天数量
        BigDecimal totalAmount=BigDecimal.ZERO;                                     //总消费金额

        //定义变量-报表相关
        List<String> dateList=new ArrayList<>();            //日期字符串集合
        List<Integer> dateValueList=new ArrayList<>();      //日期对应的-订单数值-数组
        List<BigDecimal> bigDecimalList=new ArrayList<>();  //日期对应的-金额list

        //switch验证查询日期类型-拼接日期范围
        switch (dateType){
            case 0:
                //今天
                startTime =DateUtil.nowDateTime(Constants.DATE_FORMAT_DATE);
                date=String.format(dateSB.toString(),startTime);
                break;
            case 1:
                //昨天
                startTime = DateUtil.addDay(DateUtil.nowDateTime(), -1, Constants.DATE_FORMAT_DATE);
                date=new StringBuffer(startTime).append(",").append(DateUtil.nowDateTime(Constants.DATE_FORMAT_DATE)).toString();
                break;
            case 2:
                //最近7天
                startTime = DateUtil.addDay(DateUtil.nowDateTime(), -7, Constants.DATE_FORMAT_DATE);
                date=String.format(dateSB.toString(),startTime);
                break;
            case 3:
                //本月
                startTime = DateUtil.addDay(lastMonthEndDay, 0, Constants.DATE_FORMAT);
                date=String.format(dateSB.toString(),startTime);
                break;
            case 4:
                //本年
                startTime = DateUtil.addDay(getLastYearEndDay, 0, Constants.DATE_FORMAT);
                date=String.format(dateSB.toString(),startTime);
                break;
            default:
                throw new CrmebException("日期类型传值错误！");
        }

        //得到-商户用户-某段日期订单总数量以及总消费金额
        orderCount = storeOrderService.getOrderCount(2,merIds, date);
        totalAmount = storeOrderService.getSumPayPriceByUidAndDate(2, date,merIds);

        //得到-下级用户-本周订单-每天的交易额
        Calendar calendar = Calendar.getInstance();//日期处理
        for (;distanceThisDay>=0;distanceThisDay--){
            //实例化-本周订单-统计数据-响应对象
            OrderWeekDataResponse orderWeekDataResponse=new OrderWeekDataResponse();
            orderWeekDataResponse.setOrderNum(0);
            orderWeekDataResponse.setSumPrice(BigDecimal.ZERO);

            //本周日期范围
            String startTimeWeek = DateUtil.addDay(DateUtil.nowDateTime(), -distanceThisDay, Constants.DATE_FORMAT_DATE);
            String dateWeek = startTimeWeek+","+startTimeWeek; //查当天的数据
            orderWeekDataResponse.setDateStr(startTimeWeek);

            //根据本周日期范围-开始日期-得到年月日
            Date dateDay = DateUtil.strToDate(startTimeWeek,Constants.DATE_FORMAT_DATE);
            calendar.setTime(dateDay);
            dateList.add(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));

            //得到-该商户用户-本周某一天总订单量、总消费
            gmv = storeOrderService.getSumPayPriceByUidAndDate(2, dateWeek,merIds);
            count = storeOrderService.getOrderCount(2,merIds, dateWeek);

            //赋值
            orderWeekDataResponse.setSumPrice(gmv);
            orderWeekDataResponse.setOrderNum(count);

            //添加到-日期对应的-订单数以及消费金额
            dateValueList.add(orderWeekDataResponse.getOrderNum());
            bigDecimalList.add(orderWeekDataResponse.getSumPrice());

            //添加到-本周订单统计数据list
            orderWeekDataResponseList.add(orderWeekDataResponse);
        }

        //实例化-商户用户-订单详情统计数据-响应对象
        UserMerIdOrderDetailsResponse gaodr=new UserMerIdOrderDetailsResponse();
        gaodr.setOrderCount(orderCount);
        gaodr.setOrderWeekDataResponseList(orderWeekDataResponseList);
        gaodr.setDateList(dateList);
        gaodr.setDateValueList(dateValueList);
        gaodr.setTotalAmount(totalAmount);
        gaodr.setBigDecimalList(bigDecimalList);

        //返回
        return gaodr;
    }

    @Override
    public UserMerIdOrderDetailsResponse getUserMerIdOrderInfoStatisticsData(Integer raId,Integer dateType) {
        //得到-区域代理信息
        RegionalAgency regionalAgency=regionalAgencyService.getById(raId);
        if(regionalAgency == null) return  new UserMerIdOrderDetailsResponse();
        return this.getMerIdOrderInfoStatisticsData(regionalAgency.getId(),dateType);
    }

    @Override
    public User getUserException(Integer uid) {
        User user= userDao.selectById(uid);
        if(user == null){
            throw new CrmebException("该用户不存在！");
        }
        return user;
    }

    @Override
    public Map<String, Object> sms(UserSmsRequest request) {
        Map<String, Object> map=new HashMap<>();
        List<String> phons=Arrays.asList(request.getUidsOrPhons().split(","));
        if(request.getUserType() == 1){
            List<User> userList= userDao.selectBatchIds(phons);
            phons = userList.stream().map(User::getPhone).collect(Collectors.toList());
        }

        if(request.getModelId() == null || request.getModelId()<=0){
            request.setModelId(SmsConstants.SMS_CONFIG_MAA_TONZHI_SMS_TEMP_ID);
        }

        HashMap<String,Object> mapParam=new HashMap<>();
        if(StringUtils.isNotBlank(request.getContentParam())){
            String[] param=request.getContentParam().split(",");
            try{
                switch (request.getType()){
                    case 0:
                        mapParam.put("title",param[0]);
                        mapParam.put("date",param[1]);
                        break;
                }
            }catch (Exception e){
                throw new CrmebException("参数不正确！");
            }
        }

        int okNum=0,failNum=0;
        for (String phone:phons) {
            smsService.push(phone, String.valueOf(request.getType()), request.getModelId(), mapParam);
            okNum++;
        }

        map.put("okNum",okNum);
        map.put("failNum",failNum);
        return map;
    }
}
