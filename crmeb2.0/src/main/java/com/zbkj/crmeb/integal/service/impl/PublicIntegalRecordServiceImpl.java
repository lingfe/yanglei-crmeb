package com.zbkj.crmeb.integal.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.IntegralRecordConstants;
import com.constants.PayConstants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.DateUtil;
import com.zbkj.crmeb.integal.dao.PublicIntegalRecordDao;
import com.zbkj.crmeb.integal.model.CirprsRecord;
import com.zbkj.crmeb.integal.model.PublicIntegalRecord;
import com.zbkj.crmeb.integal.request.PublicIntegalRecordSearchRequest;
import com.zbkj.crmeb.integal.response.PublicIntegalRecordResponse;
import com.zbkj.crmeb.integal.response.PublicIntegralLibraryResponse;
import com.zbkj.crmeb.integal.service.CirprsRecordService;
import com.zbkj.crmeb.integal.service.PublicIntegalRecordService;
import com.zbkj.crmeb.regionalAgency.model.RegionalAgency;
import com.zbkj.crmeb.regionalAgency.model.RegionalUser;
import com.zbkj.crmeb.regionalAgency.service.RegionalAgencyService;
import com.zbkj.crmeb.regionalAgency.service.RegionalUserService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.model.UserIntegralRecord;
import com.zbkj.crmeb.user.service.UserBillService;
import com.zbkj.crmeb.user.service.UserIntegralRecordService;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.math.BigDecimal.ZERO;

/**
 * 公共积分记录表-service层接口实现类
 * @author: 零风
 * @CreateDate: 2021/10/18 13:49
 */
@Service
public class PublicIntegalRecordServiceImpl extends ServiceImpl<PublicIntegalRecordDao, PublicIntegalRecord> implements PublicIntegalRecordService {

    @Resource
    private PublicIntegalRecordDao dao;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private UserService userService;

    @Autowired
    private CirprsRecordService cirprsRecordService;

    @Autowired
    private UserIntegralRecordService userIntegralRecordService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private RegionalAgencyService regionalAgencyService;

    @Autowired
    private RegionalUserService regionalUserService;

    @Autowired
    private UserBillService userBillService;

    public static void main(String[] args) {
        System.out.println(!"integer".equals("integer"));
        System.out.println("integer".equals("integer"));
        System.out.println(!"intege2r".equals("integer"));


        BigDecimal integral = new BigDecimal(5001.5);
        integral =  integral.divide(new BigDecimal(2));

        String tagid=",11";
        if("11".equals(tagid) ||
                (tagid.indexOf(",11")!=-1&&tagid.indexOf(",111") == -1)||
                (tagid.indexOf("11,")!=-1&&tagid.indexOf("111,") == -1)){
            System.out.println(tagid);
        }
        //得到-抽取1%~5%;
        DecimalFormat df = new DecimalFormat("0.00");
        BigDecimal tt=new BigDecimal(251);
        BigDecimal bfb= new BigDecimal(df.format(0.05));
        BigDecimal J=tt.multiply(bfb);

        System.out.println("678".indexOf("9"));

        //得到随机数
        Random r=new Random();
        System.out.println(RandomUtil.randomDouble(J.doubleValue()));
        System.out.println(RandomUtil.randomDouble(J.doubleValue()));
        System.out.println(RandomUtil.randomDouble(J.doubleValue()));
        System.out.println(RandomUtil.randomDouble(J.doubleValue()));
        System.out.println(RandomUtil.randomDouble(J.doubleValue()));
        System.out.println(RandomUtil.randomDouble(J.doubleValue()));
        System.out.println(RandomUtil.randomDouble(J.doubleValue()));
        System.out.println(RandomUtil.randomDouble(J.doubleValue()));
        System.out.println(new Random().nextInt(J.intValue()));
        System.out.println(r.nextInt(J.intValue()));
        System.out.println(r.nextInt(J.intValue()));
        System.out.println(r.nextInt(J.intValue()));
        System.out.println(r.nextInt(J.intValue()));
        System.out.println(r.nextInt(J.intValue()));
        System.out.println(r.nextInt(J.intValue()));
        System.out.println(r.nextInt(J.intValue()));

        tt=tt.add(tt);
        System.out.println(tt);

    }

