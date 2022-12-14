package com.zbkj.crmeb.finance.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.PayConstants;
import com.constants.WeChatConstants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.utils.RestTemplateUtil;
import com.utils.XmlUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.finance.dao.UserRechargeDao;
import com.zbkj.crmeb.finance.model.UserRecharge;
import com.zbkj.crmeb.finance.request.UserRechargeRefundRequest;
import com.zbkj.crmeb.finance.request.UserRechargeSearchRequest;
import com.zbkj.crmeb.finance.response.UserRechargeResponse;
import com.zbkj.crmeb.finance.service.UserRechargeService;
import com.zbkj.crmeb.front.request.UserRechargeRequest;
import com.zbkj.crmeb.payment.vo.wechat.WxRefundResponseVo;
import com.zbkj.crmeb.payment.vo.wechat.WxRefundVo;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.service.UserBillService;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
* UserRechargeServiceImpl ????????????
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB???????????????????????????????????? ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB????????????????????????????????????????????????CRMEB????????????
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
@Service
public class UserRechargeServiceImpl extends ServiceImpl<UserRechargeDao, UserRecharge> implements UserRechargeService {

    @Resource
    private UserRechargeDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserBillService userBillService;


    /**
    * ??????
    * @param request ????????????
    * @param pageParamRequest ???????????????
    * @author Mr.Zhang
    * @since 2020-05-11
    * @return List<UserRecharge>
    */
    @Override
    public PageInfo<UserRechargeResponse> getList(UserRechargeSearchRequest request, PageParamRequest pageParamRequest) {
        Page<UserRecharge> userRechargesList = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
        //??? UserExtract ?????????????????????
        LambdaQueryWrapper<UserRecharge> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(request.getUid()) && request.getUid() > 0) {
            lambdaQueryWrapper.eq(UserRecharge::getUid, request.getUid());
        }
        if(!StringUtils.isBlank(request.getKeywords())){
            lambdaQueryWrapper.and(i -> i.
                    or().like(UserRecharge::getOrderId, request.getKeywords()) //?????????
            );
        }
        //????????????
        if(request.getPaid() != null){
            lambdaQueryWrapper.eq(UserRecharge::getPaid, request.getPaid());
        }

