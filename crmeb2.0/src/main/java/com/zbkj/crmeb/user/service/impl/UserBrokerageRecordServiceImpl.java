package com.zbkj.crmeb.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.BrokerageRecordConstants;
import com.constants.Constants;
import com.constants.ConstantsFromID;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.ArrayUtil;
import com.utils.DateUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.front.response.SpreadCommissionDetailResponse;
import com.zbkj.crmeb.front.response.UserSpreadPeopleItemResponse;
import com.zbkj.crmeb.front.response.UserSpreadPeopleResponse;
import com.zbkj.crmeb.front.service.UserCenterService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.service.StoreOrderInfoService;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.store.service.StoreProductService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.dao.UserBrokerageRecordDao;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBrokerageRecord;
import com.zbkj.crmeb.user.service.UserBrokerageRecordService;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户佣金记录-service层接口实现类
 * @author: 零风
 * @CreateDate: 2021/12/23 11:51
 */
@Service
public class UserBrokerageRecordServiceImpl extends ServiceImpl<UserBrokerageRecordDao, UserBrokerageRecord> implements UserBrokerageRecordService {

    private static final Logger logger = LoggerFactory.getLogger(UserBrokerageRecordServiceImpl.class);

    @Resource
    private UserBrokerageRecordDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private StoreProductService storeProductService;

