package com.zbkj.crmeb.store.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.front.request.IndexStoreProductSearchRequest;
import com.zbkj.crmeb.front.response.ProductDetailReplyResponse;
import com.zbkj.crmeb.front.response.ProductReplyResponse;
import com.zbkj.crmeb.store.dao.StoreProductReplyDao;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.StoreProductReply;
import com.zbkj.crmeb.store.request.StoreProductReplyAddRequest;
import com.zbkj.crmeb.store.request.StoreProductReplySearchRequest;
import com.zbkj.crmeb.store.response.StoreProductReplyResponse;
import com.zbkj.crmeb.store.service.StoreOrderInfoService;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.store.service.StoreProductReplyService;
import com.zbkj.crmeb.store.service.StoreProductService;
import com.zbkj.crmeb.store.vo.StoreOrderInfoOldVo;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class StoreProductReplyServiceImpl extends ServiceImpl<StoreProductReplyDao, StoreProductReply>
        implements StoreProductReplyService {

    @Resource
    private StoreProductReplyDao dao;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TransactionTemplate transactionTemplate;

    public static void main(String[] args) {
        System.out.println(new Random().nextInt(3));
        System.out.println(new Random().nextInt(3));
        System.out.println(new Random().nextInt(3));
        System.out.println(new Random().nextInt(3));
        System.out.println(new Random().nextInt(3));
        System.out.println(new Random().nextInt(3));
        System.out.println(new Random().nextInt(3));
        System.out.println(new Random().nextInt(3));
    }

    @Override
    public StoreProductReply getStoreProductReply(Integer uid, Integer oid, Integer productId,
                                                  String unique, String replyType,
                                                  Integer productScore, Integer serviceScore,
                                                  String comment, String pics,
                                                  String merchantReplyContent, Integer merchantReplyTime,
                                                  String nickname, String avatar,
                                                  String sku) {
        //???????????????
        StoreProductReply reply=new StoreProductReply(); // ??????????????????
        reply.setUid(uid);
        reply.setOid(oid);
        reply.setProductId(productId);
        reply.setUnique(unique);
        reply.setReplyType(replyType);
        reply.setProductScore(productScore);
        reply.setServiceScore(serviceScore);
        reply.setComment(comment);
        reply.setPics(pics);
        reply.setNickname(nickname);
        reply.setAvatar(avatar);
        reply.setSku(sku);
        reply.setCreateTime(DateUtil.nowDateTime());
        return reply;
    }

    /**
    * ??????
    * @param request ????????????
    * @param pageParamRequest ???????????????
    * @author Mr.Zhang
    * @since 2020-05-27
    * @return List<StoreProductReply>
    */
    @Override
    public PageInfo<StoreProductReplyResponse> getList(StoreProductReplySearchRequest request, PageParamRequest pageParamRequest) {
        Page<StoreProductReply> pageStoreReply = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //??? StoreProductReply ?????????????????????
        LambdaQueryWrapper<StoreProductReply> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(null != request.getIsDel()){
            lambdaQueryWrapper.eq(StoreProductReply::getIsDel, request.getIsDel());
        }
        if(null != request.getIsReply()){
            lambdaQueryWrapper.eq(StoreProductReply::getIsReply, request.getIsReply());
        }
        if(null != request.getOid()){
            lambdaQueryWrapper.eq(StoreProductReply::getOid, request.getOid());
        }
        if(!StringUtils.isBlank(request.getProductId())){
            lambdaQueryWrapper.in(StoreProductReply::getProductId, CrmebUtil.stringToArray(request.getProductId()));
        }
        if(StrUtil.isNotBlank(request.getProductSearch())){
            IndexStoreProductSearchRequest storeProductPram = new IndexStoreProductSearchRequest();
            storeProductPram.setKeywords(request.getProductSearch());
            List<StoreProduct> storeProducts = storeProductService.getList(storeProductPram, new PageParamRequest());
            List<Integer> productIds = storeProducts.stream().map(StoreProduct::getId).collect(Collectors.toList());
            if(productIds.size() > 0){
                lambdaQueryWrapper.in(StoreProductReply::getProductId, productIds);
            }
        }
        if(!StringUtils.isBlank(request.getUid())){
            lambdaQueryWrapper.in(StoreProductReply::getUid, CrmebUtil.stringToArray(request.getUid()));
        }
        if(StringUtils.isNotBlank(request.getNickname())){
            lambdaQueryWrapper.like(StoreProductReply::getNickname,request.getNickname());
        }
        //????????????|0=??????,1=??????,2=??????,3=??????
        List<Integer> typeList = new ArrayList<>();
        switch (request.getType()){
            case 1:
                typeList.add(5);
                typeList.add(4);
                break;
            case 2:
                typeList.add(3);
                break;
            case 3:
                typeList.add(2);
                typeList.add(1);
                break;
            default:
               break;

        }
        if(typeList.size() > 0 ){
            lambdaQueryWrapper.in(StoreProductReply::getProductScore, typeList);
        }

        if(request.getStar() > 0){
            lambdaQueryWrapper.eq(StoreProductReply::getProductScore, request.getStar());
        }

        if(StringUtils.isNotBlank(request.getDateLimit())){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
            lambdaQueryWrapper.between(StoreProductReply::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        lambdaQueryWrapper.orderByDesc(StoreProductReply::getId);
        List<StoreProductReply> dataList = dao.selectList(lambdaQueryWrapper);
        List<StoreProductReplyResponse> dataResList = new ArrayList<>();
        for (StoreProductReply productReply : dataList) {
            StoreProductReplyResponse productReplyResponse = new StoreProductReplyResponse();
            BeanUtils.copyProperties(productReply, productReplyResponse);
            StoreProduct storeProduct = storeProductService.getById(productReply.getProductId());
            productReplyResponse.setStoreProduct(storeProduct);
            productReplyResponse.setPics(CrmebUtil.stringToArrayStr(productReply.getPics()));
            dataResList.add(productReplyResponse);
        }
        return CommonPage.copyPageInfo(pageStoreReply, dataResList);
    }

    /**
     * ????????????
     * @return Integer
     */
    @Override
    public Integer getSumStar(Integer productId) {
        QueryWrapper<StoreProductReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(sum(product_score),0) as product_score", "IFNULL(sum(service_score),0) as service_score");
        queryWrapper.eq("is_del", 0);
        queryWrapper.eq("product_id", productId);
        StoreProductReply storeProductReply = dao.selectOne(queryWrapper);
        if (ObjectUtil.isNull(storeProductReply)){
            return 0;
        }
        if (storeProductReply.getProductScore() == 0 || storeProductReply.getServiceScore() == 0) {
            return 0;
        }
        // ?????? = ??????????????? + ??????????????? / 2
        BigDecimal sumScore = new BigDecimal(storeProductReply.getProductScore() + storeProductReply.getServiceScore());
        BigDecimal divide = sumScore.divide(BigDecimal.valueOf(2L), 0, BigDecimal.ROUND_DOWN);
        return divide.intValue();
    }

    @Override
    public Boolean create(StoreProductReplyAddRequest request) {
        //??????-?????????????????????????????????
        User user = userService.getInfoException();

        //??????-????????????
        StoreOrder storeOrder = storeOrderService.getByOderId(request.getOrderNo());
        if (ObjectUtil.isNull(storeOrder) || !storeOrder.getUid().equals(user.getUid())) {
            throw new CrmebException("??????????????????");
        }

        //??????-????????????
        StoreProductReply storeProductReply = new StoreProductReply(); // ????????????
        BeanUtils.copyProperties(request, storeProductReply);

        //??????????????????
        if(!StrUtil.isNotBlank(storeProductReply.getComment())){
            String[] strings={
                    "????????????????????????",
                    "???????????????????????????????????????",
                    "?????????????????????????????????????????????",
                    "???????????????????????????",
                    "?????????????????????????????????",
                    "?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????",
                    "????????????????????????????????????????????????",
                    "????????????????????????"
            };
            storeProductReply.setComment(strings[new Random().nextInt(strings.length)]);
        }

        //??????-??????????????????
        storeProductReply.setUid(user.getUid());
        storeProductReply.setOid(storeOrder.getId());
        storeProductReply.setAvatar(systemAttachmentService.clearPrefix(user.getAvatar()));
        storeProductReply.setNickname(user.getNickname());

        //???????????????????????????
        if(StringUtils.isNotBlank(request.getPics())){
            String pics = request.getPics().replace("[\"","").replace("\"]","")
                    .replace("\"","");
            storeProductReply.setPics(systemAttachmentService.clearPrefix(ArrayUtils.toString(pics)));
        }

        //??????
        Boolean execute = transactionTemplate.execute(e -> {
            //????????????????????????
            Integer count = this.checkIsReply(storeProductReply);
            //????????????
            this.save(storeProductReply);
            //??????????????????
            this.completeOrder(storeProductReply, count, storeOrder);
            return Boolean.TRUE;
        });

        //????????????
        if (!execute) {
            throw new CrmebException("??????????????????");
        }
        return execute;
    }

    /**
     * ??????????????????
     * @param request ????????????
     * @return ????????????
     */
    @Override
    public boolean virtualCreate(StoreProductReplyAddRequest request) {
        StoreProductReply storeProductReply = new StoreProductReply(); // ??????????????????
        BeanUtils.copyProperties(request, storeProductReply);
        if(StringUtils.isNotBlank(request.getPics())){
            String pics = request.getPics()
                    .replace("[","")
                    .replace("]","")
                    .replace("\"","");
            storeProductReply.setPics(systemAttachmentService.clearPrefix(ArrayUtils.toString(pics)));
        }
        storeProductReply.setAvatar(systemAttachmentService.clearPrefix(storeProductReply.getAvatar()));
        storeProductReply.setUnique(CrmebUtil.randomCount(11111,9999)+"");
        return save(storeProductReply);
    }

    /**
     * ?????????????????????
     * @param unique ??????id
     * @param replayType ??????
     * @return ????????????
     */
    @Override
    public List<StoreProductReply> isReply(String unique, String replayType, Integer orderId) {
        LambdaQueryWrapper<StoreProductReply> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreProductReply::getUnique, unique);
        lqw.eq(StoreProductReply::getReplyType, replayType);
        lqw.eq(StoreProductReply::getOid, orderId);
        return dao.selectList(lqw);
    }

    /**
     * ?????????????????????
     * @param unique skuId
     * @param orderId ??????id
     * @return ????????????
     */
    @Override
    public Boolean isReply(String unique, Integer orderId) {
        LambdaQueryWrapper<StoreProductReply> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreProductReply::getUnique, unique);
        lqw.eq(StoreProductReply::getOid, orderId);
        List<StoreProductReply> replyList = dao.selectList(lqw);
        if (CollUtil.isEmpty(replyList)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * ??????????????????
     * @param productId     ??????ID
     * @param type          ????????????
     * @return
     */
    @Override
    public List<StoreProductReply> getAllByPidAndType(Integer productId, String type) {
        LambdaQueryWrapper<StoreProductReply> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreProductReply::getProductId, productId);
        lqw.eq(StoreProductReply::getReplyType, type);
        lqw.eq(StoreProductReply::getIsDel, false);
        lqw.orderByDesc(StoreProductReply::getId);
        return dao.selectList(lqw);
    }

    /**
     * H5??????????????????
     * @param productId ????????????
     * @return MyRecord
     */
    @Override
    public MyRecord getH5Count(Integer productId) {
        // ????????????
        Integer sumCount = getCountByScore(productId, "all");
        // ????????????
        Integer goodCount = getCountByScore(productId, "good");
        // ????????????
        Integer mediumCount = getCountByScore(productId, "medium");
        // ????????????
        Integer poorCount = getCountByScore(productId, "poor");
        // ?????????
        String replyChance = "0";
        if(sumCount > 0 && goodCount > 0){
            replyChance = String.format("%.2f", ((goodCount.doubleValue() / sumCount.doubleValue())));
        }
        // ????????????(???????????? + ????????????)/2
        Integer replyStar = 0;
        if (sumCount > 0) {
            replyStar = getSumStar(productId);

        }
        MyRecord record = new MyRecord();
        record.set("sumCount", sumCount);
        record.set("goodCount", goodCount);
        record.set("mediumCount", mediumCount);
        record.set("poorCount", poorCount);
        record.set("replyChance", replyChance);
        record.set("replyStar", replyStar);
        return record;
    }

    /**
     * H5????????????????????????
     * @param proId ????????????
     * @return ProductDetailReplyResponse
     */
    @Override
    public ProductDetailReplyResponse getH5ProductReply(Integer proId) {
        ProductDetailReplyResponse response = new ProductDetailReplyResponse();

        // ????????????
        Integer sumCount = getCountByScore(proId, "all");
        if (sumCount.equals(0)) {
            response.setSumCount(0);
            response.setReplyChance("0");
            return response;
        }
        // ????????????
        Integer goodCount = getCountByScore(proId, "good");
        // ?????????
        String replyChance = "0";
        if(sumCount > 0 && goodCount > 0){
            replyChance = String.format("%.2f", ((goodCount.doubleValue() / sumCount.doubleValue())));
        }

        // ????????????????????????
        LambdaQueryWrapper<StoreProductReply> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreProductReply::getProductId, proId);
        lqw.eq(StoreProductReply::getIsDel, false);
        lqw.orderByDesc(StoreProductReply::getId);
        lqw.last(" limit 1");
        StoreProductReply storeProductReply = dao.selectOne(lqw);
        ProductReplyResponse productReplyResponse = new ProductReplyResponse();
        BeanUtils.copyProperties(storeProductReply, productReplyResponse);
        // ?????????
        productReplyResponse.setPics(CrmebUtil.stringToArrayStr(storeProductReply.getPics()));
        // ??????
        String nickname = storeProductReply.getNickname();
        if (StrUtil.isNotBlank(nickname)) {
            if (nickname.length() == 1) {
                nickname = nickname.concat("**");
            } else if (nickname.length() == 2) {
                nickname = nickname.substring(0, 1) + "**";
            } else {
                nickname = nickname.substring(0, 1) + "**" + nickname.substring(nickname.length() - 1);
            }
            productReplyResponse.setNickname(nickname);
        }
        // ?????? = ??????????????? + ??????????????? / 2
        BigDecimal sumScore = new BigDecimal(storeProductReply.getProductScore() + storeProductReply.getServiceScore());
        BigDecimal divide = sumScore.divide(BigDecimal.valueOf(2L), 0, BigDecimal.ROUND_DOWN);
        productReplyResponse.setScore(divide.intValue());

        response.setSumCount(sumCount);
        response.setReplyChance(replyChance);
        response.setProductReply(productReplyResponse);
        return response;
    }

    /**
     * ???????????????????????????
     * @param proId ????????????
     * @param type ????????????|0=??????,1=??????,2=??????,3=??????
     * @param pageParamRequest ????????????
     * @return PageInfo<ProductReplyResponse>
     */
    @Override
    public PageInfo<ProductReplyResponse> getH5List(Integer proId, Integer type, PageParamRequest pageParamRequest) {
        Page<StoreProductReply> startPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //??? StoreProductReply ?????????????????????
        LambdaQueryWrapper<StoreProductReply> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreProductReply::getIsDel, false);
        lqw.eq(StoreProductReply::getProductId, proId);
        //????????????|0=??????,1=??????,2=??????,3=??????
        List<Integer> typeList = new ArrayList<>();
        switch (type){
            case 1:
                lqw.apply(" (product_score + service_score) >= 8");
                break;
            case 2:
                lqw.apply(" (product_score + service_score) < 8 and (product_score + service_score) > 4");
                break;
            case 3:
                lqw.apply(" (product_score + service_score) <= 4");
                break;
            default:
                break;

        }
        lqw.orderByDesc(StoreProductReply::getId);
        List<StoreProductReply> replyList = dao.selectList(lqw);
        List<ProductReplyResponse> responseList = new ArrayList<>();
        for (StoreProductReply productReply : replyList) {
            ProductReplyResponse productReplyResponse = new ProductReplyResponse();
            BeanUtils.copyProperties(productReply, productReplyResponse);
            // ?????????
            productReplyResponse.setPics(CrmebUtil.stringToArrayStr(productReply.getPics()));
            // ??????
            String nickname = productReply.getNickname();
            if (StrUtil.isNotBlank(nickname)) {
                if (nickname.length() == 1) {
                    nickname = nickname.concat("**");
                } else if (nickname.length() == 2) {
                    nickname = nickname.substring(0, 1) + "**";
                } else {
                    nickname = nickname.substring(0, 1) + "**" + nickname.substring(nickname.length() - 1);
                }
                productReplyResponse.setNickname(nickname);
            }

            //????????????
            //productReplyResponse.setComment(EmojiConverterUtil.emojiRecovery2(productReply.getComment()));

            // ?????? = ??????????????? + ??????????????? / 2
            BigDecimal sumScore = new BigDecimal(productReply.getProductScore() + productReply.getServiceScore());
            BigDecimal divide = sumScore.divide(BigDecimal.valueOf(2L), 0, BigDecimal.ROUND_DOWN);
            productReplyResponse.setScore(divide.intValue());

            responseList.add(productReplyResponse);
        }
        return CommonPage.copyPageInfo(startPage, responseList);
    }

    // ????????????????????????????????????????????????
    private Integer getCountByScore(Integer productId, String type) {
        LambdaQueryWrapper<StoreProductReply> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreProductReply::getProductId, productId);
        lqw.eq(StoreProductReply::getIsDel, false);

        switch (type) {
            case "all":
                break;
            case "good":
                lqw.apply( " (product_score + service_score) >= 8");
//                lqw.in(StoreProductReply::getProductScore, 4, 5);
                break;
            case "medium":
                lqw.apply( " (product_score + service_score) < 8 and (product_score + service_score) > 4");
//                lqw.eq(StoreProductReply::getProductScore, 3);
                break;
            case "poor":
                lqw.apply( " (product_score + service_score) <= 4");
//                lqw.in(StoreProductReply::getProductScore, 2, 1);
                break;
        }
        return dao.selectCount(lqw);
    }


    private StoreOrder getOrder(StoreProductReply storeProductReply) {
        //????????????
        StoreOrder storeOrder = new StoreOrder();
        storeOrder.setId(storeProductReply.getOid());
        storeOrder.setUid(storeProductReply.getUid());
        storeOrder = storeOrderService.getInfoByEntity(storeOrder);
        if(null == storeOrder){
            throw new CrmebException("?????????????????????");
        }
        return storeOrder;
    }

    /**
     * ????????????????????????????????????????????????
     * @author Mr.Zhang
     * @since 2020-06-03
     * @return Integer
     */
    private void completeOrder(StoreProductReply storeProductReply, Integer count, StoreOrder storeOrder) {
        Integer replyCount = getReplyCountByEntity(storeProductReply, true);
        if(replyCount.equals(count)){
            //????????????????????????
            storeOrder.setStatus(Constants.ORDER_STATUS_INT_COMPLETE);
            storeOrderService.updateById(storeOrder);
            redisUtil.lPush(Constants.ORDER_TASK_REDIS_KEY_AFTER_COMPLETE_BY_USER, storeOrder.getId());
        }
    }

    /**
     * ????????????????????????????????????
     * @author Mr.Zhang
     * @since 2020-06-03
     * @return Integer
     */
    private Integer checkIsReply(StoreProductReply storeProductReply) {
        //??????????????????
        List<StoreOrderInfoOldVo> orderInfoVoList = storeOrderInfoService.getOrderListByOrderId(storeProductReply.getOid());
        if(null == orderInfoVoList || orderInfoVoList.size() < 1){
            throw new CrmebException("????????????????????????");
        }

        boolean findResult = false;
        for (StoreOrderInfoOldVo orderInfoVo : orderInfoVoList) {
            Integer productId = orderInfoVo.getInfo().getProductId();
            if(productId < 1){
                continue;
            }
            if(storeProductReply.getProductId().equals(productId)){
                findResult = true;
                break;
            }
        }

        if(!findResult){
            throw new CrmebException("????????????????????????");
        }

        //?????????????????????
        Integer replyCount = getReplyCountByEntity(storeProductReply, false);
        if(replyCount > 0){
            throw new CrmebException("??????????????????");
        }
        return orderInfoVoList.size();
    }

    /**
     * ????????????id  ??????id  ??????id ??????????????????
     * @author Mr.Zhang
     * @since 2020-06-03
     * @return Integer
     */
    private Integer getReplyCountByEntity(StoreProductReply request, boolean isAll) {
        LambdaQueryWrapper<StoreProductReply> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreProductReply::getOid, request.getOid());
        if(null != request.getUid()){
            lambdaQueryWrapper.eq(StoreProductReply::getUid, request.getUid());
        }
        if(!isAll){
            lambdaQueryWrapper.eq(StoreProductReply::getProductId, request.getProductId());
        }
        return dao.selectCount(lambdaQueryWrapper);
    }

}