        //????????????
        if(dateLimit.getStartTime() != null && dateLimit.getEndTime() != null){
            //????????????
            int compareDateResult = DateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), Constants.DATE_FORMAT);
            if(compareDateResult == -1){
                throw new CrmebException("???????????????????????????????????????");
            }

            lambdaQueryWrapper.between(UserRecharge::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        lambdaQueryWrapper.orderByDesc(UserRecharge::getId);
        List<UserRechargeResponse> responses = new ArrayList<>();
        List<UserRecharge> userRecharges = dao.selectList(lambdaQueryWrapper);
        List<Integer> userIds = userRecharges.stream().map(UserRecharge::getUid).collect(Collectors.toList());
        HashMap<Integer, User> userHashMap = new HashMap<>();

        if(userIds.size() > 0)
        userHashMap = userService.getMapListInUid(userIds);

        HashMap<Integer, User> finalUserHashMap = userHashMap;
        userRecharges.stream().map(e-> {
            User user = finalUserHashMap.get(e.getUid());
            UserRechargeResponse r = new UserRechargeResponse();
            BeanUtils.copyProperties(e,r);
            if(null != user){
                r.setAvatar(user.getAvatar());
                r.setNickname(user.getNickname());
            }
            responses.add(r);
            return e;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(userRechargesList,responses);
    }

    /**
     * ???????????????
     * @author Mr.Zhang
     * @since 2020-05-11
     * @return HashMap<String, BigDecimal>
     */
    @Override
    public HashMap<String, BigDecimal> getBalanceList() {
        HashMap<String, BigDecimal> map = new HashMap<>();

        BigDecimal routine = dao.getSumByType("routine");
        if(null == routine) routine = BigDecimal.ZERO;
        map.put("routine", routine); //???????????????

//        BigDecimal weChat = dao.getSumByType("weixin");
        BigDecimal weChat = dao.getSumByType("public");
        if(null == weChat) weChat = BigDecimal.ZERO;
        map.put("weChat", weChat); //???????????????

        BigDecimal total = dao.getSumByType("");
        if(null == total) total = BigDecimal.ZERO;
        map.put("total", total); //?????????

        BigDecimal refund = dao.getSumByRefund();
        if(null == refund) refund = BigDecimal.ZERO;
        map.put("refund", refund);

        map.put("other", total.subtract(routine).subtract(weChat)); //????????????

        return map;
    }

    /**
     * ????????????????????????
     * @author Mr.Zhang
     * @since 2020-05-11
     * @return UserRecharge
     */
    @Override
    public UserRecharge getInfoByEntity(UserRecharge userRecharge) {
        LambdaQueryWrapper<UserRecharge> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.setEntity(userRecharge);
        return dao.selectOne(lambdaQueryWrapper);
    }

    /**
     * ??????????????????
     * @author Mr.Zhang
     * @since 2020-05-11
     * @return UserRecharge
     */
    @Override
    public UserRecharge create(UserRechargeRequest request) {
        UserRecharge userRecharge = new UserRecharge();
        userRecharge.setUid(request.getUserId());
        userRecharge.setOrderId(CrmebUtil.getOrderNo(PayConstants.PAY_TYPE_WE_CHAT));
        userRecharge.setPrice(request.getPrice());
        userRecharge.setGivePrice(request.getGivePrice());
        userRecharge.setRechargeType(request.getFromType());
        save(userRecharge);
        return userRecharge;
    }

    /**
     * ??????????????????
     * @param userRecharge UserRecharge ??????????????????
     * @author Mr.Zhang
     * @since 2020-07-01
     */
    @Override
    public Boolean complete(UserRecharge userRecharge) {
        try{
            userRecharge.setPaid(true);
            userRecharge.setPayTime(DateUtil.nowDateTime());
            return updateById(userRecharge);
        }catch (Exception e){
            throw new CrmebException("???????????????, ?????????????????????");
        }
    }

    /**
     * ????????????
     * @param request ????????????
     */
    @Override
    public Boolean refund(UserRechargeRefundRequest request) {
        UserRecharge userRecharge = getById(request.getId());
        if (ObjectUtil.isNull(userRecharge)) throw new CrmebException("??????????????????");
        if (!userRecharge.getPaid()) throw new CrmebException("???????????????");
        if (userRecharge.getPrice().compareTo(userRecharge.getRefundPrice()) == 0) {
            throw new CrmebException("?????????????????????!??????????????????");
        }
        if (userRecharge.getRechargeType().equals("balance")) {
            throw new CrmebException("?????????????????????????????????");
        }

        User user = userService.getById(userRecharge.getUid());
        if (ObjectUtil.isNull(user)) throw new CrmebException("??????????????????");

        // ????????????
        BigDecimal refundPrice;
        if (request.getType().equals(2)) {// ??????+??????
            refundPrice = userRecharge.getPrice().add(userRecharge.getGivePrice());
        } else {
            refundPrice = userRecharge.getPrice();
        }

        // ??????????????????????????????
        try {
            if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_PUBLIC)) {// ?????????
                refundJSAPI(userRecharge);
            } else {// ?????????
                refundMiniWx(userRecharge);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CrmebException("???????????????????????????");
        }

        //??????--
        userRecharge.setRefundPrice(userRecharge.getPrice());
        user.setNowMoney(user.getNowMoney().subtract(refundPrice));

        //2021-12-21 ????????????
        UserBill userBill = userBillService.getUserBill(    // ??????????????????-????????????
                userRecharge.getUid(),
                userRecharge.getOrderId(),
                Constants.USER_BILL_PM_0,
                Constants.USER_BILL_CATEGORY_MONEY,
                Constants.USER_BILL_TYPE_USER_RECHARGE_REFUND,
                refundPrice,user.getNowMoney(),
                "");

        //??????
        Boolean execute = transactionTemplate.execute(e -> {
            // ??????-????????????
            this.updateById(userRecharge);

            // ??????-??????
            userService.operationNowMoney(user.getUid(), refundPrice, user.getNowMoney(), "sub");

            // ??????-????????????
            userBillService.save(userBill);
            return Boolean.TRUE;
        });

        //????????????
        if (!execute) throw new CrmebException("????????????-???????????????????????????");
        // ??????????????????????????????
        return execute;
    }

    /**
     * ??????????????????????????????
     * @param uid ??????uid
     * @return BigDecimal
     */
    @Override
    public BigDecimal getTotalRechargePrice(Integer uid) {
        QueryWrapper<UserRecharge> query = Wrappers.query();
        query.select("IFNULL(SUM(price), 0) as price, IFNULL(SUM(give_price), 0) as give_price");
        query.eq("paid", 1);
        query.eq("uid", uid);
        UserRecharge userRecharge = dao.selectOne(query);
        return userRecharge.getPrice().add(userRecharge.getGivePrice());
    }

    /**
     * ???????????????
     */
    private WxRefundResponseVo refundMiniWx(UserRecharge userRecharge) {
        WxRefundVo wxRefundVo = new WxRefundVo();

        String appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_ID);
        String mchId = systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_ROUTINE_MCH_ID);
        wxRefundVo.setAppid(appId);
        wxRefundVo.setMch_id(mchId);
        wxRefundVo.setNonce_str(DigestUtils.md5Hex(CrmebUtil.getUuid() + CrmebUtil.randomCount(111111, 666666)));
        wxRefundVo.setOut_trade_no(userRecharge.getOrderId());
        wxRefundVo.setOut_refund_no(userRecharge.getOrderId());
        wxRefundVo.setTotal_fee(userRecharge.getPrice().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        wxRefundVo.setRefund_fee(userRecharge.getPrice().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        String signKey = systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_ROUTINE_APP_KEY);
        String sign = CrmebUtil.getSign(CrmebUtil.objectToMap(wxRefundVo), signKey);
        wxRefundVo.setSign(sign);
        String path = systemConfigService.getValueByKeyException("pay_mini_client_p12");
        return commonRefound(wxRefundVo, path);

    }

    /**
     * ???????????????
     */
    private WxRefundResponseVo refundJSAPI(UserRecharge userRecharge) {
        WxRefundVo wxRefundVo = new WxRefundVo();

        String appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
        String mchId = systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
        wxRefundVo.setAppid(appId);
        wxRefundVo.setMch_id(mchId);
        wxRefundVo.setNonce_str(DigestUtils.md5Hex(CrmebUtil.getUuid() + CrmebUtil.randomCount(111111, 666666)));
        wxRefundVo.setOut_trade_no(userRecharge.getOrderId());
        wxRefundVo.setOut_refund_no(userRecharge.getOrderId());
        wxRefundVo.setTotal_fee(userRecharge.getPrice().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        wxRefundVo.setRefund_fee(userRecharge.getPrice().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        String signKey = systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
        String sign = CrmebUtil.getSign(CrmebUtil.objectToMap(wxRefundVo), signKey);
        wxRefundVo.setSign(sign);
        String path = systemConfigService.getValueByKeyException("pay_routine_client_p12");
        return commonRefound(wxRefundVo, path);
    }

    /**
     * ??????????????????
     */
    private WxRefundResponseVo commonRefound(WxRefundVo wxRefundVo, String path) {
        String xmlStr = XmlUtil.objectToXml(wxRefundVo);
        String url = WeChatConstants.PAY_API_URL + WeChatConstants.PAY_REFUND_API_URI_WECHAT;
        HashMap<String, Object> map = CollUtil.newHashMap();
        String xml = "";
        System.out.println("??????????????????xmlStr = " + xmlStr);
        try {
            xml = restTemplateUtil.postWXRefundXml(url, xmlStr, wxRefundVo.getMch_id(), path);
            map = XmlUtil.xmlToMap(xml);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CrmebException("xmlToMap?????????xml = " + xml);
        }
        if(null == map){
            throw new CrmebException("?????????????????????");
        }

        WxRefundResponseVo responseVo = CrmebUtil.mapToObj(map, WxRefundResponseVo.class);
        if(responseVo.getReturnCode().toUpperCase().equals("FAIL")){
            throw new CrmebException("??????????????????1???" +  responseVo.getReturnMsg());
        }

        if(responseVo.getResultCode().toUpperCase().equals("FAIL")){
            throw new CrmebException("??????????????????2???" + responseVo.getErrCodeDes());
        }
        System.out.println("================????????????????????????=========================");
        System.out.println("xml = " + xml);
        return responseVo;
    }
}

