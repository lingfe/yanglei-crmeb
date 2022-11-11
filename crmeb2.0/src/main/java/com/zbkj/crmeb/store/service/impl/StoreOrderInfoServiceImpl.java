package com.zbkj.crmeb.store.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.utils.DateUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.front.vo.OrderInfoDetailVo;
import com.zbkj.crmeb.store.dao.StoreOrderInfoDao;
import com.zbkj.crmeb.store.model.StoreOrderInfo;
import com.zbkj.crmeb.store.request.StoreOrderInfoSearchRequest;
import com.zbkj.crmeb.store.service.StoreOrderInfoService;
import com.zbkj.crmeb.store.service.StoreProductReplyService;
import com.zbkj.crmeb.store.vo.StoreOrderInfoOldVo;
import com.zbkj.crmeb.store.vo.StoreOrderInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * StoreOrderInfoServiceImpl 接口实现
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
public class StoreOrderInfoServiceImpl extends ServiceImpl<StoreOrderInfoDao, StoreOrderInfo>
        implements StoreOrderInfoService {

    @Resource
    private StoreOrderInfoDao dao;

    @Autowired
    private StoreProductReplyService storeProductReplyService;

    @Override
    public List<StoreOrderInfo> getWhereProductIdAndDate(Integer productId, String date) {
        //定义查询对象
        LambdaQueryWrapper<StoreOrderInfo> storeOrderInfoLambdaQueryWrapper=new LambdaQueryWrapper<>();

        //条件-商品ID
        storeOrderInfoLambdaQueryWrapper.eq(StoreOrderInfo::getProductId,productId);

        //条件-时间范围
        if (StrUtil.isNotBlank(date)) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            storeOrderInfoLambdaQueryWrapper.between(StoreOrderInfo::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }

        //读取-订单详情
        List<StoreOrderInfo> storeOrderInfoList = dao.selectList(storeOrderInfoLambdaQueryWrapper);
        return storeOrderInfoList;
    }

    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-28
    * @return List<StoreOrderInfo>
    */
    @Override
    public List<StoreOrderInfo> getList(StoreOrderInfoSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreOrderInfo 类的多条件查询
        LambdaQueryWrapper<StoreOrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        StoreOrderInfo model = new StoreOrderInfo();
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据订单ID标识，查询订单详情。
     * @param orderList 订单ID标识list
     * @return  订单详情map
     */
    @Override
    public HashMap<Integer, List<StoreOrderInfoOldVo>> getMapInId(List<Integer> orderList){
        //实例化-存放订单详情map对象
        HashMap<Integer, List<StoreOrderInfoOldVo>> map = new HashMap<>();
        if(orderList.size() < 1){
            return map;
        }

        //定义-查询订单详情-lambada对象
        LambdaQueryWrapper<StoreOrderInfo> lambdaQueryWrapper = Wrappers.lambdaQuery();
        //条件-订单ID标识
        lambdaQueryWrapper.in(StoreOrderInfo::getOrderId, orderList);

        //得到-订单详情数据list集合
        List<StoreOrderInfo> systemStoreStaffList = dao.selectList(lambdaQueryWrapper);
        if(systemStoreStaffList.size() < 1){
            return map;
        }

        //循环处理-订单详情数据list集合
        for (StoreOrderInfo storeOrderInfo : systemStoreStaffList) {
            //实例化-查询订单详情vo对象
            StoreOrderInfoOldVo StoreOrderInfoVo = new StoreOrderInfoOldVo();
            BeanUtils.copyProperties(storeOrderInfo, StoreOrderInfoVo, "info");

            //解析-商品信息json对象
            StoreOrderInfoVo.setInfo(JSON.parseObject(storeOrderInfo.getInfo(), OrderInfoDetailVo.class));

            //验证-map是否包含该key
            if(map.containsKey(storeOrderInfo.getOrderId())){
                //是-添加值
                map.get(storeOrderInfo.getOrderId()).add(StoreOrderInfoVo);
            }else{
                //否-添加key
                List<StoreOrderInfoOldVo> storeOrderInfoVoList = new ArrayList<>();
                storeOrderInfoVoList.add(StoreOrderInfoVo);
                map.put(storeOrderInfo.getOrderId(), storeOrderInfoVoList);
            }
        }

        //返回
        return map;
    }

    @Override
    public List<StoreOrderInfoOldVo> getOrderListByOrderId(Integer orderId){
        LambdaQueryWrapper<StoreOrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreOrderInfo::getOrderId, orderId);
        List<StoreOrderInfo> systemStoreStaffList = dao.selectList(lambdaQueryWrapper);
        if(systemStoreStaffList.size() < 1){
            return null;
        }

        List<StoreOrderInfoOldVo> storeOrderInfoVoList = new ArrayList<>();
        for (StoreOrderInfo storeOrderInfo : systemStoreStaffList) {
            //解析商品详情JSON
            StoreOrderInfoOldVo storeOrderInfoVo = new StoreOrderInfoOldVo();
            BeanUtils.copyProperties(storeOrderInfo, storeOrderInfoVo, "info");

            storeOrderInfoVo.setInfo(JSON.parseObject(storeOrderInfo.getInfo(), OrderInfoDetailVo.class));
            storeOrderInfoVo.getInfo().setIsReply(
                    storeProductReplyService.isReply(storeOrderInfoVo.getUnique(), storeOrderInfoVo.getOrderId()) ? 1 : 0
            );
            storeOrderInfoVoList.add(storeOrderInfoVo);
        }
        return storeOrderInfoVoList;
    }

    /**
     * 根据id集合查询数据，返回 map
     * @param orderId 订单id
     * @return HashMap<Integer, StoreCart>
     */
    @Override
    public List<StoreOrderInfoVo> getVoListByOrderId(Integer orderId){
        LambdaQueryWrapper<StoreOrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreOrderInfo::getOrderId, orderId);
        List<StoreOrderInfo> systemStoreStaffList = dao.selectList(lambdaQueryWrapper);
        if(systemStoreStaffList.size() < 1){
            return null;
        }

        List<StoreOrderInfoVo> storeOrderInfoVoList = new ArrayList<>();
        for (StoreOrderInfo storeOrderInfo : systemStoreStaffList) {
            //解析商品详情JSON
            StoreOrderInfoVo storeOrderInfoVo = new StoreOrderInfoVo();
            BeanUtils.copyProperties(storeOrderInfo, storeOrderInfoVo, "info");
            storeOrderInfoVo.setInfo(JSON.parseObject(storeOrderInfo.getInfo(), OrderInfoDetailVo.class));
            // TODO 商品是否已评论
//            storeOrderInfoVo.getInfo().setIsReply(
//                    storeProductReplyService.isReply(storeOrderInfoVo.getUnique(), storeOrderInfoVo.getOrderId()) ? 1 : 0
//            );
            storeOrderInfoVoList.add(storeOrderInfoVo);
        }
        return storeOrderInfoVoList;
    }

    /**
     * 新增订单详情
     * @param storeOrderInfos 订单详情集合
     * @return 订单新增结果
     */
    @Override
    public boolean saveOrderInfos(List<StoreOrderInfo> storeOrderInfos) {
        return saveBatch(storeOrderInfos);
    }

    /**
     * 通过订单编号和规格号查询
     * @param uni 规格号
     * @param orderId 订单编号
     * @return StoreOrderInfo
     */
    @Override
    public StoreOrderInfo getByUniAndOrderId(String uni, Integer orderId) {
        //带 StoreOrderInfo 类的多条件查询
        LambdaQueryWrapper<StoreOrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreOrderInfo::getOrderId, orderId);
        lambdaQueryWrapper.eq(StoreOrderInfo::getUnique, uni);
        return dao.selectOne(lambdaQueryWrapper);
    }
}

