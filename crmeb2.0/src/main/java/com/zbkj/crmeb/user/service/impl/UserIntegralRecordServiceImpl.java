package com.zbkj.crmeb.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.ConstantsFromID;
import com.constants.IntegralRecordConstants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.DateUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.integal.model.PublicIntegalRecord;
import com.zbkj.crmeb.integal.service.PublicIntegalRecordService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.system.vo.SystemGroupDataSignConfigVo;
import com.zbkj.crmeb.user.dao.UserIntegralRecordDao;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserIntegralRecord;
import com.zbkj.crmeb.user.model.UserShareRecord;
import com.zbkj.crmeb.user.request.AdminIntegralSearchRequest;
import com.zbkj.crmeb.user.response.UserIntegralRecordResponse;
import com.zbkj.crmeb.user.service.UserIntegralRecordService;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.user.service.UserShareRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户积分记录Service实现类
 * @author: 零风
 * @CreateDate: 2022/3/14 14:20
 */
@Service
public class UserIntegralRecordServiceImpl extends ServiceImpl<UserIntegralRecordDao, UserIntegralRecord> implements UserIntegralRecordService {

    private static final Logger logger = LoggerFactory.getLogger(UserIntegralRecordServiceImpl.class);

    @Resource
    private UserIntegralRecordDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserShareRecordService userShareRecordService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private PublicIntegalRecordService publicIntegalRecordService;

    @Override
    public Boolean takeUnreadChangeRead(Integer id) {
        try{
            List<UserIntegralRecord> list=this.whereGet(new UserIntegralRecord()
                    .setId(id)
                    .setIsRead(IntegralRecordConstants.INTEGRAL_RECORD_ISREAD_0),null);
            if(list == null)return Boolean.TRUE;
            LambdaUpdateWrapper<UserIntegralRecord> updateWrapper=new LambdaUpdateWrapper<>();
            updateWrapper.set(UserIntegralRecord::getIsRead,IntegralRecordConstants.INTEGRAL_RECORD_ISREAD_1);
            updateWrapper.in(UserIntegralRecord::getId,list.stream().map(UserIntegralRecord::getId).collect(Collectors.toList()));
            this.update(updateWrapper);
            return Boolean.TRUE;
        }catch (Exception e){
            throw new CrmebException("发生错误："+e.getMessage());
        }
    }

    @Override
    public List<UserIntegralRecord> whereGet(UserIntegralRecord record,String lastSql) {
        LambdaQueryWrapper<UserIntegralRecord> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        if(record.getId() != null && record.getId()!=0)lambdaQueryWrapper.eq(UserIntegralRecord::getId,record.getId());
        if(record.getUid() !=null)lambdaQueryWrapper.eq(UserIntegralRecord::getUid,record.getUid());
        if(record.getType() !=null)lambdaQueryWrapper.eq(UserIntegralRecord::getType,record.getType());
        if(record.getIsRead() !=null)lambdaQueryWrapper.eq(UserIntegralRecord::getIsRead,record.getIsRead());
        lambdaQueryWrapper.orderByDesc(UserIntegralRecord::getCreateTime);
        if(lastSql !=null)lambdaQueryWrapper.last(lastSql);
        List<UserIntegralRecord> list=dao.selectList(lambdaQueryWrapper);
        return list.size()>=1?list:null;
    }

    @Override
    public List<UserIntegralRecord> getUnreadUserIntegralRecordList(Integer num) {
        User user=userService.getInfoException();
        return this.whereGet(new UserIntegralRecord()
                .setUid(user.getUid())
                .setIsRead(IntegralRecordConstants.INTEGRAL_RECORD_ISREAD_0),
                "LIMIT "+num);
    }

    @Override
    public UserIntegralRecord getNewestIncomeUserIntegralRecord() {
        User user=userService.getInfoException();
        UserIntegralRecord record=new UserIntegralRecord().setUid(user.getUid()).setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        List<UserIntegralRecord> list = this.whereGet(record, "LIMIT 1");
        return list==null?null:list.get(0);
    }

