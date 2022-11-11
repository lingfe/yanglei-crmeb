package com.zbkj.crmeb.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.PayConstants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.DateUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.finance.request.FundsMonitorRequest;
import com.zbkj.crmeb.finance.request.FundsMonitorSearchRequest;
import com.zbkj.crmeb.front.response.UserSpreadCommissionResponse;
import com.zbkj.crmeb.store.request.StoreOrderRefundRequest;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.user.dao.UserBillDao;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.response.BillType;
import com.zbkj.crmeb.user.response.UserBillResponse;
import com.zbkj.crmeb.user.service.UserBillService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户账单记录-service接口实现类
 * @author: 零风
 * @CreateDate: 2021/12/23 17:04
 */
@Service
public class UserBillServiceImpl extends ServiceImpl<UserBillDao, UserBill> implements UserBillService {

    @Resource
    private UserBillDao dao;

    @Autowired
    private StoreOrderService storeOrderService;

    private Page<UserBill> userBillPage;


    @Override
    public UserBill getUserBill(Integer uid, String linkId,
                                int pm, String category, String type,
                                BigDecimal number, BigDecimal balance,String other) {
        //实例化对象
        UserBill userBill=new UserBill(); // 公共账单对象
        userBill.setUid(uid);
        userBill.setLinkId(linkId);
        userBill.setPm(pm);
        userBill.setNumber(number);
        userBill.setBalance(balance);
        userBill.setCategory(category);

        //验证类型
        String  title = "";
        String  mark = "";
        switch (type){
            case Constants.USER_BILL_TYPE_USER_RECHARGE_REFUND:
                title="系统充值退款";
                mark = StrUtil.format("{},退款给用户{}元",title, number);
                break;
            case Constants.USER_BILL_TYPE_TRANSFER_IN:
                title = "佣金转余额";
                mark = StrUtil.format("{},增加{}",title, number);
                break;
            case Constants.USER_BILL_TYPE_PAY_ORDER:
                switch (category){
                    case PayConstants.PAY_TYPE_INTEGRAL:
                        title = storeOrderService.getOrderPayTypeStr(category);
                        mark = StrUtil.format("{},兑换消耗{}个酒米！",title,number);
                        break;
                    case Constants.USER_BILL_CATEGORY_EXPERIENCE:
                        title = Constants.ORDER_LOG_MESSAGE_PAY_SUCCESS;
                        mark = StrUtil.format("{},增加{}经验值",title,number);
                        break;
                    case PayConstants.PAY_TYPE_YUE:
                    case PayConstants.PAY_TYPE_WE_CHAT:
                    case PayConstants.PAY_TYPE_ALI_PAY:
                    case PayConstants.PAY_TYPE_ZERO_PAY:
                    case PayConstants.PAY_TYPE_OFFLINE:
                    case PayConstants.PAY_TYPE_BANK:
                    case PayConstants.PAY_TYPE_OTHER:
                        title = storeOrderService.getOrderPayTypeStr(category);
                        mark = StrUtil.format("{},支付{}元",title,number);
                        break;
                    default:
                        title = "未知支付方式";
                        mark = StrUtil.format("{},支付{}元",title,number);
                        break;
                }
                break;
            case Constants.USER_BILL_TYPE_RETAILER_ORDER:
                title = "零售商订单";
                mark = StrUtil.format("{},转入余额{}元",title, number);
                break;
            case Constants.USER_BILL_TYPE_PAY_RECHARGE:
                title = "充值支付";
                mark = StrUtil.format("{},余额增加了{}元",title, number);
                break;
            case Constants.USER_BILL_TYPE_RA_ORDER_settlement:
                title = "区域代理结算订单";
                mark = StrUtil.format("{},转入余额{}元",title, number);
                break;
            case Constants.USER_BILL_TYPE_SUPPLIER_ORDER_settlement:
                title = "供应商结算订单";
                mark = StrUtil.format("{},转入余额{}元",title, number);
                break;
            case Constants.USER_BILL_TYPE_PAY_PRODUCT_REFUND:
                switch (category){
                    case Constants.USER_BILL_CATEGORY_EXPERIENCE:
                        title = "订单退款,退还经验值";
                        mark = StrUtil.format("{},扣除{}经验值",title, number);
                        break;
                    case Constants.USER_BILL_CATEGORY_MONEY:
                        title = "订单退款,退还余额";
                        mark = StrUtil.format("{},订单退款到余额{}元",title, number);
                        break;
                }
                break;
            case Constants.USER_BILL_TYPE_SYSTEM_ADD:
                title = "系统操作,余额增加";
                mark = StrUtil.format("{},后台操作余额增加了{}元",title, number);
                break;
            case Constants.USER_BILL_TYPE_SYSTEM_SUB:
                title = "系统操作,余额减少";
                mark = StrUtil.format("{},后台操作余额减少了{}元",title, number);
                break;
            case Constants.USER_BILL_TYPE_SIGN:
                title = "签到";
                mark = StrUtil.format("{},签到经验奖励增加了{}经验值",title, number);
                break;
            case Constants.USER_BILL_TYPE_SUPPLEMENTARY_SIGNATURE:
                title = "补签";
                mark = StrUtil.format("{},补签经验奖励增加了{}经验值",title, number);
                break;
            case Constants.USER_BILL_TYPE_EXTRACT:
                title = "提现";
                mark = StrUtil.format("{},余额减少了{}元！",title, number);
                break;
            case Constants.USER_BILL_TYPE_EXTRACT_NO:
                title = "提现不通过退还(包含服务费)";
                mark = StrUtil.format("{},余额增加了{}元！",title, number);
                break;
            case Constants.USER_BILL_TYPE_EXTRACT_FEE:
                title = "提现服务费";
                mark = StrUtil.format("{},提现扣除了{}元！",title, number);
                break;
            case Constants.USER_BILL_TYPE_payProfitSharingIntegral:
                title = "积分支付订单分账-积分转余额";
                mark = StrUtil.format("{},余额增加了{}元！",title, number);
                break;
            case Constants.USER_BILL_TYPE_integralTransferIn:
                title = "积分转入余额";
                mark = StrUtil.format("{},余额增加了{}元！",title, number);
                break;
            case Constants.USER_BILL_TYPE_isAllianceMerchants:
                title = "收到【"+other+"】扫码转米转入余额！";
                mark = StrUtil.format("{},联盟商家余额增加了{}元！",title, number);
                break;
            case Constants.USER_BILL_TYPE_LMSJDTGJMZRKTXYE:
                title = "收到【"+other+"】推广酒米转入余额!";
                mark = StrUtil.format("{},联盟商家余额增加了{}元！",title, number);
                break;
            case Constants.USER_BILL_TYPE_zhuanruketixianzhanghu:
                title = "收到【"+other+"】推广酒米一半转入余额!";
                mark = StrUtil.format("{},专属推广者余额增加了{}元！",title, number);
                break;
        }
        userBill.setTitle(title);
        userBill.setMark(mark);
        userBill.setStatus(1);
        userBill.setCreateTime(DateUtil.nowDateTime());
        return userBill;
    }

    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-04-28
    * @return List<UserBill>
    */
    @Override
    public List<UserBill> getList(FundsMonitorSearchRequest request, PageParamRequest pageParamRequest) {
        userBillPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        QueryWrapper<UserBill> queryWrapper = new QueryWrapper<>();
        getMonthSql(request, queryWrapper);

        //排序
        if(request.getSort() == null){
            queryWrapper.orderByDesc("create_time");
        }else{
            if(request.getSort().equals("asc")){
                queryWrapper.orderByAsc("number");
            }else{
                queryWrapper.orderByDesc("number");
            }
        }

        // 查询类型
        if(StringUtils.isNotBlank(request.getCategory())){
            queryWrapper.eq("category", request.getCategory());
        }
        if (ObjectUtil.isNotNull(request.getPm())) {
            queryWrapper.eq("pm", request.getPm());
        }

        return dao.selectList(queryWrapper);
    }

