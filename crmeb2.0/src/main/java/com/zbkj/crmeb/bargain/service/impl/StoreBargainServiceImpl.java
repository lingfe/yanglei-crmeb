package com.zbkj.crmeb.bargain.service.impl;

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
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.constants.BargainConstants;
import com.constants.Constants;
import com.constants.ProductConstants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.zbkj.crmeb.bargain.dao.StoreBargainDao;
import com.zbkj.crmeb.bargain.model.StoreBargain;
import com.zbkj.crmeb.bargain.model.StoreBargainUser;
import com.zbkj.crmeb.bargain.request.StoreBargainRequest;
import com.zbkj.crmeb.bargain.request.StoreBargainSearchRequest;
import com.zbkj.crmeb.bargain.response.StoreBargainResponse;
import com.zbkj.crmeb.bargain.service.StoreBargainService;
import com.zbkj.crmeb.bargain.service.StoreBargainUserHelpService;
import com.zbkj.crmeb.bargain.service.StoreBargainUserService;
import com.zbkj.crmeb.front.request.BargainFrontRequest;
import com.zbkj.crmeb.front.response.BargainDetailH5Response;
import com.zbkj.crmeb.front.response.BargainHeaderResponse;
import com.zbkj.crmeb.front.response.BargainIndexResponse;
import com.zbkj.crmeb.front.response.StoreBargainDetailResponse;
import com.zbkj.crmeb.store.model.*;
import com.zbkj.crmeb.store.request.StoreProductAttrValueRequest;
import com.zbkj.crmeb.store.request.StoreProductStockRequest;
import com.zbkj.crmeb.store.response.StoreProductAttrValueResponse;
import com.zbkj.crmeb.store.response.StoreProductResponse;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StoreBargainService ?????????
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
public class StoreBargainServiceImpl extends ServiceImpl<StoreBargainDao, StoreBargain> implements StoreBargainService {

    @Resource
    private StoreBargainDao dao;

    @Autowired
    private StoreBargainUserService storeBargainUserService;

    @Autowired
    private StoreBargainUserHelpService storeBargainUserHelpService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private StoreProductAttrService attrService;

    @Autowired
    private StoreProductAttrValueService attrValueService;

    @Autowired
    private StoreProductAttrResultService storeProductAttrResultService;

    @Autowired
    private StoreProductDescriptionService storeProductDescriptionService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StoreProductService storeProductService;

    private static final Logger logger = LoggerFactory.getLogger(StoreBargainServiceImpl.class);