    @Override
    public UserIntegralRecord getUserIntegralRecord(Integer uid,BigDecimal integralBalance,String likeId,String likType,Integer type,Integer status,BigDecimal integral,String other) {
        //生成-用户积分记录
        UserIntegralRecord utr = new UserIntegralRecord();
        utr.setUid(uid);
        utr.setBalance(integralBalance);
        utr.setLinkType(likType);
        utr.setType(type);
        utr.setIntegral(integral);
        utr.setStatus(status);
        utr.setLinkId(likeId);

        //验证-关联类型
        String title="";
        String mark="";
        switch (likType){
            case IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_SYSTEM:
                title=String.format("平台用户下单，恭喜您获得平台随机奖励!");
                mark=String.format("%s用户下单随机奖励%s个米",title,integral);
                break;
            case IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER:
                switch (type){
                    case 1:
                        title=String.format("您分享的用户下单了，恭喜您获得米奖励！");
                        mark=String.format("%s获得%s个米!",title,integral);
                        break;
                    case 2:
                        title=String.format("订单米兑换！");
                        mark=String.format("%s支出%s个米！",title,integral);
                        break;
                }
                break;
            case IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_RETAILER_RA:
                title=String.format("您的零售商新增客户下单奖励米了！");
                mark=String.format("%s奖励%s个米!",title,integral);
                break;
            case IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_PUBLIC:
                title="推荐【"+other+"】下单-待结算米";
                mark=String.format("%s,转入待结算%s个米!",title,integral);
                break;
            case IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_PUBLIC_FEE:
                title="专属推广者的推广酒米转入可提现账户";
                mark=String.format("%s,酒米余额减少%s个米!",title,integral);
                break;
            case IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_LMSJDTGJMZRKTXYE:
                title="联盟商家的推广酒米转入可提现账户";
                mark=String.format("%s,酒米余额减少%s个米!",title,integral);
                break;
            case IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_Collection:
                title="收到【"+other+"】,通过账户ID向你转米";
                mark=String.format("收入%s个米!",title,integral);
                break;
            case IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_Collection_CODE:
                title="收到【"+other+"】,通过二维码向你转米";
                mark=String.format("收入%s个米!",title,integral);
                break;
            case IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_transfer:
                title="账户ID转米,向【"+other+"】转米";
                mark=String.format("%s,转米支出%s个米!",title,integral);
                break;
            case IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_transfer_CODE:
                title="扫二维码转米,向【"+other+"】转米";
                mark=String.format("%s,转米支出%s个米!",title,integral);
                break;
            case IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_integralTransferIn:
                title="积分转入账户余额";
                mark=String.format("%s,支出%s个米!",title,integral);
                break;
            case IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_SERVICE_FEE:
                title="服务费";
                mark=String.format("%s,支出%s个米!",title,integral);
                break;
            case IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_tixintuihuan:
                title="审核不通过退还,包含服务费";
                mark=String.format("%s,退还%s个米!",title,integral);
                break;
            case IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_isAllianceMerchants:
                title="扫码向联盟商家转米";
                mark=String.format("%s,支出%s个米!",title,integral);
                break;
        }
        utr.setTitle(title);
        utr.setMark(mark);
        return utr;
    }

