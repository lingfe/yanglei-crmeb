package com.zbkj.crmeb.marketing.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.DateUtil;
import com.zbkj.crmeb.front.response.ProductAttrResponse;
import com.zbkj.crmeb.front.response.StoreIntegalShopDetailH5Response;
import com.zbkj.crmeb.marketing.dao.StoreIntegalShopDao;
import com.zbkj.crmeb.marketing.model.StoreIntegalShop;
import com.zbkj.crmeb.marketing.request.StoreIntegalShopRequest;
import com.zbkj.crmeb.marketing.request.StoreIntegalShopSearchRequest;
import com.zbkj.crmeb.marketing.response.StoreIntegalShopResponse;
import com.zbkj.crmeb.marketing.service.StoreIntegalShopService;
import com.zbkj.crmeb.seckill.model.StoreSeckill;
import com.zbkj.crmeb.store.model.*;
import com.zbkj.crmeb.store.request.StoreProductAttrValueRequest;
import com.zbkj.crmeb.store.response.StoreProductAttrValueResponse;
import com.zbkj.crmeb.store.response.StoreProductResponse;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.system.service.SystemGroupDataService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @program: crmeb
 * @description: 商城-营销-积分商品表service实现类
 * @author: 零风
 * @create: 2021-07-01 15:28
 **/
@Service
public class StoreIntegalShopServiceImpl extends ServiceImpl<StoreIntegalShopDao, StoreIntegalShop> implements StoreIntegalShopService {

    @Resource
    private StoreIntegalShopDao dao;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private StoreProductAttrService storeProductAttrService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private StoreProductAttrResultService storeProductAttrResultService;

    @Autowired
    private StoreProductDescriptionService storeProductDescriptionService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreProductRelationService storeProductRelationService;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemGroupDataService systemGroupDataService;