    @Override
    public List<PublicIntegalRecord> getNeedThawPublicIntegalRecordList() {
        LambdaQueryWrapper<PublicIntegalRecord> lqw = Wrappers.lambdaQuery();
        lqw.le(PublicIntegalRecord::getThawTime, System.currentTimeMillis());
        lqw.eq(PublicIntegalRecord::getLinkType, IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_LINK_TYPE_ORDER_INT);
        lqw.eq(PublicIntegalRecord::getType, Constants.ADD);
        lqw.eq(PublicIntegalRecord::getStatus, IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_FROZEN);
        return this.list(lqw);
    }

    @Override
    public PublicIntegalRecord add(Integer uid,
                                   String linkId,
                                   BigDecimal integral,
                                   BigDecimal integralBalance,
                                   Integer linkType,
                                   Integer type,
                                   Integer status,
                                   Boolean isHandle,
                                   Integer spreadUid,String other) {
        //实例化-公共积分记录对象
        PublicIntegalRecord integralRecord = new PublicIntegalRecord();

        //赋值
        integralRecord.setUid(uid);
        integralRecord.setLinkId(linkId);
        integralRecord.setLinkType(linkType);
        integralRecord.setType(type);
        integralRecord.setIntegral(integral);
        integralRecord.setIntegralBalance(integralBalance);
        integralRecord.setStatus(status);
        integralRecord.setIsHandle(isHandle);
        integralRecord.setRaId(0);
        integralRecord.setSpreadUid(spreadUid);

        //验证-关联类型-自动生成标题备注
        String title="";
        String mark="";
        switch (linkType){
            case IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_LINK_TYPE_ORDER_INT:
                //订单
                title = IntegralRecordConstants.BROKERAGE_RECORD_TITLE_ORDER;
                mark = StrUtil.format("{},来自商品{}个积分!", title,integral);

                //设置积分冻结期
                String fronzenTime = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_INTEGRAL_EXTRACT_TIME);
                integralRecord.setFrozenTime(Integer.valueOf(Optional.ofNullable(fronzenTime).orElse("0")));
                integralRecord.setCreateTime(DateUtil.nowDateTime());
                break;
            case IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_LINK_TYPE_RANDOM:
                title = "系统随机奖励";
                mark = StrUtil.format("{},奖励{}个积分!{}",title, integral,other);
                break;
            case IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_LINK_TYPE_REWARD:
                title = "推广奖励";
                mark = StrUtil.format("{},推广【{}】下单奖励{}个积分!",title,other, integral);
                break;
            default:
                title= StrUtil.format("未知来源");
                mark = StrUtil.format("{},{}个积分!",title, integral);
                break;
        }

        //标题与备注
        integralRecord.setTitle(title);
        integralRecord.setMark(mark);