    /**
    * ??????
    * @param request ????????????
    * @param pageParamRequest ???????????????
    * @author HZW
    * @since 2020-11-06
    * @return List<StoreBargain>
    */
    @Override
    public PageInfo<StoreBargainResponse> getList(StoreBargainSearchRequest request, PageParamRequest pageParamRequest) {
        Page<StoreBargain> storeBargainPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreBargain> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreBargain::getIsDel, false);
        if (StrUtil.isNotEmpty(request.getKeywords())) {
            lambdaQueryWrapper.and(i -> i.like(StoreBargain::getId, request.getKeywords())
                    .or().like(StoreBargain::getStoreName, request.getKeywords())
                    .or().like(StoreBargain::getTitle, request.getKeywords()));
        }
        if (ObjectUtil.isNotNull(request.getStatus())) {
            lambdaQueryWrapper.eq(StoreBargain::getStatus, request.getStatus());
        }
        lambdaQueryWrapper.orderByDesc(StoreBargain::getSort, StoreBargain::getId);
        List<StoreBargain> storeBargainList = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(storeBargainList)) {
            return CommonPage.copyPageInfo(storeBargainPage, CollUtil.newArrayList());
        }
        // 1.?????????????????????2.?????????????????????3.??????????????????
        List<StoreBargainResponse> storeProductResponses = CollUtil.newArrayList();
        for (StoreBargain storeBargain : storeBargainList) {
            StoreBargainResponse storeBargainResponse = new StoreBargainResponse();
            BeanUtils.copyProperties(storeBargain, storeBargainResponse);
            storeBargainResponse.setStartTime(DateUtil.timestamp2DateStr(storeBargain.getStartTime(), Constants.DATE_FORMAT_DATE));
            storeBargainResponse.setStopTime(DateUtil.timestamp2DateStr(storeBargain.getStopTime(), Constants.DATE_FORMAT_DATE));
            storeBargainResponse.setAddTime(DateUtil.timestamp2DateStr(storeBargain.getAddTime(), Constants.DATE_FORMAT));
            List<StoreBargainUser> bargainUserList = storeBargainUserService.getListByBargainId(storeBargain.getId());
            if (CollUtil.isEmpty(bargainUserList)) {
                storeBargainResponse.setCountPeopleAll(0L);
                storeBargainResponse.setCountPeopleHelp(0L);
                storeBargainResponse.setCountPeopleSuccess(0L);
                //????????????
                storeBargainResponse.setSurplusQuota(storeBargain.getQuota());
                storeProductResponses.add(storeBargainResponse);
                continue ;
            }
            //??????????????????
            Integer countPeopleAll = bargainUserList.size();
            //??????????????????
            Long countPeopleSuccess = bargainUserList.stream()
                    .filter(o -> o.getStatus().equals(BargainConstants.BARGAIN_USER_STATUS_SUCCESS)).count();
            //??????????????????
            Long countPeopleHelp = storeBargainUserHelpService.getHelpCountByBargainId(storeBargain.getId());
            storeBargainResponse.setCountPeopleAll(countPeopleAll.longValue());
            storeBargainResponse.setCountPeopleHelp(countPeopleHelp);
            storeBargainResponse.setCountPeopleSuccess(countPeopleSuccess);
            //????????????
            storeBargainResponse.setSurplusQuota(storeBargain.getQuota());
            storeProductResponses.add(storeBargainResponse);
        }
        return CommonPage.copyPageInfo(storeBargainPage, storeProductResponses);
    }

    /**
     * ??????????????????
     * @param id ????????????id
     * @return Boolean
     */
    @Override
    public boolean deleteById(Integer id) {
        StoreBargain existBargain = getById(id);
        long timeMillis = System.currentTimeMillis();
        if (existBargain.getStatus().equals(true) && existBargain.getStartTime() <= timeMillis && timeMillis <= existBargain.getStopTime()) {
            throw new CrmebException("???????????????????????????????????????");
        }

        StoreBargain storeBargain = new StoreBargain();
        storeBargain.setId(id).setIsDel(true);
        return dao.updateById(storeBargain) > 0;
    }

    /**
     * ??????????????????
     * @param request   ????????????result
     * @return ????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBargain(StoreBargainRequest request) {
        // ????????????
        if (null == request.getAttrValue() || request.getAttrValue().size() < 1) {
            throw new CrmebException("????????????????????????????????????");
        }
        StoreProductAttrValueRequest attrValueRequest = request.getAttrValue().get(0);
        if (ObjectUtil.isNull(attrValueRequest.getQuota()) || attrValueRequest.getQuota() <= 0) {
            throw new CrmebException("??????????????????????????????0");
        }
        // ??????????????????
        BigDecimal tempPrice = attrValueRequest.getPrice().subtract(attrValueRequest.getMinPrice());
        // ???????????? * 0.01 = ?????????1???????????????
        BigDecimal multiply = new BigDecimal(request.getPeopleNum()).multiply(new BigDecimal("0.01"));
        if (tempPrice.compareTo(multiply) < 0) {
            // ?????????????????? - ??????????????? >= ???????????? * 0.01
            throw new CrmebException("??????????????????????????????1??????");
        }

        StoreBargain bargain = new StoreBargain();
        BeanUtils.copyProperties(request, bargain);
        bargain.setId(null);
        // ??????????????????
        bargain.setImage(systemAttachmentService.clearPrefix(request.getImage()));
        bargain.setImages(systemAttachmentService.clearPrefix(request.getImages()));
        // ????????????????????????
        bargain.setStartTime(DateUtil.dateStr2Timestamp(request.getStartTime(), Constants.DATE_TIME_TYPE_BEGIN));
        bargain.setStopTime(DateUtil.dateStr2Timestamp(request.getStopTime(), Constants.DATE_TIME_TYPE_END));
        bargain.setAddTime(System.currentTimeMillis());
        bargain.setStoreName(request.getProName());
        // ??????????????????
        bargain.setPrice(attrValueRequest.getPrice());
        bargain.setMinPrice(attrValueRequest.getMinPrice());
        bargain.setCost(attrValueRequest.getCost());
        bargain.setStock(attrValueRequest.getStock());
        bargain.setQuota(attrValueRequest.getQuota());
        bargain.setIsDel(false);
        bargain.setQuotaShow(bargain.getQuota());
        bargain.setSales(0);
        boolean save = save(bargain);
        if (!save) throw new CrmebException("????????????????????????");

        // ???????????????????????????????????????????????????????????????
        StoreProductAttr singleAttr = new StoreProductAttr();
        singleAttr.setProductId(bargain.getId()).setAttrName(ProductConstants.SINGLE_ATTR_NAME)
                .setAttrValues(ProductConstants.SINGLE_ATTR_VALUE).setType(ProductConstants.PRODUCT_TYPE_BARGAIN);
        boolean attrAddResult = attrService.save(singleAttr);
        if (!attrAddResult) throw new CrmebException("?????????????????????");

        // ?????????????????????????????????????????????????????????
        StoreProductAttrValue singleAttrValue = new StoreProductAttrValue();
        BeanUtils.copyProperties(attrValueRequest, singleAttrValue);
        singleAttrValue.setProductId(bargain.getId()).setType(ProductConstants.PRODUCT_TYPE_BARGAIN);
        singleAttrValue.setImage(systemAttachmentService.clearPrefix(singleAttrValue.getImage()));
        boolean saveAttrValue = attrValueService.save(singleAttrValue);
        if(!saveAttrValue) throw new CrmebException("????????????????????????");

        // ?????????????????????result
        StoreProductAttrResult attrResult = new StoreProductAttrResult(
                0,
                bargain.getId(),
                systemAttachmentService.clearPrefix(JSON.toJSONString(request.getAttrValue())),
                DateUtil.getNowTime(),ProductConstants.PRODUCT_TYPE_BARGAIN);
        boolean saveResult = storeProductAttrResultService.save(attrResult);
        if(!saveResult) throw new CrmebException("????????????????????????????????????");

        // ???????????????
        StoreProductDescription spd = new StoreProductDescription(
                bargain.getId(),  request.getContent().length() > 0
                ? systemAttachmentService.clearPrefix(request.getContent()) : "" , ProductConstants.PRODUCT_TYPE_BARGAIN);
        storeProductDescriptionService.deleteByProductId(spd.getProductId(), ProductConstants.PRODUCT_TYPE_BARGAIN);
        boolean saveDesc = storeProductDescriptionService.save(spd);
        if (!saveDesc) throw new CrmebException("???????????????????????????");

        return save;
    }

    /**
     * ??????????????????
     * @param request ????????????
     * @return Boolean
     */
    @Override
    public boolean updateBargain(StoreBargainRequest request) {
        StoreBargain existBargain = getById(request.getId());
        long timeMillis = System.currentTimeMillis();
        if (existBargain.getStatus().equals(true) && existBargain.getStartTime() <= timeMillis && timeMillis <= existBargain.getStopTime()) {
            throw new CrmebException("???????????????????????????????????????");
        }

        if (null == request.getAttrValue() || request.getAttrValue().size() < 1) {
            throw new CrmebException("????????????????????????????????????");
        }

        StoreProductAttrValueRequest attrValueRequest = request.getAttrValue().get(0);

        // ??????????????????
        BigDecimal tempPrice =attrValueRequest.getPrice().subtract(attrValueRequest.getMinPrice());
        // ???????????? * 0.01 = ?????????1???????????????
        BigDecimal multiply = new BigDecimal(request.getPeopleNum()).multiply(new BigDecimal("0.01"));
        if (tempPrice.compareTo(multiply) < 0) {
            // ?????????????????? - ??????????????? >= ???????????? * 0.01
            throw new CrmebException("??????????????????????????????1??????");
        }

        StoreBargain bargain = new StoreBargain();
        BeanUtils.copyProperties(request, bargain);
        // ??????????????????
        bargain.setImage(systemAttachmentService.clearPrefix(request.getImage()));
        bargain.setImages(systemAttachmentService.clearPrefix(request.getImages()));
        // ????????????????????????
        bargain.setStartTime(DateUtil.dateStr2Timestamp(request.getStartTime(), Constants.DATE_TIME_TYPE_BEGIN));
        bargain.setStopTime(DateUtil.dateStr2Timestamp(request.getStopTime(), Constants.DATE_TIME_TYPE_END));
        bargain.setStoreName(request.getProName());
        // ??????????????????
        bargain.setPrice(attrValueRequest.getPrice());
        bargain.setMinPrice(attrValueRequest.getMinPrice());
        bargain.setCost(attrValueRequest.getCost());
        bargain.setStock(attrValueRequest.getStock());
        bargain.setQuota(attrValueRequest.getQuota());
        bargain.setQuotaShow(attrValueRequest.getQuota());
        int saveCount = dao.updateById(bargain);
        if (saveCount <= 0) {
            throw new CrmebException("????????????????????????");
        }

        // ??????????????????attr???????????????attrValue?????????????????????????????????
        attrValueService.removeByProductId(request.getId(), ProductConstants.PRODUCT_TYPE_BARGAIN);
        StoreProductAttrValue singleAttrValue = new StoreProductAttrValue();
        BeanUtils.copyProperties(attrValueRequest, singleAttrValue);
        singleAttrValue.setProductId(bargain.getId()).setType(ProductConstants.PRODUCT_TYPE_BARGAIN);
        singleAttrValue.setImage(systemAttachmentService.clearPrefix(singleAttrValue.getImage()));
        boolean saveAttrValue = attrValueService.save(singleAttrValue);
        if(!saveAttrValue) throw new CrmebException("????????????????????????");

        // ???????????????
        StoreProductDescription spd = new StoreProductDescription(
                bargain.getId(),  request.getContent().length() > 0
                ? systemAttachmentService.clearPrefix(request.getContent()) : "" , ProductConstants.PRODUCT_TYPE_BARGAIN);
        storeProductDescriptionService.deleteByProductId(spd.getProductId(), ProductConstants.PRODUCT_TYPE_BARGAIN);
        boolean saveDesc = storeProductDescriptionService.save(spd);
        if (!saveDesc) throw new CrmebException("???????????????????????????");

        // attrResult???????????????????????????
        storeProductAttrResultService.deleteByProductId(bargain.getId(),ProductConstants.PRODUCT_TYPE_BARGAIN);
        StoreProductAttrResult attrResult = new StoreProductAttrResult(
                0,
                bargain.getId(),
                systemAttachmentService.clearPrefix(JSON.toJSONString(request.getAttrValue())),
                DateUtil.getNowTime(),ProductConstants.PRODUCT_TYPE_BARGAIN);
        storeProductAttrResultService.save(attrResult);

        return saveCount > 0;
    }

    /**
     * ????????????????????????
     * @param id ????????????id
     * @param status ??????????????????
     * @return Boolean
     */
    @Override
    public boolean updateBargainStatus(Integer id, boolean status) {
        //??????-??????????????????
        StoreBargain temp = getById(id);

        //??????-????????????
        if (ObjectUtil.isNull(temp) || temp.getIsDel()) {
            throw new CrmebException("?????????????????????");
        }

        //??????-????????????
        if (status) {
            //??????-?????????-????????????
            StoreProduct product = storeProductService.getById(temp.getProductId());
            if (ObjectUtil.isNull(product)) {
                throw new CrmebException("?????????????????????????????????????????????!");
            }
        }

        //????????????
        StoreBargain storeBargain = new StoreBargain();
        storeBargain.setId(id).setStatus(status);
        return dao.updateById(storeBargain) > 0;
    }

    /**
     * ????????????????????????
     * @param bargainId ????????????id
     * @return StoreProductResponse
     */
    @Override
    public StoreProductResponse getAdminDetail(Integer bargainId) {
        StoreBargain storeBargain = dao.selectById(bargainId);
        if (ObjectUtil.isNull(storeBargain)) throw new CrmebException("?????????????????????????????????");
        StoreProductResponse storeProductResponse = new StoreProductResponse();
        BeanUtils.copyProperties(storeBargain, storeProductResponse);
        storeProductResponse.setStartTime(new Date(storeBargain.getStartTime()));
        storeProductResponse.setStopTime(new Date(storeBargain.getStopTime()));
        storeProductResponse.setStatus(storeBargain.getStatus().equals(true) ? 1 : 0);

        // ??????attr
        StoreProductAttr spaPram = new StoreProductAttr();
        spaPram.setProductId(storeBargain.getProductId() ).setType(ProductConstants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttr> attrs = attrService.getByEntity(spaPram);
        storeProductResponse.setAttr(attrs);
        storeProductResponse.setSliderImage(String.join(",",storeBargain.getImages()));

        boolean specType = false;
        if (attrs.size() > 1) {
            specType = true;
        }
        storeProductResponse.setSpecType(specType);

        // ???????????????????????????????????????????????????????????????????????????sku????????????????????????sku???????????????????????????????????????sku??????
        StoreProductAttrValue spavPramBargain = new StoreProductAttrValue();
        spavPramBargain.setProductId(bargainId).setType(ProductConstants.PRODUCT_TYPE_BARGAIN);
        List<StoreProductAttrValue> storeProductAttrValuesBargain = attrValueService.getByEntity(spavPramBargain);
        List<HashMap<String, Object>> attrValuesBargain = genratorSkuInfo(bargainId, specType, storeBargain, storeProductAttrValuesBargain, ProductConstants.PRODUCT_TYPE_BARGAIN);

        // ??????attrValue
        StoreProductAttrValue spavPramProduct = new StoreProductAttrValue();
        spavPramProduct.setProductId(storeBargain.getProductId()).setType(ProductConstants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttrValue> storeProductAttrValuesProduct = attrValueService.getByEntity(spavPramProduct);
        List<HashMap<String, Object>> attrValuesProduct = genratorSkuInfo(storeBargain.getProductId(), specType, storeBargain, storeProductAttrValuesProduct, ProductConstants.PRODUCT_TYPE_NORMAL);

        // H5 ???????????????skuList
        List<StoreProductAttrValueResponse> sPAVResponses = new ArrayList<>();

        for (StoreProductAttrValue storeProductAttrValue : storeProductAttrValuesBargain) {
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            BeanUtils.copyProperties(storeProductAttrValue, atr);
            // ?????????????????????????????????
            atr.setQuota(storeProductResponse.getQuota());
            atr.setMinPrice(storeBargain.getMinPrice());
            atr.setChecked(true);
            sPAVResponses.add(atr);
        }

        for (int k = 0; k < attrValuesProduct.size(); k++) {
            for (int i = 0; i < attrValuesBargain.size(); i++) {
                HashMap<String, Object> bargainMap = attrValuesBargain.get(i);
                HashMap<String, Object> productMap = attrValuesProduct.get(k);
                productMap.put("checked", false);
                productMap.put("quota", productMap.get("stock"));
                productMap.put("price", productMap.get("price"));
                if(bargainMap.get("suk").equals(productMap.get("suk"))){
                    productMap.put("checked", true);
                    productMap.put("quota", bargainMap.get("quota"));
                    productMap.put("price",bargainMap.get("price"));
                    break;
                }
            }
        }

        storeProductResponse.setAttrValues(attrValuesProduct);
        storeProductResponse.setAttrValue(sPAVResponses);

        StoreProductDescription sd = storeProductDescriptionService.getOne(
                new LambdaQueryWrapper<StoreProductDescription>()
                        .eq(StoreProductDescription::getProductId, bargainId)
                        .eq(StoreProductDescription::getType, ProductConstants.PRODUCT_TYPE_BARGAIN));
        if(null != sd){
            storeProductResponse.setContent(StrUtil.isBlank(sd.getDescription()) ? "" : sd.getDescription());
        }
        if (StrUtil.isNotBlank(storeProductResponse.getRule())) {
            storeProductResponse.setRule(systemAttachmentService.clearPrefix(storeBargain.getRule()));
        }
        return storeProductResponse;
    }

    /**
     * h5 ????????????????????????
     * @param pageParamRequest ????????????
     * @return PageInfo<StoreBargainDetailResponse>
     */
    @Override
    public PageInfo<StoreBargainDetailResponse> getH5List(PageParamRequest pageParamRequest) {
        Page<StoreBargain> storeBargainPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreBargain> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreBargain::getId, StoreBargain::getProductId, StoreBargain::getTitle, StoreBargain::getImage,
                StoreBargain::getStartTime, StoreBargain::getStopTime, StoreBargain::getMinPrice, StoreBargain::getQuota);
        lqw.eq(StoreBargain::getStatus, true);
        lqw.eq(StoreBargain::getIsDel, false);
        long timeMillis = System.currentTimeMillis();
        lqw.le(StoreBargain::getStartTime, timeMillis);
        lqw.ge(StoreBargain::getStopTime, timeMillis);
        lqw.orderByDesc(StoreBargain::getSort, StoreBargain::getId);
        List<StoreBargain> storeBargains = dao.selectList(lqw);
        if (CollUtil.isEmpty(storeBargains)) {
            return CommonPage.copyPageInfo(storeBargainPage, CollUtil.newArrayList());
        }
        List<StoreBargainDetailResponse> bargainResponseList = storeBargains.stream().map(bargain -> {
            StoreBargainDetailResponse storeBargainResponse = new StoreBargainDetailResponse();
            BeanUtils.copyProperties(bargain, storeBargainResponse);
            return storeBargainResponse;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(storeBargainPage, bargainResponseList);
    }

    /**
     * H5 ??????????????????
     * @param id ????????????id
     */
    @Override
    public BargainDetailH5Response getH5Detail(Integer id) {
        StoreBargain storeBargain = dao.selectById(id);
        if (ObjectUtil.isNull(storeBargain) || storeBargain.getIsDel()) {
            throw new CrmebException("?????????????????????????????????");
        }
        if (!storeBargain.getStatus()) {
            throw new CrmebException("?????????????????????");
        }
        BargainDetailH5Response detailH5Response = new BargainDetailH5Response();
        BeanUtils.copyProperties(storeBargain, detailH5Response);

        StoreProductAttrValue spavPramBargain = new StoreProductAttrValue();
        spavPramBargain.setProductId(id).setType(ProductConstants.PRODUCT_TYPE_BARGAIN);
        List<StoreProductAttrValue> storeProductAttrValuesBargain = storeProductAttrValueService.getByEntity(spavPramBargain);
        if (CollUtil.isEmpty(storeProductAttrValuesBargain)) {
            throw new CrmebException("????????????????????????????????????");
        }
        StoreProductAttrValue productAttrValue = storeProductAttrValuesBargain.get(0);
        detailH5Response.setAttrValueId(productAttrValue.getId());
        detailH5Response.setSku(productAttrValue.getSuk());

        StoreProductDescription sd = storeProductDescriptionService.getOne(
                new LambdaQueryWrapper<StoreProductDescription>()
                        .eq(StoreProductDescription::getProductId, id)
                        .eq(StoreProductDescription::getType, ProductConstants.PRODUCT_TYPE_BARGAIN));
        if (ObjectUtil.isNotNull(sd)) {
            detailH5Response.setContent(ObjectUtil.isNull(sd.getDescription()) ? "" : sd.getDescription());
        }
        return detailH5Response;
    }

    /**
     * ????????????????????????????????????
     * @param productId ????????????id
     * @return List<StoreBargain>
     */
    @Override
    public List<StoreBargain> getCurrentBargainByProductId(Integer productId) {
        long timeMillis = System.currentTimeMillis();
        LambdaQueryWrapper<StoreBargain> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreBargain::getProductId, productId);
        lqw.eq(StoreBargain::getIsDel, false);
        lqw.le(StoreBargain::getStartTime, timeMillis);
        lqw.ge(StoreBargain::getStopTime, timeMillis);
        lqw.orderByDesc(StoreBargain::getSort, StoreBargain::getId);
        return dao.selectList(lqw);
    }

    /**
     * ??????????????????
     * @param request ????????????
     * @return MyRecord
     */
    @Override
    public MyRecord start(BargainFrontRequest request) {
        StoreBargain storeBargain = dao.selectById(request.getBargainId());
        if (ObjectUtil.isNull(storeBargain) || storeBargain.getIsDel()) {
            throw new CrmebException("??????????????????????????????");
        }
        if (!storeBargain.getStatus()) {
            throw new CrmebException("?????????????????????");
        }
        if (storeBargain.getQuota() <= 0 || storeBargain.getStock() <= 0) {
            throw new CrmebException("?????????????????????");
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis > storeBargain.getStopTime()) {
            throw new CrmebException("?????????????????????");
        }
        User user = userService.getInfoException();

        // ?????????????????????????????????
        StoreBargainUser spavBargainUser = new StoreBargainUser();
        spavBargainUser.setIsDel(false).setBargainId(request.getBargainId()).setUid(user.getUid());
        List<StoreBargainUser> historyList = storeBargainUserService.getByEntity(spavBargainUser);
        if (CollUtil.isNotEmpty(historyList)) {
            List<StoreBargainUser> collect = historyList.stream().filter(i -> i.getStatus().equals(BargainConstants.BARGAIN_USER_STATUS_PARTICIPATE)).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(collect)) {
                throw new CrmebException("??????????????????????????????");
            }
            // ??????????????????????????????????????????
            if (historyList.size() >= storeBargain.getNum()) {
                throw new CrmebException("????????????????????????????????????");
            }
        }

        StoreBargainUser storeBargainUser = new StoreBargainUser();
        storeBargainUser.setUid(user.getUid());
        storeBargainUser.setBargainId(request.getBargainId());
        storeBargainUser.setBargainPriceMin(storeBargain.getMinPrice());
        storeBargainUser.setBargainPrice(storeBargain.getPrice());
        storeBargainUser.setPrice(BigDecimal.ZERO);
        storeBargainUser.setAddTime(System.currentTimeMillis());
        storeBargainUser.setStatus(BargainConstants.BARGAIN_USER_STATUS_PARTICIPATE);
        boolean save = storeBargainUserService.save(storeBargainUser);
        if (!save) {
            throw new CrmebException("??????????????????");
        }
        MyRecord record = new MyRecord();
        record.set("storeBargainUserId", storeBargainUser.getId());
        return record;
    }

    /**
     * ??????????????????????????????
     * @param storeBargainParam ????????????
     * @return list
     */
    @Override
    public List<StoreBargain> getByEntity(StoreBargain storeBargainParam) {
        LambdaQueryWrapper<StoreBargain> lqw = new LambdaQueryWrapper<>();
        lqw.setEntity(storeBargainParam);
        return dao.selectList(lqw);
    }

    /**
     * ????????????
     * @param stockRequest  StoreProductStockRequest ????????????
     * @return Boolean
     */
    @Override
    public Boolean stockAddRedis(StoreProductStockRequest stockRequest) {
        String _productString = JSON.toJSONString(stockRequest);
        redisUtil.lPush(Constants.PRODUCT_BARGAIN_STOCK_UPDATE, _productString);
        return true;
    }

    /**
     * ??????????????????????????????
     */
    @Override
    public void consumeProductStock() {
        String redisKey = Constants.PRODUCT_BARGAIN_STOCK_UPDATE;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("StoreBargainServiceImpl.consumeProductStock | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //??????10????????????????????????????????????????????????
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (ObjectUtil.isNull(data)) {
                continue;
            }
            try{
                StoreProductStockRequest storeProductStockRequest =
                        com.alibaba.fastjson.JSONObject.toJavaObject(com.alibaba.fastjson.JSONObject.parseObject(data.toString()), StoreProductStockRequest.class);
                boolean result = doProductStock(storeProductStockRequest);
                if(!result){
                    redisUtil.lPush(redisKey, data);
                }
            }catch (Exception e){
                redisUtil.lPush(redisKey, data);
            }
        }

    }

    /**
     * ???????????????????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stopAfterChange() {
        // ?????????????????????????????????????????????????????????????????????
        List<StoreBargain> storeBargainList = getByStatusAndGtStopTime();
        logger.info("StoreBargainServiceImpl.stopAfterChange | size:" + storeBargainList.size());
        if (CollUtil.isEmpty(storeBargainList)) {
            return;
        }
        List<StoreBargainUser> bargainUserList = CollUtil.newArrayList();
        for (StoreBargain bargain : storeBargainList) {
            // ?????????????????????????????????????????????????????????
            StoreBargainUser spavBargainUser = new StoreBargainUser();
            spavBargainUser.setBargainId(bargain.getId());
            spavBargainUser.setStatus(BargainConstants.BARGAIN_USER_STATUS_PARTICIPATE);
            spavBargainUser.setIsDel(false);
            List<StoreBargainUser> bargainUsers = storeBargainUserService.getByEntity(spavBargainUser);
            if (CollUtil.isEmpty(bargainUsers)) {
                continue ;
            }
            for (StoreBargainUser bargainUser : bargainUsers) {
                bargainUser.setStatus(BargainConstants.BARGAIN_USER_STATUS_FAIL);
            }
            bargainUserList.addAll(bargainUsers);
        }
        boolean b = storeBargainUserService.updateBatchById(bargainUserList, 100);
        if (!b) {
            logger.error("???????????????????????????????????????????????????????????????????????????");
            throw new CrmebException("?????????????????????????????????????????????");
        }
    }

    /**
     * ??????????????????????????????
     * @param productId ????????????
     * @return Boolean
     */
    @Override
    public Boolean isExistActivity(Integer productId) {
        // ?????????????????????????????????
        LambdaQueryWrapper<StoreBargain> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreBargain::getProductId, productId);
        List<StoreBargain> bargainList = dao.selectList(lqw);
        if (CollUtil.isEmpty(bargainList)) {
            return false;
        }
        // ???????????????????????????????????????????????????
        List<StoreBargain> list = bargainList.stream().filter(i -> i.getStatus().equals(true)).collect(Collectors.toList());
        return CollUtil.isNotEmpty(list);
    }

    /**
     * ???????????????
     * @param id ????????????id
     * @return StoreBargain
     */
    @Override
    public StoreBargain getByIdException(Integer id) {
        LambdaQueryWrapper<StoreBargain> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreBargain::getId, id);
        lqw.eq(StoreBargain::getIsDel, false);
        lqw.eq(StoreBargain::getStatus, true);
        StoreBargain storeBargain = dao.selectOne(lqw);
        if (ObjectUtil.isNull(storeBargain)) throw new CrmebException("?????????????????????????????????");
        return storeBargain;
    }

    /**
     * ??????/????????????
     * @param id ????????????id
     * @param num ??????
     * @param type ?????????add????????????sub?????????
     */
    @Override
    public Boolean operationStock(Integer id, Integer num, String type) {
        UpdateWrapper<StoreBargain> updateWrapper = new UpdateWrapper<>();
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
     * ??????????????????6???
     * @return BargainIndexResponse
     */
    @Override
    public BargainIndexResponse getIndexInfo() {
        LambdaQueryWrapper<StoreBargain> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreBargain::getId, StoreBargain::getProductId, StoreBargain::getTitle, StoreBargain::getMinPrice, StoreBargain::getPrice, StoreBargain::getImage);
        lqw.eq(StoreBargain::getStatus, true);
        lqw.eq(StoreBargain::getIsDel, false);
        lqw.gt(StoreBargain::getStock, 0);
        long timeMillis = System.currentTimeMillis();
        lqw.le(StoreBargain::getStartTime, timeMillis);
        lqw.ge(StoreBargain::getStopTime, timeMillis);
        lqw.orderByDesc(StoreBargain::getSort, StoreBargain::getId);
        lqw.last(" limit 6");
        List<StoreBargain> storeBargains = dao.selectList(lqw);
        if (CollUtil.isEmpty(storeBargains)) {
            return null;
        }
        BargainIndexResponse response = new BargainIndexResponse();
        response.setProductList(storeBargains);
        return response;
    }

    /**
     * ??????????????????header
     * @return BargainHeaderResponse
     */
    @Override
    public BargainHeaderResponse getHeader() {
        BargainHeaderResponse headerResponse = new BargainHeaderResponse();
        // ???????????????????????????
        Integer bargainTotal = storeBargainUserHelpService.getCount();
        headerResponse.setBargainTotal(bargainTotal);
        if (bargainTotal <= 0) {
            return headerResponse;
        }
        // ????????????????????????
        List<StoreBargainUser> bargainUserList = storeBargainUserService.getHeaderList();
        List<Integer> uidList = bargainUserList.stream().map(StoreBargainUser::getUid).distinct().collect(Collectors.toList());
        HashMap<Integer, User> userMap = userService.getMapListInUid(uidList);
        List<Integer> bargainIdList = bargainUserList.stream().map(StoreBargainUser::getBargainId).distinct().collect(Collectors.toList());
        HashMap<Integer, String> bargainMap = getStoreNameMapInId(bargainIdList);
        List<HashMap<String, Object>> mapList = bargainUserList.stream().map(e -> {
            // ????????????????????????
            User user = userMap.get(e.getUid());
            //?????????map
            HashMap<String, Object> map = CollUtil.newHashMap();
            map.put("price", e.getBargainPriceMin());
            map.put("title", bargainMap.get(e.getBargainId()));
            if(user==null){
                map.put("nickName", "????????????");
                map.put("avatar", "https://bing.ioliu.cn/v1/rand");
            }else{
                map.put("nickName", user.getNickname());
                map.put("avatar", user.getAvatar());
            }
            return map;
        }).collect(Collectors.toList());
        headerResponse.setBargainSuccessList(mapList);
        return headerResponse;
    }

    /**
     * ??????id????????????????????????map
     * @param bargainIdList ????????????id??????
     * @return HashMap<Integer, StoreBargain>
     */
    @Override
    public HashMap<Integer, StoreBargain> getMapInId(List<Integer> bargainIdList) {
        LambdaQueryWrapper<StoreBargain> lqw = new LambdaQueryWrapper<>();
        lqw.in(StoreBargain::getId, bargainIdList);
        List<StoreBargain> bargainList = dao.selectList(lqw);
        HashMap<Integer, StoreBargain> map = CollUtil.newHashMap();
        bargainList.forEach(e -> {
            map.put(e.getId(), e);
        });
        return map;
    }

    /**
     * ????????????????????????Map
     * @param bargainIdList ????????????id??????
     * @return List<HashMap<Object, Object>>
     */
    private HashMap<Integer, String> getStoreNameMapInId(List<Integer> bargainIdList) {
        LambdaQueryWrapper<StoreBargain> lqw = new LambdaQueryWrapper<>();
        lqw.select(StoreBargain::getId, StoreBargain::getTitle);
        lqw.in(StoreBargain::getId, bargainIdList);
        List<StoreBargain> bargainList = dao.selectList(lqw);
        HashMap<Integer, String> map = CollUtil.newHashMap();
        bargainList.forEach(e -> {
            map.put(e.getId(), e.getTitle());
        });
        return map;
    }

    /**
     * ?????????????????????????????????????????????????????????????????????
     * @return List<StoreBargain>
     */
    private List<StoreBargain> getByStatusAndGtStopTime() {
        LambdaQueryWrapper<StoreBargain> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreBargain::getStatus, true);
        lqw.lt(StoreBargain::getStopTime, System.currentTimeMillis());
        return dao.selectList(lqw);
    }

    /**
     * ??????????????????sku????????????
     * @param productId     ??????id
     * @param specType  ??????????????????
     * @param storeProductAttrValues    ????????????
     * @param productType   ?????????????????????
     * @return  sku??????
     */
    private  List<HashMap<String, Object>> genratorSkuInfo(int productId, boolean specType, StoreBargain storeBargain,
                                                           List<StoreProductAttrValue> storeProductAttrValues,
                                                           int productType) {
        List<HashMap<String, Object>> attrValues = new ArrayList<>();
        if (specType) {
            StoreProductAttrResult sparPram = new StoreProductAttrResult();
            sparPram.setProductId(productId).setType(productType);
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
                    attrValue.put("minPrice", storeBargain.getMinPrice());
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

    // ??????????????????
    private boolean doProductStock(StoreProductStockRequest storeProductStockRequest){
        // ????????????????????????
        StoreBargain existProduct = getById(storeProductStockRequest.getBargainId());
        List<StoreProductAttrValue> existAttr =
                storeProductAttrValueService.getListByProductIdAndAttrId(
                        storeProductStockRequest.getBargainId(),
                        storeProductStockRequest.getAttrId().toString(),
                        storeProductStockRequest.getType());
        if(ObjectUtil.isNull(existProduct) || ObjectUtil.isNull(existAttr)){ // ???????????????
            logger.info("??????????????????????????????????????????"+JSON.toJSONString(storeProductStockRequest));
            return true;
        }

        // ??????????????????/?????? ?????????
        boolean isPlus = storeProductStockRequest.getOperationType().equals("add");
        int productStock = isPlus ? existProduct.getStock() + storeProductStockRequest.getNum() : existProduct.getStock() - storeProductStockRequest.getNum();
        existProduct.setStock(productStock);
        existProduct.setSales(existProduct.getSales() - storeProductStockRequest.getNum());
        existProduct.setQuota(existProduct.getQuota() + storeProductStockRequest.getNum());
        updateById(existProduct);

        // ??????sku??????
        for (StoreProductAttrValue attrValue : existAttr) {
            int productAttrStock = isPlus ? attrValue.getStock() + storeProductStockRequest.getNum() : attrValue.getStock() - storeProductStockRequest.getNum();
            attrValue.setStock(productAttrStock);
            attrValue.setSales(attrValue.getSales()-storeProductStockRequest.getNum());
            attrValue.setQuota(attrValue.getQuota() + storeProductStockRequest.getNum());
            storeProductAttrValueService.updateById(attrValue);
        }

        // ????????????????????????
        // StoreProductStockRequest ?????????????????????????????????????????????????????????????????????
        StoreProductResponse existProductLinkedSeckill = storeProductService.getByProductId(storeProductStockRequest.getProductId());
        for (StoreProductAttrValueResponse attrValueResponse : existProductLinkedSeckill.getAttrValue()) {
            if(attrValueResponse.getSuk().equals(storeProductStockRequest.getSuk())){
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

}

