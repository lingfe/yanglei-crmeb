package com.zbkj.crmeb.combination.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.PayConstants;
import com.constants.UserConstants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.DateUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.combination.dao.StorePinkDao;
import com.zbkj.crmeb.combination.model.StoreCombination;
import com.zbkj.crmeb.combination.model.StorePink;
import com.zbkj.crmeb.combination.request.StorePinkSearchRequest;
import com.zbkj.crmeb.combination.response.StorePinkAdminListResponse;
import com.zbkj.crmeb.combination.response.StorePinkDetailResponse;
import com.zbkj.crmeb.combination.service.StoreCombinationService;
import com.zbkj.crmeb.combination.service.StorePinkService;
import com.zbkj.crmeb.front.request.OrderRefundApplyRequest;
import com.zbkj.crmeb.front.service.OrderService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserToken;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.user.service.UserTokenService;
import com.zbkj.crmeb.wechat.service.TemplateMessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StorePinkService ?????????
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB???????????????????????????????????? ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB????????????????????????????????????????????????CRMEB????????????
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class StorePinkServiceImpl extends ServiceImpl<StorePinkDao, StorePink> implements StorePinkService {

    @Resource
    private StorePinkDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreCombinationService storeCombinationService;

    @Autowired
    private TemplateMessageService templateMessageService;

    @Autowired
    private UserTokenService userTokenService;


    /**
    * ??????
    * @param request ????????????
    * @param pageParamRequest ???????????????
    * @author HZW
    * @since 2020-11-13
    * @return List<StorePink>
    */
    @Override
    public PageInfo<StorePinkAdminListResponse> getList(StorePinkSearchRequest request, PageParamRequest pageParamRequest) {
        Page<StorePink> pinkPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StorePink> lqw = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(request.getStatus())) {
            lqw.eq(StorePink::getStatus, request.getStatus());
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
            lqw.between(StorePink::getAddTime, DateUtil.dateStr2Timestamp(dateLimit.getStartTime(), Constants.DATE_TIME_TYPE_BEGIN), DateUtil.dateStr2Timestamp(dateLimit.getEndTime(), Constants.DATE_TIME_TYPE_END));
        }
        lqw.eq(StorePink::getKId, 0);
        lqw.orderByDesc(StorePink::getId);
        List<StorePink> storePinks = dao.selectList(lqw);
        if (CollUtil.isEmpty(storePinks)) {
            return CommonPage.copyPageInfo(pinkPage, CollUtil.newArrayList());
        }
        List<StorePinkAdminListResponse> list = storePinks.stream().map(pink -> {
            StorePinkAdminListResponse storePinkResponse = new StorePinkAdminListResponse();
            BeanUtils.copyProperties(pink, storePinkResponse);
            Integer countPeople = getCountByKidAndCid(pink.getCid(), pink.getId());
            storePinkResponse.setCountPeople(countPeople);
            storePinkResponse.setAddTime(DateUtil.timestamp2DateStr(pink.getAddTime(), Constants.DATE_FORMAT));
            storePinkResponse.setStopTime(DateUtil.timestamp2DateStr(pink.getStopTime(), Constants.DATE_FORMAT));
            StoreCombination combination = storeCombinationService.getById(pink.getCid());
            storePinkResponse.setTitle(combination.getTitle());
            return storePinkResponse;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pinkPage, list);
    }

    /**
     * ??????????????????cid
     * @param cid ????????????id
     * @return
     */
    @Override
    public List<StorePink> getListByCid(Integer cid) {
        LambdaQueryWrapper<StorePink> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StorePink::getCid, cid);
        lqw.orderByDesc(StorePink::getId);
        return dao.selectList(lqw);
    }

    /**
     * ??????????????????
     * @param storePink
     * @return
     */
    @Override
    public List<StorePink> getByEntity(StorePink storePink) {
        LambdaQueryWrapper<StorePink> lqw = Wrappers.lambdaQuery();
        lqw.setEntity(storePink);
        return dao.selectList(lqw);
    }

    /**
     * PC??????????????????
     * @param pinkId ??????pinkId
     * @return
     */
    @Override
    public List<StorePinkDetailResponse> getAdminList(Integer pinkId) {
        LambdaQueryWrapper<StorePink> lqw = Wrappers.lambdaQuery();
        lqw.eq(StorePink::getId, pinkId).or().eq(StorePink::getKId, pinkId);
        lqw.orderByDesc(StorePink::getId);
        List<StorePink> pinkList = dao.selectList(lqw);
        // ????????????????????????????????????
        List<StorePinkDetailResponse> responseList = pinkList.stream().map(pink -> {
            StorePinkDetailResponse response = new StorePinkDetailResponse();
            BeanUtils.copyProperties(pink, response);
            StoreOrder storeOrder = storeOrderService.getByOderId(pink.getOrderId());
            if (ObjectUtil.isNotNull(storeOrder)) {
                response.setOrderStatus(storeOrder.getStatus());
                response.setRefundStatus(storeOrder.getRefundStatus());
            }
            return response;
        }).collect(Collectors.toList());
        return responseList;
    }

    @Override
    public List<StorePink> getListByCidAndKid(Integer cid, Integer kid) {
        LambdaQueryWrapper<StorePink> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StorePink::getCid, cid);
        lqw.eq(StorePink::getKId, kid);
        lqw.eq(StorePink::getIsRefund, false);
        lqw.orderByDesc(StorePink::getId);
        return dao.selectList(lqw);
    }

    @Override
    public Integer getCountByKid(Integer pinkId) {
        LambdaQueryWrapper<StorePink> lqw = new LambdaQueryWrapper<>();
        lqw.select(StorePink::getId);
        lqw.eq(StorePink::getIsRefund, false);
        lqw.and(i -> i.eq(StorePink::getKId, pinkId).or().eq(StorePink::getId, pinkId));
        return dao.selectCount(lqw);
    }

    /**
     * ???????????????????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void detectionStatus() {
        // ???????????????????????????????????????????????????????????????
        LambdaQueryWrapper<StorePink> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StorePink::getStatus, 1);
        lqw.eq(StorePink::getKId, 0);
        lqw.le(StorePink::getStopTime, System.currentTimeMillis());
        List<StorePink> headList = dao.selectList(lqw);
        if (CollUtil.isEmpty(headList)) {
            return ;
        }
        /**
         * 1.????????????????????????
         * 2.?????????????????????
         * 3.????????????????????????????????????????????????
         */
        List<StorePink> pinkSuccessList = CollUtil.newArrayList();
        List<StorePink> pinkFailList = CollUtil.newArrayList();
        List<OrderRefundApplyRequest> applyList = CollUtil.newArrayList();
        for (StorePink headPink : headList) {
            // ????????????
            List<StorePink> memberList = getListByCidAndKid(headPink.getCid(), headPink.getId());
            memberList.add(headPink);
            if (headPink.getPeople().equals(memberList.size())) {
                memberList.forEach(i -> i.setStatus(2));
                pinkSuccessList.addAll(memberList);
                continue;
            }
            // ?????????????????????????????????????????????
            StoreCombination storeCombination = storeCombinationService.getById(headPink.getCid());
            Integer virtual = storeCombination.getVirtualRation();// ??????????????????
            if (headPink.getPeople() <= memberList.size() + virtual) {
                // ??????????????????
                memberList.forEach(i -> i.setStatus(2).setIs_virtual(true));
                pinkSuccessList.addAll(memberList);
                continue;
            }
            // ??????
            headPink.setStatus(3);
            // ??????????????????
            OrderRefundApplyRequest refundRequest = new OrderRefundApplyRequest();
            refundRequest.setId(headPink.getOrderIdKey());
            refundRequest.setText("?????????????????????????????????");
            refundRequest.setExplain("???????????????????????????????????????");
            pinkFailList.add(headPink);
            applyList.add(refundRequest);

            // ????????????
            if (CollUtil.isNotEmpty(memberList)) {
                memberList.forEach(i -> i.setStatus(3));
                List<OrderRefundApplyRequest> tempApplyList = memberList.stream().map(i -> {
                    OrderRefundApplyRequest tempRefundRequest = new OrderRefundApplyRequest();
                    tempRefundRequest.setId(headPink.getOrderIdKey());
                    tempRefundRequest.setText("?????????????????????????????????");
                    tempRefundRequest.setExplain("???????????????????????????????????????");
                    return tempRefundRequest;
                }).collect(Collectors.toList());
                pinkFailList.addAll(memberList);
                applyList.addAll(tempApplyList);
            }
        }
        if (CollUtil.isNotEmpty(pinkFailList) && pinkFailList.size() > 0) {
            boolean failUpdate = updateBatchById(pinkFailList, 100);
            if (!failUpdate) throw new CrmebException("?????????????????????????????????????????????????????????");
        }
        if (applyList.size() > 0) {
            boolean task = orderService.refundApplyTask(applyList);
            if (!task) throw new CrmebException("???????????????,????????????????????????");
        }
        if (CollUtil.isNotEmpty(pinkSuccessList) && pinkSuccessList.size() > 0) {
            boolean successUpdate = updateBatchById(pinkSuccessList, 100);
            if (!successUpdate) throw new CrmebException("??????????????????????????????????????????????????????");
            pinkSuccessList.forEach(i -> {
                StoreOrder storeOrder = storeOrderService.getByOderId(i.getOrderId());
                StoreCombination storeCombination = storeCombinationService.getById(i.getCid());
                User tempUser = userService.getById(i.getUid());
                // ????????????????????????
                MyRecord record = new MyRecord();
                record.set("orderNo", storeOrder.getOrderId());
                record.set("proName", storeCombination.getTitle());
                record.set("payType", storeOrder.getPayType());
                record.set("isChannel", storeOrder.getIsChannel());
                pushMessageOrder(record, tempUser);
            });
        }
    }

    /**
     * ??????????????????
     * @param record ??????
     * @param user ????????????
     */
    private void pushMessageOrder(MyRecord record, User user) {
        //??????-????????????
        if (!record.getStr("payType").equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            return ;
        }

        //??????-????????????
        if (record.getInt("isChannel").equals(2)) {
            return ;
        }

        UserToken userToken;
        HashMap<String, String> temMap = new HashMap<>();

        // ?????????
        if (record.getInt("isChannel").equals(PayConstants.ORDER_PAY_CHANNEL_PUBLIC)) {
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            // ????????????????????????
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "??????????????????????????????????????????????????????");
            temMap.put("keyword1", record.getStr("orderNo"));
            temMap.put("keyword2", record.getStr("proName"));
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "?????????????????????");
            templateMessageService.pushTemplateMessage(Constants.WE_CHAT_TEMP_KEY_COMBINATION_SUCCESS, temMap, userToken.getToken());
            return;
        }

        // ???????????????????????????
        userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
        if (ObjectUtil.isNull(userToken)) {
            return ;
        }

        // ????????????
        temMap.put("character_string1",  record.getStr("orderNo"));
        temMap.put("thing2", record.getStr("proName"));
        temMap.put("thing5", "??????????????????????????????????????????????????????");
        templateMessageService.pushMiniTemplateMessage(Constants.WE_CHAT_PROGRAM_TEMP_KEY_COMBINATION_SUCCESS, temMap, userToken.getToken());


    }

    /**
     * ????????????
     * @param kid
     * @return
     */
    @Override
    public boolean pinkSuccess(Integer kid) {
        if (ObjectUtil.isNull(kid)) {
            return false;
        }
        StorePink teamPink = getById(kid);
        List<StorePink> memberList = getListByCidAndKid(teamPink.getCid(), kid);
        long timeMillis = System.currentTimeMillis();
        memberList.add(teamPink);
        memberList.forEach(i -> {
            i.setStatus(2);
            i.setStopTime(timeMillis);
        });
        return updateBatchById(memberList);
    }

    /**
     * ????????????????????????
     * @param orderId
     * @return
     */
    @Override
    public StorePink getByOrderId(String orderId) {
        LambdaQueryWrapper<StorePink> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StorePink::getOrderId, orderId);
        return dao.selectOne(lqw);
    }

    /**
     * ????????????3?????????????????????????????????
     * @return List
     */
    @Override
    public List<StorePink> findSizePink(Integer size) {
        LambdaQueryWrapper<StorePink> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StorePink::getIsRefund, false);
        lqw.in(StorePink::getStatus, 1, 2);
        lqw.groupBy(StorePink::getUid);
        lqw.orderByDesc(StorePink::getId);
        lqw.last(" limit " + size);
        return dao.selectList(lqw);
    }

    /**
     * ???????????????????????????
     * @return Integer
     */
    @Override
    public Integer getTotalPeople() {
        LambdaQueryWrapper<StorePink> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StorePink::getIsRefund, false);
        lqw.in(StorePink::getStatus, 1, 2);
        return dao.selectCount(lqw);
    }

    /**
     * ??????????????????
     * @return StorePink
     */
    @Override
    public StorePink getByUidAndKid(Integer uid, Integer kid) {
        LambdaQueryWrapper<StorePink> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StorePink::getUid, uid);
        lqw.in(StorePink::getKId, kid);
        return dao.selectOne(lqw);
    }

    private Integer getCountByKidAndCid(Integer cid, Integer kid) {
        LambdaQueryWrapper<StorePink> lqw = new LambdaQueryWrapper<>();
        lqw.select(StorePink::getId);
        lqw.eq(StorePink::getCid, cid);
        lqw.and(i -> i.eq(StorePink::getKId, kid).or().eq(StorePink::getId, kid));
        lqw.eq(StorePink::getIsRefund, false);
        return dao.selectCount(lqw);
    }
}