    @Override
    public SystemGroupDataSignConfigVo shareFriendsPoints(Integer shareUserId) {
        //实例化对象
        SystemGroupDataSignConfigVo sgdsv=new SystemGroupDataSignConfigVo();

        //得到分享人信息
        User user=userService.getById(shareUserId);
        if(user == null){
            throw new CrmebException("操作失败！分享人user对象为空!");
        }

        //得到分享配置表单
        Map<String,String> mapSysForm=systemConfigService.info(ConstantsFromID.INT_SHARE_CONFIG_FORM);
        if(mapSysForm == null){
            throw new CrmebException("失败! 未对应表单id:"+ConstantsFromID.INT_SHARE_CONFIG_FORM);
        }
        //分享好友获得积分
        Integer sfIntegarl=Integer.valueOf(mapSysForm.get("shearFriendsGetIntegarl"));
        //每天分享好友获得积分次数上限
        Integer sfDayGet=Integer.valueOf(mapSysForm.get("shareFriendsGetIntegarlUpper"));

        //查询验证当月是否已获得分享好友积分上限
        LambdaQueryWrapper<UserShareRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(UserShareRecord::getUserId, user.getUid())
                .eq(UserShareRecord::getShareType,2)
                .last(" and DATE_FORMAT(share_datetime, '%Y%m' ) = DATE_FORMAT( CURDATE( ) , '%Y%m' )");
        List<UserShareRecord> userShareDayList = userShareRecordService.list(lambdaQueryWrapper);
        if(userShareDayList.size()>=sfDayGet){
            throw new CrmebException("分享好友获得积分失败！本月已上限！");
        }

        //更新用户积分
        user.setIntegral(user.getIntegral().add(new BigDecimal(sfIntegarl)));
        userService.updateById(user);

        //生成用户积分记录
        UserIntegralRecord integralRecord = new UserIntegralRecord();
        integralRecord.setUid(user.getUid());
        integralRecord.setLinkType("shareFriends");
        integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        integralRecord.setTitle(mapSysForm.get("shareFriendsIntegealRecordTitle"));
        integralRecord.setIntegral(new BigDecimal(sfIntegarl));
        integralRecord.setBalance(user.getIntegral());
        integralRecord.setMark(StrUtil.format("分享好友积分奖励增加了{}积分", sfIntegarl));
        integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        dao.insert(integralRecord);

        //保存分享好友记录
        UserShareRecord usr=new UserShareRecord();
        usr.setUserId(user.getUid());
        usr.setShareType(2);
        usr.setShareDatetime(new Date());
        userShareRecordService.save(usr);

        //返回参数
        sgdsv.setIntegral(sfIntegarl);
        sgdsv.setTitle(mapSysForm.get("okTitle"));

        //返回
        return sgdsv;
    }

    @Override
    public SystemGroupDataSignConfigVo sharePYQPoints() {
        //实例化对象
        SystemGroupDataSignConfigVo sgdsv=new SystemGroupDataSignConfigVo();

        //得到分享配置表单
        Map<String,String> mapSysForm=systemConfigService.info(ConstantsFromID.INT_SHARE_CONFIG_FORM);
        if(mapSysForm == null){
            throw new CrmebException("失败! 未对应表单id:"+ConstantsFromID.INT_SHARE_CONFIG_FORM);
        }

        //分享朋友圈获得积分
        Integer integarl=Integer.valueOf(mapSysForm.get("shareGetIntegarl"));
        //每天分享朋友圈获得积分次数上限
        Integer dayGet=Integer.valueOf(mapSysForm.get("dayGetIntegarlValue"));

        //得到用户信息
        User user=userService.getInfoException();
        if(user == null){
            throw new CrmebException("操作失败！user对象为空!");
        }

        //查询验证今天是否已获得分享朋友圈积分
        LambdaQueryWrapper<UserShareRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(UserShareRecord::getUserId, user.getUid())
                .eq(UserShareRecord::getShareType,1)
                .last(" and to_days(share_datetime) = to_days(now()) ");
        List<UserShareRecord> userShareDayList = userShareRecordService.list(lambdaQueryWrapper);
        if(userShareDayList.size()>=dayGet){
            throw new CrmebException("分享朋友圈获得积分失败！今天已上限！");
        }

        //更新用户积分
        user.setIntegral(user.getIntegral().add(new BigDecimal(integarl)));
        userService.updateById(user);

        //生成用户积分记录
        UserIntegralRecord integralRecord = new UserIntegralRecord();
        integralRecord.setUid(user.getUid());
        integralRecord.setLinkType("sharePYQ");
        integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        integralRecord.setTitle(mapSysForm.get("shareIntegealRecordTitle"));
        integralRecord.setIntegral(new BigDecimal(integarl));
        integralRecord.setBalance(user.getIntegral());
        integralRecord.setMark(StrUtil.format("分享朋友圈积分奖励增加了{}积分", integarl));
        integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        dao.insert(integralRecord);

        //保存分享朋友圈记录
        UserShareRecord usr=new UserShareRecord();
        usr.setUserId(user.getUid());
        usr.setShareType(1);
        usr.setShareDatetime(new Date());
        userShareRecordService.save(usr);

        //返回参数
        sgdsv.setIntegral(integarl);
        sgdsv.setTitle(mapSysForm.get("okTitle"));

        //返回
        return sgdsv;
    }

