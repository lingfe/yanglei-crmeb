package com.zbkj.crmeb.marketing.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.common.SearchAndPageRequest;
import com.constants.Constants;
import com.constants.CouponConstants;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.zbkj.crmeb.category.model.Category;
import com.zbkj.crmeb.category.service.CategoryService;
import com.zbkj.crmeb.marketing.dao.StoreCouponDao;
import com.zbkj.crmeb.marketing.model.StoreCoupon;
import com.zbkj.crmeb.marketing.model.StoreCouponUser;
import com.zbkj.crmeb.marketing.request.StoreCouponRequest;
import com.zbkj.crmeb.marketing.request.StoreCouponSearchRequest;
import com.zbkj.crmeb.marketing.response.StoreCouponFrontResponse;
import com.zbkj.crmeb.marketing.response.StoreCouponInfoResponse;
import com.zbkj.crmeb.marketing.service.StoreCouponService;
import com.zbkj.crmeb.marketing.service.StoreCouponUserService;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.request.StoreProductSearchRequest;
import com.zbkj.crmeb.store.service.StoreProductService;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreCouponServiceImpl extends ServiceImpl<StoreCouponDao, StoreCoupon> implements StoreCouponService {

    @Resource
    private StoreCouponDao dao;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreCouponUserService storeCouponUserService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;


    /**
    * ??????
    * @param request ????????????
    * @param pageParamRequest ???????????????
    * @author Mr.Zhang
    * @since 2020-05-18
    * @return List<StoreCoupon>
    */
    @Override
    public List<StoreCoupon> getList(StoreCouponSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //??? StoreCoupon ?????????????????????
        LambdaQueryWrapper<StoreCoupon> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreCoupon::getIsDel, false);

        if(null != request.getType()){
            lambdaQueryWrapper.eq(StoreCoupon::getType, request.getType());
        }

        if(null != request.getStatus()){
            lambdaQueryWrapper.eq(StoreCoupon::getStatus, request.getStatus());
        }

        if(StringUtils.isNotBlank(request.getName())){
            lambdaQueryWrapper.like(StoreCoupon::getName, request.getName());
        }

        if(StringUtils.isNotBlank(request.getPrimaryKey())){
            lambdaQueryWrapper.apply(CrmebUtil.getFindInSetSql("primary_key", request.getPrimaryKey()) + " and use_type = " + request.getUseType());
        }

        lambdaQueryWrapper.orderByDesc(StoreCoupon::getSort).orderByDesc(StoreCoupon::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * ??????????????????
     * @param request StoreCouponRequest ????????????
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    @Override
    public boolean create(StoreCouponRequest request) {
        StoreCoupon storeCoupon = new StoreCoupon();
        BeanUtils.copyProperties(request, storeCoupon);
        if(storeCoupon.getIsLimited() && (storeCoupon.getTotal() == null || storeCoupon.getTotal() == 0)){
            throw new CrmebException("??????????????????");
        }

        if(request.getUseType() > 1 && (StringUtils.isBlank(request.getPrimaryKey()))){
            throw new CrmebException("???????????????/?????????");
        }

        storeCoupon.setLastTotal(storeCoupon.getTotal());
        if(!request.getIsForever()){
            storeCoupon.setReceiveStartTime(DateUtil.nowDateTime()); //?????????????????????????????????
        }else{
            if(storeCoupon.getReceiveStartTime() == null || storeCoupon.getReceiveEndTime() == null){
                throw new CrmebException("??????????????????????????????");
            }

            int compareDate = DateUtil.compareDate(DateUtil.dateToStr(storeCoupon.getReceiveStartTime(), Constants.DATE_FORMAT), DateUtil.dateToStr(storeCoupon.getReceiveEndTime(), Constants.DATE_FORMAT), Constants.DATE_FORMAT);
            if(compareDate > -1){
                throw new CrmebException("???????????????????????????????????????");
            }
        }

        //???????????????, ??????????????????
        if(!request.getIsFixedTime()){
            if(storeCoupon.getDay() == null || storeCoupon.getDay() == 0){
                throw new CrmebException("??????????????????");
            }
            storeCoupon.setUseStartTime(null);
            storeCoupon.setUseEndTime(null);
        }
        return save(storeCoupon);
    }

    /**
     * ????????????
     * @param id Integer id
     * @author Mr.Zhang
     * @since 2020-05-18
     * @return StoreCoupon
     */
    @Override
    public StoreCoupon getInfoException(Integer id) {
        //?????????????????????
        StoreCoupon storeCoupon = getById(id);
        checkException(storeCoupon);

        return storeCoupon;
    }

    /**
     * ?????????????????????????????????
     * @param storeCoupon StoreCoupon ???????????????`
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    @Override
    public void checkException(StoreCoupon storeCoupon) {
        if(storeCoupon == null || storeCoupon.getIsDel() || !storeCoupon.getStatus()){
            throw new CrmebException("??????????????????????????????????????????");
        }

        //???????????????
        if(!(storeCoupon.getReceiveEndTime() == null || storeCoupon.getReceiveEndTime().equals(""))){
            //??????????????????
            String date = DateUtil.nowDateTimeStr();
            int result = DateUtil.compareDate(date, DateUtil.dateToStr(storeCoupon.getReceiveEndTime(), Constants.DATE_FORMAT), Constants.DATE_FORMAT);
            if(result == 1){
                //?????????
                throw new CrmebException("???????????????????????????????????????");
            }
        }

        //????????????????????????
        if(storeCoupon.getIsLimited()){
            //?????????????????????????????????????????????
            if(storeCoupon.getLastTotal() < 1){
                throw new CrmebException("?????????????????????????????????");
            }
        }
    }

    @Override
    public StoreCouponInfoResponse info(Integer id) {
        try{
            StoreCoupon storeCoupon = getById(id);
            if(storeCoupon == null || storeCoupon.getIsDel() || !storeCoupon.getStatus()){
                throw new CrmebException("??????????????????????????????????????????");
            }
            List<StoreProduct> productList = null;
            List<Category> categoryList = null;
            if(StringUtils.isNotBlank(storeCoupon.getPrimaryKey()) && storeCoupon.getUseType() > 1){
                List<Integer> primaryIdList = CrmebUtil.stringToArray(storeCoupon.getPrimaryKey());
                if(storeCoupon.getUseType() == 2){
                    productList = storeProductService.getList(new StoreProductSearchRequest(), new PageParamRequest(), primaryIdList);
                }else if(storeCoupon.getUseType() == 3){
                    categoryList = categoryService.getByIds(primaryIdList);
                }
            }
            StoreCouponRequest coupon = new StoreCouponRequest();
            BeanUtils.copyProperties(storeCoupon, coupon);
            coupon.setIsForever(false);
            if(null != coupon.getReceiveEndTime()){
                coupon.setIsForever(true);
            }
            return new StoreCouponInfoResponse(coupon, productList, categoryList);
        }catch (Exception e){
            throw new CrmebException(e.getMessage());
        }
    }

    /**
     * ???????????????id??????
     * @param ids ?????????id??????
     * @return List<StoreCoupon>
     */
    @Override
    public List<StoreCoupon> getByIds(List<Integer> ids) {
        LambdaQueryWrapper<StoreCoupon> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(StoreCoupon::getId, ids);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * ????????????
     * @param id ?????????id
     * @param num ??????
     * @param isLimited ????????????
     */
    @Override
    public Boolean deduction(Integer id, Integer num, Boolean isLimited) {
        UpdateWrapper<StoreCoupon> updateWrapper = new UpdateWrapper<>();
        if (isLimited) {
            updateWrapper.setSql(StrUtil.format("last_total = last_total - {}", num));
            updateWrapper.last(StrUtil.format(" and (last_total - {} >= 0)", num));
        } else {
            updateWrapper.setSql(StrUtil.format("last_total = last_total + {}", num));
        }
        updateWrapper.eq("id", id);
        return update(updateWrapper);
    }

    /**
     * ?????????????????????????????????
     * @return List<StoreCoupon>
     */
    @Override
    public List<StoreCoupon> findRegisterList() {
        String dateStr = DateUtil.nowDate(Constants.DATE_FORMAT);
        LambdaQueryWrapper<StoreCoupon> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreCoupon::getType, 2);
        lqw.eq(StoreCoupon::getStatus, true);
        lqw.eq(StoreCoupon::getIsDel, false);
        lqw.le(StoreCoupon::getReceiveStartTime, dateStr);
        List<StoreCoupon> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return CollUtil.newArrayList();
        }
        List<StoreCoupon> couponList = list.stream().filter(coupon -> {
            // ????????????
            if (coupon.getIsLimited() && coupon.getLastTotal() <= 0) {
                return false;
            }
            // ?????????????????????????????????
            if (ObjectUtil.isNotNull(coupon.getReceiveStartTime())) {
                //??????????????????
                int result = DateUtil.compareDate(dateStr, DateUtil.dateToStr(coupon.getReceiveStartTime(), Constants.DATE_FORMAT), Constants.DATE_FORMAT);
                if(result == -1){
                    // ?????????
                    return false;
                }
            }

            // ???????????????????????????
            if (ObjectUtil.isNotNull(coupon.getReceiveEndTime())) {
                int compareDate = DateUtil.compareDate(dateStr, DateUtil.dateToStr(coupon.getReceiveEndTime(), Constants.DATE_FORMAT), Constants.DATE_FORMAT);
                if (compareDate > 0) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        return couponList;
    }

    /**
     * ?????????????????????
     * @param request ??????????????????
     * @return ???????????????
     * PC????????????????????????????????????????????????????????????????????????????????????????????????
     * ????????????????????????????????????
     */
    @Override
    public List<StoreCoupon> getSendList(SearchAndPageRequest request) {
        PageHelper.startPage(request.getPage(), request.getLimit());

        LambdaQueryWrapper<StoreCoupon> lqw = new LambdaQueryWrapper<>();
        lqw.select(StoreCoupon::getId, StoreCoupon::getName, StoreCoupon::getMoney, StoreCoupon::getMinPrice,
                StoreCoupon::getUseStartTime, StoreCoupon::getUseEndTime, StoreCoupon::getIsFixedTime, StoreCoupon::getDay,
                StoreCoupon::getIsLimited, StoreCoupon::getLastTotal);
        lqw.eq(StoreCoupon::getIsDel, false);
        if (ObjectUtil.isNotNull(request.getType())) {
            lqw.eq(StoreCoupon::getType, request.getType());
        }
        lqw.eq(StoreCoupon::getStatus, true);
        if (StringUtils.isNotBlank(request.getKeywords())) {
            lqw.like(StoreCoupon::getName, request.getKeywords());
        }
        lqw.and(o -> o.eq(StoreCoupon::getIsLimited, false).or().ge(StoreCoupon::getLastTotal, 0));
        lqw.and(o -> o.isNull(StoreCoupon::getReceiveEndTime).or().gt(StoreCoupon::getReceiveEndTime, DateUtil.nowDate(Constants.DATE_FORMAT)));
        lqw.orderByDesc(StoreCoupon::getSort, StoreCoupon::getId);
        return dao.selectList(lqw);
    }

    /**
     * ???????????????
     * @param id ?????????id
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer id) {
        StoreCoupon coupon = getById(id);
        if (ObjectUtil.isNull(coupon) || coupon.getIsDel()) {
            throw new CrmebException("??????????????????");
        }
        coupon.setIsDel(true);
        return dao.updateById(coupon) > 0;
    }

    @Override
    public List<StoreCoupon> getHomeIndexCoupon() {
        Date date = DateUtil.nowDateTime();
        LambdaQueryWrapper<StoreCoupon> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(StoreCoupon::getId, StoreCoupon::getName, StoreCoupon::getMoney, StoreCoupon::getMinPrice);
        lambdaQueryWrapper.eq(StoreCoupon::getIsDel, false);
        lambdaQueryWrapper.eq(StoreCoupon::getStatus, 1);
        //??????????????????0 ?????????????????????
        lambdaQueryWrapper.and(i -> i.gt(StoreCoupon::getLastTotal, 0).or().eq(StoreCoupon::getIsLimited, false));
        //??????????????????, ???????????????null???????????????
        lambdaQueryWrapper.and(i -> i.isNull(StoreCoupon::getReceiveEndTime).or( p -> p.lt(StoreCoupon::getReceiveStartTime, date).gt(StoreCoupon::getReceiveEndTime, date)));
        // ??????????????????????????????????????????null???????????????
        lambdaQueryWrapper.and(i -> i.isNull(StoreCoupon::getUseEndTime).or(p -> p.gt(StoreCoupon::getUseEndTime, date)));
        lambdaQueryWrapper.eq(StoreCoupon::getType, CouponConstants.COUPON_TYPE_RECEIVE);
        lambdaQueryWrapper.orderByDesc(StoreCoupon::getSort).orderByDesc(StoreCoupon::getId);
        lambdaQueryWrapper.last(" limit 2");
        List<StoreCoupon> couponList = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(couponList) || couponList.size() < 2) {
            return null;
        }
        return couponList;
    }

    /**
     * ????????????????????????
     * @param type ?????????1-?????????2-?????????3-??????
     * @param productId ??????id??????????????????????????????
     * @param pageParamRequest ????????????
     * @return List<StoreCouponFrontResponse>
     */
    @Override
    public List<StoreCouponFrontResponse> getH5List(Integer type, Integer productId, PageParamRequest pageParamRequest) {
        // ?????????????????????
        List<StoreCoupon> list = getListByReceive(type, productId, pageParamRequest);
        if(ObjectUtil.isNull(list)){
            return null;
        }
        //????????????????????????????????????????????????
        HashMap<Integer, StoreCouponUser> couponUserMap = null;
        Integer userId = userService.getUserId();
        if(userId > 0){
            couponUserMap = storeCouponUserService.getMapByUserId(userId);
        }
        List<StoreCouponFrontResponse> storeCouponFrontResponseArrayList = new ArrayList<>();
        for (StoreCoupon storeCoupon : list) {
            StoreCouponFrontResponse response = new StoreCouponFrontResponse();
            BeanUtils.copyProperties(storeCoupon, response);

            if (userId > 0) {
                if(CollUtil.isNotEmpty(couponUserMap) && couponUserMap.containsKey(storeCoupon.getId())){
                    response.setIsUse(true);
                }
            }

            if(response.getReceiveEndTime() == null){
                response.setReceiveStartTime(null);
            }

            // ??????????????????????????????????????????
            response.setUseStartTimeStr(DateUtil.dateToStr(storeCoupon.getUseStartTime(), Constants.DATE_FORMAT_DATE));
            response.setUseEndTimeStr(DateUtil.dateToStr(storeCoupon.getUseEndTime(), Constants.DATE_FORMAT_DATE));
            storeCouponFrontResponseArrayList.add(response);
        }

        return storeCouponFrontResponseArrayList;
    }

    /**
     * ???????????????????????????
     * @return List<StoreCoupon>
     */
    private List<StoreCoupon> getListByReceive(Integer type, Integer productId, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        Date date = DateUtil.nowDateTime();
        //??? StoreCoupon ?????????????????????
        LambdaQueryWrapper<StoreCoupon> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreCoupon::getIsDel, false);
        lqw.eq(StoreCoupon::getStatus, 1);
        //??????????????????0 ?????????????????????
        lqw.and(i -> i.gt(StoreCoupon::getLastTotal, 0).or().eq(StoreCoupon::getIsLimited, false));
        //??????????????????, ???????????????null???????????????
        lqw.and(i -> i.isNull(StoreCoupon::getReceiveEndTime).or( p -> p.lt(StoreCoupon::getReceiveStartTime, date).gt(StoreCoupon::getReceiveEndTime, date)));
        // ??????????????????????????????????????????null???????????????
        lqw.and(i -> i.isNull(StoreCoupon::getUseEndTime).or(p -> p.gt(StoreCoupon::getUseEndTime, date)));
        lqw.eq(StoreCoupon::getType, 1);
        if(productId > 0){
            //?????????id  ?????????????????????????????????????????????????????????????????????
            getPrimaryKeySql(lqw, productId.toString());
        }
        switch (type) {
            case 1:
                lqw.eq(StoreCoupon::getUseType, CouponConstants.COUPON_USE_TYPE_COMMON);
                break;
            case 2:
                lqw.eq(StoreCoupon::getUseType, CouponConstants.COUPON_USE_TYPE_PRODUCT);
                break;
            case 3:
                lqw.eq(StoreCoupon::getUseType, CouponConstants.COUPON_USE_TYPE_CATEGORY);
                break;
        }

        lqw.orderByDesc(StoreCoupon::getSort).orderByDesc(StoreCoupon::getId);
        return dao.selectList(lqw);
    }

    private void getPrimaryKeySql(LambdaQueryWrapper<StoreCoupon> lambdaQueryWrapper, String productIdStr){
        if(StringUtils.isBlank(productIdStr)){
            return;
        }

        List<Integer> categoryIdList = storeProductService.getSecondaryCategoryByProductId(productIdStr);
        lambdaQueryWrapper.and(i -> i.and(
                //?????????  ?????????  ?????????
                t -> t.eq(StoreCoupon::getUseType, 1)
                .or(p -> p.eq(StoreCoupon::getUseType , 2).apply(CrmebUtil.getFindInSetSql("primary_key", productIdStr)))
                .or(c -> c.eq(StoreCoupon::getUseType , 3).apply(CrmebUtil.getFindInSetSql("primary_key", (ArrayList<Integer>) categoryIdList)))
        ));
    }
}

