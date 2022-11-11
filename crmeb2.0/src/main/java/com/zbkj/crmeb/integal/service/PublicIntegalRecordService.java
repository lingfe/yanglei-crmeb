package com.zbkj.crmeb.integal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.integal.model.PublicIntegalRecord;
import com.zbkj.crmeb.integal.request.PublicIntegalRecordSearchRequest;
import com.zbkj.crmeb.integal.response.PublicIntegalRecordResponse;
import com.zbkj.crmeb.integal.response.PublicIntegralLibraryResponse;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
* 公共积分记录表-service层接口
* @author: 零风
* @CreateDate: 2021/10/18 13:46
*/
public interface PublicIntegalRecordService extends IService<PublicIntegalRecord> {

    /**
     * 查询需要解冻的-公共积分记录
     * @Author 零风
     * @Date  2022/2/23
     * @return 公共积分记录列表
     */
    List<PublicIntegalRecord> getNeedThawPublicIntegalRecordList();

    /**
     * 公共函数-统计公共积分
     * @param df            格式化
     * @param recordList    待-统计数据
     * @return
     */
    PublicIntegralLibraryResponse getP(DecimalFormat df, List<PublicIntegalRecord> recordList);

    /**
     * 根据订单编号、uid获取记录列表
     * @param orderNo 订单编号
     * @param uid 用户uid
     * @author  零风
     * @Date  2021/10/18
     * @return 记录列表
     */
    List<PublicIntegalRecord> findListByOrderIdAndUid(String orderNo, Integer uid);

    /**
     * 获取-公共积分库
     * @param raId 区域代理表ID标识
     * @Author 零风
     * @Date  2021/10/18
     * @return 公共积分库-响应对象
     */
    PublicIntegralLibraryResponse getPublicIntegralLibrary(Integer raId);

    /**
     * 分页查询
     * @param request
     * @param pageParamRequest
     * @author  零风
     * @Date  2021/10/18
     * @return  记录列表
     */
    PageInfo<PublicIntegalRecordResponse> getList(PublicIntegalRecordSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 添加
     * @param publicIntegalRecord
     * @author 零风
     * @Date  2021/10/18
     * @return
     */
    Boolean insert(PublicIntegalRecord publicIntegalRecord);

    /**
     * 将积分放入公共积分库
     * @param pir
     */
    void putPublicIntegalLibrary(PublicIntegalRecord pir);

    /**
     * 更新-推广人用户积分(消费有推广模式)
     * @Author 零风
     * @Date  2021/10/19
     */
    void updateUserIntegal();

    /**
     * 从可分配积分中分配积分
     * @Author 零风
     * @Date  2022/6/17 13:44
     * @return
     */
    void distributionTask();

    /**
     * 积分放入公共积分库
     * @param pir               公共积分记录对象
     * @param isHandle          是否处理
     * @Author 零风
     * @Date  2021/10/22
     */
    PublicIntegalRecord extractedYCF(PublicIntegalRecord pir,Boolean isHandle,Integer state);

    /**
     * 添加-公共积分记录
     * @param uid           用户ID
     * @param linkId        关联ID
     * @param linkType      关联类型：（1=订单,2=提现，3=转余额）
     * @param type          积分类型：1-增加，2-扣减（提现），3-扣减（消费）
     * @param integral          积分
     * @param integralBalance   用户积分余额
     * @param status            状态
     * @param isHandle          是否处理
     * @param spreadUid         推荐人ID(表示该公共积分是他推荐的而来的)
     * @param other             其他参数
     * @Author 零风
     * @Date  2022/4/23
     * @return 公共积分对象
     */
    PublicIntegalRecord add(
            Integer uid,
            String linkId,
            BigDecimal integral,
            BigDecimal integralBalance,
            Integer linkType,
            Integer type,
            Integer status,
            Boolean isHandle,
            Integer spreadUid,String other);
}