    /**
     * 根据订单编号、uid获取记录列表
     * @param orderNo 订单编号
     * @param uid 用户uid
     * @return 记录列表
     */
    @Override
    public List<UserIntegralRecord> findListByOrderIdAndUid(String orderNo, Integer uid) {
        LambdaQueryWrapper<UserIntegralRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserIntegralRecord::getUid, uid);
        lqw.eq(UserIntegralRecord::getLinkId, orderNo);
        lqw.in(UserIntegralRecord::getStatus, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_CREATE, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_FROZEN, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        List<UserIntegralRecord> recordList = dao.selectList(lqw);
        if (CollUtil.isEmpty(recordList)) {
            return recordList;
        }
        for (int i = 0; i < recordList.size();) {
            UserIntegralRecord record = recordList.get(i);
            if (record.getType().equals(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD)) {
                if (record.getStatus().equals(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE)) {
                    recordList.remove(i);
                    continue;
                }
            }
            i++;
        }
        return recordList;
    }

    @Override
    public void integralThawTask() {
        // 查询需要解冻的积分
        List<UserIntegralRecord> thawList = this.getNeedThawUserIntegralRecordList();
        if (thawList == null) thawList = new ArrayList<>();
        for (UserIntegralRecord record : thawList) {
            // 查询对应的用户
            User user = userService.getById(record.getUid());
            if (ObjectUtil.isNull(user)) {
                continue ;
            }

            // 更新状态
            record.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);

            // 计算积分余额
            BigDecimal balance = user.getIntegral().add(record.getIntegral());
            record.setBalance(balance);

            // 执行解冻
            Boolean execute = transactionTemplate.execute(e -> {
                updateById(record);
                userService.operationIntegral(record.getUid(), record.getIntegral(), user.getIntegral(), "add");
                return Boolean.TRUE;
            });

            // 执行结果
            if (!execute) {
                logger.error(StrUtil.format("用户积分解冻处理—解冻出错，记录id = {}", record.getId()));
            }
        }