    private void getMonthSql(FundsMonitorSearchRequest request, QueryWrapper<UserBill> queryWrapper){
        queryWrapper.gt("status", 0); // -1无效
        if(!StringUtils.isBlank(request.getKeywords())){
            queryWrapper.and(i -> i.
                    or().eq("id", request.getKeywords()).   //用户账单id
                    or().eq("uid", request.getKeywords()). //用户uid
                    or().eq("link_id", request.getKeywords()). //关联id
                    or().like("title", request.getKeywords()) //账单标题
            );
        }

        //时间范围
        if(StringUtils.isNotBlank(request.getDateLimit())){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
            //判断时间
            int compareDateResult = DateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), Constants.DATE_FORMAT);
            if(compareDateResult == -1){
                throw new CrmebException("开始时间不能大于结束时间！");
            }

            queryWrapper.between("create_time", dateLimit.getStartTime(), dateLimit.getEndTime());

            //资金范围
            if(request.getMax() != null && request.getMin() != null){
                //判断时间
                if(request.getMax().compareTo(request.getMin()) < 0){
                    throw new CrmebException("最大金额不能小于最小金额！");
                }
                queryWrapper.between("number", request.getMin(), request.getMax());
            }
        }


        //关联id
        if(StringUtils.isNotBlank(request.getLinkId())){
            if(request.getLinkId().equals("gt")){
                queryWrapper.ne("link_id", 0);
            }else{
                queryWrapper.eq("link_id", request.getLinkId());
            }
        }

        //用户id集合
        if(null != request.getUserIdList() && request.getUserIdList().size() > 0){
            queryWrapper.in("uid", request.getUserIdList());
        } else if (ObjectUtil.isNotNull(request.getUid())) {
            queryWrapper.eq("uid", request.getUid());
        }



        if(StringUtils.isNotBlank(request.getCategory())){
            queryWrapper.eq("category", request.getCategory());
        }

        if(StringUtils.isNotBlank(request.getType())){
            queryWrapper.eq("type", request.getType());
        }
    }

    /**
     * 列表
     * @param request 请求参数
     * @param pageParamRequest 分页类参数
     * @author Mr.Zhang
     * @since 2020-04-28
     * @return List<UserBill>
     */
    @Override
    public PageInfo<UserBillResponse> getListAdmin(FundsMonitorSearchRequest request, PageParamRequest pageParamRequest) {
        userBillPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        Map<String, Object> map = new HashMap<>();
        if (StrUtil.isNotBlank(request.getKeywords())) {
            map.put("keywords", "%"+request.getKeywords()+"%");
        }
        if (StrUtil.isNotBlank(request.getCategory())) {
            map.put("category", request.getCategory());
        }
        //时间范围
        if(StrUtil.isNotBlank(request.getDateLimit())){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
            //判断时间
            int compareDateResult = DateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), Constants.DATE_FORMAT);
            if(compareDateResult == -1){
                throw new CrmebException("开始时间不能大于结束时间！");
            }

            map.put("startTime", dateLimit.getStartTime());
            map.put("endTime", dateLimit.getEndTime());
        }
        if (CollUtil.isNotEmpty(request.getUserIdList())) {
            map.put("userIdList", request.getUserIdList());
        }

        List<UserBillResponse> responses = dao.getListAdminAndIntegeal(map);
        return CommonPage.copyPageInfo(userBillPage, responses);
    }

    /**
     * 新增/消耗 总数
     * @param pm Integer 0 = 支出 1 = 获得
     * @param userId Integer 用户uid
     * @param category String 类型
     * @param date String 时间范围
     * @param type String 小类型
     * @author Mr.Zhang
     * @since 2020-05-29
     * @return UserBill
     */
    @Override
    public Integer getSumInteger(Integer pm, Integer userId, String category, String date, String type) {
        QueryWrapper<UserBill> queryWrapper = new QueryWrapper<>();


        queryWrapper.select("sum(number) as number").
                eq("category", category).
                eq("uid", userId).
                eq("status", 1);
        if(null != pm){
            queryWrapper.eq("pm", pm);
        }
        if(null != type){
            queryWrapper.eq("type", type);
        }
        if(null != date){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            queryWrapper.between("create_time", dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        UserBill userBill = dao.selectOne(queryWrapper);
        if(null == userBill || null == userBill.getNumber()){
            return 0;
        }
        return userBill.getNumber().intValue();
    }

    /**
     * 新增/消耗  总金额
     * @param pm Integer 0 = 支出 1 = 获得
     * @param userId Integer 用户uid
     * @param category String 类型
     * @param date String 时间范围
     * @param type String 小类型
     * @author Mr.Zhang
     * @since 2020-05-29
     * @return UserBill 总金额
     */
    @Override
    public BigDecimal getSumBigDecimal(Integer pm, Integer userId, String category, String date, String type) {
        //查询对象
        QueryWrapper<UserBill> queryWrapper = new QueryWrapper<>();

        //条件-明细种类
        queryWrapper.eq("category", category);
        //条件-状态
        queryWrapper.eq("status", 1);
        //条件-用户id
        if (ObjectUtil.isNotNull(userId)) {
            queryWrapper.eq("uid", userId);
        }
        //条件-支出或收入
        if(null != pm){
            queryWrapper.eq("pm", pm);
        }
        //条件-明细类型
        if(null != type){
            queryWrapper.eq("type", type);
        }
        //条件-日期范围
        if(null != date){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            queryWrapper.between("create_time", dateLimit.getStartTime(), dateLimit.getEndTime());
        }

        //得到数据-账单记录
        List<UserBill> userBills = dao.selectList(queryWrapper);

        //验证非空
        if (CollUtil.isEmpty(userBills)) {
            return BigDecimal.ZERO;
        }

        //返回统计金额
        return userBills.stream().map(UserBill::getNumber).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_DOWN);
    }

    /**
     * 按照月份分组, 余额
     * @author Mr.Zhang
     * @since 2020-06-08
     * @return CommonPage<UserBill>
     */
    @Override
    public PageInfo<UserSpreadCommissionResponse> getListGroupByMonth(Integer userId, List<String> typeList, PageParamRequest pageParamRequest, String category) {
        Page<UserBill> userBillPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        ArrayList<UserSpreadCommissionResponse> userSpreadCommissionResponseList = new ArrayList<>();

        QueryWrapper<UserBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", userId).eq("status", 1).eq("category", category);
        if(CollUtil.isNotEmpty(typeList)){
            queryWrapper.in("type", typeList);
        }

        queryWrapper.groupBy("left(create_time, 7)");
        queryWrapper.orderByDesc("left(create_time, 7)");
        List<UserBill> list = dao.selectList(queryWrapper);
        if(list.size() < 1){
            return new PageInfo<>();
        }

        for (UserBill userBill : list) {
            String date = DateUtil.dateToStr(userBill.getCreateTime(), Constants.DATE_FORMAT_MONTH);
            userSpreadCommissionResponseList.add(new UserSpreadCommissionResponse(date, getListByMonth(userId, typeList, date, category)));
        }
       return CommonPage.copyPageInfo(userBillPage, userSpreadCommissionResponseList);
    }

    /**
     * 保存退款日志
     * @author Mr.Zhang
     * @since 2020-06-08
     * @return boolean
     */
    @Override
    public boolean saveRefundBill(StoreOrderRefundRequest request, User user) {
        UserBill userBill =this.getUserBill(    // 商品退款退还余额
                user.getUid(),
                request.getOrderId().toString(),
                1,
                Constants.USER_BILL_CATEGORY_MONEY,
                Constants.USER_BILL_TYPE_PAY_PRODUCT_REFUND,
                request.getAmount(),
                user.getNowMoney().add(request.getAmount()),
                ""
        );
        return save(userBill);
    }

    @Override
    public List<UserBill> getBillGroupType() {
        LambdaQueryWrapper<UserBill> lqw = Wrappers.lambdaQuery();
        lqw.groupBy(UserBill::getType);
        return dao.selectList(lqw);
    }

    /**
     * 返回资金操作类型 仅仅转换数据用
     *
     * @return 操作类型
     */
    @Override
    public List<BillType> getBillType() {
        List<BillType> responses = new ArrayList<>();
        List<UserBill> billGroupType = getBillGroupType();
        if(null != billGroupType && billGroupType.size() > 0){
            billGroupType.stream().map(e->{
                BillType b = new BillType(e.getTitle(),e.getCategory());
                responses.add(b);
                return e;
            }).collect(Collectors.toList());
        }
        return responses;
    }

    /**
     * 根据基本条件查询
     *
     * @param bill 基本参数
     * @return 查询结果
     */
    @Override
    public List<UserBill> getByEntity(UserBill bill) {
        LambdaQueryWrapper<UserBill> lqw = Wrappers.lambdaQuery();
        lqw.setEntity(bill);
        return dao.selectList(lqw);
    }

    /**
     * 查询搜索明细类型参数
     *
     * @return 明细类型集合
     */
    @Override
    public List<UserBill> getSearchOption() {
        QueryWrapper<UserBill> qw = new QueryWrapper<>();
        qw.select("DISTINCT title, type");
        qw.notIn("type","gain", "system_sub", "deduction", "sign");
        qw.notIn("category","exp", "integral");
        return dao.selectList(qw);
    }

    /**
     * 获取订单历史处理记录(退款使用)
     * @param orderId 订单id
     * @param uid 用户id
     * @return
     */
    @Override
    public List<UserBill> findListByOrderIdAndUid(Integer orderId, Integer uid) {
        LambdaQueryWrapper<UserBill> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserBill::getUid, uid);
        lqw.eq(UserBill::getLinkId, String.valueOf(orderId));
        lqw.eq(UserBill::getStatus, 1);
        return dao.selectList(lqw);
    }

    /**
     * 资金监控
     * @param request 查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<UserBillResponse> fundMonitoring(FundsMonitorRequest request, PageParamRequest pageParamRequest) {
        Page<UserBill> billPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        Map<String, Object> map = new HashMap<>();
        if (StrUtil.isNotBlank(request.getKeywords())) {
            map.put("keywords", StrUtil.format("%{}%", request.getKeywords()));
        }
        //时间范围
        if(StrUtil.isNotBlank(request.getDateLimit())){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
            map.put("startTime", dateLimit.getStartTime());
            map.put("endTime", dateLimit.getEndTime());
        }
        List<UserBillResponse> userBillResponses = dao.fundMonitoring(map);
        return CommonPage.copyPageInfo(billPage, userBillResponses);
    }

    /**
     * 用户账单记录（现金）
     * @param uid 用户uid
     * @param type 记录类型：all-全部，expenditure-支出，income-收入
     * @return
     */
    @Override
    public PageInfo<UserBill> nowMoneyBillRecord(Integer uid, String type, PageParamRequest pageRequest) {
        Page<UserBill> billPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<UserBill> lqw = Wrappers.lambdaQuery();
        lqw.select(UserBill::getTitle, UserBill::getNumber, UserBill::getBalance, UserBill::getMark, UserBill::getCreateTime, UserBill::getPm);
        lqw.eq(UserBill::getUid, uid);
        lqw.eq(UserBill::getCategory, Constants.USER_BILL_CATEGORY_MONEY);
        switch (type) {
            case "all":
                break;
            case "expenditure":
                lqw.eq(UserBill::getPm, 0);
                break;
            case "income":
                lqw.eq(UserBill::getPm, 1);
                break;
        }
        lqw.eq(UserBill::getStatus, 1);
        lqw.orderByDesc(UserBill::getCreateTime);
        List<UserBill> billList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(billPage, billList);
    }

    /**
     * 获取H5列表
     *
     * @param userId   Integer 用户uid
     * @param category String 类型
     * @param pageParamRequest 分页类型
     * @return List<UserBill>
     */
    @Override
    public List<UserBill> getH5List(Integer userId, String category, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        QueryWrapper<UserBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "pm", "title", "number", "balance", "mark", "create_time");
        queryWrapper.eq("uid", userId);
        queryWrapper.eq("category", category);
        queryWrapper.eq("status", 1);
        queryWrapper.orderByDesc("create_time");
        return dao.selectList(queryWrapper);
    }

    /////////////////////////////////////////////////////////////////////// 自定义方法

    /**
     * 按照月份获取数据
     * @author Mr.Zhang
     * @since 2020-06-08
     * @return List<UserBill>
     */
    private List<UserBill> getListByMonth(Integer userId, List<String> typeList, String month, String category) {
        QueryWrapper<UserBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("pm,title,number,create_time").eq("uid", userId). eq("status", 1).eq("left(create_time, 7)", month).eq("category", category);
        if(CollUtil.isNotEmpty(typeList)){
            queryWrapper.in("type", typeList);
        }

        queryWrapper.orderByDesc("create_time");
        return dao.selectList(queryWrapper);
    }

}