        //返回-公共积分记录对象
        return integralRecord;
    }

    @Override
    public void distributionTask(){
        //得到-可分配积分
        //验证-可分配积分是否充足
        //计算-可抽取最大积分
        String distributableIntegralStr = systemConfigService.getValueByKeyException("distributableIntegral");
        BigDecimal distributableIntegral = new BigDecimal(distributableIntegralStr);
        if(distributableIntegral.compareTo(ZERO) < 1)return;

        //得到-积分分配比例
        //得到-抽取1%~5%,则取最高：5% = 0.05
        String randomDistributionProportionStr = systemConfigService.getValueByKeyException("randomDistributionProportion");
        DecimalFormat df = new DecimalFormat("0.00");
        BigDecimal randomDistributionProportion=new BigDecimal(randomDistributionProportionStr);
        BigDecimal bfb = randomDistributionProportion.divide(new BigDecimal(100));

        //得到-消费用户
        //定义查询对象
        LambdaQueryWrapper<RegionalUser> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(RegionalUser::getUpdateTime);
        //lqw.eq(RegionalUser::getRaId,0); //查询-未关联区域的用户

        //得到-分配用户数据
        //公共积分分配时-验证数量以及去掉最后一个人。
        List<RegionalUser> userList = regionalUserService.list(lqw);
        if(userList.size() <= 1)return;
        userList.remove(userList.size() -1);

        //循环处理
        for (RegionalUser regionalUser: userList) {
            //得到-可分配积分
            //验证-可分配积分是否充足
            //计算-可抽取最大积分
            distributableIntegralStr = systemConfigService.getValueByKeyException("distributableIntegral");
            distributableIntegral = new BigDecimal(distributableIntegralStr);
            if(distributableIntegral.compareTo(ZERO) < 1)break;
            BigDecimal integralMax = distributableIntegral.multiply(bfb);

            //得到-用户信息
            //得到-用户累计消费金额
            User user = userService.getById(regionalUser.getUid());
            if(user == null) continue;
            BigDecimal orderStatusSumChushihua = storeOrderService.getSumBigDecimal(user.getUid(), null,new Integer[]{3},true); // 从可分配积分中分配积分-初始化统计已完成的累计消费金额
            BigDecimal orderStatusSum = userService.getKeyonMiED2(user,orderStatusSumChushihua);
            if(orderStatusSum.compareTo(ZERO) < 1)continue;

            //得到-积分余额
            //判断-用户积分余额是否小于累计消费金额
            BigDecimal integral = user.getIntegral();
            if(integral.compareTo(orderStatusSum) == -1){
                //随机抽取积分
                BigDecimal value = new BigDecimal(df.format(new BigDecimal(RandomUtil.randomDouble(integralMax.doubleValue()))));
                if(value.compareTo(new BigDecimal(1.00)) < 1 ){
                    value = value.add(new BigDecimal(1.00));
                }

                //加上-抽取的积分之后再次验证，积分余额是否大于累计消费金额
                if(integral.add(value).compareTo(orderStatusSum) == 1){
                    value = orderStatusSum.subtract(integral);
                }

                //减扣-可分配积分并验证是否充足
                if(distributableIntegral.compareTo(value) > -1){
                    distributableIntegral = distributableIntegral.subtract(value);
                }else{
                    //否则-将最后一点可分配积分-给接下来的一个用户
                    value = distributableIntegral;
                    distributableIntegral = distributableIntegral.subtract(value);
                }

                //更新-用户积分
                BigDecimal integralBalance = user.getIntegral().add(value);
                LambdaUpdateWrapper<User> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
                lambdaUpdateWrapper.set(User::getIntegral, integralBalance);                //更新-积分余额
                lambdaUpdateWrapper.eq(User::getUid, user.getUid());                        //修改条件-用户id
                userService.update(lambdaUpdateWrapper);                                    //执行修改

                //生成-用户积分记录
                UserIntegralRecord integralRecord = userIntegralRecordService.getUserIntegralRecord( // 公共积分转用户积分余额记录
                        user.getUid(),
                        integralBalance,
                        "0",
                        IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_SYSTEM,
                        IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD,
                        IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE,
                        value,null);
                userIntegralRecordService.save(integralRecord);

                //生成公共积分记录
                PublicIntegalRecord publicIntegalRecord = this.add( // 从可分配积分中分配积分
                        user.getUid(),
                        "0",
                        value,
                        integralBalance,
                        IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_LINK_TYPE_RANDOM,
                        Constants.SUB,
                        IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_distribu,
                        Boolean.TRUE,
                        0,
                        new StringBuffer("本次抽取最高:")
                                .append(integralMax).append("个,可分配剩余:")
                                .append(distributableIntegral).append("个!").toString());
                dao.insert(publicIntegalRecord);

                //更新-公共积分库-可分配积分
                systemConfigService.updateOrSaveValueByName("distributableIntegral",df.format(distributableIntegral));
            }
        }
    }

    @Override
    public PublicIntegalRecord extractedYCF(PublicIntegalRecord pir,Boolean isHandle,Integer state) {
        //修改-公共积分记录处理状态
        PublicIntegalRecord publicIntegalRecord=new PublicIntegalRecord();
        BeanUtils.copyProperties(pir, publicIntegalRecord);//转换

        //更新值
        publicIntegalRecord.setIsHandle(isHandle);
        publicIntegalRecord.setUpdateTime(DateUtil.nowDateTime());//更新日期
        publicIntegalRecord.setStatus(state);

        //保存
        Boolean bl=dao.insert(publicIntegalRecord)>0;
        if(bl)dao.deleteById(pir.getId());//删掉之前的
        return publicIntegalRecord;
    }

    @Override
    public void updateUserIntegal() {
        //定义查询对象
        LambdaQueryWrapper<PublicIntegalRecord> lqwPublic = Wrappers.lambdaQuery();
        lqwPublic.eq(PublicIntegalRecord::getIsHandle,Boolean.FALSE);
        lqwPublic.in(PublicIntegalRecord::getStatus,
                IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_CREATE,
                IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_COMPLETE);

        //得到数据
        List<PublicIntegalRecord> recordList = dao.selectList(lqwPublic);
        if(recordList==null || recordList.size()<=0)return;

        //更新-用户积分
        for (PublicIntegalRecord pir:recordList) {
            //得到-用户信息
            User user = userService.getById(pir.getUid());
            if(user == null )continue;

            //得到-推广人用户信息
            Boolean isYinZhengfanRecord=Boolean.FALSE;//区别两种推广方式
            User spUser = userService.getById(pir.getSpreadUid());//用户推广的订单公共积分
            if(spUser == null){
                spUser = userService.getById(user.getSpreadUid());//用户推广的用户
                if(spUser == null ){
                    if(pir.getStatus() == IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_COMPLETE){
                        this.putPublicIntegalLibrary(pir);
                    }
                    continue;
                }
            }else{
                isYinZhengfanRecord =Boolean.TRUE;
            }

            //验证-是否存在返积分记录(只返一次),用户推广的订单公共积分不验证记录
            if(!isYinZhengfanRecord){
                LambdaQueryWrapper<CirprsRecord> lqwCirprs = Wrappers.lambdaQuery();
                lqwCirprs.eq(CirprsRecord::getUid,user.getUid());
                lqwCirprs.eq(CirprsRecord::getPopularizeId,spUser.getUid());
                List<CirprsRecord> cirprsRecordList = cirprsRecordService.list(lqwCirprs);
                if(cirprsRecordList !=null && cirprsRecordList.size() >= 1 ){
                    if(pir.getStatus() == IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_COMPLETE){
                        this.putPublicIntegalLibrary(pir);
                    }
                    continue;
                }
            }

            //得到订单
            StoreOrder storeOrder=storeOrderService.getById(pir.getLinkId());
            String payType="订单不存在了";
            if(storeOrder != null){
                payType = storeOrderService.getOrderPayTypeStr(storeOrder.getPayType());
            }

            //先查返积分记录-推荐【张三】通过【微信支付】下单-待结算米
            String other=new StringBuffer(user.getNickname()).append("】通过【").append(payType).toString();
            LambdaQueryWrapper<UserIntegralRecord> userIntegralRecordLambdaQueryWrapper=new LambdaQueryWrapper<>();
            userIntegralRecordLambdaQueryWrapper.eq(UserIntegralRecord::getUid,spUser.getUid());
            userIntegralRecordLambdaQueryWrapper.eq(UserIntegralRecord::getLinkId,pir.getId());
            userIntegralRecordLambdaQueryWrapper.eq(UserIntegralRecord::getLinkType,IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_PUBLIC);
            userIntegralRecordLambdaQueryWrapper.eq(UserIntegralRecord::getStatus,IntegralRecordConstants.INTEGRAL_RECORD_STATUS_DJS);
            userIntegralRecordLambdaQueryWrapper.last("LIMIT 1");//只取一条
            UserIntegralRecord userIntegralRecord = userIntegralRecordService.getOne(userIntegralRecordLambdaQueryWrapper);
            if(userIntegralRecord == null ){
                //生成-推广人用户积分记录-待结算积分记录
                userIntegralRecord = userIntegralRecordService.getUserIntegralRecord( // 推广人用户积分记录-待结算积分记录
                        spUser.getUid(),
                        spUser.getIntegral(),
                        pir.getId().toString(),
                        IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_PUBLIC,
                        IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD,
                        IntegralRecordConstants.INTEGRAL_RECORD_STATUS_DJS,
                        pir.getIntegral(),other);
                userIntegralRecordService.save(userIntegralRecord);
            }

            //验证状态
            if(pir.getStatus() != IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_COMPLETE){
                continue;
            }

            //修改-公共积分记录处理状态(为了方便排序，会复制添加然后删掉之前的)
            PublicIntegalRecord newPir = this.extractedYCF(pir,
                    Boolean.TRUE,   //修改为已处理
                    IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_YCF); //已存放

            //处理-推广人用户积分记录-待结算状态
            //更新并保存(为了方便排序，会复制添加然后删掉之前的)
            UserIntegralRecord newUserIntegralRecord=new UserIntegralRecord();
            BeanUtils.copyProperties(userIntegralRecord, newUserIntegralRecord);//转换
            newUserIntegralRecord.setLinkId(String.valueOf(newPir.getId()));
            newUserIntegralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
            newUserIntegralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
            newUserIntegralRecord.setTitle(new StringBuffer(userIntegralRecord.getTitle()).append("(已结算)").toString());
            if(userIntegralRecordService.save(newUserIntegralRecord)){
                userIntegralRecordService.removeById(userIntegralRecord.getId());
            }

            //定义过渡变量
            BigDecimal integral=newPir.getIntegral();
            String tagid=spUser.getTagId();
            if(tagid == null) tagid="";

            //验证支付方式-是否不是酒米兑换
            //验证是否为（专属推广者=11）标签
            Boolean payBL=!PayConstants.PAY_TYPE_INTEGRAL.equals(storeOrder.getPayType());
            if((Constants.TARGID_ZSTGZ.equals(tagid) && payBL)||
                    ((tagid.indexOf(",11")!=-1&&tagid.indexOf(",111") == -1)&&payBL)||
                    ((tagid.indexOf("11,")!=-1&&tagid.indexOf("111,") == -1)&&payBL)){
                //减少一半积分，另一半转入可提现账户余额
                integral = integral.divide(new BigDecimal(2));

                //专属推广者的推广酒米转入可提现账户-积分记录
                UserIntegralRecord integralRecord = userIntegralRecordService.getUserIntegralRecord( // 专属推广者的推广酒米转入可提现账户-积分记录
                        spUser.getUid(),
                        spUser.getIntegral().add(integral),
                        String.valueOf(newPir.getId()),
                        IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_PUBLIC_FEE,
                        IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB,
                        IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE,
                        integral,
                        new StringBuffer(user.getUid()).append("【").append(user.getNickname()).toString()
                );
                userIntegralRecordService.save(integralRecord);
                userService.operationIntegral(spUser.getUid(),integral,spUser.getIntegral(),Constants.ADD_STR);//专属推广者的推广酒米转入可提现账户-更新积分余额

                //专属推广者的推广酒米转入可提现账户
                UserBill userBill = userBillService.getUserBill(    // 专属推广者的推广酒米转入可提现账户-账单记录
                        spUser.getUid(),
                        String.valueOf(newPir.getId()),
                        Constants.USER_BILL_PM_1,
                        Constants.USER_BILL_CATEGORY_MONEY,
                        Constants.USER_BILL_TYPE_zhuanruketixianzhanghu,
                        integral,
                        spUser.getNowMoney().add(integral),
                        new StringBuffer(user.getUid()).append("【").append(user.getNickname()).toString()
                );
                userBillService.save(userBill);
                userService.operationNowMoney(spUser.getUid(),integral,spUser.getNowMoney(),Constants.ADD_STR);//专属推广者的推广酒米转入可提现账户
            }

            //验证是否为联盟商家
            else if((Constants.TARGID_LMSJ.equals(tagid)&&payBL) ||
                    ((tagid.indexOf(",10")!=-1&&tagid.indexOf(",1010") == -1)&&payBL)||
                    ((tagid.indexOf("10,")!=-1&&tagid.indexOf("1010,") == -1)&&payBL)){
                //联盟商家的推广酒米转入可提现账户-积分记录
                UserIntegralRecord integralRecord = userIntegralRecordService.getUserIntegralRecord( // 联盟商家的推广酒米转入可提现账户-积分记录
                        spUser.getUid(),
                        spUser.getIntegral().add(integral),
                        String.valueOf(newPir.getId()),
                        IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_LMSJDTGJMZRKTXYE,
                        IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB,
                        IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE,
                        integral,
                        new StringBuffer(user.getUid()).append("【").append(user.getNickname()).toString()
                );
                userIntegralRecordService.save(integralRecord);

                //联盟商家的推广酒米转入可提现账户
                UserBill userBill = userBillService.getUserBill(    // 联盟商家的推广酒米转入可提现账户-账单记录
                        spUser.getUid(),
                        String.valueOf(newPir.getId()),
                        Constants.USER_BILL_PM_1,
                        Constants.USER_BILL_CATEGORY_MONEY,
                        Constants.USER_BILL_TYPE_LMSJDTGJMZRKTXYE,
                        integral,
                        spUser.getNowMoney().add(integral),
                        new StringBuffer(user.getUid()).append("【").append(user.getNickname()).toString()
                );
                userBillService.save(userBill);
                userService.operationNowMoney(spUser.getUid(),integral,spUser.getNowMoney(),Constants.ADD_STR);//联盟商家的推广酒米转入可提现账户
            }else{
                //更新-推广人用户积分
                userService.operationIntegral(spUser.getUid(),integral,spUser.getIntegral(),Constants.ADD_STR);//推荐用户下单积分-更新积分余额
            }

            //添加-返积分记录
            if(!isYinZhengfanRecord){
                CirprsRecord cirprsRecord=new CirprsRecord();
                cirprsRecord.setUid(user.getUid());
                cirprsRecord.setPopularizeId(spUser.getUid());
                cirprsRecord.setPublicirId(newPir.getId());
                cirprsRecordService.save(cirprsRecord);
            }

            //生成公共积分记录
            PublicIntegalRecord publicIntegalRecord = this.add( //推广人用户积分(消费有推广模式)
                    spUser.getUid(),
                    String.valueOf(newPir.getId()),
                    newPir.getIntegral(),
                    spUser.getIntegral().add(newPir.getIntegral()),
                    IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_LINK_TYPE_REWARD,
                    Constants.SUB,
                    IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_distribu,
                    Boolean.TRUE,
                    0,
                    new StringBuffer(user.getUid()).append("【").append(user.getNickname()).toString()
            );
            dao.insert(publicIntegalRecord);
        }
    }

    @Override
    public void putPublicIntegalLibrary(PublicIntegalRecord pir) {
        //得到-系统配置字段可分配积分并更新
        String distributableIntegralStr= systemConfigService.getValueByKeyException("distributableIntegral"); //可分配积分
        BigDecimal distributableIntegral=new BigDecimal(distributableIntegralStr).add(pir.getIntegral());
        systemConfigService.updateOrSaveValueByName("distributableIntegral",distributableIntegral.toString());  //更新-可分配积分

        //无-推广人-进入(消费无推广模式)-把积分放入-待分配中
        //修改-公共积分记录处理状态
        this.extractedYCF(pir,
                Boolean.TRUE,  //修改为已处理
                IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_YCF);//修改为-已存放
    }

    @Override
    public PublicIntegralLibraryResponse getP(DecimalFormat df,List<PublicIntegalRecord> recordList){
        //统计-冻结中积分
        BigDecimal freezingIntegral = recordList.stream()
                .filter(i -> i.getStatus() == IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_FROZEN)
                .map(PublicIntegalRecord::getIntegral).reduce(ZERO, BigDecimal::add);

        //统计-已分配积分
        BigDecimal alreadyDistributionIntegral=recordList.stream()
                .filter(i -> i.getStatus() == IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_distribu)
                .map(PublicIntegalRecord::getIntegral).reduce(ZERO, BigDecimal::add);

        //统计-已存放(已放入公共积分库)
        BigDecimal waitDistributionIntegral=recordList.stream()
                .filter(i -> i.getStatus() == IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_YCF)
                .map(PublicIntegalRecord::getIntegral).reduce(ZERO, BigDecimal::add);

        //统计-其他积分
        BigDecimal otherIntegral=recordList.stream()
                .filter(i -> i.getStatus() == IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_CREATE
                        || i.getStatus() == IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_COMPLETE
                        || i.getStatus() == IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_INVALIDATION
                        || i.getStatus() == IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_DJS)
                .map(PublicIntegalRecord::getIntegral).reduce(ZERO, BigDecimal::add);

        //计算-总积分
        BigDecimal totalIntegral=freezingIntegral.add(alreadyDistributionIntegral).add(otherIntegral);

        //返回-公共积分库-响应对象
        return PublicIntegralLibraryResponse.builder()
                .freezingIntegral(freezingIntegral)
                .alreadyDistributionIntegral(alreadyDistributionIntegral)
                .totalIntegral(totalIntegral)
                .otherIntegral(otherIntegral)
                .waitDistributionIntegral(waitDistributionIntegral)
                .build();
    }

    @Override
    public List<PublicIntegalRecord> findListByOrderIdAndUid(String orderNo, Integer uid) {
        //定义查询对象
        LambdaQueryWrapper<PublicIntegalRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(PublicIntegalRecord::getUid, uid);
        lqw.eq(PublicIntegalRecord::getLinkId, orderNo);
        lqw.eq(PublicIntegalRecord::getStatus,IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_CREATE);

        //得到数据
        List<PublicIntegalRecord> recordList = dao.selectList(lqw);

        //验证非空
        if (CollUtil.isEmpty(recordList)) {
            return recordList;
        }

        //返回
        return recordList;
    }

    @Override
    public PublicIntegralLibraryResponse getPublicIntegralLibrary(Integer raId) {
        //定义变量
        PublicIntegralLibraryResponse response=null;  //公共积分库-响应对象
        LambdaQueryWrapper<PublicIntegalRecord> lqw=null; //查询对象
        List<PublicIntegalRecord> recordList =null;
        raId = null; //查询全部，关闭区域代理积分

        //验证-条件是否为空
        if(raId!=null&&raId>0){
            //得到-区域代理信息
            RegionalAgency regionalAgency = regionalAgencyService.getById(raId);
            if(regionalAgency == null){
                throw new CrmebException("该区域代理已不存在！");
            }

            //设置-查询条件
            lqw = Wrappers.lambdaQuery();
            lqw.eq(PublicIntegalRecord::getRaId,raId);

            //得到-待统计公共积分数据
            recordList = dao.selectList(lqw);

            //得到-统计数据
            response = this.getP(new DecimalFormat("#.##"),recordList);

            //验证-区域代理可分配积分是否为空
            if(regionalAgency.getDistributableIntegral() == null) regionalAgency.setDistributableIntegral(ZERO);

            //得到-可分配积分并统计总积分
            response.setDistributableIntegral(regionalAgency.getDistributableIntegral());
            response.setTotalIntegral(response.getTotalIntegral().add(response.getDistributableIntegral()));
        }else{
            //设置-查询条件
            lqw = Wrappers.lambdaQuery();
            //lqw.eq(PublicIntegalRecord::getRaId,0); //查询系统公共积分库
            //lqw.or().isNull(PublicIntegalRecord::getRaId);

            //得到-待统计公共积分数据
            recordList = dao.selectList(lqw);

            //得到-统计数据
            response = this.getP(new DecimalFormat("#.##"),recordList);

            //验证-系统可分配积分是否为空
            String  distributableIntegralStr= systemConfigService.getValueByKeyException("distributableIntegral"); //可分配积分
            BigDecimal distributableIntegral = new BigDecimal(distributableIntegralStr);
            if(distributableIntegral == null) distributableIntegral= ZERO;

            //得到-可分配积分并统计总积分
            response.setDistributableIntegral(distributableIntegral);
            response.setTotalIntegral(response.getTotalIntegral().add(response.getDistributableIntegral()));
        }

        //返回
        return response;
    }

    @Override
    public Boolean insert(PublicIntegalRecord publicIntegalRecord) {
        //执行保存
        return dao.insert(publicIntegalRecord)>=1;
    }

    @Override
    public PageInfo<PublicIntegalRecordResponse> getList(PublicIntegalRecordSearchRequest request, PageParamRequest pageParamRequest) {
        //分页
        Page<PublicIntegalRecordResponse> articlePage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //定义查询对象
        LambdaQueryWrapper<PublicIntegalRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //条件-状态
        if(StringUtils.isNotBlank(String.valueOf(request.getStatus())) && request.getStatus() > 0){
            lambdaQueryWrapper.eq(PublicIntegalRecord::getStatus,request.getStatus());
        }

        //条件-linkType关联类型
        if(StringUtils.isNotBlank(String.valueOf(request.getLinkType())) && request.getLinkType() > 0){
            lambdaQueryWrapper.eq(PublicIntegalRecord::getLinkType,request.getLinkType());
        }

        //条件-关键字搜索
        if(StringUtils.isNotBlank(request.getKeywords())){
            if(StringUtils.isNumeric(request.getKeywords())){
                lambdaQueryWrapper.eq(PublicIntegalRecord::getUid,request.getKeywords());
            }else{
                lambdaQueryWrapper.and(i -> i
                        .like(PublicIntegalRecord::getTitle, request.getKeywords()).or()
                        .like(PublicIntegalRecord::getMark, request.getKeywords()));
            }
        }

        //排序
        lambdaQueryWrapper.orderByDesc(PublicIntegalRecord::getId); //排序

        //得到-数据
        List<PublicIntegalRecord> publicIntegalRecordList= dao.selectList(lambdaQueryWrapper);
        List<PublicIntegalRecordResponse> publicIntegalRecordResponseList=new ArrayList<>();
        for (PublicIntegalRecord record:publicIntegalRecordList) {
            //实例化-公共接口记录-响应对象
            PublicIntegalRecordResponse publicIntegalRecordResponse=new PublicIntegalRecordResponse();
            BeanUtils.copyProperties(record, publicIntegalRecordResponse);//将-拼团商品信息-转换为-拼团商品详情H5

            //得到-用户名称
            User user = userService.getById(publicIntegalRecordResponse.getUid());
            if(user == null) continue;
            publicIntegalRecordResponse.setNickname(user.getNickname());

            //验证状态
            switch (publicIntegalRecordResponse.getStatus()){
                case IntegralRecordConstants.PUBLIC_INTEGRAL_RECORD_STATUS_YCF:
                    StoreOrder storeOrder = storeOrderService.getById(publicIntegalRecordResponse.getLinkId());
                    if(storeOrder!=null) publicIntegalRecordResponse.setLinkId(storeOrder.getOrderId());
                    break;
            }

            //添加到-公共积分记录list响应集合
            publicIntegalRecordResponseList.add(publicIntegalRecordResponse);
        }

        //返回
        return CommonPage.copyPageInfo(articlePage, publicIntegalRecordResponseList);
    }
}
