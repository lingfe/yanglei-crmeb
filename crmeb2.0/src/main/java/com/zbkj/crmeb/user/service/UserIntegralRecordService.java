package com.zbkj.crmeb.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.system.vo.SystemGroupDataSignConfigVo;
import com.zbkj.crmeb.user.model.UserIntegralRecord;
import com.zbkj.crmeb.user.request.AdminIntegralSearchRequest;
import com.zbkj.crmeb.user.response.UserIntegralRecordResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户积分记录表-Service层接口
 * @author: 零风
 * @CreateDate: 2021/12/27 10:22
 */
public interface UserIntegralRecordService extends IService<UserIntegralRecord> {

    /**
     * 将未读的积分记录改变为已读
     * @param id    积分记录ID标识
     * @Author 零风
     * @Date  2022/3/14
     */
    Boolean takeUnreadChangeRead(Integer id);

    /**
     * 根据条件获取积分记录list
     * @param record 积分记录
     * @param lastSql 附加条件
     * @Author 零风
     * @Date  2022/3/14
     * @return 记录list
     */
    List<UserIntegralRecord> whereGet(UserIntegralRecord record,String lastSql);

    /**
     * 获取用户未读的积分记录
     * @param num   数量
     * @Author 零风
     * @Date  2022/3/14
     * @return 记录list
     */
    List<UserIntegralRecord> getUnreadUserIntegralRecordList(Integer num);

    /**
     * 获取用户最新的积分收入记录
     * @Author 零风
     * @Date  2022/3/14
     * @return 记录
     */
    UserIntegralRecord getNewestIncomeUserIntegralRecord();

    /**
     * 新增-用户积分记录
     * @Author 零风
     * @Date  2021/10/22
     * @return 积分记录
     */
    UserIntegralRecord getUserIntegralRecord(Integer uid,
             BigDecimal integralBalance,
             String likeId,
             String likType, Integer type,
             Integer status, BigDecimal integral,
             String other);

    /**
     * 根据订单编号、uid获取记录列表
     * @param orderNo 订单编号
     * @param uid 用户uid
     * @return 记录列表
     */
    List<UserIntegralRecord> findListByOrderIdAndUid(String orderNo, Integer uid);

    /**
     * 查询需要解冻的-用户积分记录
     * @Author 零风
     * @Date  2022/2/23
     * @return 用户积分记录list
     */
    List<UserIntegralRecord> getNeedThawUserIntegralRecordList();

    /**
     * 积分解冻
     * -用户积分记录解冻、公共积分记录解冻
     * @Author 零风
     * @Date  2022/2/23
     */
    void integralThawTask();

    /**
     * PC后台列表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @return 记录列表
     */
    PageInfo<UserIntegralRecordResponse> findAdminList(AdminIntegralSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 根据条件-计算统计积分总数
     * @param uid 用户uid
     * @param type 类型：1-增加，2-扣减
     * @param date 日期
     * @param linkType 关联类型
     * @param status 状态
     * @Author 零风
     * @Date  2021/12/27
     * @return 积分总数
     */
    BigDecimal getSumIntegral(Integer uid, Integer type, String date, String linkType,Integer status);

    /**
     * H5用户积分列表
     * @param uid 用户uid
     * @param pageParamRequest 分页参数
     * @Author 零风
     * @Date  2021/12/27
     * @return  积分记录列表
     */
    List<UserIntegralRecordResponse> findUserIntegralRecordList(Integer uid, PageParamRequest pageParamRequest);

    /**
     * 获取用户冻结的积分
     * @param uid 用户uid
     * @return 积分数量
     */
    BigDecimal getFrozenIntegralByUid(Integer uid);

    /**
     * 分享朋友圈得积分
     * @return
     */
    SystemGroupDataSignConfigVo sharePYQPoints();

    /**
     * 分享好友得积分
     * @param shareUserId 分享人id
     * @return
     */
    SystemGroupDataSignConfigVo shareFriendsPoints(Integer shareUserId);
}