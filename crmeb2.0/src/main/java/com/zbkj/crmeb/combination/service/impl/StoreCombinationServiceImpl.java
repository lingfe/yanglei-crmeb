package com.zbkj.crmeb.combination.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.zbkj.crmeb.combination.dao.StoreCombinationDao;
import com.zbkj.crmeb.combination.model.StoreCombination;
import com.zbkj.crmeb.combination.model.StorePink;
import com.zbkj.crmeb.combination.request.StoreCombinationRequest;
import com.zbkj.crmeb.combination.request.StoreCombinationSearchRequest;
import com.zbkj.crmeb.combination.request.StorePinkRequest;
import com.zbkj.crmeb.combination.response.StoreCombinationResponse;
import com.zbkj.crmeb.combination.response.StorePinkResponse;
import com.zbkj.crmeb.combination.service.StoreCombinationService;
import com.zbkj.crmeb.combination.service.StorePinkService;
import com.zbkj.crmeb.front.request.OrderRefundApplyRequest;
import com.zbkj.crmeb.front.response.*;
import com.zbkj.crmeb.front.service.OrderService;
import com.zbkj.crmeb.store.model.*;
import com.zbkj.crmeb.store.request.StoreProductAttrValueRequest;
import com.zbkj.crmeb.store.request.StoreProductStockRequest;
import com.zbkj.crmeb.store.response.StoreProductAttrValueResponse;
import com.zbkj.crmeb.store.response.StoreProductResponse;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.utilService.ProductUtils;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.system.service.SystemGroupDataService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * StoreCombinationService ?????????
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB???????????????????????????????????? ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB????????????????????????????????????????????????CRMEB????????????
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class StoreCombinationServiceImpl extends ServiceImpl<StoreCombinationDao, StoreCombination> implements StoreCombinationService {

    @Resource
    private StoreCombinationDao dao;

    @Autowired
    private StorePinkService storePinkService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private ProductUtils productUtils;

    @Autowired
    private StoreProductAttrService storeProductAttrService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private StoreProductAttrResultService storeProductAttrResultService;

    @Autowired
    private StoreProductDescriptionService storeProductDescriptionService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductRelationService storeProductRelationService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SystemGroupDataService systemGroupDataService;

    private static final Logger logger = LoggerFactory.getLogger(StoreCombinationServiceImpl.class);

    /**
     * ???????????????????????????
     *
     * @param request          ????????????
     * @param pageParamRequest ???????????????
     * @return List<StoreCombination>
     */
    @Override
    public PageInfo<StoreCombinationResponse> getList(StoreCombinationSearchRequest request, PageParamRequest pageParamRequest) {
        Page<StoreCombination> combinationPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreCombination> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreCombination::getIsDel, false);
        if (StrUtil.isNotEmpty(request.getKeywords())) {
            lambdaQueryWrapper.and(i -> i.like(StoreCombination::getProductId, request.getKeywords())
                    .or().like(StoreCombination::getId, request.getKeywords())
                    .or().like(StoreCombination::getTitle, request.getKeywords()));
        }
        if (ObjectUtil.isNotNull(request.getIsShow())) {
            lambdaQueryWrapper.eq(StoreCombination::getIsShow, request.getIsShow() == 1);
        }
        lambdaQueryWrapper.orderByDesc(StoreCombination::getSort, StoreCombination::getId);
        List<StoreCombination> storeCombinationList = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(storeCombinationList)) {
            return CommonPage.copyPageInfo(combinationPage, CollUtil.newArrayList());
        }

        List<StoreCombinationResponse> responseList = storeCombinationList.stream().map(combination -> {
            //?????? ???????????? ???????????? ???????????? ????????????
            StoreCombinationResponse combinationResponse = new StoreCombinationResponse();
            BeanUtils.copyProperties(combination, combinationResponse);
            combinationResponse.setRemainingQuota(combination.getQuota());
            List<StorePink> pinkList = storePinkService.getListByCid(combination.getId());
            combinationResponse.setCountPeople(0);
            combinationResponse.setCountPeopleAll(0);
            combinationResponse.setCountPeoplePink(0);
            if (CollUtil.isNotEmpty(pinkList)) {
                List<StorePink> teamList = pinkList.stream().filter(i -> i.getKId() == 0).collect(Collectors.toList());
                List<StorePink> successTeam = teamList.stream().filter(i -> i.getStatus() == 2).collect(Collectors.toList());
                combinationResponse.setCountPeople(teamList.size());
                combinationResponse.setCountPeopleAll(pinkList.size());
                combinationResponse.setCountPeoplePink(successTeam.size());
            }
            combinationResponse.setStopTimeStr(DateUtil.timestamp2DateStr(combination.getStopTime(), Constants.DATE_FORMAT_DATE));
            return combinationResponse;
        }).collect(Collectors.toList());

        return CommonPage.copyPageInfo(combinationPage, responseList);
    }

    /**
     * ??????????????????
     *
     * @param request ??????????????????
     * @return Boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveCombination(StoreCombinationRequest request) {
        // ?????????checked=false?????????
        clearNotCheckedAndValidationPrice(request);

        StoreCombination storeCombination = new StoreCombination();
        BeanUtils.copyProperties(request, storeCombination);
        // ??????????????????
        Long stopTime = DateUtil.dateStr2Timestamp(request.getStopTime(), Constants.DATE_TIME_TYPE_END);
        if (stopTime <= System.currentTimeMillis()) {
            throw new CrmebException("??????????????????????????????????????????");
        }

        storeCombination.setId(null);
        // ??????????????????
        storeCombination.setImage(systemAttachmentService.clearPrefix(request.getImage()));
        storeCombination.setImages(systemAttachmentService.clearPrefix(request.getImages()));
        // ????????????????????????
        storeCombination.setStartTime(DateUtil.dateStr2Timestamp(request.getStartTime(), Constants.DATE_TIME_TYPE_BEGIN));
        storeCombination.setStopTime(stopTime);
        storeCombination.setAddTime(System.currentTimeMillis());
        storeCombination.setSales(0);
        // ????????????
        productUtils.calcPriceForAttrValuesCombination(request, storeCombination);
        // ????????????
        boolean save = save(storeCombination);
        if (!save) throw new CrmebException("????????????????????????");

        // sku??????
        if (request.getSpecType()) { // ?????????
            if (CollUtil.isNotEmpty(request.getAttr()) && request.getAttr().size() > 0) {
                request.getAttr().forEach(e -> {
                    e.setId(null);
                    e.setProductId(storeCombination.getId());
                    e.setAttrValues(StringUtils.strip(e.getAttrValues().replace("\"", ""), "[]"));
                    e.setType(Constants.PRODUCT_TYPE_PINGTUAN);
                });
                boolean attrSave = storeProductAttrService.saveBatch(request.getAttr());
                if (!attrSave) throw new CrmebException("?????????????????????????????????");
            }
        } else { //?????????
            StoreProductAttr singleAttr = new StoreProductAttr();
            singleAttr.setProductId(storeCombination.getId())
                      .setAttrName("??????")
                      .setAttrValues("??????")
                      .setType(Constants.PRODUCT_TYPE_PINGTUAN);
            boolean attrAddResult = storeProductAttrService.save(singleAttr);
            if (!attrAddResult) throw new CrmebException("?????????????????????");

            //?????????
            StoreProductAttrValue singleAttrValue = new StoreProductAttrValue();
            BigDecimal commissionL1 = BigDecimal.ZERO;
            BigDecimal commissionL2 = BigDecimal.ZERO;
            if (request.getAttrValue().size() > 0) {
                commissionL1 = null != request.getAttrValue().get(0).getBrokerage() ?
                        request.getAttrValue().get(0).getBrokerage() : BigDecimal.ZERO;
                commissionL2 = null != request.getAttrValue().get(0).getBrokerageTwo() ?
                        request.getAttrValue().get(0).getBrokerageTwo() : BigDecimal.ZERO;
            }

            singleAttrValue.setProductId(storeCombination
                    .getId())
                    .setStock(storeCombination.getStock())
                    .setSuk("??????")
                    .setSales(storeCombination.getSales()).setPrice(storeCombination.getPrice())
                    .setImage(systemAttachmentService.clearPrefix(storeCombination.getImage()))
                    .setCost(storeCombination.getCost())
                    .setType(Constants.PRODUCT_TYPE_PINGTUAN)
                    .setOtPrice(storeCombination.getOtPrice()).setBrokerage(commissionL1)
                    .setBrokerageTwo(commissionL2).setQuota(storeCombination.getQuota())
                    .setQuotaShow(storeCombination.getQuota());
            boolean saveOrUpdateResult = storeProductAttrValueService.save(singleAttrValue);
            if (!saveOrUpdateResult) throw new CrmebException("????????????????????????");
        }

        if (null != request.getAttrValue() && request.getAttrValue().size() > 0) {
            // ????????????attrValues???????????????id
            List<StoreProductAttrValueRequest> storeCombinationAttrValueRequests = request.getAttrValue();
            storeCombinationAttrValueRequests.forEach(e -> e.setProductId(storeCombination.getId()));
            List<StoreProductAttrValue> storeProductAttrValues = new ArrayList<>();
            for (StoreProductAttrValueRequest attrValuesRequest : storeCombinationAttrValueRequests) {
                StoreProductAttrValue spav = new StoreProductAttrValue();
                BeanUtils.copyProperties(attrValuesRequest, spav);
                //??????sku??????
                if (null == attrValuesRequest.getAttrValue()) {
                    break;
                }
                List<String> skuList = new ArrayList<>();
                for (Map.Entry<String, String> vo : attrValuesRequest.getAttrValue().entrySet()) {
                    skuList.add(vo.getValue());
                    spav.setSuk(String.join(",", skuList));
                }
                spav.setImage(systemAttachmentService.clearPrefix(spav.getImage()));
                spav.setAttrValue(JSON.toJSONString(attrValuesRequest.getAttrValue()));
                spav.setQuotaShow(spav.getQuota());
                spav.setType(Constants.PRODUCT_TYPE_PINGTUAN);
                storeProductAttrValues.add(spav);
            }
            // ????????????
            if (storeProductAttrValues.size() > 0) {
                boolean saveOrUpdateResult = storeProductAttrValueService.saveBatch(storeProductAttrValues);
                StoreProductAttrResult attrResult = new StoreProductAttrResult(
                        0,
                        storeCombination.getId(),
                        systemAttachmentService.clearPrefix(JSON.toJSONString(request.getAttrValue())),
                        DateUtil.getNowTime(), Constants.PRODUCT_TYPE_PINGTUAN);
                storeProductAttrResultService.save(attrResult);
                if (!saveOrUpdateResult) throw new CrmebException("????????????????????????????????????");
            }
        }
        // ???????????????
        StoreProductDescription spd = new StoreProductDescription(
                storeCombination.getId(), request.getContent().length() > 0
                ? systemAttachmentService.clearPrefix(request.getContent()) : "", Constants.PRODUCT_TYPE_PINGTUAN);
        storeProductDescriptionService.deleteByProductId(spd.getProductId(), Constants.PRODUCT_TYPE_PINGTUAN);
        boolean descSave = storeProductDescriptionService.save(spd);
        if (!descSave) throw new CrmebException("??????????????????????????????");
        return save;
    }

    /**
     * ??????????????????
     */
    @Override
    public Boolean deleteById(Integer id) {
        StoreCombination combination = getById(id);
        long timeMillis = System.currentTimeMillis();
        if (combination.getIsShow().equals(true) && combination.getStartTime() <= timeMillis && timeMillis <= combination.getStopTime()) {
            throw new CrmebException("???????????????????????????????????????");
        }

        StoreCombination storeCombination = new StoreCombination();
        storeCombination.setId(id).setIsDel(true);
        return updateById(storeCombination);
    }

    /**
     * ??????????????????
     *
     * @param request ??????????????????
     * @return Boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCombination(StoreCombinationRequest request) {
        StoreCombination existCombination = getById(request.getId());
        if (ObjectUtil.isNull(existCombination) || existCombination.getIsDel()) throw new CrmebException("?????????????????????");

        long timeMillis = System.currentTimeMillis();
        if (existCombination.getIsShow().equals(true) && existCombination.getStartTime() <= timeMillis && timeMillis <= existCombination.getStopTime()) {
            throw new CrmebException("???????????????????????????????????????");
        }

        // ?????????checked=false?????????
        clearNotCheckedAndValidationPrice(request);

        StoreCombination storeCombination = new StoreCombination();
        BeanUtils.copyProperties(request, storeCombination);
        // ??????????????????
        storeCombination.setImage(systemAttachmentService.clearPrefix(request.getImage()));
        storeCombination.setImages(systemAttachmentService.clearPrefix(request.getImages()));
        // ????????????????????????
        storeCombination.setStartTime(DateUtil.dateStr2Timestamp(request.getStartTime(), Constants.DATE_TIME_TYPE_BEGIN));
        storeCombination.setStopTime(DateUtil.dateStr2Timestamp(request.getStopTime(), Constants.DATE_TIME_TYPE_END));
        // ????????????
        productUtils.calcPriceForAttrValuesCombination(request, storeCombination);
        // ????????????
        boolean update = updateById(storeCombination);
        if (!update) throw new CrmebException("????????????????????????");

        // ???attr????????????????????????????????????????????????????????????
        if (request.getSpecType()) { // ??????????????????????????????
            storeProductAttrService.removeByProductId(request.getId(), Constants.PRODUCT_TYPE_PINGTUAN);
            request.getAttr().forEach(e -> {
                e.setProductId(request.getId());
                e.setAttrValues(StringUtils.strip(e.getAttrValues().replace("\"", ""), "[]"));
                e.setType(Constants.PRODUCT_TYPE_PINGTUAN);
            });
            boolean updateAttr = storeProductAttrService.saveBatch(request.getAttr());
            if (!updateAttr) throw new CrmebException("??????????????????????????????");
        }

        if (null != request.getAttrValue() && request.getAttrValue().size() > 0) {
            storeProductAttrValueService.removeByProductId(request.getId(), Constants.PRODUCT_TYPE_PINGTUAN);
            List<StoreProductAttrValueRequest> storeProductAttrValuesRequest = request.getAttrValue();
            // ????????????attrValues???????????????id
            storeProductAttrValuesRequest.forEach(e -> e.setProductId(request.getId()));
            List<StoreProductAttrValue> storeProductAttrValues = new ArrayList<>();
            for (StoreProductAttrValueRequest attrValuesRequest : storeProductAttrValuesRequest) {
                StoreProductAttrValue spav = new StoreProductAttrValue();
                BeanUtils.copyProperties(attrValuesRequest, spav);
                //??????sku??????
                if (null != attrValuesRequest.getAttrValue()) {
                    List<String> skuList = new ArrayList<>();
                    for (Map.Entry<String, String> vo : attrValuesRequest.getAttrValue().entrySet()) {
                        skuList.add(vo.getValue());
                    }
                    spav.setSuk(String.join(",", skuList));
                }
                String attrValue = null;
                if (null != attrValuesRequest.getAttrValue() && attrValuesRequest.getAttrValue().size() > 0) {
                    attrValue = JSON.toJSONString(attrValuesRequest.getAttrValue());
                }
                spav.setAttrValue(attrValue);
                spav.setImage(systemAttachmentService.clearPrefix(spav.getImage()));
                spav.setType(Constants.PRODUCT_TYPE_PINGTUAN);
                spav.setQuotaShow(spav.getQuota());
                storeProductAttrValues.add(spav);
            }
            boolean saveOrUpdateResult = storeProductAttrValueService.saveBatch(storeProductAttrValues);
            if (!saveOrUpdateResult) throw new CrmebException("????????????????????????");
            // attrResult???????????????????????????
            storeProductAttrResultService.deleteByProductId(storeCombination.getId(), Constants.PRODUCT_TYPE_PINGTUAN);
            StoreProductAttrResult attrResult = new StoreProductAttrResult(
                    0,
                    storeCombination.getId(),
                    systemAttachmentService.clearPrefix(JSON.toJSONString(request.getAttrValue())),
                    DateUtil.getNowTime(), Constants.PRODUCT_TYPE_PINGTUAN);
            boolean resultSave = storeProductAttrResultService.save(attrResult);
            if (!resultSave) throw new CrmebException("??????????????????????????????");
        }
        // ???????????????
        if (StrUtil.isNotBlank(request.getContent())) {
            StoreProductDescription spd = new StoreProductDescription(
                    storeCombination.getId(),
                    request.getContent().length() > 0
                            ? systemAttachmentService.clearPrefix(request.getContent()) : "",
                    Constants.PRODUCT_TYPE_PINGTUAN);
            storeProductDescriptionService.deleteByProductId(storeCombination.getId(), Constants.PRODUCT_TYPE_PINGTUAN);
            boolean saveDesc = storeProductDescriptionService.save(spd);
            if (!saveDesc) throw new CrmebException("??????????????????????????????");
        }
        return update;
    }

    /**
     * ??????????????????
     *
     * @param id ????????????ID
     */
    @Override
    public StoreProductResponse getAdminDetail(Integer id) {
        StoreCombination storeCombination = dao.selectById(id);
        if (ObjectUtil.isNull(storeCombination)) throw new CrmebException("???????????????????????????");
        StoreProductResponse storeProductResponse = new StoreProductResponse();
        BeanUtils.copyProperties(storeCombination, storeProductResponse);
        storeProductResponse.setStartTimeStr(DateUtil.timestamp2DateStr(storeCombination.getStartTime(), Constants.DATE_FORMAT_DATE));
        storeProductResponse.setStopTimeStr(DateUtil.timestamp2DateStr(storeCombination.getStopTime(), Constants.DATE_FORMAT_DATE));


        // ??????attr
        StoreProductAttr spaPram = new StoreProductAttr();
        spaPram.setProductId(id).setType(Constants.PRODUCT_TYPE_PINGTUAN);
        List<StoreProductAttr> attrs = storeProductAttrService.getByEntity(spaPram);
        storeProductResponse.setAttr(attrs);
        storeProductResponse.setSliderImage(String.join(",", storeCombination.getImages()));

        boolean specType = false;
        StoreProductAttr proPram = new StoreProductAttr();
        proPram.setProductId(storeCombination.getProductId()).setType(Constants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttr> proAttrs = storeProductAttrService.getByEntity(proPram);
        if (proAttrs.size() > 1) {
            specType = true;
        } else if (proAttrs.size() == 1) {
            if (!proAttrs.get(0).getAttrValues().equals("??????")) {
                specType = true;
            }
        }
        storeProductResponse.setSpecType(specType);

        // ????????????
        StoreProductAttrValue spavPramCombination = new StoreProductAttrValue();
        spavPramCombination.setProductId(id).setType(Constants.PRODUCT_TYPE_PINGTUAN);
        List<StoreProductAttrValue> storeProductAttrValuesCombination = storeProductAttrValueService.getByEntity(spavPramCombination);
        List<HashMap<String, Object>> attrValuesCombination = genratorSkuInfo(id, storeCombination, storeProductAttrValuesCombination, Constants.PRODUCT_TYPE_PINGTUAN, specType);

        // ??????attrValue
        StoreProductAttrValue spavPramProduct = new StoreProductAttrValue();
        spavPramProduct.setProductId(storeCombination.getProductId()).setType(Constants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttrValue> storeProductAttrValuesProduct = storeProductAttrValueService.getByEntity(spavPramProduct);
        List<HashMap<String, Object>> attrValuesProduct = genratorSkuInfo(storeCombination.getProductId(), storeCombination, storeProductAttrValuesProduct, Constants.PRODUCT_TYPE_NORMAL, specType);

        // H5 ???????????????skuList
        List<StoreProductAttrValueResponse> sPAVResponses = new ArrayList<>();

        for (StoreProductAttrValue storeProductAttrValue : storeProductAttrValuesCombination) {
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            BeanUtils.copyProperties(storeProductAttrValue, atr);
            // ?????????????????????????????????
            atr.setQuota(storeProductResponse.getQuota());
            atr.setChecked(true);
            sPAVResponses.add(atr);
        }

        for (int k = 0; k < attrValuesProduct.size(); k++) {
            for (int i = 0; i < attrValuesCombination.size(); i++) {
                HashMap<String, Object> skill = attrValuesCombination.get(i);
                HashMap<String, Object> product = attrValuesProduct.get(k);
                product.put("checked", false);
                product.put("quota", product.get("stock"));
                product.put("price", product.get("price"));
                if (skill.get("suk").equals(product.get("suk"))) {
                    product.put("checked", true);
                    product.put("quota", skill.get("quota"));
                    product.put("price", skill.get("price"));
                    break;
                }
            }
        }

        storeProductResponse.setAttrValues(attrValuesProduct);
        storeProductResponse.setAttrValue(sPAVResponses);
        StoreProductDescription sd = storeProductDescriptionService.getOne(
                new LambdaQueryWrapper<StoreProductDescription>()
                        .eq(StoreProductDescription::getProductId, id)
                        .eq(StoreProductDescription::getType, Constants.PRODUCT_TYPE_PINGTUAN));
        if (null != sd) {
            storeProductResponse.setContent(null == sd.getDescription() ? "" : sd.getDescription());
        }
        return storeProductResponse;
    }


    @Override
    public Boolean updateCombinationShow(Integer id, Boolean isShow) {
        //??????-??????????????????
        StoreCombination temp = getById(id);
        if (ObjectUtil.isNull(temp) || temp.getIsDel()) {
            throw new CrmebException("?????????????????????");
        }

        //??????-????????????
        if (isShow) {
            //??????-?????????-????????????
            StoreProduct product = storeProductService.getById(temp.getProductId());
            if (ObjectUtil.isNull(product)) {
                throw new CrmebException("?????????????????????????????????????????????");
            }
        }

        //????????????
        StoreCombination storeCombination = new StoreCombination();
        storeCombination.setId(id).setIsShow(isShow);
        return updateById(storeCombination);
    }

    /**
     * admin????????????
     */
    @Override
    public Map<String, Object> getAdminStatistics() {
        StorePink spavPink = new StorePink();
        spavPink.setKId(0);
        List<StorePink> pinkList = storePinkService.getByEntity(spavPink);
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("countPeople", 0);
        map.put("countTeam", 0);
        if (CollUtil.isNotEmpty(pinkList)) {
            map.put("countPeople", storePinkService.count());
            long countTeam = pinkList.stream().filter(i -> i.getStatus() == 2).count();
            map.put("countTeam", countTeam);
        }
        return map;
    }

    /**
     * H5??????????????????
     */
    @Override
    public List<StoreCombinationH5Response> getH5List(PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreCombination> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreCombination::getId ,StoreCombination::getProductId ,StoreCombination::getImage ,StoreCombination::getTitle
                ,StoreCombination::getPeople ,StoreCombination::getOtPrice ,StoreCombination::getPrice ,StoreCombination::getStock);
        lqw.eq(StoreCombination::getIsDel, false);
        lqw.eq(StoreCombination::getIsShow, true);
        long millis = System.currentTimeMillis();
        lqw.le(StoreCombination::getStartTime, millis);
        lqw.ge(StoreCombination::getStopTime, millis);
        lqw.orderByDesc(StoreCombination::getSort, StoreCombination::getId);
        List<StoreCombination> combinationList = dao.selectList(lqw);
        if (CollUtil.isEmpty(combinationList)) {
            return CollUtil.newArrayList();
        }
        List<StoreCombinationH5Response> responseList = combinationList.stream().map(e -> {
            StoreCombinationH5Response response = new StoreCombinationH5Response();
            BeanUtils.copyProperties(e, response);
            return response;
        }).collect(Collectors.toList());
        return responseList;
    }

    /**
     * H5??????????????????
     * @param comId ??????????????????
     * @return CombinationDetailResponse
     */
    @Override
    public CombinationDetailResponse getH5Detail(Integer comId) {
        //?????????-??????????????????-????????????
        CombinationDetailResponse detailResponse = new CombinationDetailResponse();

        //??????-??????????????????
        StoreCombination storeCombination = getById(comId);
        if (ObjectUtil.isNull(storeCombination) || storeCombination.getIsDel()) {
            throw new CrmebException("???????????????????????????");
        }
        if (!storeCombination.getIsShow()) {
            throw new CrmebException("?????????????????????");
        }

        //??????-???????????????
        StoreProduct storeProduct = storeProductService.getById(storeCombination.getProductId());
        if(storeProduct == null )storeProduct=new StoreProduct();

        //??????-???????????????
        StoreProductDescription sd = storeProductDescriptionService.getOne(
                new LambdaQueryWrapper<StoreProductDescription>()
                        .eq(StoreProductDescription::getProductId, comId)
                        .eq(StoreProductDescription::getType, Constants.PRODUCT_TYPE_PINGTUAN));
        if (sd == null) sd=new StoreProductDescription();

        //?????????-??????????????????-H5-????????????
        CombinationDetailH5Response infoResponse = new CombinationDetailH5Response();
        BeanUtils.copyProperties(storeCombination, infoResponse);//???-??????????????????-?????????-??????????????????H5

        //??????-????????????
        infoResponse.setStoreName(storeCombination.getTitle());
        infoResponse.setSliderImage(storeCombination.getImages());
        infoResponse.setStoreInfo(storeCombination.getInfo());
        infoResponse.setContent(ObjectUtil.isNull(sd.getDescription()) ? "" : sd.getDescription()); //??????????????????-????????????????????????
        infoResponse.setSales(storeProduct.getSales());// ???????????? = ???????????????????????????????????????
        infoResponse.setFicti(storeProduct.getFicti());// ????????????-???????????????

        //?????????-????????????????????????-h5????????????
        detailResponse.setStoreCombination(infoResponse);

        //?????????-????????????-??????
        StoreProductAttr spaPram = new StoreProductAttr();
        spaPram.setProductId(comId).setType(Constants.PRODUCT_TYPE_PINGTUAN);

        //??????-????????????list
        List<StoreProductAttr> attrList = storeProductAttrService.getByEntity(spaPram);

        // ??????????????????attr??????
        List<ProductAttrResponse> skuAttr = storeProductService.getSkuAttr(attrList);
        detailResponse.setProductAttr(skuAttr);

        // ??????????????????sku??????
        HashMap<String, Object> skuMap = CollUtil.newHashMap();
        // ???????????????sku
        StoreProductAttrValue spavPram = new StoreProductAttrValue();
        spavPram.setProductId(storeCombination.getProductId()).setType(Constants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttrValue> storeProductAttrValues = storeProductAttrValueService.getByEntity(spavPram);
        // ??????????????????sku
        StoreProductAttrValue spavPram1 = new StoreProductAttrValue();
        spavPram1.setProductId(storeCombination.getId()).setType(Constants.PRODUCT_TYPE_PINGTUAN);
        List<StoreProductAttrValue> combinationAttrValues = storeProductAttrValueService.getByEntity(spavPram1);

        for (StoreProductAttrValue productAttrValue : storeProductAttrValues) {
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            List<StoreProductAttrValue> valueList = combinationAttrValues.stream().filter(e -> productAttrValue.getSuk().equals(e.getSuk())).collect(Collectors.toList());
            if (CollUtil.isEmpty(valueList)) {
                BeanUtils.copyProperties(productAttrValue, atr);
            } else {
                BeanUtils.copyProperties(valueList.get(0), atr);
            }
            if (ObjectUtil.isNull(atr.getQuota())) {
                atr.setQuota(0);
            }
            skuMap.put(atr.getSuk(), atr);
        }
        detailResponse.setProductValue(skuMap);

        // ?????????????????????
        User user = userService.getInfo();
        if (ObjectUtil.isNotNull(user) && ObjectUtil.isNotNull(user.getUid())) {
            detailResponse.setUserCollect(storeProductRelationService.getLikeOrCollectByUser(user.getUid(), storeCombination.getProductId(), false).size() > 0);
        } else {
            detailResponse.setUserCollect(false);
        }

        detailResponse.setPinkOkSum(0);
        // ??????????????????
        List<StorePink> headList = storePinkService.getListByCidAndKid(storeCombination.getId(), 0);
        if (CollUtil.isNotEmpty(headList)) {
            List<StorePinkResponse> headPinkList = headList.stream().map(i -> {
                StorePinkResponse pinkResponse = new StorePinkResponse();
                BeanUtils.copyProperties(i, pinkResponse);
                User teamUser = userService.getById(i.getUid());
                pinkResponse.setAvatar(Optional.ofNullable(teamUser.getAvatar()).orElse(""));
                pinkResponse.setNickname(teamUser.getNickname());
                return pinkResponse;
            }).collect(Collectors.toList());

            // ??????????????????
            List<StorePinkResponse> okList = headPinkList.stream().filter(i -> i.getStatus().equals(2)).collect(Collectors.toList());
            if (ObjectUtil.isNotEmpty(okList)) {
                // ??????????????????????????????
                List<StorePink> pinkOkList = CollUtil.newArrayList();
                okList.forEach(e -> {
                    List<StorePink> list = storePinkService.getListByCidAndKid(e.getCid(), e.getId());
                    if (CollUtil.isNotEmpty(list)) {
                        pinkOkList.addAll(list);
                    }
                });
                List<StorePinkResponse> responseList = pinkOkList.stream().map(i -> {
                    StorePinkResponse pinkResponse = new StorePinkResponse();
                    BeanUtils.copyProperties(i, pinkResponse);
                    User teamUser = userService.getById(i.getUid());
                    pinkResponse.setAvatar(Optional.ofNullable(teamUser.getAvatar()).orElse(""));
                    pinkResponse.setNickname(teamUser.getNickname());
                    return pinkResponse;
                }).collect(Collectors.toList());
                okList.addAll(responseList);
                int pinkOkSum = okList.stream().mapToInt(StorePinkResponse::getTotalNum).sum();
                detailResponse.setPinkOkSum(pinkOkSum);
                //?????????????????????
                detailResponse.setPinkOkList(okList);
            }

            // ?????????????????????
            List<StorePinkResponse> pinkingList = headPinkList.stream().filter(i -> {
                boolean filter = false;
                if (i.getStatus().equals(1) && i.getStopTime() > System.currentTimeMillis()) {
                    filter = true;
                }
                return filter;
            }).collect(Collectors.toList());
            // ????????????????????????
            pinkingList.forEach(i -> {
                Integer countPeople = storePinkService.getCountByKid(i.getId());
                i.setCountPeople(countPeople);
                i.setCount(i.getPeople() - countPeople);
            });

            // ??????????????????
            detailResponse.setPinkList(pinkingList);
        }

        return detailResponse;
    }


    /**
     * ?????????
     *
     * @param pinkId ???????????????ID
     * @return GoPinkResponse
     */
    @Override
    public GoPinkResponse goPink(Integer pinkId) {
        int isOk = 0;//????????????????????????
        int userBool = 0;//?????????????????????????????????  0?????? 1???
        int pinkBool = 0;//????????????????????????  0????????? 1??????

        StorePink teamPink = storePinkService.getById(pinkId);
        if (ObjectUtil.isNull(teamPink) || teamPink.getIsRefund()) throw new CrmebException("????????????????????????");
        StoreCombination storeCombination = getById(teamPink.getCid());
        if (ObjectUtil.isNull(storeCombination) || storeCombination.getIsDel()) throw new CrmebException("???????????????????????????");

        // ????????????????????????
        if (!storeCombination.getIsShow()) {
            throw new CrmebException("?????????????????????");
        }
        if (System.currentTimeMillis() > storeCombination.getStopTime()) {
            throw new CrmebException("?????????????????????");
        }

        User user = userService.getInfo();

        GoPinkResponse goPinkResponse = new GoPinkResponse();
        List<StorePink> pinkList;
        if (teamPink.getKId().equals(0)) {
            pinkList = storePinkService.getListByCidAndKid(teamPink.getCid(), teamPink.getId());
        } else {
            pinkList = storePinkService.getListByCidAndKid(teamPink.getCid(), teamPink.getKId());
        }
        //??????????????????
        int count = teamPink.getPeople() - 1 - Optional.ofNullable(pinkList.size()).orElse(0);

        if (teamPink.getStatus() == 2) {//?????????
            isOk = 1;
            pinkBool = 1;
        }
        if (teamPink.getStatus() == 1) {//?????????
            if (count < 1) {// ???????????????
                isOk = 1;
                pinkBool = 1;
                // ??????????????????
                storePinkService.pinkSuccess(teamPink.getId());
            }
        }

        List<Integer> uidList = pinkList.stream().map(StorePink::getUid).collect(Collectors.toList());
        uidList.add(teamPink.getUid());
        if (uidList.contains(user.getUid())) {
            userBool = 1;
        }

        // ????????????????????????
        List<StorePinkResponse> pinkResponseList = pinkList.stream().map(i -> {
            StorePinkResponse storePinkResponse = new StorePinkResponse();
            BeanUtils.copyProperties(i, storePinkResponse);
            User userOne = userService.getById(i.getUid());
            storePinkResponse.setNickname(userOne.getNickname());
            storePinkResponse.setAvatar(userOne.getAvatar());
            return storePinkResponse;
        }).collect(Collectors.toList());
        // ??????
        StorePinkResponse storePinkResponse = new StorePinkResponse();
        if (teamPink.getKId().equals(0)) {
            BeanUtils.copyProperties(teamPink, storePinkResponse);
            if (teamPink.getUid().equals(user.getUid())) {
                storePinkResponse.setNickname(user.getNickname());
                storePinkResponse.setAvatar(user.getAvatar());
            } else {
                User teamUser = userService.getById(teamPink.getUid());
                storePinkResponse.setNickname(teamUser.getNickname());
                storePinkResponse.setAvatar(teamUser.getAvatar());
            }
        } else {
            StorePink pinkT = storePinkService.getById(teamPink.getKId());
            User teamUser = userService.getById(pinkT.getUid());
            BeanUtils.copyProperties(pinkT, storePinkResponse);
            storePinkResponse.setNickname(teamUser.getNickname());
            storePinkResponse.setAvatar(teamUser.getAvatar());
        }


        goPinkResponse.setCount(count);
        goPinkResponse.setCurrentPinkOrder(teamPink.getOrderId());
        goPinkResponse.setIsOk(isOk);
        goPinkResponse.setPinkBool(pinkBool);
        goPinkResponse.setUserBool(userBool);
        if (userBool == 1) {
            if (!teamPink.getUid().equals(user.getUid())) {
                StorePink itemPink = storePinkService.getByUidAndKid(user.getUid(), teamPink.getId());
                goPinkResponse.setCurrentPinkOrder(itemPink.getOrderId());
            }
        }
        goPinkResponse.setPinkAll(pinkResponseList);
        goPinkResponse.setPinkT(storePinkResponse);
        goPinkResponse.setUserInfo(user);

        // storeCombination??????
        StoreCombinationResponse detailResponse = new StoreCombinationResponse();
        BeanUtils.copyProperties(storeCombination, detailResponse);
        detailResponse.setSpecType(false);
        // sku??????
        StoreProductAttr spavAttr = new StoreProductAttr();
        spavAttr.setProductId(storeCombination.getId());
        spavAttr.setType(Constants.PRODUCT_TYPE_PINGTUAN);
        List<StoreProductAttr> attrList = storeProductAttrService.getByEntity(spavAttr);
        List<HashMap<String, Object>> skuAttrList = getSkuAttrList(attrList);
        detailResponse.setProductAttr(skuAttrList);
        if (CollUtil.isNotEmpty(attrList) && attrList.size() > 1) {
            detailResponse.setSpecType(true);
        }
        // ???????????????attrValueId ?????????????????????????????????
        if (!detailResponse.getSpecType()) {
            detailResponse.setAloneAttrValueId(attrList.get(0).getId());
        }


        StoreProductAttrValue spavValue = new StoreProductAttrValue();
        spavValue.setProductId(storeCombination.getId());
        spavValue.setType(Constants.PRODUCT_TYPE_PINGTUAN);
        List<StoreProductAttrValue> valueList = storeProductAttrValueService.getByEntity(spavValue);
        // H5 ???????????????skuList
        List<StoreProductAttrValueResponse> sPAVResponses = new ArrayList<>();
        for (StoreProductAttrValue storeProductAttrValue : valueList) {
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            BeanUtils.copyProperties(storeProductAttrValue, atr);
            sPAVResponses.add(atr);
        }
        HashMap<String, Object> skuMap = new HashMap<>();
        for (StoreProductAttrValueResponse attrValue : sPAVResponses) {
            skuMap.put(attrValue.getSuk(), attrValue);
        }

        detailResponse.setProductValue(skuMap);
        goPinkResponse.setStoreCombination(detailResponse);

        return goPinkResponse;
    }

    /**
     * ??????????????????
     */
    @Override
    public PageInfo<StoreCombination> getMore(PageParamRequest pageParamRequest, Integer comId) {
        Page<StoreCombination> combinationPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreCombination> lqw = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(comId)) {
            lqw.ne(StoreCombination::getId, comId);
        }
        lqw.eq(StoreCombination::getIsDel, false);
        lqw.eq(StoreCombination::getIsShow, true);
        long millis = System.currentTimeMillis();
        lqw.le(StoreCombination::getStartTime, millis);
        lqw.ge(StoreCombination::getStopTime, millis);
        lqw.orderByDesc(StoreCombination::getSort, StoreCombination::getId);
        List<StoreCombination> storeCombinations = dao.selectList(lqw);
        return CommonPage.copyPageInfo(combinationPage, storeCombinations);
    }

    /**
     * ????????????
     *
     * @return ????????????????????????????????????
     * ?????????????????????????????????????????????????????????
     * ?????????????????????????????????
     */
    @Override
    public Boolean removePink(StorePinkRequest storePinkRequest) {
        StorePink userPink = storePinkService.getById(storePinkRequest.getId());
        if (ObjectUtil.isNull(userPink)) throw new CrmebException("????????????????????????????????????");
        if (userPink.getIsRefund()) throw new CrmebException("?????????????????????");
        // ????????????????????????
        if (userPink.getStatus() == 2) {
            throw new CrmebException("??????????????????????????????");
        }
        Integer kid = userPink.getKId() > 0 ? userPink.getKId() : userPink.getId();
        Integer count = storePinkService.getCountByKid(kid);
        if (count.equals(userPink.getPeople())) {
            // ??????????????????
            storePinkService.pinkSuccess(kid);
            throw new CrmebException("??????????????????????????????");
        }
        if (userPink.getStatus() == 3) {
            throw new CrmebException("?????????????????????");
        }

        StoreOrder order = storeOrderService.getByOderId(userPink.getOrderId());
        if (ObjectUtil.isNull(order) || order.getIsDel()) throw new CrmebException("?????????????????????");
        if (order.getStatus() == -1 && order.getRefundStatus() != 0) {
            throw new CrmebException("?????????????????????????????????");
        }

        // ??????????????????
        OrderRefundApplyRequest refundRequest = new OrderRefundApplyRequest();
        refundRequest.setId(order.getId());
        refundRequest.setUni(order.getOrderId());
        refundRequest.setText("?????????????????????????????????");
        refundRequest.setExplain("???????????????????????????????????????");
        boolean apply = orderService.refundApply(refundRequest);
        if (!apply) throw new CrmebException("????????????????????????");

        // ?????????????????????
        userPink.setStatus(3).setStopTime(System.currentTimeMillis());
        storePinkService.updateById(userPink);
        if (userPink.getKId() == 0) {// ?????????
            List<StorePink> pinkList = storePinkService.getListByCidAndKid(userPink.getCid(), userPink.getId());
            if (CollUtil.isNotEmpty(pinkList)) {
                // ??????????????????
                StorePink newHeadPink = pinkList.get(pinkList.size() - 1);
                newHeadPink.setKId(0);
                pinkList.remove(pinkList.size() - 1);
                pinkList.forEach(i -> i.setKId(newHeadPink.getId()));
                pinkList.add(newHeadPink);
                storePinkService.updateBatchById(pinkList);
            }
        }

        return true;
    }

    /**
     * ????????????
     */
    @Override
    public List<StoreCombination> getByEntity(StoreCombination storeCombination) {
        LambdaQueryWrapper<StoreCombination> lqw = new LambdaQueryWrapper<>();
        lqw.setEntity(storeCombination);
        return dao.selectList(lqw);
    }

    /**
     * ????????????
     */
    @Override
    public Boolean stockAddRedis(StoreProductStockRequest request) {
        String _productString = JSON.toJSONString(request);
        redisUtil.lPush(Constants.PRODUCT_COMBINATION_STOCK_UPDATE, _productString);
        return true;
    }

    @Override
    public void consumeProductStock() {
        String redisKey = Constants.PRODUCT_COMBINATION_STOCK_UPDATE;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("StoreProductServiceImpl.doProductStock | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //??????10????????????????????????????????????????????????
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (null == data) {
                continue;
            }
            try {
                StoreProductStockRequest storeProductStockRequest =
                        com.alibaba.fastjson.JSONObject.toJavaObject(com.alibaba.fastjson.JSONObject.parseObject(data.toString()), StoreProductStockRequest.class);
                boolean result = doProductStock(storeProductStockRequest);
                if (!result) {
                    redisUtil.lPush(redisKey, data);
                }
            } catch (Exception e) {
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param productId ????????????
     */
    @Override
    public List<StoreCombination> getCurrentBargainByProductId(Integer productId) {
        LambdaQueryWrapper<StoreCombination> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreCombination::getProductId, productId);
        lqw.eq(StoreCombination::getIsShow, true);
        long millis = System.currentTimeMillis();
        lqw.le(StoreCombination::getStartTime, millis);
        lqw.ge(StoreCombination::getStopTime, millis);
        lqw.orderByDesc(StoreCombination::getSort, StoreCombination::getId);
        return dao.selectList(lqw);
    }

    /**
     * ??????????????????????????????
     *
     * @param productId ????????????
     */
    @Override
    public Boolean isExistActivity(Integer productId) {
        LambdaQueryWrapper<StoreCombination> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreCombination::getProductId, productId);
        List<StoreCombination> combinationList = dao.selectList(lqw);
        if (CollUtil.isEmpty(combinationList)) {
            return false;
        }
        // ???????????????????????????????????????????????????
        List<StoreCombination> list = combinationList.stream().filter(i -> i.getIsShow().equals(true)).collect(Collectors.toList());
        return CollUtil.isNotEmpty(list);
    }

    /**
     * ???????????????
     *
     * @param combinationId ????????????id
     * @return StoreCombination
     */
    @Override
    public StoreCombination getByIdException(Integer combinationId) {
        LambdaQueryWrapper<StoreCombination> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreCombination::getId, combinationId);
        lqw.eq(StoreCombination::getIsDel, false);
        lqw.eq(StoreCombination::getIsShow, true);
        StoreCombination storeCombination = dao.selectOne(lqw);
        if (ObjectUtil.isNull(storeCombination)) throw new CrmebException("?????????????????????????????????");
        return storeCombination;
    }

    /**
     * ??????/????????????
     *
     * @param id   ????????????id
     * @param num  ??????
     * @param type ?????????add????????????sub?????????
     */
    @Override
    public Boolean operationStock(Integer id, Integer num, String type) {
        UpdateWrapper<StoreCombination> updateWrapper = new UpdateWrapper<>();
        if (type.equals("add")) {
            updateWrapper.setSql(StrUtil.format("stock = stock + {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales - {}", num));
            updateWrapper.setSql(StrUtil.format("quota = quota + {}", num));
        }
        if (type.equals("sub")) {
            updateWrapper.setSql(StrUtil.format("stock = stock - {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales + {}", num));
            updateWrapper.setSql(StrUtil.format("quota = quota - {}", num));
            // ??????????????????????????????????????????
            updateWrapper.last(StrUtil.format(" and (quota - {} >= 0)", num));
        }
        updateWrapper.eq("id", id);
        boolean update = update(updateWrapper);
        if (!update) {
            throw new CrmebException("??????????????????????????????,??????id = " + id);
        }
        return update;
    }

    /**
     * ??????????????????
     * ???????????? + ????????????6???
     * 3???????????????????????????
     * ?????????????????????
     * @return CombinationIndexResponse
     */
    @Override
    public CombinationIndexResponse getIndexInfo() {
        // ???????????????3???????????????
        List<StorePink> tempPinkList = storePinkService.findSizePink(3);
        List<String> avatarList = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(tempPinkList)) {
            // ???????????????????????????
            avatarList = tempPinkList.stream().map(StorePink::getAvatar).collect(Collectors.toList());
        }
        // ???????????????????????????
        Integer totalPeople = storePinkService.getTotalPeople();

        // ??????6???????????????
        LambdaQueryWrapper<StoreCombination> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreCombination::getIsDel, false);
        lqw.eq(StoreCombination::getIsShow, true);
        lqw.ge(StoreCombination::getStock, 0);
        long millis = System.currentTimeMillis();
        lqw.le(StoreCombination::getStartTime, millis);
        lqw.ge(StoreCombination::getStopTime, millis);
        lqw.orderByDesc(StoreCombination::getSort, StoreCombination::getId);
        lqw.last(" limit 6");
        List<StoreCombination> combinationList = dao.selectList(lqw);
        if (CollUtil.isEmpty(combinationList)) {
            return null;
        }
        combinationList.forEach(e -> {
            int percentIntVal = CrmebUtil.percentInstanceIntVal(e.getQuota(), e.getQuotaShow());
            e.setQuotaPercent(percentIntVal);
        });

        CombinationIndexResponse response = new CombinationIndexResponse();
        response.setAvatarList(avatarList);
        response.setTotalPeople(totalPeople);
        response.setProductList(combinationList);
        return response;
    }

    /**
     * ????????????header
     * @return CombinationHeaderResponse
     */
    @Override
    public CombinationHeaderResponse getHeader() {
        // ???????????????3???????????????
        List<StorePink> tempPinkList = storePinkService.findSizePink(7);
        List<String> avatarList = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(tempPinkList)) {
            // ???????????????????????????
            avatarList = tempPinkList.stream().map(StorePink::getAvatar).collect(Collectors.toList());
        }

        // ???????????????????????????
        Integer totalPeople = storePinkService.getTotalPeople();

        // ??????????????????banner
        List<HashMap<String, Object>> bannerList = systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_COMBINATION_LIST_BANNNER);

        CombinationHeaderResponse response = new CombinationHeaderResponse();
        response.setAvatarList(avatarList);
        response.setTotalPeople(totalPeople);
        response.setBannerList(bannerList);
        return response;
    }

    /**
     * ??????????????????
     */
    private boolean doProductStock(StoreProductStockRequest storeProductStockRequest) {
        // ????????????????????????
        StoreCombination existCombination = getById(storeProductStockRequest.getCombinationId());
        List<StoreProductAttrValue> existAttr =
                storeProductAttrValueService.getListByProductIdAndAttrId(
                        storeProductStockRequest.getCombinationId(),
                        storeProductStockRequest.getAttrId().toString(),
                        storeProductStockRequest.getType());
        if (ObjectUtil.isNull(existCombination) || ObjectUtil.isNull(existAttr)) { // ???????????????
            logger.info("??????????????????????????????????????????" + JSON.toJSONString(storeProductStockRequest));
            return true;
        }

        // ??????????????????/?????? ?????????
        boolean isPlus = storeProductStockRequest.getOperationType().equals("add");
        int productStock = isPlus ? existCombination.getStock() + storeProductStockRequest.getNum() : existCombination.getStock() - storeProductStockRequest.getNum();
        existCombination.setStock(productStock);
        existCombination.setSales(existCombination.getSales() - storeProductStockRequest.getNum());
        existCombination.setQuota(existCombination.getQuota() + storeProductStockRequest.getNum());
        updateById(existCombination);

        // ??????sku??????
        for (StoreProductAttrValue attrValue : existAttr) {
            int productAttrStock = isPlus ? attrValue.getStock() + storeProductStockRequest.getNum() : attrValue.getStock() - storeProductStockRequest.getNum();
            attrValue.setStock(productAttrStock);
            attrValue.setSales(attrValue.getSales() - storeProductStockRequest.getNum());
            attrValue.setQuota(attrValue.getQuota() + storeProductStockRequest.getNum());
            storeProductAttrValueService.updateById(attrValue);
        }

        // ????????????????????????
        // StoreProductStockRequest ?????????????????????????????????????????????????????????????????????
        StoreProductResponse existProductLinkedSeckill = storeProductService.getByProductId(storeProductStockRequest.getProductId());
        for (StoreProductAttrValueResponse attrValueResponse : existProductLinkedSeckill.getAttrValue()) {
            if (attrValueResponse.getSuk().equals(storeProductStockRequest.getSuk())) {
                StoreProductStockRequest r = new StoreProductStockRequest()
                        .setAttrId(attrValueResponse.getId())
                        .setNum(storeProductStockRequest.getNum())
                        .setOperationType("add")
                        .setProductId(storeProductStockRequest.getProductId())
                        .setType(Constants.PRODUCT_TYPE_NORMAL)
                        .setSuk(storeProductStockRequest.getSuk());
                storeProductService.doProductStock(r);
            }
        }

        return true;
    }

    /**
     * ??????????????????sku????????????
     *
     * @param id                     ??????id
     * @param storeCombination       ??????????????????
     * @param storeProductAttrValues ????????????
     * @param productType            ?????????????????????
     * @return List
     */
    private List<HashMap<String, Object>> genratorSkuInfo(Integer id, StoreCombination storeCombination, List<StoreProductAttrValue> storeProductAttrValues, int productType, boolean specType) {
        List<HashMap<String, Object>> attrValues = new ArrayList<>();
        if (specType) {

            StoreProductAttrResult sparPram = new StoreProductAttrResult();
            sparPram.setProductId(id).setType(productType);
            List<StoreProductAttrResult> attrResults = storeProductAttrResultService.getByEntity(sparPram);
            if (null == attrResults || attrResults.size() == 0) {
                throw new CrmebException("????????????????????????");
            }
            StoreProductAttrResult attrResult = attrResults.get(0);
            //PC ?????????skuAttrInfo
            List<StoreProductAttrValueRequest> storeProductAttrValueRequests =
                    com.alibaba.fastjson.JSONObject.parseArray(attrResult.getResult(), StoreProductAttrValueRequest.class);
            if (null != storeProductAttrValueRequests) {
                for (int i = 0; i < storeProductAttrValueRequests.size(); i++) {
                    HashMap<String, Object> attrValue = new HashMap<>();
                    String currentSku = storeProductAttrValues.get(i).getSuk();
                    List<StoreProductAttrValue> hasCurrentSku =
                            storeProductAttrValues.stream().filter(e -> e.getSuk().equals(currentSku)).collect(Collectors.toList());
                    StoreProductAttrValue currentAttrValue = hasCurrentSku.get(0);
                    attrValue.put("id", hasCurrentSku.size() > 0 ? hasCurrentSku.get(0).getId() : 0);
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
                        attrValue.put("value" + k, skus[k]);
                    }
                    attrValues.add(attrValue);
                }

            }
        }
        return attrValues;
    }

    /**
     * ?????????????????????????????????????????????
     */
    private void clearNotCheckedAndValidationPrice(StoreCombinationRequest request) {
        if (request.getSpecType()) {
            request.setAttrValue(request.getAttrValue().stream().filter(StoreProductAttrValueRequest::getChecked).collect(Collectors.toList()));
        }
        if (CollUtil.isEmpty(request.getAttrValue())) {
            throw new CrmebException("????????? ??????");
        }
        for (StoreProductAttrValueRequest attr : request.getAttrValue()) {
            if (ObjectUtil.isNull(attr.getPrice()) || attr.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new CrmebException("??????????????? ????????????");
            }
            if (ObjectUtil.isNull(attr.getQuota()) || attr.getQuota() <= 0) {
                throw new CrmebException("??????????????? ??????");
            }
            if (attr.getQuota() > attr.getStock()) {
                throw new CrmebException("?????? ??????????????????");
            }

        }
    }

    /**
     * ?????????????????????attr??????
     */
    private List<HashMap<String, Object>> getSkuAttrList(List<StoreProductAttr> attrList) {
        List<HashMap<String, Object>> attrMapList = new ArrayList<>();
        if (CollUtil.isEmpty(attrList)) {
            return attrMapList;
        }
        for (StoreProductAttr attr : attrList) {
            HashMap<String, Object> attrMap = new HashMap<>();
            attrMap.put("productId", attr.getProductId());
            attrMap.put("attrName", attr.getAttrName());
            List<String> attrValues = new ArrayList<>();
            String trimAttr = attr.getAttrValues()
                    .replace("[", "")
                    .replace("]", "");
            if (attr.getAttrValues().contains(",")) {
                attrValues = Arrays.asList(trimAttr.split(","));
            } else {
                attrValues.add(trimAttr);
            }
            attrMap.put("attrValues", attrValues);

            List<HashMap<String, Object>> attrValueMapList = new ArrayList<>();
            for (String attrValue : attrValues) {
                HashMap<String, Object> attrValueMap = new HashMap<>();
                attrValueMap.put("attr", attrValue);
                attrValueMapList.add(attrValueMap);
            }
            attrMap.put("attrValue", attrValueMapList);
            attrMapList.add(attrMap);
        }
        return attrMapList;
    }
}

