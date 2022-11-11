package com.zbkj.crmeb.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.IntegralRecordConstants;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.marketing.model.StoreIntegalShop;
import com.zbkj.crmeb.marketing.service.StoreIntegalShopService;
import com.zbkj.crmeb.user.dao.UserIntegralExchangeRecordDao;
import com.zbkj.crmeb.user.dao.UserIntegralRecordDao;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserIntegralExchangeRecord;
import com.zbkj.crmeb.user.model.UserIntegralRecord;
import com.zbkj.crmeb.user.request.AdminIntegralSearchRequest;
import com.zbkj.crmeb.user.request.UserIntegralExchangeRecordSearchRequest;
import com.zbkj.crmeb.user.response.UserIntegralExchangeRecordResponse;
import com.zbkj.crmeb.user.response.UserIntegralRecordResponse;
import com.zbkj.crmeb.user.service.UserIntegralExchangeRecordService;
import com.zbkj.crmeb.user.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: crmeb
 * @description: 用户积分兑换记录表-service实现类
 * @author: 零风
 * @create: 2021-07-07 15:16
 **/
@Service
public class UserIntegralExchangeRecordServiceImpl extends ServiceImpl<UserIntegralExchangeRecordDao, UserIntegralExchangeRecord> implements UserIntegralExchangeRecordService {

    @Resource
    private UserIntegralExchangeRecordDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreIntegalShopService storeIntegalShopService;

    @Override
    public PageInfo<UserIntegralExchangeRecordResponse> findAdminList(UserIntegralExchangeRecordSearchRequest request, PageParamRequest pageParamRequest) {
        //得到分页对象
        Page<UserIntegralExchangeRecordResponse> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        //定义查询条件
        LambdaQueryWrapper<UserIntegralExchangeRecord> lqw = Wrappers.lambdaQuery();

        //条件-用户id
        if(ObjectUtil.isNotNull(request.getUserId())&&request.getUserId()>0){
            lqw.eq(UserIntegralExchangeRecord::getUserId, request.getUserId());
        }

        //条件-订单ID
        if(ObjectUtil.isNotNull(request.getOrderId())&&request.getOrderId()>0){
            lqw.eq(UserIntegralExchangeRecord::getOrderId,request.getOrderId());
        }

        //排序
        lqw.orderByDesc(UserIntegralExchangeRecord::getCrtDatetime);

        //得到数据
        List<UserIntegralExchangeRecord> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }

        //遍历处理
        List<UserIntegralExchangeRecordResponse> responseList = list.stream().map(i -> {
            //实例化响应对象
            UserIntegralExchangeRecordResponse response = new UserIntegralExchangeRecordResponse();
            BeanUtils.copyProperties(i, response);

            //获取用户昵称
            User user = userService.getById(i.getUserId());
            if(user!=null){
                response.setNickName(user.getNickname());
            }

            //获取商品名称
            StoreIntegalShop storeIntegalShop=storeIntegalShopService.getByIdException(i.getIntegralId());
            if(storeIntegalShop!=null){
                response.setIntegralProductName(storeIntegalShop.getTitle());
                response.setImg(storeIntegalShop.getImage());
                response.setProductId(storeIntegalShop.getProductId());
                if(storeIntegalShop.getPayType()==1){
                    response.setPrice(storeIntegalShop.getPrice());
                }
            }

            //返回当前次
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }
}