    @Override
    public void brokerageABonusTask() {
        //得到佣金分红配置
        HashMap<String, String> formMap = systemConfigService.info(ConstantsFromID.INT_CONFIG_FORM_ID_165);
        if(formMap == null ){
            logger.error("佣金分红　|| 配置表单为空！");
            return;
        }

        //验证是否开启
        if(!Boolean.valueOf(formMap.get("brokerageABonusTask_isOpen"))){
            logger.error("佣金分红　|| 佣金分红-未开启！");
            return;
        }

        //得到所有用户信息
        List<User> userList=userService.list(new LambdaQueryWrapper<User>().select(User::getUid,User::getBrokeragePrice));
        for (User user:userList) {
            //验证推荐人数
            UserSpreadPeopleResponse response=userCenterService.getSpreadPeopleCount(user.getUid());
            Integer recommendLeasNum=Integer.valueOf(formMap.get("brokerageABonusTask_recommendLeasNum"));
            if(response.getCount()<recommendLeasNum){
                break;
            }else{
                //统计累计消费
                BigDecimal cumulativeConsumption=BigDecimal.ZERO;
                Integer payCount=0;
                for (UserSpreadPeopleItemResponse upr:response.getSpreadPeopleList()) {
                    //定义查询对象
                    LambdaQueryWrapper<StoreOrder> storeOrderInfoLambdaQueryWrapper=new LambdaQueryWrapper<>();
                    storeOrderInfoLambdaQueryWrapper.eq(StoreOrder::getUid,upr.getUid());
                    storeOrderInfoLambdaQueryWrapper.eq(StoreOrder::getStatus,Constants.ORDER_STATUS_INT_COMPLETE);
                    storeOrderInfoLambdaQueryWrapper.between(StoreOrder::getCreateTime, DateUtil.getLastMonthStartDay(), DateUtil.getLastMonthEndDay());

                    //用户上月订单
                    List<StoreOrder> storeOrderList = storeOrderService.list(storeOrderInfoLambdaQueryWrapper);
                    if(storeOrderList == null || storeOrderList.size() == 0)continue;
                    storeOrderList = storeOrderList.stream().filter(e->e.getIsBrokerageAbonus()).collect(Collectors.toList());
                    BigDecimal payPrice = storeOrderList.stream().map(StoreOrder::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
                    if(payPrice.compareTo(BigDecimal.ZERO) == 1 ){
                        cumulativeConsumption.add(payPrice);
                        payCount++;
                    }
                }

                //验证交易人数
                Integer transactionLeastNum=Integer.valueOf(formMap.get("brokerageABonusTask_transactionLeast"));
                if(payCount < transactionLeastNum)continue;

                //计算分红比例
                BigDecimal proportion=new BigDecimal(formMap.get("brokerageABonusTask_proportion"));
                BigDecimal value=proportion.divide(new BigDecimal(100)).multiply(cumulativeConsumption);

                //添加佣金记录
                UserBrokerageRecord userBrokerageRecord = this.getUserBrokerageRecord(  //佣金分红task(定时任务)
                        user.getUid(),user.getUid().toString(),
                        BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ABONUS,
                        BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_6,
                        BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD,
                        value,user.getBrokeragePrice(),
                        0, 0L);
                boolean saveBrokerageRecord=this.save(userBrokerageRecord);
                if(!saveBrokerageRecord)logger.error("佣金分红 || 用户佣金记录保存失败！");

                //更新用户佣金
                Boolean isUpdate = userService.operationBrokerage(
                        user.getUid(),
                        userBrokerageRecord.getPrice(),
                        userBrokerageRecord.getBalance(),
                        BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD_STR);
                System.out.println("更新-用户佣金->"+isUpdate);
                if(isUpdate){
                    logger.info("佣金分红　|| 佣金分红成功！账户uid："+user.getUid()+",分红："+value);
                }else{
                    logger.error("佣金分红　|| 佣金分红失败！");
                }
            }
        }
    }

    @Override
    public UserBrokerageRecord getUserBrokerageRecord(Integer uid, String linkId,
                                                      String linkType, Integer type,Integer pm,
                                                      BigDecimal price, BigDecimal balance,
                                                      Integer frozenTime, Long thawTime) {
        //实例化对象
        UserBrokerageRecord record=new UserBrokerageRecord();   // 用户佣金记录实体对象(公共)
        record.setUid(uid);
        record.setLinkId(linkId);
        record.setLinkType(linkType);
        record.setPrice(price);
        record.setBalance(balance);
        record.setFrozenTime(frozenTime);
        record.setThawTime(thawTime);
        record.setCreateTime(DateUtil.nowDateTime());
        record.setType(type);
        record.setPm(pm);

        //验证关联类型
        String title = "";
        String mark ="";
        switch (record.getLinkType()){
            case BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_EXTRACT:
                switch (record.getType()){
                    case BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_2:
                        title = "提现申请";
                        mark =StrUtil.format("{},扣除佣金{}元", title,record.getPrice());
                        record.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_WITHDRAW);
                        break;
                    case BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_3:
                        title = BrokerageRecordConstants.BROKERAGE_RECORD_TITLE_WITHDRAW_FAIL;
                        mark = StrUtil.format("{},提现申请拒绝返还佣金,佣金余额增加{}元",title,record.getPrice());
                        record.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
                        break;
                    case BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_4:
                        title = "提现取消";
                        mark = StrUtil.format("{},提现申请取消返还佣金{}元",title,record.getPrice());
                        record.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
                        break;
                }
                break;
            case BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_YUE:
                title = "佣金转余额";
                mark = StrUtil.format("{},佣金余额减少{}元",title,record.getPrice());
                record.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
                break;
            case  BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER:
                record.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_CREATE);
                break;
            case BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ABONUS:
                title = "佣金分红";
                mark = StrUtil.format("{},佣金余额增加{}元",title,record.getPrice());
                record.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
                break;
            default:
                title = "未知类型";
                mark = StrUtil.format("{},{}元",title,record.getPrice());
                break;
        }
        record.setTitle(title);
        record.setMark(mark);

