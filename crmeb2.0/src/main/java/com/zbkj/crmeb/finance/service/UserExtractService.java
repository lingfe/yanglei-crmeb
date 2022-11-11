package com.zbkj.crmeb.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.model.UserExtract;
import com.zbkj.crmeb.finance.request.UserExtractRequest;
import com.zbkj.crmeb.finance.request.UserExtractSearchRequest;
import com.zbkj.crmeb.finance.response.BalanceResponse;
import com.zbkj.crmeb.finance.response.UserExtractResponse;
import com.zbkj.crmeb.front.response.UserExtractRecordResponse;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户提现记录表-service层接口
 */
public interface UserExtractService extends IService<UserExtract> {

    /**
     * 账户余额提现到微信零钱(微信支付平台)
     * @Author 零风
     * @Date  2022/4/27
     * @return
     */
    HashMap<String, Object> weixinPayToChange(BigDecimal extractPrice, Integer appidType, Integer uid);

    /**
     * 佣金提现审核(新)(云账户)
     * @param isExtract     是否-通过提现申请
     * @param id            提现申请ID标识
     * @param backMessage   拒绝：理由/原因
     * @return 操作结果
     */
    Boolean isExtract(Boolean isExtract, Integer id,String backMessage);

    /**
     * 申请打款-至微信零钱(云账户)
     * @param userExtractRequest
     * @return
     */
    Boolean applyWithdrawalToWeixin(UserExtractRequest userExtractRequest);

    /**
     * 申请打款-到支付宝(支付宝提现)(云账户)
     * @return
     */
    Boolean applyWithdrawalToAlipay(UserExtractRequest userExtractRequest);

    /**
     * 申请打款-到银行卡(银行卡提现)(云账户)
     * @return
     */
    Boolean applyWithdrawalToBankCard(UserExtractRequest userExtractRequest) ;

    /**
     * 获取-银行卡打款申请记录
     * @param pageParamRequest  分页对象
     * @return  数据
     * @throws Exception
     */
    @Deprecated
    List<Object> getList(PageParamRequest pageParamRequest) throws Exception;

    /**
     * 银行卡-下单打款-回调接口
     * @param request
     * @return
     */
    String applyCallback(HttpServletRequest request);

    /**
     * 银行卡-打款申请重试
     * @param userExtract_id  提现记录ID标识
     * @return
     */
    Map<String,Object> retry(Integer userExtract_id) ;

    /**
     * 取消-银行卡打款申请
     * @param userExtract_id  提现记录ID标识
     * @return
     */
    Boolean cancel(Integer userExtract_id) ;

    /**
     * 提现记录
     * @param request   搜索参数
     * @param pageParamRequest  分页对象
     * @return
     */
    List<UserExtract> getList(UserExtractSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 提现总金额
     */
    BalanceResponse getBalance(String dateLimit);

    /**
     * 提现总金额
     * @author Mr.Zhang
     * @since 2020-05-11
     * @return BalanceResponse
     */
    BigDecimal getWithdrawn(String startTime,String endTime);

    /**
     * 审核中总金额
     * @author Mr.Zhang
     * @since 2020-05-11
     * @return BalanceResponse
     */
    BigDecimal getWithdrawning(String startTime, String endTime);

    /**
     * 佣金提现申请
     * @param request
     * @param userId
     * @return
     */
    @Deprecated
    Boolean create(UserExtractRequest request, Integer userId);

    BigDecimal getFreeze(Integer userId);

    UserExtractResponse getUserExtractByUserId(Integer userId);

    List<UserExtract> getListByUserIds(List<Integer> userIds);

    /**
     * 提现审核(旧)
     * @param id            提现ID标识
     * @param status        审核状态： -1=未通过、0=审核中、1=已提现
     * @param backMessage   驳回原因
     * @Author 零风
     * @Date  2021/12/23
     * @return
     */
    Boolean updateStatus(Integer id,Integer status,String backMessage);

    /**
     * 根据用户id获取提现记录(分页)
     * @param userId    用户id
     * @param pageParamRequest  分页参数
     * @return 分页记录
     */
    PageInfo<UserExtractRecordResponse> getPageInfo(UserExtractSearchRequest request,Integer userId, PageParamRequest pageParamRequest);

    /**
     * 用户-累计已提取佣金
     * @param userId 用户ID标识
     * @Author 零风
     * @Date  2022/1/5
     * @return 总和
     */
    BigDecimal getExtractTotalMoney(Integer userId);

    /**
     * 申请提现-佣金余额提现(旧方式(手动打款))
     * @author Mr.Zhang
     * @since 2020-06-08
     */
    Boolean extractApply(UserExtractRequest request);

}
