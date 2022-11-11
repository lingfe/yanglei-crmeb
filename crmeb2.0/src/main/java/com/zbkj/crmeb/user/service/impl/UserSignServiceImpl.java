package com.zbkj.crmeb.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.ConstantsFromID;
import com.constants.IntegralRecordConstants;
import com.constants.SysGroupDataConstants;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.utils.DateUtil;
import com.zbkj.crmeb.front.response.UserSignInfoResponse;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.system.service.SystemGroupDataService;
import com.zbkj.crmeb.system.vo.SystemGroupDataSignConfigVo;
import com.zbkj.crmeb.user.dao.UserSignDao;
import com.zbkj.crmeb.user.model.*;
import com.zbkj.crmeb.user.service.*;
import com.zbkj.crmeb.user.vo.UserSignMonthVo;
import com.zbkj.crmeb.user.vo.UserSignVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * UserSignServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class UserSignServiceImpl extends ServiceImpl<UserSignDao, UserSign> implements UserSignService {

    @Resource
    private UserSignDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemGroupDataService systemGroupDataService;

    @Autowired
    private UserBillService userBillService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserIntegralRecordService userIntegralRecordService;

    @Autowired
    private UserLevelService userLevelService;

    @Autowired
    private UserSignSuppService iuserSignSuppService;

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 列表
     *
     * @param pageParamRequest 分页类参数
     * @return List<UserSignVo>
     */
    @Override
    public List<UserSignVo> getList(PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserSign> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserSign::getType, 1);
        lambdaQueryWrapper.eq(UserSign::getUid, userService.getUserIdException());
        lambdaQueryWrapper.orderByDesc(UserSign::getId);
        List<UserSign> userSignList = dao.selectList(lambdaQueryWrapper);

        ArrayList<UserSignVo> userSignVoList = new ArrayList<>();
        if (userSignList.size() < 1) {
            return userSignVoList;
        }

        for (UserSign userSign : userSignList) {
            userSignVoList.add(new UserSignVo(userSign.getTitle(), userSign.getNumber(), userSign.getCreateDay()));
        }
        return userSignVoList;
    }

    /**
     * 补签
     * @param date 补签日期
     * @return
     */
    @Override
    public SystemGroupDataSignConfigVo suppSign(String date) {
        //实例化对象
        SystemGroupDataSignConfigVo sgdscv=new SystemGroupDataSignConfigVo();

        //得到补签表单
        Map<String,String> mapSysForm=systemConfigService.info(ConstantsFromID.INT_SIGNSUPP_CONFIG_FORM);
        if(mapSysForm == null){
            throw new CrmebException("补签失败! 未对应表单id:"+ConstantsFromID.INT_SIGNSUPP_CONFIG_FORM);
        }
        //补签次数
        Integer signSuppNum= Integer.valueOf(mapSysForm.get("signSuppNum"));
        //补签积分
        Integer suppSignIntegral= Integer.valueOf(mapSysForm.get("suppSignIntegral"));

        //基础积分，补签只有基础积分
        sgdscv.setIntegral(suppSignIntegral);
        sgdscv.setExperience(sgdscv.getIntegral());
        sgdscv.setTitle("补签成功!");

        //得到用户信息
        User user = userService.getInfoException();
        if(user==null)return null;

        //查询该用户当月的补签记录
        LambdaQueryWrapper<UserSignSupp> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(UserSignSupp::getUserId, user.getUid())
                .last(" and DATE_FORMAT(supp_date, '%Y%m' ) = DATE_FORMAT( CURDATE( ) , '%Y%m' ) ");
        List<UserSignSupp> userSignList = iuserSignSuppService.list(lambdaQueryWrapper);
        if(userSignList.size()>=signSuppNum){
            throw new CrmebException("补签失败! 本月补签已上限！");
        }

        //保存补签记录
        UserSignSupp uss=new UserSignSupp();
        uss.setUserId(user.getUid());
        uss.setSuppIntegral(sgdscv.getIntegral());

        //保存签到数据
        UserSign userSign = new UserSign();
        userSign.setUid(user.getUid());
        userSign.setTitle(mapSysForm.get("suppSignIntegalRecordTitle"));
        userSign.setBalance(user.getIntegral().add(new BigDecimal(sgdscv.getIntegral())).intValue());
        userSign.setNumber(sgdscv.getIntegral());
        userSign.setCreateDay(DateUtil.strToDate(DateUtil.nowDate(Constants.DATE_FORMAT_DATE), Constants.DATE_FORMAT_DATE));
        userSign.setType(Constants.SIGN_TYPE_INTEGRAL);

        //生成用户积分记录
        UserIntegralRecord utr = new UserIntegralRecord();
        utr.setUid(user.getUid());
        utr.setBalance(new BigDecimal(userSign.getBalance()));
        utr.setLinkType("signSupp");
        utr.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        utr.setTitle(mapSysForm.get("suppSignIntegalRecordTitle"));
        utr.setIntegral(new BigDecimal(sgdscv.getIntegral()));
        utr.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        utr.setMark(StrUtil.format("补签积分奖励增加了{}积分", sgdscv.getIntegral()));

        //更新用户经验信息
        UserBill experienceBill = userBillService.getUserBill(  // 补签-增加经验值
                user.getUid(),
                "0",
                1,
                Constants.USER_BILL_CATEGORY_EXPERIENCE,
                Constants.USER_BILL_TYPE_SUPPLEMENTARY_SIGNATURE,
                new BigDecimal(sgdscv.getExperience()),
                new BigDecimal(user.getExperience()).add(new BigDecimal(sgdscv.getExperience())),
                ""
        );

        //补签日期
        Date d =DateUtil.strToDate(date,Constants.DATE_FORMAT_DATE);
        uss.setSuppDate(d);
        experienceBill.setCreateTime(d);
        utr.setCreateTime(d);
        userSign.setCreateTime(DateUtil.strToDate(date,Constants.DATE_FORMAT));
        userSign.setCreateDay(d);

        //更新用户连续签到天数
        user.setSignNum(user.getSignNum() + 1);
        sgdscv.setDay(user.getSignNum());
        //更新用户积分
        user.setIntegral(utr.getBalance());
        //更新用户经验
        user.setExperience(user.getExperience() + sgdscv.getExperience());

        //执行操作
        Boolean execute = transactionTemplate.execute(e -> {
            //保存补签记录
            iuserSignSuppService.save(uss);
            //保存签到数据
            save(userSign);
            // 更新用户积分记录
            userIntegralRecordService.save(utr);
            //更新用户经验信息
            userBillService.save(experienceBill);
            //更新用户 签到天数、积分、经验
            userService.updateById(user);
            //用户升级
            userLevelService.upLevel(user);
            return Boolean.TRUE;
        });

        //执行结果
        if (!execute) {
            throw new CrmebException("补签失败!");
        }

        //返回
        return sgdscv;
    }

    /**
     * 签到
     */
    @Override
    public SystemGroupDataSignConfigVo sign() {
        //得到用户信息
        User user = userService.getInfoException();
        if(user==null)return null;

        //得到签到积分
        //SystemGroupDataSignConfigVo configVo = getSignInfo(user.getUid());
        SystemGroupDataSignConfigVo configVo = getSignInfo2(user);
        if (configVo == null) {
            throw new CrmebException("请先配置签到天数！");
        }

        //保存签到数据
        UserSign userSign = new UserSign();
        userSign.setUid(user.getUid());
        userSign.setTitle(Constants.SIGN_TYPE_INTEGRAL_TITLE);
        userSign.setNumber(configVo.getIntegral());
        userSign.setType(Constants.SIGN_TYPE_INTEGRAL);
        userSign.setBalance(user.getIntegral().intValue() + configVo.getIntegral());
        userSign.setCreateDay(DateUtil.strToDate(DateUtil.nowDate(Constants.DATE_FORMAT_DATE), Constants.DATE_FORMAT_DATE));

        // 生成用户积分记录
        UserIntegralRecord integralRecord = new UserIntegralRecord();
        integralRecord.setUid(user.getUid());
        integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_SIGN);
        integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        integralRecord.setTitle(IntegralRecordConstants.BROKERAGE_RECORD_TITLE_SIGN);
        integralRecord.setIntegral(new BigDecimal(configVo.getIntegral()));
        integralRecord.setBalance(user.getIntegral().add(integralRecord.getIntegral()));
        integralRecord.setMark(StrUtil.format("签到积分奖励增加了{}积分", configVo.getIntegral()));
        integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);

        //更新用户经验信息
        UserBill experienceBill = userBillService.getUserBill(  // 签到-增加经验值
                user.getUid(),
                "0",
                1,
                Constants.USER_BILL_CATEGORY_EXPERIENCE,
                Constants.USER_BILL_TYPE_SIGN,
                new BigDecimal(configVo.getExperience()),
                new BigDecimal(user.getExperience()).add(new BigDecimal(configVo.getExperience())),
                ""
        );

        //更新用户签到天数,检测昨天是否签到
        Boolean yesterdaySign = checkYesterdaySign(user.getUid());
        if (yesterdaySign) {
            user.setSignNum(user.getSignNum() + 1);
        } else {
            user.setSignNum(0);
        }
        //更新用户积分
        user.setIntegral(user.getIntegral().add(new BigDecimal(configVo.getIntegral())));
        //更新用户经验
        user.setExperience(user.getExperience() + configVo.getExperience());

        //执行操作
        Boolean execute = transactionTemplate.execute(e -> {
            //保存签到数据
            save(userSign);
            // 更新用户积分记录
            userIntegralRecordService.save(integralRecord);
            //更新用户经验信息
            userBillService.save(experienceBill);
            //更新用户 签到天数、积分、经验
            userService.updateById(user);
            // 用户升级
            userLevelService.upLevel(user);
            return Boolean.TRUE;
        });

        if (!execute) {
            throw new CrmebException("修改用户签到信息失败!");
        }

        return configVo;
    }

    /**
     * 获取签到积分2
     * @param user  用户资料
     * @return Integer
     * @author Mr.Zhang
     * @since 2020-04-30
     */
    private SystemGroupDataSignConfigVo getSignInfo2(User user) {
        //得到签到配置表单
        Map<String,String> mapSysForm=systemConfigService.info(ConstantsFromID.INT_SIGN_CONFIG_FORM);
        if(mapSysForm == null){
            throw new CrmebException("签到失败! 未对应表单id:"+ConstantsFromID.INT_SIGN_CONFIG_FORM);
        }
        //连续签到增长积分
        Integer lianxuSignAddIntegral= Integer.valueOf(mapSysForm.get("lianxuSignAddIntegral"));
        //连续签到积分增长天数
        Integer lianxuSignIntegralAddDay= Integer.valueOf(mapSysForm.get("lianxuSignIntegralAddDay"));

        //先看用户上次签到是什么日期， 如果有断开那么就重置连续签到天数。
        checkRepeat(user.getUid());

        //假如用户资料已更改,重新得到用户资料
        user=userService.getInfoException();

        //实例化对象
        SystemGroupDataSignConfigVo systemGroupDataSignConfigVo=new SystemGroupDataSignConfigVo();
        systemGroupDataSignConfigVo.setExperience(0);

        //获取用户连续签到天数
        Integer signNum= user.getSignNum()+1;
        systemGroupDataSignConfigVo.setDay(signNum);

        //标题
        systemGroupDataSignConfigVo.setTitle("签到，第"+signNum+"天");

        //积分
        signNum=signNum>=lianxuSignIntegralAddDay?lianxuSignIntegralAddDay:signNum;
        Integer integral=signNum * lianxuSignAddIntegral;
        systemGroupDataSignConfigVo.setIntegral(integral);
        return systemGroupDataSignConfigVo;
    }

    /**
     * 获取签到积分
     * @param userId Integer 用户id
     * @return Integer
     * @author Mr.Zhang
     * @since 2020-04-30
     */
    private SystemGroupDataSignConfigVo getSignInfo(Integer userId) {
        //先看用户上次签到是什么日期， 如果有断开那么就重置
        checkRepeat(userId);

        //获取用户连续签到天数
        User user = userService.getInfo();

        //获取签到数据
        List<SystemGroupDataSignConfigVo> config = config();

        //如果已经签到一个周期，那么再次签到的时候直接从第一天重新开始
        if (user.getSignNum().equals(config.size())) {
            user.setSignNum(0);
            userService.repeatSignNum(userId);
        }

        //返回已签到~到第几天..
        for (SystemGroupDataSignConfigVo systemSignConfigVo : config) {
            if (user.getSignNum() + 1 <= systemSignConfigVo.getDay()) {
                return systemSignConfigVo;
            }
        }

        return null;
    }
    /**
     * 详情
     *
     * @return map
     * @author Mr.Zhang
     * @since 2020-04-30
     */
    @Override
    public HashMap<String, Object> get() {
        HashMap<String, Object> map = new HashMap<>();
        //当前积分
        User info = userService.getInfo();
        map.put("integral", info.getIntegral());
        //总计签到天数
        map.put("count", signCount(info.getUid()));
        //连续签到数据

        //今日是否已经签到
        map.put("today", false);
        return map;
    }

    /**
     * 签到配置
     *
     * @return List<SystemGroupDataSignConfigVo>
     */
    @Override
    public List<SystemGroupDataSignConfigVo> config() {
        //获取配置数据
        return systemGroupDataService.getListByGid(SysGroupDataConstants.GROUP_DATA_ID_SIGN, SystemGroupDataSignConfigVo.class);
    }

    /**
     * 列表年月
     *
     * @param pageParamRequest 分页类参数
     * @return List<UserSignVo>
     */
    @Override
    public List<UserSignMonthVo> getListGroupMonth(PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserSign> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserSign::getType, 1);
        lambdaQueryWrapper.eq(UserSign::getUid, userService.getUserIdException());
        lambdaQueryWrapper.orderByDesc(UserSign::getCreateDay);
        List<UserSign> userSignList = dao.selectList(lambdaQueryWrapper);

        ArrayList<UserSignMonthVo> signMonthVoArrayList = new ArrayList<>();
        if (userSignList.size() < 1) {
            return signMonthVoArrayList;
        }

        for (UserSign userSign : userSignList) {
            String date = DateUtil.dateToStr(userSign.getCreateDay(), Constants.DATE_FORMAT_MONTH);
            UserSignVo userSignVo = new UserSignVo(userSign.getTitle(), userSign.getNumber(), userSign.getCreateDay());

            boolean findResult = false;
            if (signMonthVoArrayList.size() > 0) {
                //有数据之后则 判断是否已存在，存在则更新
                for (UserSignMonthVo userSignMonthVo : signMonthVoArrayList) {
                    if (userSignMonthVo.getMonth().equals(date)) {
                        userSignMonthVo.getList().add(userSignVo);
                        findResult = true;
                        break;
                    }
                }
            }

            //不存在则创建
            if (!findResult) {
                //如果没有找到则需要单独添加
                ArrayList<UserSignVo> userSignVoArrayList = new ArrayList<>();
                userSignVoArrayList.add(userSignVo);
                signMonthVoArrayList.add(new UserSignMonthVo(date, userSignVoArrayList));
            }
        }
        return signMonthVoArrayList;
    }

    /**
     * 获取用户签到信息
     * @return UserSignInfoResponse
     */
    @Override
    public UserSignInfoResponse getUserSignInfo() {
        User user = userService.getInfoException();
        UserSignInfoResponse userSignInfoResponse = new UserSignInfoResponse();
        BeanUtils.copyProperties(user, userSignInfoResponse);

        // 签到总次数
        userSignInfoResponse.setSumSignDay(getCount(user.getUid()));
        // 今天是否签到
        Boolean isCheckNowDaySign = checkDaySign(user.getUid());
        userSignInfoResponse.setIsDaySign(isCheckNowDaySign);
        // 昨天是否签到
        Boolean isYesterdaySign = checkYesterdaySign(user.getUid());
        userSignInfoResponse.setIsYesterdaySign(isYesterdaySign);
        if (!isYesterdaySign) {
            // 今天是否签到
            if (!isCheckNowDaySign) {
                userSignInfoResponse.setSignNum(0);
            }
        }

        // 连续签到天数：当前用户已经签到完一个周期，那么重置
        if (userSignInfoResponse.getSignNum() > 0 &&  userSignInfoResponse.getSignNum().equals(config().size())) {
            userSignInfoResponse.setSignNum(0);
            userService.repeatSignNum(user.getUid());
        }

        userSignInfoResponse.setIntegral(user.getIntegral());
        return userSignInfoResponse;
    }

    /**
     * 检测今天是否签到
     *
     * @param userId Integer 用户id
     * @return UserSignInfoResponse
     * @author Mr.Zhang
     * @since 2020-05-29
     */
    private Boolean checkDaySign(Integer userId) {
        List<UserSign> userSignList = getInfoByDay(userId, DateUtil.nowDate(Constants.DATE_FORMAT_DATE));
        return userSignList.size() != 0;
    }

    /**
     * 检测昨天天是否签到
     *
     * @param userId Integer 用户id
     * @return UserSignInfoResponse
     * @author Mr.Zhang
     * @since 2020-05-29
     */
    private Boolean checkYesterdaySign(Integer userId) {
        String day = DateUtil.nowDate(Constants.DATE_FORMAT_DATE);
        String yesterday = DateUtil.addDay(day, -1, Constants.DATE_FORMAT_DATE);
        List<UserSign> userSignList = getInfoByDay(userId, yesterday);
        return userSignList.size() != 0;
    }

    /**
     * 根据日期查询数据
     *
     * @param userId Integer 用户id
     * @param date   Date 日期
     * @return UserSignInfoResponse
     * @author Mr.Zhang
     * @since 2020-05-29
     */
    private List<UserSign> getInfoByDay(Integer userId, String date) {
        LambdaQueryWrapper<UserSign> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserSign::getUid, userId).eq(UserSign::getType, 1).eq(UserSign::getCreateDay, date);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 累计签到次数
     *
     * @param userId Integer 用户id
     * @return UserSignInfoResponse
     * @author Mr.Zhang
     * @since 2020-05-29
     */
    private Integer getCount(Integer userId) {
        LambdaQueryWrapper<UserSign> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserSign::getUid, userId).eq(UserSign::getType, 1);
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 检测是否需要重置累计签到天数
     * @param userId Integer 用户id
     */
    private void checkRepeat(Integer userId) {
        //得到签到记录
        PageHelper.startPage(Constants.DEFAULT_PAGE, Constants.DEFAULT_PAGE);
        LambdaQueryWrapper<UserSign> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(UserSign::getCreateDay).eq(UserSign::getUid, userId).orderByDesc(UserSign::getCreateDay);
        List<UserSign> userSignList = dao.selectList(lambdaQueryWrapper);
        if (userSignList.size() < 1) {
            //没有签到过，重置
            userService.repeatSignNum(userId);
            return;
        }

        //签到时间 +1 天
        String lastDate = DateUtil.dateToStr(userSignList.get(0).getCreateDay(), Constants.DATE_FORMAT_DATE);
        String nowDate = DateUtil.nowDate(Constants.DATE_FORMAT_DATE);
        String nextDate = DateUtil.addDay(userSignList.get(0).getCreateDay(), 1, Constants.DATE_FORMAT_DATE);

        //验证今天是否已经签到，对比今天数据
        if (DateUtil.compareDate(lastDate, nowDate, Constants.DATE_FORMAT_DATE) == 0) {
            throw new CrmebException("今日已签到。不可重复签到");
        }

        //验证是否连续签到
        int compareDate = DateUtil.compareDate(nextDate, nowDate, Constants.DATE_FORMAT_DATE);
        if (compareDate != 0) {
            //不相等，所以不是连续签到,重置
            userService.repeatSignNum(userId);
        }
    }

    /**
     * 累计签到天数
     *
     * @param userId Integer 用户id
     * @return Integer
     * @author Mr.Zhang
     * @since 2020-04-30
     */
    private Integer signCount(Integer userId) {
        LambdaQueryWrapper<UserSign> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserSign::getUid, userId);
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 条件获取列表
     *
     * @param sign sign
     * @param pageParamRequest 分页参数
     * @return List<UserSign>
     */
    @Override
    public List<UserSign> getListByCondition(UserSign sign, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<UserSign> lqw = new LambdaQueryWrapper<>();
        lqw.setEntity(sign);
        lqw.orderByDesc(UserSign::getCreateTime);
        return dao.selectList(lqw);
    }
}