        //返回
        return record;
    }

    @Override
    public List<UserBrokerageRecord> findListByLinkIdAndLinkType(String linkId, String linkType) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserBrokerageRecord::getLinkId, linkId);
        lqw.eq(UserBrokerageRecord::getLinkType, linkType);
        return dao.selectList(lqw);
    }

    @Override
    public UserBrokerageRecord getByLinkIdAndLinkType(String linkId, String linkType) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserBrokerageRecord::getLinkId, linkId);
        lqw.eq(UserBrokerageRecord::getLinkType, linkType);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    @Override
    public void brokerageThaw() {
        // 查询需要解冻的佣金
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.le(UserBrokerageRecord::getThawTime, System.currentTimeMillis());
        lqw.eq(UserBrokerageRecord::getLinkType, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        lqw.eq(UserBrokerageRecord::getPm, BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD);
        lqw.eq(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_FROZEN);
        List<UserBrokerageRecord> thawList = dao.selectList(lqw);
        if (CollUtil.isEmpty(thawList)) {
            return;
        }

        // 循环处理
        for (UserBrokerageRecord record : thawList) {
            // 查询对应的用户
            User user = userService.getById(record.getUid());
            if (ObjectUtil.isNull(user)) {
                continue ;
            }

            // 更新状态
            BigDecimal balance = user.getBrokeragePrice().add(record.getPrice());
            record.setBalance(balance);
            record.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);

            // 执行
            Boolean execute = transactionTemplate.execute(e -> {
                // 更新记录
                this.updateById(record);

                // 更新用户佣金余额
                userService.operationBrokerage(record.getUid(), record.getPrice(), user.getBrokeragePrice(), "add");
                return Boolean.TRUE;
            });

            // 执行结果
            if (!execute) {
                logger.error(StrUtil.format("佣金解冻处理—出错，记录id = {}", record.getId()));
            }
        }

    }

    @Override
    public BigDecimal getYesterdayIncomes(Integer uid) {
        //定义查询对象
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.select(UserBrokerageRecord::getPrice);
        lqw.eq(UserBrokerageRecord::getUid, uid);
        lqw.eq(UserBrokerageRecord::getPm, 1);
        lqw.eq(UserBrokerageRecord::getLinkType, "order");
        lqw.eq(UserBrokerageRecord::getStatus, 3);

        //条件-日期范围
        dateLimitUtilVo dateLimit = DateUtil.getDateLimit(Constants.SEARCH_DATE_YESTERDAY);
        lqw.between(UserBrokerageRecord::getUpdateTime, dateLimit.getStartTime(), dateLimit.getEndTime());

        //验证非空
        List<UserBrokerageRecord> recordList = dao.selectList(lqw);
        if (CollUtil.isEmpty(recordList)) {
            return BigDecimal.ZERO;
        }

        //统计并返回
        return recordList.stream().map(UserBrokerageRecord::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public PageInfo<SpreadCommissionDetailResponse> findDetailListByUid(Integer uid, PageParamRequest pageParamRequest) {
        //得到-用户佣金记录-分页对象
        Page<UserBrokerageRecord> recordPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //定义查询对象
        QueryWrapper<UserBrokerageRecord> queryWrapper = new QueryWrapper<>();

        //条件-用户id
        queryWrapper.eq("uid", uid);

        //排血
        queryWrapper.groupBy("left(update_time, 7)");
        queryWrapper.orderByDesc("left(update_time, 7)");

        //得到数据-佣金记录
        List<UserBrokerageRecord> list = dao.selectList(queryWrapper);
        if (CollUtil.isEmpty(list)) {
            return new PageInfo<>();
        }

        //实例化-推广佣金明细-响应对象
        List<SpreadCommissionDetailResponse> responseList = CollUtil.newArrayList();
        for (UserBrokerageRecord record : list) {
            String month = DateUtil.dateToStr(record.getUpdateTime(), Constants.DATE_FORMAT_MONTH);
            QueryWrapper<UserBrokerageRecord> query = new QueryWrapper<>();
            query.select("id", "title", "price", "update_time", "type", "status");
            query.eq("uid", uid);
            query.eq("left(update_time, 7)", month);
            query.orderByDesc("update_time");
            responseList.add(new SpreadCommissionDetailResponse(month, dao.selectList(query)));
        }

        //返回
        return CommonPage.copyPageInfo(recordPage, responseList);
    }

    @Override
    public Integer getSpreadCountByUid(Integer uid) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.select(UserBrokerageRecord::getId);
        lqw.eq(UserBrokerageRecord::getUid, uid);
        lqw.eq(UserBrokerageRecord::getLinkType, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        lqw.in(UserBrokerageRecord::getStatus,
                BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_CREATE,
                BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_FROZEN,
                BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE,
                BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_INVALIDATION);
        return dao.selectCount(lqw);
    }

    @Override
    public List<UserBrokerageRecord> findSpreadListByUid(Integer uid, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserBrokerageRecord::getUid, uid);
        lqw.eq(UserBrokerageRecord::getLinkType, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        lqw.in(UserBrokerageRecord::getStatus,
                BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_CREATE,
                BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_FROZEN,
                BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE,
                BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_INVALIDATION);
        lqw.orderByDesc(UserBrokerageRecord::getUpdateTime);
        return dao.selectList(lqw);
    }

    @Override
    public Map<String, Integer> getSpreadCountByUidAndMonth(Integer uid, List<String> monthList) {
        QueryWrapper<UserBrokerageRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("count(id) as uid, update_time");
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("link_type", BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        queryWrapper.in("status", BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_CREATE,
                BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_FROZEN,
                BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE,
                BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_INVALIDATION);
        queryWrapper.apply(StrUtil.format("left(update_time, 7) in ({})", ArrayUtil.strListToSqlJoin(monthList)));
        queryWrapper.groupBy("left(update_time, 7)");
        List<UserBrokerageRecord> list = dao.selectList(queryWrapper);
        Map<String, Integer> map = CollUtil.newHashMap();
        if (CollUtil.isEmpty(list)) {
            return map;
        }
        list.forEach(record -> {
            map.put(DateUtil.dateToStr(record.getUpdateTime(), Constants.DATE_FORMAT_MONTH), record.getUid());
        });
        return map;
    }

    @Override
    public List<UserBrokerageRecord> getBrokerageTopByDate(String type, PageParamRequest pageParamRequest) {
        QueryWrapper<UserBrokerageRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("uid", "sum(price)AS price");
        queryWrapper.eq("link_type", BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        queryWrapper.eq("status", BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        dateLimitUtilVo dateLimit = DateUtil.getDateLimit(type);
        if(!StringUtils.isBlank(dateLimit.getStartTime())){
            queryWrapper.between("update_time", dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        queryWrapper.groupBy("uid");
        queryWrapper.orderByDesc("price");
        return dao.selectList(queryWrapper);
    }

    @Override
    public List<UserBrokerageRecord> getSpreadListByUid(Integer uid) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserBrokerageRecord::getUid, uid);
        lqw.eq(UserBrokerageRecord::getLinkType, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        lqw.eq(UserBrokerageRecord::getPm, BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD);
        lqw.eq(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        return dao.selectList(lqw);
    }

    @Override
    public BigDecimal getTotalSpreadPriceBydateLimit(String dateLimit,Integer pm) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.select(UserBrokerageRecord::getPrice);
        lqw.eq(UserBrokerageRecord::getLinkType, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        lqw.eq(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        lqw.eq(UserBrokerageRecord::getPm, pm);

        //条件-日期范围
        if (StrUtil.isNotBlank(dateLimit)) {
            dateLimitUtilVo dateLimitVo = DateUtil.getDateLimit(dateLimit);
            lqw.between(UserBrokerageRecord::getUpdateTime, dateLimitVo.getStartTime(), dateLimitVo.getEndTime());
        }

        //验证非空
        List<UserBrokerageRecord> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return BigDecimal.ZERO;
        }

        //统计并返回
        return list.stream().map(UserBrokerageRecord::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public PageInfo<UserBrokerageRecord> getFundsMonitorDetail(Integer uid, String dateLimit, PageParamRequest pageParamRequest) {
        Page<UserBrokerageRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserBrokerageRecord::getUid, uid);
        lqw.in(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);

        //条件-日期范围
        if (StrUtil.isNotBlank(dateLimit)) {
            dateLimitUtilVo utilVo = DateUtil.getDateLimit(dateLimit);
            lqw.between(UserBrokerageRecord::getUpdateTime, utilVo.getStartTime(), utilVo.getEndTime());
        }
        lqw.orderByDesc(UserBrokerageRecord::getUpdateTime);

        //得到数据并转成分页对象
        List<UserBrokerageRecord> recordList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, recordList);
    }

    @Override
    public BigDecimal getFreezePrice(Integer uid) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.select(UserBrokerageRecord::getPrice);
        lqw.eq(UserBrokerageRecord::getUid, uid);
        lqw.eq(UserBrokerageRecord::getLinkType, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        lqw.in(UserBrokerageRecord::getStatus,
                BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_CREATE,
                BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_FROZEN);
        List<UserBrokerageRecord> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return BigDecimal.ZERO;
        }
        return list.stream().map(UserBrokerageRecord::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public PageInfo<UserBrokerageRecord> findListByLinkIdsAndLinkTypeAndUid(List<String> linkIds, String linkType, Integer uid, PageParamRequest pageParamRequest) {
        Page<UserBrokerageRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.in(UserBrokerageRecord::getLinkId, linkIds);
        lqw.eq(UserBrokerageRecord::getLinkType, linkType);
        lqw.eq(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        lqw.eq(UserBrokerageRecord::getUid, uid);
        lqw.eq(UserBrokerageRecord::getPm, BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD);
        lqw.orderByDesc(UserBrokerageRecord::getUpdateTime);
        List<UserBrokerageRecord> list = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, list);
    }

}