    @Override
    public PageInfo<StoreIntegalShopResponse> getList(StoreIntegalShopSearchRequest request, PageParamRequest pageParamRequest) {
        //获取分页对象
        Page<StoreIntegalShop> combinationPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //lambada参数条件
        LambdaQueryWrapper<StoreIntegalShop> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //条件-是否删除=否
        lambdaQueryWrapper.eq(StoreIntegalShop::getIsDel, false);

        //条件-是否显示
        if(ObjectUtil.isNotNull(request.getIsShow())&&request.getIsShow()!=0){
            boolean isShow=true;
            if(request.getIsShow()==2)isShow=false;
            lambdaQueryWrapper.eq(StoreIntegalShop::getIsShow,isShow);
        }

        //条件-状态查询
        if (ObjectUtil.isNotNull(request.getState())) {
            lambdaQueryWrapper.eq(StoreIntegalShop::getState, request.getState() == 1);
        }

        //条件-关键字查询
        if (StrUtil.isNotEmpty(request.getKeywords())) {
            lambdaQueryWrapper.and(i -> i.like(StoreIntegalShop::getProductId, request.getKeywords())
                    .or().like(StoreIntegalShop::getId, request.getKeywords())
                    .or().like(StoreIntegalShop::getTitle, request.getKeywords()));
        }
        //条件-排序
        lambdaQueryWrapper.orderByDesc(StoreIntegalShop::getSort, StoreIntegalShop::getId);

        //得到积分兑换商品list
        List<StoreIntegalShop> StoreIntegalShopList = dao.selectList(lambdaQueryWrapper);
        //验证非空
        if (CollUtil.isEmpty(StoreIntegalShopList)) {
            //等于空，返回空对象
            return CommonPage.copyPageInfo(combinationPage, CollUtil.newArrayList());
        }

        //转换处理返回对象
        List<StoreIntegalShopResponse> responseList = StoreIntegalShopList.stream().map(integalShop -> {
            //实例化返回对象
            StoreIntegalShopResponse integalShopResponse = new StoreIntegalShopResponse();
            //转换
            BeanUtils.copyProperties(integalShop, integalShopResponse);

            //再这里处理返回参数
            integalShopResponse.setStopTimeStr(DateUtil.timestamp2DateStr(integalShop.getStopTime(), Constants.DATE_FORMAT_DATE));
            return integalShopResponse;
        }).collect(Collectors.toList());

        //返回
        return CommonPage.copyPageInfo(combinationPage, responseList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBargain(StoreIntegalShopRequest request) {
        //过滤未选择的商品属性，验证是否开启多规格
        Boolean specType=request.getSpecType();
        if (specType) {
            List<StoreProductAttrValueRequest>  ListStoreProductAttrValueRequest= request.getAttrValue();
            Stream<StoreProductAttrValueRequest> streamStoreProductAttrValueRequest = ListStoreProductAttrValueRequest.stream();
            Stream<StoreProductAttrValueRequest> filterStoreProductAttrValueRequest = streamStoreProductAttrValueRequest.filter(StoreProductAttrValueRequest::getChecked);
            request.setAttrValue(filterStoreProductAttrValueRequest.collect(Collectors.toList()));
            //request.setAttrValue(request.getAttrValue().stream().filter(StoreProductAttrValueRequest::getChecked).collect(Collectors.toList()));
        }

        //验证是否选择商品属性
        if (CollUtil.isEmpty(request.getAttrValue())) {
            throw new CrmebException("请选择规格");
        }

        //验证属性是否正确
        for (StoreProductAttrValueRequest attr : request.getAttrValue()) {
            if (ObjectUtil.isNull(attr.getPrice()) || attr.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new CrmebException("请正确输入价格");
            }
            if (ObjectUtil.isNull(attr.getQuota()) || attr.getQuota() <= 0) {
                throw new CrmebException("请正确输入限量");
            }
            if (attr.getQuota() > attr.getStock()) {
                throw new CrmebException("限量不能大于库存");
            }
        }

        //实例化对象
        StoreIntegalShop storeIntegalShop=new StoreIntegalShop();
        BeanUtils.copyProperties(request,storeIntegalShop);//转换
        storeIntegalShop.setId(null);

        //验证是否开启时间段
        if(request.getIsDate()){
            // 活动开始时间
            storeIntegalShop.setStartTime(DateUtil.dateStr2Timestamp(request.getStartTime(), Constants.DATE_TIME_TYPE_BEGIN));

            //验证结束时间
            Long stopTime = DateUtil.dateStr2Timestamp(request.getStopTime(), Constants.DATE_TIME_TYPE_END);
            if (stopTime <= System.currentTimeMillis()) {
                throw new CrmebException("活动结束时间不能小于当前时间");
            }
            storeIntegalShop.setStopTime(stopTime);
        }

        //头图
        String image=request.getImage();
        image=systemAttachmentService.clearPrefix(image);
        storeIntegalShop.setImage(image);

        //轮播图
        String images=request.getImages();
        images=systemAttachmentService.clearPrefix(images);
        storeIntegalShop.setImages(images);

        //其他参数
        storeIntegalShop.setAddTime(System.currentTimeMillis());
        storeIntegalShop.setSales(0);

        // 计算价格 设置商品成本价和市场价
        List<StoreProductAttrValueRequest> attrValuesSortAsc = request
                .getAttrValue()
                .stream()
                .sorted(Comparator.comparing(StoreProductAttrValueRequest::getPrice))
                .collect(Collectors.toList());
        if(attrValuesSortAsc.size() >= 1){
            storeIntegalShop.setPrice(attrValuesSortAsc.get(0).getPrice());
            storeIntegalShop.setOtPrice(attrValuesSortAsc.get(0).getOtPrice());
            storeIntegalShop.setCost(attrValuesSortAsc.get(0).getOtPrice());
            storeIntegalShop.setStock(attrValuesSortAsc.stream().mapToInt(StoreProductAttrValueRequest::getStock).sum());
        }

        // 保存数据
        boolean save = save(storeIntegalShop);
        if (!save) throw new CrmebException("新增积分兑换商品失败");

        // sku处理
        if (specType) { // 多规格
            //验证属性
            if (CollUtil.isNotEmpty(request.getAttr()) && request.getAttr().size() > 0) {
                request.getAttr().forEach(e -> {
                    e.setId(null);
                    e.setProductId(storeIntegalShop.getId());
                    e.setAttrValues(StringUtils.strip(e.getAttrValues().replace("\"", ""), "[]"));
                    e.setType(Constants.PRODUCT_TYPE_INTEGAL);
                });

                //保存积分商品属性
                boolean attrSave = storeProductAttrService.saveBatch(request.getAttr());
                if (!attrSave) throw new CrmebException("新增积分商品属性名失败");
            }
        } else { //单规格
            //实例化商品属性对象
            StoreProductAttr singleAttr = new StoreProductAttr();
            singleAttr.setProductId(storeIntegalShop.getId())
                    .setAttrName("规格")
                    .setAttrValues("默认")
                    .setType(Constants.PRODUCT_TYPE_INTEGAL);

            //保存积分商品属性
            boolean attrAddResult = storeProductAttrService.save(singleAttr);
            if (!attrAddResult) throw new CrmebException("新增属性名失败");

            //实例化积分商品属性值对象
            StoreProductAttrValue singleAttrValue = new StoreProductAttrValue();
            BigDecimal commissionL1 = BigDecimal.ZERO;
            BigDecimal commissionL2 = BigDecimal.ZERO;

            //验证属性值
            if (request.getAttrValue().size() > 0) {
                //一级二级，佣金设置
                commissionL1 = null
                        != request.getAttrValue().get(0).getBrokerage()
                        ? request.getAttrValue().get(0).getBrokerage()
                        : BigDecimal.ZERO;
                commissionL2 = null
                        != request.getAttrValue().get(0).getBrokerageTwo()
                        ? request.getAttrValue().get(0).getBrokerageTwo()
                        : BigDecimal.ZERO;
            }

            //设置属性值
            singleAttrValue.setProductId(storeIntegalShop.getId())
                    .setStock(storeIntegalShop.getStock())
                    .setSuk("默认")
                    .setSales(storeIntegalShop.getSales())
                    .setPrice(storeIntegalShop.getPrice())
                    .setImage(systemAttachmentService.clearPrefix(storeIntegalShop.getImage()))
                    .setCost(storeIntegalShop.getCost())
                    .setType(Constants.PRODUCT_TYPE_INTEGAL)
                    .setOtPrice(storeIntegalShop.getOtPrice())
                    .setBrokerage(commissionL1)
                    .setBrokerageTwo(commissionL2);

            //保存属性值，与当前积分兑换商品对应，1对多
            boolean saveOrUpdateResult = storeProductAttrValueService.save(singleAttrValue);
            if (!saveOrUpdateResult) throw new CrmebException("新增属性值失败");
        }

        //商品属性值处理，验证非空
        if (null != request.getAttrValue() && request.getAttrValue().size() > 0) {
            //存放属性值
            List<StoreProductAttrValue> storeProductAttrValues = new ArrayList<>();

            //批量设置attrValues对象的商品id
            List<StoreProductAttrValueRequest> StoreIntegalShopAttrValueRequests = request.getAttrValue();
            StoreIntegalShopAttrValueRequests.forEach(e ->
                    e.setProductId(storeIntegalShop.getId())
            );

            //属性转换
            for (StoreProductAttrValueRequest attrValuesRequest : StoreIntegalShopAttrValueRequests) {
                //实例化属性值对象
                StoreProductAttrValue spav = new StoreProductAttrValue();
                BeanUtils.copyProperties(attrValuesRequest, spav);//转换

                //设置sku字段
                if (null == attrValuesRequest.getAttrValue()) {
                    break;
                }

                //读取sku值
                List<String> skuList = new ArrayList<>();
                for (Map.Entry<String, String> vo : attrValuesRequest.getAttrValue().entrySet()) {
                    skuList.add(vo.getValue());
                    spav.setSuk(String.join(",", skuList));
                }

                //设置处理属性值
                spav.setImage(systemAttachmentService.clearPrefix(spav.getImage()));
                spav.setAttrValue(JSON.toJSONString(attrValuesRequest.getAttrValue()));
                spav.setQuotaShow(spav.getQuota());
                spav.setType(Constants.PRODUCT_TYPE_INTEGAL);

                //添加到属性值list
                storeProductAttrValues.add(spav);
            }

            //验证并保存属性值
            if (storeProductAttrValues.size() > 0) {
                //保存
                boolean saveOrUpdateResult = storeProductAttrValueService.saveBatch(storeProductAttrValues);
                if (!saveOrUpdateResult) throw new CrmebException("新增属性详情失败");

                //属性详情
                StoreProductAttrResult attrResult = new StoreProductAttrResult(
                        0,
                        storeIntegalShop.getId(),
                        systemAttachmentService.clearPrefix(JSON.toJSONString(request.getAttrValue())),
                        DateUtil.getNowTime(),
                        Constants.PRODUCT_TYPE_INTEGAL);

                //保存属性详情
                boolean resultSave = storeProductAttrResultService.save(attrResult);
                if (!resultSave) throw new CrmebException("新增积分兑换商品属性详情失败");
            }
        }

        //处理富文本
        StoreProductDescription spd = new StoreProductDescription(
                storeIntegalShop.getId(),
                request.getContent().length() > 0 ? systemAttachmentService.clearPrefix(request.getContent()) : "",
                Constants.PRODUCT_TYPE_INTEGAL);

        //先根据商品id和type删除对应描述
        storeProductDescriptionService.deleteByProductId(spd.getProductId(), Constants.PRODUCT_TYPE_INTEGAL);

        //再进行保存
        boolean descSave = storeProductDescriptionService.save(spd);
        if (!descSave) throw new CrmebException("新增积分兑换商品详情失败");

        //返回
        return save;
    }

    @Override
    public Boolean deleteById(Integer id) {
        //实例化对象
        StoreIntegalShop storeIntegalShop=null;
        storeIntegalShop = getById(id);
        if(storeIntegalShop == null)throw new CrmebException("积分兑换商品不存在！");

        //验证日期是否结束
        if(storeIntegalShop.getIsDate()){
            long timeMillis = System.currentTimeMillis();
            if (storeIntegalShop.getState()== 2 && storeIntegalShop.getStartTime() <= timeMillis && timeMillis <= storeIntegalShop.getStopTime()) {
                throw new CrmebException("活动进行中，积分商品不支持删除");
            }
        }

        //执行删除
        storeIntegalShop=new StoreIntegalShop();
        storeIntegalShop.setId(id).setIsDel(true);
        return updateById(storeIntegalShop);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateIntegal(StoreIntegalShopRequest request) {
        //得到积分商品信息
        StoreIntegalShop exitx = getById(request.getId());
        if (ObjectUtil.isNull(exitx) || exitx.getIsDel()) throw new CrmebException("积分兑换商品不存在");

        //验证日期
        if(exitx.getIsDate()){
            long timeMillis = System.currentTimeMillis();
            if (exitx.getState() == 2 && exitx.getStartTime() <= timeMillis && timeMillis <= exitx.getStopTime()) {
                throw new CrmebException("进行中，积分商品不支持修改");
            }
        }

        //过滤未选择的商品属性，验证是否开启多规格
        if (request.getSpecType()) {
            request.setAttrValue(request.getAttrValue().stream().filter(StoreProductAttrValueRequest::getChecked).collect(Collectors.toList()));
        }

        //验证是否选择商品属性值
        if (CollUtil.isEmpty(request.getAttrValue())) {
            throw new CrmebException("请选择积分商品规格!");
        }

        //验证属性是否正确
        for (StoreProductAttrValueRequest attr : request.getAttrValue()) {
            if (ObjectUtil.isNull(attr.getPrice()) || attr.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new CrmebException("请正确输入价格!");
            }
            if (ObjectUtil.isNull(attr.getQuota()) || attr.getQuota() <= 0) {
                throw new CrmebException("请正确输入限量!");
            }
            if (attr.getQuota() > attr.getStock()) {
                throw new CrmebException("限量不能大于库存!");
            }
        }

        //实例化对象
        StoreIntegalShop storeIntegalShop = new StoreIntegalShop();
        BeanUtils.copyProperties(request, storeIntegalShop);//转换

        // 头图、轮播图
        storeIntegalShop.setImage(systemAttachmentService.clearPrefix(request.getImage()));
        storeIntegalShop.setImages(systemAttachmentService.clearPrefix(request.getImages()));

        //验证是否开启日期
        if(request.getIsDate()){
            // 活动开始结束时间
            storeIntegalShop.setStartTime(DateUtil.dateStr2Timestamp(request.getStartTime(), Constants.DATE_TIME_TYPE_BEGIN));
            storeIntegalShop.setStopTime(DateUtil.dateStr2Timestamp(request.getStopTime(), Constants.DATE_TIME_TYPE_END));
        }

        // 计算价格 设置商品成本价和市场价
        List<StoreProductAttrValueRequest> attrValuesSortAsc = request
                .getAttrValue()
                .stream()
                .sorted(Comparator.comparing(StoreProductAttrValueRequest::getPrice))
                .collect(Collectors.toList());
        if(attrValuesSortAsc.size() >= 1){
            storeIntegalShop.setPrice(attrValuesSortAsc.get(0).getPrice());
            storeIntegalShop.setOtPrice(attrValuesSortAsc.get(0).getOtPrice());
            storeIntegalShop.setCost(attrValuesSortAsc.get(0).getOtPrice());
            storeIntegalShop.setStock(attrValuesSortAsc.stream().mapToInt(StoreProductAttrValueRequest::getStock).sum());
        }

        // 修改数据
        boolean update = updateById(storeIntegalShop);
        if (!update) throw new CrmebException("编辑拼团商品失败");

        // 对attr表做覆盖式更新，删除原有数据保存现有数据
        if (request.getSpecType()) {

            //验证多规格属性
            if (!CollUtil.isNotEmpty(request.getAttr()) || request.getAttr().size() <= 0) {
                throw new CrmebException("请选择积分商品属性！");
            }

            //删除之前的
            storeProductAttrService.removeByProductId(request.getId(), Constants.PRODUCT_TYPE_INTEGAL);
            //进行更新
            request.getAttr().forEach(e -> {
                e.setId(null);
                e.setProductId(storeIntegalShop.getId());
                e.setAttrValues(StringUtils.strip(e.getAttrValues().replace("\"", ""), "[]"));
                e.setType(Constants.PRODUCT_TYPE_INTEGAL);
            });

            //保存积分商品属性
            boolean attrSave = storeProductAttrService.saveBatch(request.getAttr());
            if (!attrSave) throw new CrmebException("编辑积分商品属性名失败");
        }

        //商品属性处理，验证非空
        if (null != request.getAttrValue() && request.getAttrValue().size() > 0) {
            //存放属性值
            List<StoreProductAttrValue> storeProductAttrValues = new ArrayList<>();

            //删除之前的
            storeProductAttrValueService.removeByProductId(request.getId(), 4);

            //批量设置attrValues对象的商品id
            List<StoreProductAttrValueRequest> StoreIntegalShopAttrValueRequests = request.getAttrValue();
            StoreIntegalShopAttrValueRequests.forEach(e ->
                    e.setProductId(storeIntegalShop.getId())
            );

            //属性转换
            for (StoreProductAttrValueRequest attrValuesRequest : StoreIntegalShopAttrValueRequests) {
                //实例化属性值对象
                StoreProductAttrValue spav = new StoreProductAttrValue();
                //转换
                BeanUtils.copyProperties(attrValuesRequest, spav);

                //设置sku字段
                if(null != attrValuesRequest.getAttrValue()){
                    List<String> skuList = new ArrayList<>();
                    for(Map.Entry<String,String> vo: attrValuesRequest.getAttrValue().entrySet()){
                        skuList.add(vo.getValue());
                    }
                    spav.setSuk(String.join(",",skuList));
                }
                String attrValue = null;
                if(null != attrValuesRequest.getAttrValue() && attrValuesRequest.getAttrValue().size() > 0){
                    attrValue = JSON.toJSONString(attrValuesRequest.getAttrValue());
                }
                spav.setAttrValue(attrValue);

                //设置处理属性值
                spav.setImage(systemAttachmentService.clearPrefix(spav.getImage()));
                spav.setQuotaShow(spav.getQuota());
                spav.setType(4);

                //添加到属性值list
                storeProductAttrValues.add(spav);
            }

            //验证并保存属性值
            if (storeProductAttrValues.size() > 0) {
                //保存
                boolean saveOrUpdateResult = storeProductAttrValueService.saveBatch(storeProductAttrValues);
                if (!saveOrUpdateResult) throw new CrmebException("编辑属性详情失败");

                // attrResult整存整取，不做更新
                storeProductAttrResultService.deleteByProductId(storeIntegalShop.getId(), 4);

                //属性详情
                StoreProductAttrResult attrResult = new StoreProductAttrResult(
                        0,
                        storeIntegalShop.getId(),
                        systemAttachmentService.clearPrefix(JSON.toJSONString(request.getAttrValue())),
                        DateUtil.getNowTime(),
                        4);
                //保存属性详情
                boolean resultSave = storeProductAttrResultService.save(attrResult);
                if (!resultSave) throw new CrmebException("编辑积分兑换商品属性详情失败");
            }
        }

        // 处理富文本
        if (StrUtil.isNotBlank(request.getContent())) {
            //处理富文本
            StoreProductDescription spd = new StoreProductDescription(
                    storeIntegalShop.getId(),
                    request.getContent().length() > 0 ? systemAttachmentService.clearPrefix(request.getContent()) : "",
                    4);

            //先根据商品id和type删除对应描述
            storeProductDescriptionService.deleteByProductId(spd.getProductId(), 4);
            //再进行保存
            boolean descSave = storeProductDescriptionService.save(spd);
            if (!descSave) throw new CrmebException("编辑积分兑换商品详情失败");
        }

        //返回
        return update;
    }

    @Override
    public StoreProductResponse getAdminDetail(Integer id) {
        //得到积分商品信息
        StoreIntegalShop storeIntegalShop = dao.selectById(id);
        if (ObjectUtil.isNull(storeIntegalShop)) {
            throw new CrmebException("未找到对应商品信息");
        }

        //实例化响应对象
        StoreProductResponse storeProductResponse = new StoreProductResponse();
        BeanUtils.copyProperties(storeIntegalShop, storeProductResponse);//转换

        //验证是否开启日期
        if(storeIntegalShop.getIsDate()){
            //处理开始和结束时间
            storeProductResponse.setStartTimeStr(DateUtil.timestamp2DateStr(storeIntegalShop.getStartTime(), Constants.DATE_FORMAT));
            storeProductResponse.setStopTimeStr(DateUtil.timestamp2DateStr(storeIntegalShop.getStopTime(), Constants.DATE_FORMAT));
        }

        //实例化商品属性对象
        StoreProductAttr spaPram = new StoreProductAttr();
        //赋值
        spaPram.setProductId(id);
        spaPram.setType(Constants.PRODUCT_TYPE_INTEGAL);

        //查询attr商品属性-得到属性数据
        storeProductResponse.setAttr(storeProductAttrService.getByEntity(spaPram));

        //轮播图
        storeProductResponse.setSliderImage(String.join(",", storeIntegalShop.getImages()));

        //实例化商品属性对象
        StoreProductAttr proPram = new StoreProductAttr();
        proPram.setProductId(storeIntegalShop.getProductId()).setType(Constants.PRODUCT_TYPE_NORMAL);

        //得到主商品属性
        List<StoreProductAttr> proAttrs = storeProductAttrService.getByEntity(proPram);

        //是否多是规格
        boolean specType = false;
        if (proAttrs.size() > 1)  specType = true;
        else if (proAttrs.size() == 1) {
            if (!proAttrs.get(0).getAttrValues().equals("默认")) specType = true;
        }
        storeProductResponse.setSpecType(specType);

        //实例化商品属性值对象
        StoreProductAttrValue spavPramCombination = new StoreProductAttrValue();
        spavPramCombination.setProductId(id).setType(Constants.PRODUCT_TYPE_INTEGAL);

        //得到数据-属性值
        List<StoreProductAttrValue> storeProductAttrValuesCombination = storeProductAttrValueService.getByEntity(spavPramCombination);

        //实例化商品属性值对象
        StoreProductAttrValue mainProAttrValue = new StoreProductAttrValue();
        mainProAttrValue.setType(Constants.PRODUCT_TYPE_NORMAL).setProductId(storeIntegalShop.getProductId());

        //得到主商品-属性值
        List<StoreProductAttrValue> storeProductAttrValuesProduct = null;
        storeProductAttrValuesProduct = storeProductAttrValueService.getByEntity(mainProAttrValue);
        List<HashMap<String, Object>> attrValuesProduct = null;
        attrValuesProduct = genratorSkuInfo(
                storeIntegalShop.getProductId(),
                storeIntegalShop,
                storeProductAttrValuesProduct,
                Constants.PRODUCT_TYPE_NORMAL,
                specType);

        // H5 端用于生成skuList
        List<StoreProductAttrValueResponse> sPAVResponses =null;
        sPAVResponses=new ArrayList<>();
        for (StoreProductAttrValue storeProductAttrValue : storeProductAttrValuesCombination) {
            //实例化商品属性值-响应对象
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            BeanUtils.copyProperties(storeProductAttrValue, atr);

            //单规格限量数据处理
            atr.setQuota(storeProductResponse.getQuota());
            atr.setChecked(true);
            sPAVResponses.add(atr);
        }

        //设置属性值
        storeProductResponse.setAttrValues(attrValuesProduct);
        storeProductResponse.setAttrValue(sPAVResponses);

        //得到详情
        StoreProductDescription sd = storeProductDescriptionService.getOne(new LambdaQueryWrapper<StoreProductDescription>()
                        .eq(StoreProductDescription::getProductId, id)
                        .eq(StoreProductDescription::getType, Constants.PRODUCT_TYPE_INTEGAL));
        if (null != sd) {
            storeProductResponse.setContent(null == sd.getDescription() ? "" : sd.getDescription());
        }

        //返回数据
        return storeProductResponse;
    }

    /**
     * 根据配置生成sku配置信息
     * @param productId     商品id
     * @param storeIntegalShop  积分商品信息
     * @param storeProductAttrValues    属性信息
     * @param productType   积分商品和正常数据
     * @return  sku信息
     */
    private  List<HashMap<String, Object>> genratorSkuInfo(int productId, StoreIntegalShop storeIntegalShop,
                                                           List<StoreProductAttrValue> storeProductAttrValues,
                                                           int productType,
                                                           boolean specType) {
        List<HashMap<String, Object>> attrValues = new ArrayList<>();
        if(specType){
            StoreProductAttrResult sparPram = new StoreProductAttrResult();
            sparPram.setProductId(productId).setType(productType);
            List<StoreProductAttrResult> attrResults = storeProductAttrResultService.getByEntity(sparPram);
            if(null == attrResults || attrResults.size() == 0){
                throw new CrmebException("未找到对应属性值");
            }
            StoreProductAttrResult attrResult = attrResults.get(0);
            //PC 端生成skuAttrInfo
            List<StoreProductAttrValueRequest> storeProductAttrValueRequests =
                    com.alibaba.fastjson.JSONObject.parseArray(attrResult.getResult(), StoreProductAttrValueRequest.class);
            if(null != storeProductAttrValueRequests){
                for (int i = 0; i < storeProductAttrValueRequests.size(); i++) {
                    HashMap<String, Object> attrValue = new HashMap<>();
                    String currentSku = storeProductAttrValues.get(i).getSuk();
                    List<StoreProductAttrValue> hasCurrentSku =
                            storeProductAttrValues.stream().filter(e -> e.getSuk().equals(currentSku)).collect(Collectors.toList());
                    StoreProductAttrValue currentAttrValue = hasCurrentSku.get(0);
                    attrValue.put("id", hasCurrentSku.size() > 0 ? hasCurrentSku.get(0).getId():0);
                    attrValue.put("image", currentAttrValue.getImage());
                    attrValue.put("cost", currentAttrValue.getCost());
                    attrValue.put("price", currentAttrValue.getPrice());
                    attrValue.put("otPrice", currentAttrValue.getOtPrice());
                    attrValue.put("stock", currentAttrValue.getStock());
                    attrValue.put("barCode", currentAttrValue.getBarCode());
                    attrValue.put("weight", currentAttrValue.getWeight());
                    attrValue.put("volume", currentAttrValue.getVolume());
                    attrValue.put("suk", currentSku);
                    attrValue.put("attrValue", JSON.parse(storeProductAttrValues.get(i).getAttrValue(), Feature.OrderedField));
                    attrValue.put("brokerage", currentAttrValue.getBrokerage());
                    attrValue.put("brokerageTwo", currentAttrValue.getBrokerageTwo());
                    attrValue.put("quota", currentAttrValue.getQuota());
                    String[] skus = currentSku.split(",");
                    for (int k = 0; k < skus.length; k++) {
                        attrValue.put("value"+k,skus[k]);
                    }
                    attrValues.add(attrValue);
                }

            }
        }
        return attrValues;
    }

    @Override
    public Boolean updateIsShow(Integer id, Boolean isShow) {
        //得到兑换商品
        StoreIntegalShop sls = getById(id);

        //验证非空
        if (ObjectUtil.isNull(sls) || sls.getIsDel()) {
            throw new CrmebException("积分兑换商品不存在");
        }

        //是否显示
        if (isShow) {
            // 判断商品是否存在，与兑换商品关联的商品
            StoreProduct product = storeProductService.getById(sls.getProductId());
            if (ObjectUtil.isNull(product)) {
                throw new CrmebException("关联的商品已删除，无法显示活动");
            }
        }

        //执行修改并返回结果
        StoreIntegalShop storeIntegalShop = new StoreIntegalShop();
        storeIntegalShop.setId(id).setIsShow(isShow);
        return updateById(storeIntegalShop);
    }

    @Override
    public StoreIntegalShop getByIdException(Integer id) {
        LambdaQueryWrapper<StoreIntegalShop> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreIntegalShop::getId, id);
        lqw.eq(StoreIntegalShop::getIsDel, false);
        lqw.eq(StoreIntegalShop::getIsShow, true);
        StoreIntegalShop storeIntegalShop = dao.selectOne(lqw);
        if (ObjectUtil.isNull(storeIntegalShop)) throw new CrmebException("积分兑换商品不存在或以删除");
        return storeIntegalShop;
    }

    @Override
    public StoreIntegalShopDetailH5Response getDetailH5(Integer id) {
        //实例化对象
        StoreIntegalShopDetailH5Response sisdhr=new StoreIntegalShopDetailH5Response();

        // 获取积分兑换商品信息
        StoreIntegalShop storeIntegalShop = dao.selectById(id);

        //验证非空
        if (ObjectUtil.isNull(storeIntegalShop) || storeIntegalShop.getIsDel()) {
            throw new CrmebException("未找到对应的积分兑换商品信息");
        }

        //验证-是否显示
        if (!storeIntegalShop.getIsShow()) {
            throw new CrmebException("积分兑换商品已关闭");
        }

        //验证-是否开启日期
        if(storeIntegalShop.getIsDate()){
           Integer state = getIntegalStatus(storeIntegalShop);
           storeIntegalShop.setState(state);
        }

        //处理积分兑换商品相关信息
        StoreIntegalShopResponse detailH5Response = new StoreIntegalShopResponse();
        BeanUtils.copyProperties(storeIntegalShop, detailH5Response);

        //验证是否开启日期
        if(storeIntegalShop.getIsDate()){
            //处理日期
            detailH5Response.setStartTimeStr(DateUtil.timestamp2DateStr(storeIntegalShop.getStartTime(),Constants.DATE_FORMAT));
            detailH5Response.setStopTimeStr(DateUtil.timestamp2DateStr(storeIntegalShop.getStopTime(),Constants.DATE_FORMAT));
        }

        // 商品详情
        StoreProductDescription spd = storeProductDescriptionService.getOne(
                new LambdaQueryWrapper<StoreProductDescription>()
                        .eq(StoreProductDescription::getProductId, id)
                        .eq(StoreProductDescription::getType, Constants.PRODUCT_TYPE_INTEGAL));
        if (ObjectUtil.isNotNull(spd)) {
            detailH5Response.setContent(ObjectUtil.isNull(spd.getDescription()) ? "" : spd.getDescription());
        }

        //获取主商品信息
        StoreProduct storeProduct = storeProductService.getById(storeIntegalShop.getProductId());
        //销量 = 原商品销量（包含虚拟销量）
        detailH5Response.setSales(storeProduct.getSales());
        detailH5Response.setFicti(storeProduct.getFicti());
        sisdhr.setSisDetalH5Response(detailH5Response);

        //实例化商品属性对象
        StoreProductAttr spaPram = new StoreProductAttr();
        spaPram.setProductId(id).setType(Constants.PRODUCT_TYPE_INTEGAL);
        //得到商品属性
        List<StoreProductAttr> attrList = storeProductAttrService.getByEntity(spaPram);

        // 根据制式设置attr属性
        List<ProductAttrResponse> attrResponseList = new ArrayList<>();
        for (StoreProductAttr attr : attrList) {
            ProductAttrResponse attrResponse = new ProductAttrResponse();
            attrResponse.setProductId(attr.getProductId());
            attrResponse.setAttrName(attr.getAttrName());
            attrResponse.setType(attr.getType());
            List<String> attrValues = new ArrayList<>();
            String trimAttr = attr.getAttrValues()
                    .replace("[","")
                    .replace("]","");
            if(attr.getAttrValues().contains(",")){
                attrValues = Arrays.asList(trimAttr.split(","));
            }else{
                attrValues.add(trimAttr);
            }
            attrResponse.setAttrValues(attrValues);
            attrResponseList.add(attrResponse);
        }
        sisdhr.setProductAttr(attrResponseList);

        // 根据制式设置sku属性
        HashMap<String, Object> skuMap = CollUtil.newHashMap();
        //获取主商品sku
        StoreProductAttrValue spavPram = new StoreProductAttrValue();
        spavPram.setProductId(storeIntegalShop.getProductId()).setType(Constants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttrValue> storeProductAttrValues = storeProductAttrValueService.getByEntity(spavPram);

        //获取积分商品sku
        StoreProductAttrValue spavPram1 = new StoreProductAttrValue();
        spavPram1.setProductId(storeIntegalShop.getId()).setType(Constants.PRODUCT_TYPE_INTEGAL);
        List<StoreProductAttrValue> seckillAttrValues = storeProductAttrValueService.getByEntity(spavPram1);

        //循环处理
        for (int i = 0; i < storeProductAttrValues.size(); i++) {
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            StoreProductAttrValue productAttrValue = storeProductAttrValues.get(i);
            List<StoreProductAttrValue> valueList = seckillAttrValues.stream().filter(e -> {
                return productAttrValue.getSuk().equals(e.getSuk());
            }).collect(Collectors.toList());
            if (CollUtil.isEmpty(valueList)) {
                BeanUtils.copyProperties(productAttrValue, atr);
            } else {
                BeanUtils.copyProperties(valueList.get(0), atr);
            }
            skuMap.put(atr.getSuk(), atr);
        }
        sisdhr.setProductValue(skuMap);

        // 设置点赞和收藏
        User user = userService.getInfo();
        if(ObjectUtil.isNotNull(user)){
            sisdhr.setUserCollect(storeProductRelationService.getLikeOrCollectByUser(user.getUid(), detailH5Response.getProductId(),false).size() > 0);
        }else{
            sisdhr.setUserCollect(false);
        }

        //返回
        return sisdhr;
    }

    /**
     * 获取积分兑换商品状态
     * @param storeIntegalShop 积分兑换商品
     * @return 积分兑换商品状态状态
     */
    private Integer getIntegalStatus(StoreIntegalShop storeIntegalShop) {
        //验证状态
        Long tt=DateUtil.nowDateTime().getTime();
        boolean bb=tt<storeIntegalShop.getStartTime();
        if(storeIntegalShop.getState() == 1 &&  bb){
            //未开始
            return 1;
        }
        else if( DateUtil.nowDateTime().getTime() >= storeIntegalShop.getStartTime()
                && DateUtil.nowDateTime().getTime() < storeIntegalShop.getStopTime()) {
            // 进行中
            return 2;
        }
        else if(DateUtil.nowDateTime().getTime() >= storeIntegalShop.getStopTime()){
            // 已结束
            return 3;
        }
        else if(storeIntegalShop.getState() == 4) {
            // 已失效
            return 4;
        }
        return storeIntegalShop.getState();
    }

    @Override
    public List<StoreIntegalShopResponse> getListH5(PageParamRequest pageParamRequest) {
        //分页对象
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        String currentDate = DateUtil.nowDate(Constants.DATE_FORMAT_DATE);

        //查询条件
        LambdaQueryWrapper<StoreIntegalShop> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreIntegalShop::getIsDel,false);
        lqw.eq(StoreIntegalShop::getIsShow,true);
        lqw.orderByDesc(StoreIntegalShop::getId);

        //得到列表
        List<StoreIntegalShop> storeIntegalShopList = dao.selectList(lqw);
        if (CollUtil.isEmpty(storeIntegalShopList)) {
            return CollUtil.newArrayList();
        }

        //处理转换
        List<StoreIntegalShopResponse> responses = new ArrayList<>();
        storeIntegalShopList.forEach(e->{
            StoreIntegalShopResponse response = new StoreIntegalShopResponse();
            BeanUtils.copyProperties(e, response);
            responses.add(response);
        });

        //返回
        return responses;
    }

    @Override
    public List<HashMap<String, Object>> getBannerH5() {
        //得到数据
        int gid=Constants.GROUP_DATA_ID_INTEGAL_BANNER;
        List<HashMap<String, Object>> list=systemGroupDataService.getListMapByGid(gid);
        return list;
    }

    @Override
    public List<HashMap<String, Object>> getDaohangMenuH5() {
        //得到数据
        int gid=Constants.GROUP_DATA_ID_INTEGAL_DAOHANG;
        List<HashMap<String, Object>> list=systemGroupDataService.getListMapByGid(gid);
        return list;
    }
}