        //查询需要解冻的-公共积分
        List<PublicIntegalRecord> publicIntegalRecordList = publicIntegalRecordService.getNeedThawPublicIntegalRecordList();
        if (CollUtil.isEmpty(publicIntegalRecordList)) return;
        for (PublicIntegalRecord record : publicIntegalRecordList) {
            logger.info(StrUtil.format("公共积分解冻处理，记录id = {}", record.getId()));
            logger.info(record.toString());
            record.setStatus(IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_COMPLETE);
            publicIntegalRecordService.updateById(record);
        }
    }

    /**
     * PC后台列表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @return 记录列表
     */
    @Override
    public PageInfo<UserIntegralRecordResponse> findAdminList(AdminIntegralSearchRequest request, PageParamRequest pageParamRequest) {
        Page<UserIntegralRecordResponse> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserIntegralRecord> lqw = Wrappers.lambdaQuery();
        lqw.select(UserIntegralRecord::getId, UserIntegralRecord::getTitle, UserIntegralRecord::getBalance, UserIntegralRecord::getIntegral,
                UserIntegralRecord::getMark, UserIntegralRecord::getUid, UserIntegralRecord::getUpdateTime);
        lqw.eq(UserIntegralRecord::getStatus, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        if (ObjectUtil.isNotNull(request.getUid())) {
            lqw.eq(UserIntegralRecord::getUid, request.getUid());
        }
        if (StrUtil.isNotBlank(request.getKeywords())) {
            List<Integer> idList = userService.findIdListLikeName(request.getKeywords());
            if (CollUtil.isNotEmpty(idList)) {
                lqw.in(UserIntegralRecord::getUid, idList);
            }
        }
        //时间范围
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
            //判断时间
            int compareDateResult = DateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), Constants.DATE_FORMAT);
            if(compareDateResult == -1){
                throw new CrmebException("开始时间不能大于结束时间！");
            }

            lqw.between(UserIntegralRecord::getUpdateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        lqw.orderByDesc(UserIntegralRecord::getUpdateTime);
        List<UserIntegralRecord> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }

        List<UserIntegralRecordResponse> responseList = list.stream().map(i -> {
            UserIntegralRecordResponse response = new UserIntegralRecordResponse();
            BeanUtils.copyProperties(i, response);
            // 获取用户昵称
            User user = userService.getById(i.getUid());
           if(user!=null){
               response.setNickName(user.getNickname());
           }
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }

    @Override
    public BigDecimal getSumIntegral(Integer uid, Integer type, String date, String linkType,Integer status) {
        QueryWrapper<UserIntegralRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(integral) as integral");
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("type", type);

        //条件-关联类型
        if (StrUtil.isNotBlank(linkType)) {
            queryWrapper.eq("link_type", linkType);
        }

        //条件-状态
        if(status != null){
            queryWrapper.eq("status",status);
        }

        //条件-日期范围
        if (StrUtil.isNotBlank(date)) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            queryWrapper.between("update_time", dateLimit.getStartTime(), dateLimit.getEndTime());
        }

        //得到数据
        UserIntegralRecord integralRecord = dao.selectOne(queryWrapper);
        if (ObjectUtil.isNull(integralRecord) || ObjectUtil.isNull(integralRecord.getIntegral())) {
            return BigDecimal.ZERO;
        }
        return integralRecord.getIntegral();
    }

    @Override
    public List<UserIntegralRecordResponse> findUserIntegralRecordList(Integer uid, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserIntegralRecord> lqw = Wrappers.lambdaQuery();
        lqw.select(UserIntegralRecord::getId, UserIntegralRecord::getTitle,UserIntegralRecord::getLinkType,UserIntegralRecord::getStatus,
                UserIntegralRecord::getType, UserIntegralRecord::getIntegral, UserIntegralRecord::getUpdateTime);
        lqw.eq(UserIntegralRecord::getUid, uid);
        lqw.orderByDesc(UserIntegralRecord::getId);
        List<UserIntegralRecord> list=dao.selectList(lqw);
        List<UserIntegralRecordResponse> responseList=new ArrayList<>();
        for (UserIntegralRecord record:list) {
            UserIntegralRecordResponse response=new UserIntegralRecordResponse();
            BeanUtils.copyProperties(record, response);
            if(record.getTitle().indexOf("支付")!=-1 || Constants.USER_BILL_TYPE_LMSJDTGJMZRKTXYE.equals(record.getLinkType())){
                response.setIsColor(Boolean.TRUE);
            }else{
                response.setIsColor(Boolean.FALSE);
            }
            responseList.add(response);
        }
        return responseList;
    }

    /**
     * 获取用户冻结的积分
     * @param uid 用户uid
     * @return 积分数量
     */
    @Override
    public BigDecimal getFrozenIntegralByUid(Integer uid) {
        QueryWrapper<UserIntegralRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(integral) as integral");
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("type", IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        queryWrapper.eq("link_type", IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
        queryWrapper.eq("status", IntegralRecordConstants.INTEGRAL_RECORD_STATUS_FROZEN);
        UserIntegralRecord integralRecord = dao.selectOne(queryWrapper);
        if (ObjectUtil.isNull(integralRecord) || ObjectUtil.isNull(integralRecord.getIntegral())) {
            return BigDecimal.ZERO;
        }
        return integralRecord.getIntegral();
    }

    @Override
    public List<UserIntegralRecord> getNeedThawUserIntegralRecordList() {
        LambdaQueryWrapper<UserIntegralRecord> lqw = Wrappers.lambdaQuery();
        lqw.le(UserIntegralRecord::getThawTime, System.currentTimeMillis());
        lqw.eq(UserIntegralRecord::getLinkType, IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
        lqw.eq(UserIntegralRecord::getType, IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        lqw.eq(UserIntegralRecord::getStatus, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_FROZEN);
        return dao.selectList(lqw);
    }
}

