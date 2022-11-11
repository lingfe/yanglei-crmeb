package com.zbkj.crmeb.store.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.aliyun.oss.common.utils.HttpHeaders;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.constants.CategoryConstants;
import com.constants.Constants;
import com.constants.SysConfigConstants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.utils.UploadUtil;
import com.utils.lingfe.files.FilesUtils;
import com.utils.lingfe.images.ImageHttpGet;
import com.zbkj.crmeb.bargain.service.StoreBargainService;
import com.zbkj.crmeb.category.model.Category;
import com.zbkj.crmeb.category.service.CategoryService;
import com.zbkj.crmeb.combination.service.StoreCombinationService;
import com.zbkj.crmeb.front.request.IndexStoreProductSearchRequest;
import com.zbkj.crmeb.front.request.ProductRequest;
import com.zbkj.crmeb.front.response.ProductAttrResponse;
import com.zbkj.crmeb.marketing.model.StoreCoupon;
import com.zbkj.crmeb.marketing.service.StoreCouponService;
import com.zbkj.crmeb.pass.service.OnePassService;
import com.zbkj.crmeb.seckill.service.StoreSeckillService;
import com.zbkj.crmeb.store.dao.StoreProductDao;
import com.zbkj.crmeb.store.model.*;
import com.zbkj.crmeb.store.request.StoreProductAttrValueRequest;
import com.zbkj.crmeb.store.request.StoreProductRequest;
import com.zbkj.crmeb.store.request.StoreProductSearchRequest;
import com.zbkj.crmeb.store.request.StoreProductStockRequest;
import com.zbkj.crmeb.store.response.StoreProductAttrValueResponse;
import com.zbkj.crmeb.store.response.StoreProductExcelResponse;
import com.zbkj.crmeb.store.response.StoreProductResponse;
import com.zbkj.crmeb.store.response.StoreProductTabsHeader;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.utilService.ProductUtils;
import com.zbkj.crmeb.store.vo.StoreProductAttrExcel;
import com.zbkj.crmeb.store.vo.StoreProductAttrValueExcel;
import com.zbkj.crmeb.store.vo.StoreProductExcel;
import com.zbkj.crmeb.store.vo.StoreProductInfoExcel;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.task.order.OrderRefundTask;
import com.zbkj.crmeb.upload.vo.FileResultVo;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Service
public class StoreProductServiceImpl extends ServiceImpl<StoreProductDao, StoreProduct> implements StoreProductService {

    @Resource
    private StoreProductDao dao;

    @Autowired
    private StoreBrandsService storeBrandsService;

    @Autowired
    private StoreProductAttrService attrService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private StoreProductDescriptionService storeProductDescriptionService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StoreProductRelationService storeProductRelationService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private StoreProductAttrResultService storeProductAttrResultService;

    @Autowired
    private StoreProductCouponService storeProductCouponService;

    @Autowired
    private StoreCouponService storeCouponService;

    @Autowired
    private ProductUtils productUtils;

    @Autowired
    private StoreBargainService storeBargainService;

    @Autowired
    private StoreCombinationService storeCombinationService;

    @Autowired
    private StoreSeckillService storeSeckillService;

    @Autowired
    private OnePassService onePassService;

    @Autowired
    private StoreCartService storeCartService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private StoreProductRAService storeProductRAService;

    @Autowired
    private SupplierService supplierService;

    private static final Logger logger = LoggerFactory.getLogger(OrderRefundTask.class);

    @Override
    public List<ProductAttrResponse> getSkuAttr(List<StoreProductAttr> attrList) {
        //定义-商品属性-响应list集合
        List<ProductAttrResponse> attrResponseList = new ArrayList<>();

        //循环处理
        for (StoreProductAttr attr : attrList) {
            //实例化-商品属性-响应对象
            ProductAttrResponse attrResponse = new ProductAttrResponse();

            //赋值
            attrResponse.setProductId(attr.getProductId());
            attrResponse.setAttrName(attr.getAttrName());
            attrResponse.setType(attr.getType());

            //定义-属性值-存放list集合
            List<String> attrValues = new ArrayList<>();

            //去除-属性值-数组括号
            String trimAttr = attr.getAttrValues()
                    .replace("[","")
                    .replace("]","");

            //检测-是否由逗号隔开
            if(attr.getAttrValues().contains(",")){
                attrValues = Arrays.asList(trimAttr.split(","));
            }else{
                attrValues.add(trimAttr);
            }

            //赋值到-商品属性响应对象
            attrResponse.setAttrValues(attrValues);

            //添加到-商品属性响应list集合中
            attrResponseList.add(attrResponse);
        }

        //返回-商品属性响应list集合中
        return attrResponseList;
    }

    @Override
    public Boolean setMerId(String productId, Integer merId) {
        //验证非空
        if(productId == null)throw new CrmebException("商品ID不能为空!");
        if(merId == null)throw new CrmebException("商户ID不能为空！");

        //将商品ID字符串转为数组
        List<String> productIdList= Arrays.stream(productId.split(",")).collect(Collectors.toList());
        for (String idString:productIdList) {
            StoreProduct storeProduct =  dao.selectById(idString);
            if(storeProduct == null) continue;
            storeProduct.setMerId(merId);  // 商品-绑定供应商ID/商户id
            dao.updateById(storeProduct);
        }

        //返回结果
        return Boolean.TRUE;
    }

    @Override
    public List<StoreProduct> getList(StoreProductSearchRequest request, PageParamRequest pageParamRequest, List<Integer> productIdList) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(request.getIsBest() != null){
            lambdaQueryWrapper.eq(StoreProduct::getIsBest, request.getIsBest());
        }

        if(request.getIsHot() != null){
            lambdaQueryWrapper.eq(StoreProduct::getIsHot, request.getIsHot());
        }

        if(request.getIsNew() != null){
            lambdaQueryWrapper.eq(StoreProduct::getIsNew, request.getIsNew());
        }

        if(request.getIsBenefit() != null){
            lambdaQueryWrapper.eq(StoreProduct::getIsBest, request.getIsBenefit());
        }

        if(null != productIdList && productIdList.size() > 0){
            lambdaQueryWrapper.in(StoreProduct::getId, productIdList);
        }

        lambdaQueryWrapper.eq(StoreProduct::getIsDel, false)
                .eq(StoreProduct::getMerId, false)
                .gt(StoreProduct::getStock, 0)
                .eq(StoreProduct::getIsShow, true)
                .orderByDesc(StoreProduct::getSort)
                .orderByDesc(StoreProduct::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据条件得到商品list
     * @param request           条件
     * @return
     */
    List<StoreProduct> getStoreProductList(StoreProductSearchRequest request){
        //带 StoreProduct 类的多条件查询
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //类型搜索-商品状态
        switch (request.getType()){
            case 1:
                //出售中（已上架）
                lambdaQueryWrapper.eq(StoreProduct::getIsShow, true);
                lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                break;
            case 2:
                //仓库中（未上架）
                lambdaQueryWrapper.eq(StoreProduct::getIsShow, false);
                lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                break;
            case 3:
                //已售罄
                lambdaQueryWrapper.le(StoreProduct::getStock, 0);
                lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                break;
            case 4:
                //警戒库存
                Integer stock = Integer.parseInt(systemConfigService.getValueByKey("store_stock"));
                lambdaQueryWrapper.le(StoreProduct::getStock, stock);
                lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                break;
            case 5:
                //回收站
                lambdaQueryWrapper.eq(StoreProduct::getIsDel, true);
                break;
            default:
                break;
        }

        //关键字搜索
        if(!StringUtils.isBlank(request.getKeywords())){
            lambdaQueryWrapper.and(i -> i
                    .or().eq(StoreProduct::getId, request.getKeywords())
                    .or().like(StoreProduct::getStoreName, request.getKeywords())
                    .or().like(StoreProduct::getStoreInfo, request.getKeywords())
                    .or().like(StoreProduct::getKeyword, request.getKeywords())
                    .or().like(StoreProduct::getBarCode, request.getKeywords()));
        }

        //条件-是否为区域代理批发商品
        if(request.getIsRegionalAgent()){
            //是-只取该区域代理分类商品
            lambdaQueryWrapper.apply(CrmebUtil.getFindInSetSql("cate_id", 616));
        }else{
            //否-取其他普通分类商品
            if(StringUtils.isNotBlank(request.getCateId())){
                lambdaQueryWrapper.apply(CrmebUtil.getFindInSetSql("cate_id", request.getCateId()));
            }
        }

        //条件-筛选区域代理代理的产品-2021-11-24
        if(request.getRaId()!=null && request.getRaId() > 0){
            //通过-区域代理ID标识-得到产品代理表信息
            List<StoreProductRA> storeProductRAList=storeProductRAService.getWhereRaIDList(request.getRaId());
            if(storeProductRAList == null || storeProductRAList.size() == 0){
                lambdaQueryWrapper.in(StoreProduct::getId,9999);
            }else{
                //得到-商品ID标识list
                List<Integer> getProductIdList = storeProductRAList.stream().map(StoreProductRA::getProductId).collect(Collectors.toList());
                lambdaQueryWrapper.in(StoreProduct::getId,getProductIdList);
            }
        }

        //条件-筛选供应商绑定的商品
        if(request.getSupplierId() !=null && request.getSupplierId() > 0){
            lambdaQueryWrapper.eq(StoreProduct::getMerId,request.getSupplierId());  //筛选供应商绑定商品
        }

        //条件-是否已绑定供应商
        switch (request.getIsBindSupplier()){
            case 1: lambdaQueryWrapper.gt(StoreProduct::getMerId,0);break;
            case 2: lambdaQueryWrapper.eq(StoreProduct::getMerId,0);break;
        }

        //排序
        if (StrUtil.isNotBlank(request.getSalesOrder())) {
            //销量
            if(request.getSalesOrder().equals(Constants.SORT_DESC)){
                lambdaQueryWrapper.last(" order by (sales + ficti) desc, sort desc, id desc");
            }else{
                lambdaQueryWrapper.last(" order by (sales + ficti) asc, sort asc, id asc");
            }
        } else {
            //价格
            if(!StringUtils.isBlank(request.getPriceOrder())){
                if(request.getPriceOrder().equals(Constants.SORT_DESC)){
                    lambdaQueryWrapper.orderByDesc(StoreProduct::getPrice);
                }else{
                    lambdaQueryWrapper.orderByAsc(StoreProduct::getPrice);
                }
            }

            //顺序和ID
            lambdaQueryWrapper.orderByDesc(StoreProduct::getSort);
            lambdaQueryWrapper.orderByDesc(StoreProduct::getId);
        }

        //返回数据
        return dao.selectList(lambdaQueryWrapper);
    }


    @Override
    public PageInfo<StoreProductResponse> getList(StoreProductSearchRequest request, PageParamRequest pageParamRequest,Boolean isYw) {
        //得到分页对象
        Page<StoreProduct> storeProductPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        //得到数据
        List<StoreProduct> storeProducts = this.getStoreProductList(request);

        //循环处理-转成商品响应-list集合
        List<StoreProductResponse> storeProductResponses = new ArrayList<>();
        for (StoreProduct product : storeProducts) {
            //实例化-商品-响应对象
            StoreProductResponse storeProductResponse = new StoreProductResponse();
            BeanUtils.copyProperties(product, storeProductResponse);

            //得到商品绑定的供应商
            Supplier supplier=supplierService.getById(product.getMerId());
            if(supplier==null){
                storeProductResponse.setMerName("没有绑定供应商！");
            }else{
                storeProductResponse.setMerName(supplier.getSuppName());
            }

            //是否处理-业务逻辑
            if(isYw)storeProductResponse=this.handleProductInfo(product,storeProductResponse);

            //添加到-商品响应-list集合
            storeProductResponses.add(storeProductResponse);
        }

        // 多条sql查询处理分页正确
        return CommonPage.copyPageInfo(storeProductPage, storeProductResponses);
    }

    /**
     * 处理商品信息-赋值到-商品信息响应对象
     * @param product   商品
     * @param storeProductResponse  商品响应对象
     * @return  响应对象
     */
    public StoreProductResponse handleProductInfo(StoreProduct product,StoreProductResponse storeProductResponse){
        //得到-商品属性
        StoreProductAttr storeProductAttrPram = new StoreProductAttr();
        storeProductAttrPram.setProductId(product.getId()).setType(Constants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttr> attrs = attrService.getByEntity(storeProductAttrPram);
        if(attrs.size() > 0){
            storeProductResponse.setAttr(attrs);
        }

        //实例化-商品-属性值-list响应集合
        List<StoreProductAttrValueResponse> storeProductAttrValueResponse = new ArrayList<>();
        StoreProductAttrValue storeProductAttrValuePram = new StoreProductAttrValue();
        storeProductAttrValuePram.setProductId(product.getId()).setType(Constants.PRODUCT_TYPE_NORMAL);
        //得到-商品属性值list
        List<StoreProductAttrValue> storeProductAttrValues = storeProductAttrValueService.getByEntity(storeProductAttrValuePram);
        storeProductAttrValues.stream().map(e->{
            StoreProductAttrValueResponse response = new StoreProductAttrValueResponse();
            BeanUtils.copyProperties(e,response);
            storeProductAttrValueResponse.add(response);
            return e;
        }).collect(Collectors.toList());
        storeProductResponse.setAttrValue(storeProductAttrValueResponse);

        //得到-商品详情
        StoreProductDescription sd = storeProductDescriptionService.getOne(
                new LambdaQueryWrapper<StoreProductDescription>()
                        .eq(StoreProductDescription::getProductId, product.getId())
                        .eq(StoreProductDescription::getType, Constants.PRODUCT_TYPE_NORMAL));
        if(null != sd){
            storeProductResponse.setContent(null == sd.getDescription()?"":sd.getDescription());
        }

        //得到-分类中文
        List<Category> cg = categoryService.getByIds(CrmebUtil.stringToArray(product.getCateId()));
        if (CollUtil.isEmpty(cg)) {
            storeProductResponse.setCateValues("");
        } else {
            storeProductResponse.setCateValues(cg.stream().map(Category::getName).collect(Collectors.joining(",")));
        }

        //得到-收藏数量
        storeProductResponse.setCollectCount(storeProductRelationService.getList(product.getId(),"collect").size());

        //得到-商品品牌信息
        Integer brandId=product.getBrandId();
        StoreBrands storeBrands=storeBrandsService.getById(brandId);
        if(storeBrands!=null){
            storeProductResponse.setStoreBrands(storeBrands);
        }

        //返回-商品响应对象
        return storeProductResponse;
    }


    @Override
    public List<StoreProduct> getListInIds(List<Integer> productIds) {
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(StoreProduct::getId,productIds);
        return dao.selectList(lambdaQueryWrapper);
    }

    @Override
    public StoreProduct getByEntity(StoreProduct storeProduct) {
        LambdaQueryWrapper<StoreProduct> lqw = new LambdaQueryWrapper<>();
        lqw.setEntity(storeProduct);
        return dao.selectOne(lqw);
    }

    @Override
    public boolean save(StoreProductRequest storeProductRequest) {
        // 多规格需要校验规格参数
        if (storeProductRequest.getSpecType()) {
            if (CollUtil.isEmpty(storeProductRequest.getAttr())) {
                throw new CrmebException("商品属性不能为空");
            }
            if (CollUtil.isEmpty(storeProductRequest.getAttrValue())) {
                throw new CrmebException("商品属性详情不能为空");
            }
            // 校验商品属性详情参数
            checkAttrValue(storeProductRequest.getAttrValue());
        }

        //实例化商品对象
        StoreProduct storeProduct = new StoreProduct();
        BeanUtils.copyProperties(storeProductRequest, storeProduct);
        storeProduct.setAddTime(DateUtil.getNowTime());

        // 设置Acticity活动
        productUtils.setProductActivity(storeProductRequest, storeProduct);

        //视频url
        storeProduct.setVideoLink(systemAttachmentService.clearPrefix(storeProduct.getVideoLink()));

        //主图
        storeProduct.setImage(systemAttachmentService.clearPrefix(storeProduct.getImage()));

        //轮播图
        storeProduct.setSliderImage(systemAttachmentService.clearPrefix(storeProduct.getSliderImage()));

        //计算价格
        productUtils.calcPriceForAttrValues(storeProductRequest, storeProduct);

        // 商品活动默认值
        storeProduct.setActivity("0, 1, 2, 3");

        //保存商品数据
        boolean save = this.save(storeProduct);

        // 验证规格类型
        if(storeProductRequest.getSpecType()) {
            // 多属性，多规格
            storeProductRequest.getAttr().forEach(e->{
                e.setProductId(storeProduct.getId());
                e.setAttrValues(StringUtils.strip(e.getAttrValues().replace("\"",""),"[]"));
                e.setType(Constants.PRODUCT_TYPE_NORMAL);
            });
            boolean attrAddResult = attrService.saveOrUpdateBatch(storeProductRequest.getAttr());
            if (!attrAddResult) throw new CrmebException("新增属性名失败");
        }else{
            // 单属性，单规格
            StoreProductAttr singleAttr = new StoreProductAttr();
            singleAttr.setProductId(storeProduct.getId()).setAttrName("规格").setAttrValues("默认").setType(Constants.PRODUCT_TYPE_NORMAL);
            boolean attrAddResult = attrService.save(singleAttr);
            if (!attrAddResult) throw new CrmebException("新增属性名失败");
            StoreProductAttrValue singleAttrValue = new StoreProductAttrValue();
            BigDecimal commissionL1= BigDecimal.ZERO;
            BigDecimal commissionL2= BigDecimal.ZERO;
            if(storeProductRequest.getAttrValue().size()>0){
                commissionL1 = null != storeProductRequest.getAttrValue().get(0).getBrokerage() ?
                        storeProductRequest.getAttrValue().get(0).getBrokerage():BigDecimal.ZERO;
                commissionL2 = null != storeProductRequest.getAttrValue().get(0).getBrokerageTwo() ?
                        storeProductRequest.getAttrValue().get(0).getBrokerageTwo():BigDecimal.ZERO;
            }

            singleAttrValue.setProductId(storeProduct.getId()).setStock(storeProduct.getStock()).setSuk("默认")
                    .setSales(storeProduct.getSales()).setPrice(storeProduct.getPrice())
                    .setImage(systemAttachmentService.clearPrefix(storeProduct.getImage()))
                    .setCost(storeProduct.getCost()).setBarCode(storeProduct.getBarCode())
                    .setOtPrice(storeProduct.getOtPrice()).setBrokerage(commissionL1).setBrokerageTwo(commissionL2)
                    .setType(Constants.PRODUCT_TYPE_NORMAL);
            boolean saveOrUpdateResult = storeProductAttrValueService.save(singleAttrValue);
            if(!saveOrUpdateResult) throw new CrmebException("新增属性详情失败");
        }

        // 验证商品属性值详情
        if (null != storeProductRequest.getAttrValue() && storeProductRequest.getAttrValue().size() > 0) {
            // 批量设置attrValues对象的商品id
            List<StoreProductAttrValueRequest> storeProductAttrValuesRequest = storeProductRequest.getAttrValue();
            storeProductAttrValuesRequest.forEach(e->{
                e.setProductId(storeProduct.getId());
            });

            // 处理商品属性详情值
            List<StoreProductAttrValue> storeProductAttrValues = new ArrayList<>();
            for (StoreProductAttrValueRequest attrValuesRequest : storeProductAttrValuesRequest) {
                StoreProductAttrValue spav = new StoreProductAttrValue();
                BeanUtils.copyProperties(attrValuesRequest,spav);
                //设置sku字段
                if(null == attrValuesRequest.getAttrValue()){
                    break;
                }
                List<String> skuList = new ArrayList<>();
                for(Map.Entry<String,String> vo: attrValuesRequest.getAttrValue().entrySet()){
                    skuList.add(vo.getValue());
                    spav.setSuk(String.join(",",skuList));
                }
                spav.setImage(systemAttachmentService.clearPrefix(spav.getImage()));
                spav.setAttrValue(JSON.toJSONString(attrValuesRequest.getAttrValue()));
                spav.setType(Constants.PRODUCT_TYPE_NORMAL);
                storeProductAttrValues.add(spav);
            }

            // 保存属性
            if(storeProductAttrValues.size() > 0){
                boolean saveOrUpdateResult = storeProductAttrValueService.saveOrUpdateBatch(storeProductAttrValues);
                StoreProductAttrResult attrResult = new StoreProductAttrResult(
                        0,
                        storeProduct.getId(),
                        systemAttachmentService.clearPrefix(JSON.toJSONString(storeProductRequest.getAttrValue())),
                        DateUtil.getNowTime(),Constants.PRODUCT_TYPE_NORMAL);
                storeProductAttrResultService.save(attrResult);
                if(!saveOrUpdateResult) throw new CrmebException("新增属性详情失败");
            }
        }

        // 处理富文本
        StoreProductDescription spd = new StoreProductDescription(
                storeProduct.getId(),  storeProductRequest.getContent().length() > 0
                ? systemAttachmentService.clearPrefix(storeProductRequest.getContent()):"",Constants.PRODUCT_TYPE_NORMAL);
        storeProductDescriptionService.deleteByProductId(spd.getProductId(),Constants.PRODUCT_TYPE_NORMAL);
        storeProductDescriptionService.save(spd);

        // 处理优惠券关联信息
        productUtils.shipProductCoupons(storeProductRequest, storeProduct);
        return save;
    }

    /**
     * 校验商品属性详情参数
     * @param attrValue 商品属性详情
     */
    private void checkAttrValue(List<StoreProductAttrValueRequest> attrValue) {
        for (StoreProductAttrValueRequest attr : attrValue) {
            if (CollUtil.isEmpty(attr.getAttrValue())) {
                throw new CrmebException("商品属性详情sku不能为空");
            }
            if (ObjectUtil.isNull(attr.getStock())) {
                throw new CrmebException("商品属性详情库存不能为空");
            }
            if (attr.getStock() < 0) {
                throw new CrmebException("商品属性详情库存不能小于0");
            }
            if (ObjectUtil.isNull(attr.getPrice())) {
                throw new CrmebException("商品属性详情金额不能为空");
            }
            if (attr.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new CrmebException("商品属性详情金额必须大于0");
            }
            if (ObjectUtil.isNull(attr.getCost()) || attr.getCost().compareTo(BigDecimal.ZERO) < 0) {
                throw new CrmebException("商品属性详情成本价 不能为空或者小于0");
            }
            if (ObjectUtil.isNull(attr.getOtPrice()) || attr.getOtPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new CrmebException("商品属性详情原价 不能为空或者小于0");
            }
            if (ObjectUtil.isNull(attr.getWeight()) || attr.getWeight().compareTo(BigDecimal.ZERO) < 0) {
                throw new CrmebException("商品属性详情重量 不能为空或者小于0");
            }
            if (ObjectUtil.isNull(attr.getVolume()) || attr.getVolume().compareTo(BigDecimal.ZERO) < 0) {
                throw new CrmebException("商品属性详情体积 不能为空或者小于0");
            }
//            if (ObjectUtil.isNull(attr.getBrokerage()) || attr.getBrokerage().compareTo(BigDecimal.ZERO) < 0) {
//                throw new CrmebException("商品属性详情一级返佣 不能为空或者小于0");
//            }
//            if (ObjectUtil.isNull(attr.getBrokerageTwo()) || attr.getBrokerageTwo().compareTo(BigDecimal.ZERO) < 0) {
//                throw new CrmebException("商品属性详情二级返佣 不能为空或者小于0");
//            }
//            if (ObjectUtil.isNull(attr.getType())) {
//                throw new CrmebException("商品属性详情活动类型不能为空");
//            }
        }
    }

    @Override
    public boolean update(StoreProductRequest storeProductRequest) {
        // 多规格需要校验规格参数
        if (storeProductRequest.getSpecType()) {
            if (CollUtil.isEmpty(storeProductRequest.getAttr())) {
                throw new CrmebException("商品属性不能为空");
            }
            if (CollUtil.isEmpty(storeProductRequest.getAttrValue())) {
                throw new CrmebException("商品属性详情不能为空");
            }
            // 校验商品属性详情参数
            checkAttrValue(storeProductRequest.getAttrValue());
        }

        StoreProduct tempProduct = getById(storeProductRequest.getId());
        if (ObjectUtil.isNull(tempProduct)) {
            throw new CrmebException("商品不存在");
        }
        if (tempProduct.getIsShow()) {
            throw new CrmebException("请先下架商品，再进行修改");
        }

        StoreProduct storeProduct = new StoreProduct();
        BeanUtils.copyProperties(storeProductRequest, storeProduct);
        // 设置Acticity活动
        productUtils.setProductActivity(storeProductRequest, storeProduct);

        storeProduct.setAddTime(DateUtil.getNowTime());

        //视频url
        storeProduct.setVideoLink(systemAttachmentService.clearPrefix(storeProduct.getVideoLink()));

        //主图
        storeProduct.setImage(systemAttachmentService.clearPrefix(storeProduct.getImage()));

        //轮播图
        storeProduct.setSliderImage(systemAttachmentService.clearPrefix(storeProduct.getSliderImage()));

        productUtils.calcPriceForAttrValues(storeProductRequest, storeProduct);
        int saveCount = dao.updateById(storeProduct);
        // 对attr表做全量更新，删除原有数据保存现有数据
        attrService.removeByProductId(storeProduct.getId(),Constants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttrValue> attrValueList = storeProductAttrValueService.getListByProductIdAndAttrId(
                storeProduct.getId(), null, Constants.PRODUCT_TYPE_NORMAL);
        Map<String, Integer> valueMap = CollUtil.newHashMap();
        attrValueList.forEach(e -> {
            valueMap.put(e.getSuk(), e.getId());
        });
        storeProductAttrValueService.removeByProductId(storeProduct.getId(),Constants.PRODUCT_TYPE_NORMAL);

        // 处理attr
        if(storeProductRequest.getSpecType()) {// 多规格
            storeProductRequest.getAttr().forEach(e -> {
                e.setProductId(storeProductRequest.getId());
                e.setAttrValues(StringUtils.strip(e.getAttrValues().replace("\"", ""), "[]"));
                e.setType(Constants.PRODUCT_TYPE_NORMAL);
            });
            attrService.saveOrUpdateBatch(storeProductRequest.getAttr());
        }

        // 处理attrValue
        if(storeProductRequest.getSpecType()) {// 多规格
            storeProductRequest.getAttr().forEach(e->{
                e.setProductId(storeProductRequest.getId());
                e.setAttrValues(StringUtils.strip(e.getAttrValues().replace("\"",""),"[]"));
                e.setType(Constants.PRODUCT_TYPE_NORMAL);
            });
            attrService.saveOrUpdateBatch(storeProductRequest.getAttr());
            if(null != storeProductRequest.getAttrValue() && storeProductRequest.getAttrValue().size() > 0){
                List<StoreProductAttrValueRequest> storeProductAttrValuesRequest = storeProductRequest.getAttrValue();
                // 批量设置attrValues对象的商品id
                storeProductAttrValuesRequest.forEach(e->e.setProductId(storeProductRequest.getId()));
                List<StoreProductAttrValue> storeProductAttrValues = new ArrayList<>();
                for (StoreProductAttrValueRequest attrValuesRequest : storeProductAttrValuesRequest) {
                    StoreProductAttrValue spav = new StoreProductAttrValue();
                    BeanUtils.copyProperties(attrValuesRequest,spav);
                    //设置sku字段
                    if(null != attrValuesRequest.getAttrValue()){
                        List<String> skuList = new ArrayList<>();
                        for(Map.Entry<String,String> vo: attrValuesRequest.getAttrValue().entrySet()){
                            skuList.add(vo.getValue());
                        }
                        spav.setSuk(String.join(",",skuList));
                    }
                    spav.setAttrValue(JSON.toJSONString(attrValuesRequest.getAttrValue()));
                    spav.setImage(systemAttachmentService.clearPrefix(spav.getImage()));
                    spav.setType(Constants.PRODUCT_TYPE_NORMAL);
                    storeProductAttrValues.add(spav);
                }
                storeProductAttrValues.forEach(e -> {
                    if (valueMap.containsKey(e.getSuk())) {
                        e.setId(valueMap.get(e.getSuk()));
                    }
                });

                boolean saveOrUpdateResult = storeProductAttrValueService.saveBatch(storeProductAttrValues);

                // attrResult整存整取，不做更新
                storeProductAttrResultService.deleteByProductId(storeProduct.getId(),Constants.PRODUCT_TYPE_NORMAL);
                StoreProductAttrResult attrResult = new StoreProductAttrResult(
                        0,
                        storeProduct.getId(),
                        systemAttachmentService.clearPrefix(JSON.toJSONString(storeProductRequest.getAttrValue())),
                        DateUtil.getNowTime(),Constants.PRODUCT_TYPE_NORMAL);
                storeProductAttrResultService.save(attrResult);
                if(!saveOrUpdateResult) throw new CrmebException("编辑属性详情失败");
            }
        }else{
            StoreProductAttr singleAttr = new StoreProductAttr();
            singleAttr.setProductId(storeProduct.getId()).setAttrName("规格").setAttrValues("默认").setType(0);

            boolean attrAddResult = attrService.save(singleAttr);
            if (!attrAddResult) throw new CrmebException("新增属性名失败");
            StoreProductAttrValue singleAttrValue = new StoreProductAttrValue();
            if(storeProductRequest.getAttrValue().size() == 0) throw new CrmebException("attrValue不能为空");
            StoreProductAttrValueRequest attrValueRequest = storeProductRequest.getAttrValue().get(0);
            BeanUtils.copyProperties(attrValueRequest,singleAttrValue);
            singleAttrValue.setProductId(storeProduct.getId());
            singleAttrValue.setSuk("默认");
            singleAttrValue.setImage(systemAttachmentService.clearPrefix(singleAttrValue.getImage()));
            singleAttrValue.setType(Constants.PRODUCT_TYPE_NORMAL);
            if (valueMap.containsKey(singleAttrValue.getSuk())) {
                singleAttrValue.setId(valueMap.get(singleAttrValue.getSuk()));
            }
            boolean saveOrUpdateResult = storeProductAttrValueService.save(singleAttrValue);
            if(!saveOrUpdateResult) throw new CrmebException("新增属性详情失败");
        }

        // 处理富文本
        StoreProductDescription spd = new StoreProductDescription(
                storeProduct.getId(),
                storeProductRequest.getContent().length() > 0
                        ? systemAttachmentService.clearPrefix(storeProductRequest.getContent()):storeProductRequest.getContent()
                ,Constants.PRODUCT_TYPE_NORMAL);
        storeProductDescriptionService.deleteByProductId(spd.getProductId(),Constants.PRODUCT_TYPE_NORMAL);
        storeProductDescriptionService.save(spd);

        // 处理优惠券关联信息
        productUtils.shipProductCoupons(storeProductRequest, storeProduct);
        return saveCount > 0;
    }


    @Override
    public StoreProductResponse getByProductId(int id) {
        StoreProduct storeProduct = dao.selectById(id);
        if(null == storeProduct) throw new CrmebException("未找到对应商品信息");
        StoreProductResponse storeProductResponse = new StoreProductResponse();
        BeanUtils.copyProperties(storeProduct, storeProductResponse);
        StoreProductAttr spaPram = new StoreProductAttr();
        spaPram.setProductId(storeProduct.getId()).setType(Constants.PRODUCT_TYPE_NORMAL);
        storeProductResponse.setAttr(attrService.getByEntity(spaPram));

        //商品品牌信息
        Integer brandId=storeProduct.getBrandId();
        StoreBrands storeBrands=storeBrandsService.getById(brandId);
        storeProductResponse.setStoreBrands(storeBrands!=null?storeBrands:new StoreBrands());

        // 设置商品所参与的活动
        storeProductResponse.setActivityH5(productUtils.getProductCurrentActivity(storeProduct));
        StoreProductAttrValue spavPram = new StoreProductAttrValue();
        spavPram.setProductId(id).setType(Constants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttrValue> storeProductAttrValues = storeProductAttrValueService.getByEntity(spavPram);
        // 根据attrValue生成前端所需的数据
        List<HashMap<String, Object>> attrValues = new ArrayList<>();
        if(storeProduct.getSpecType()){
            // 后端多属性用于编辑
            StoreProductAttrResult sparPram = new StoreProductAttrResult();
            sparPram.setProductId(storeProduct.getId()).setType(Constants.PRODUCT_TYPE_NORMAL);

            //得到-商品-属性值-详情
            List<StoreProductAttrResult> attrResults = storeProductAttrResultService.getByEntity(sparPram);
            if(null == attrResults || attrResults.size() == 0){
                throw new CrmebException("未找到对应属性值");
            }
            StoreProductAttrResult attrResult = attrResults.get(0);

            //PC 端生成skuAttrInfo
            List<StoreProductAttrValueRequest> storeProductAttrValueRequests =  com.alibaba.fastjson.JSONObject.parseArray(attrResult.getResult(), StoreProductAttrValueRequest.class);
            if(null != storeProductAttrValueRequests){
                for (int i = 0; i < storeProductAttrValueRequests.size(); i++) {
//                    StoreProductAttrValueRequest storeProductAttrValueRequest = storeProductAttrValueRequests.get(i);
                    HashMap<String, Object> attrValue = new HashMap<>();
                    String currentSku = storeProductAttrValues.get(i).getSuk();
                    List<StoreProductAttrValue> hasCurrentSku = storeProductAttrValues.stream().filter(e -> e.getSuk().equals(currentSku)).collect(Collectors.toList());

                    //得到商品属性
                    StoreProductAttrValue currentAttrValue = hasCurrentSku.get(0);

                    //设置-多规格商品属性值
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
                    attrValue.put("attrValue", JSON.parseObject(storeProductAttrValues.get(i).getAttrValue(), Feature.OrderedField));
                    attrValue.put("brokerage", currentAttrValue.getBrokerage());
                    attrValue.put("brokerageTwo", currentAttrValue.getBrokerageTwo());
                    attrValue.put("integral",currentAttrValue.getIntegral());
                    attrValue.put("firstOrderBrokerage",currentAttrValue.getFirstOrderBrokerage());

                    //区域代理-单独设置的返佣-比例或金额
                    attrValue.put("ugaBrokerage",currentAttrValue.getUgaBrokerage());
                    attrValue.put("ugaPrice",currentAttrValue.getUgaPrice());

                    //sku
                    String[] skus = currentSku.split(",");
                    for (int k = 0; k < skus.length; k++) {
                        attrValue.put("value"+k,skus[k]);
                    }
                    attrValues.add(attrValue);
                }
            }
        }

        // H5 端用于生成skuList
        List<StoreProductAttrValueResponse> sPAVResponses = new ArrayList<>();
        for (StoreProductAttrValue storeProductAttrValue : storeProductAttrValues) {
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            BeanUtils.copyProperties(storeProductAttrValue,atr);
            sPAVResponses.add(atr);
        }
        storeProductResponse.setAttrValues(attrValues);
        storeProductResponse.setAttrValue(sPAVResponses);
        StoreProductDescription sd = storeProductDescriptionService.getOne(
                new LambdaQueryWrapper<StoreProductDescription>()
                        .eq(StoreProductDescription::getProductId, storeProduct.getId())
                        .eq(StoreProductDescription::getType, Constants.PRODUCT_TYPE_NORMAL));
        if(null != sd){
            storeProductResponse.setContent(null == sd.getDescription()?"":sd.getDescription());
        }

        // 获取已关联的优惠券
        List<StoreProductCoupon> storeProductCoupons = storeProductCouponService.getListByProductId(storeProduct.getId());
        if(null != storeProductCoupons && storeProductCoupons.size() > 0){
            List<Integer> ids = storeProductCoupons.stream().map(StoreProductCoupon::getIssueCouponId).collect(Collectors.toList());
            List<StoreCoupon> shipCoupons = storeCouponService.getByIds(ids);
            storeProductResponse.setCoupons(shipCoupons);
            storeProductResponse.setCouponIds(ids);
        }
        return storeProductResponse;
    }

    @Override
    public List<StoreProduct> getList(IndexStoreProductSearchRequest request, PageParamRequest pageParamRequest) {
        //分页和查询对象
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = Wrappers.lambdaQuery();

        //条件-是否精品
        if(request.getIsBest() != null){
            lambdaQueryWrapper.eq(StoreProduct::getIsBest, request.getIsBest());
        }
        //条件-是否热门
        if(request.getIsHot() != null){
            lambdaQueryWrapper.eq(StoreProduct::getIsHot, request.getIsHot());
        }
        //条件-是否新品
        if(request.getIsNew() != null){
            lambdaQueryWrapper.eq(StoreProduct::getIsNew, request.getIsNew());
        }
        //条件-是否优惠
        if(request.getIsBenefit() != null){
            lambdaQueryWrapper.eq(StoreProduct::getIsBenefit, request.getIsBenefit());
        }
        //条件-是否优品推荐
        if(request.getIsGood() != null){
            lambdaQueryWrapper.eq(StoreProduct::getIsGood, request.getIsGood());
        }

        lambdaQueryWrapper.eq(StoreProduct::getIsDel, false)
                .gt(StoreProduct::getStock, 0)
                .eq(StoreProduct::getIsShow, true);

        if(null != request.getCateId() && request.getCateId().size() > 0 ){
            lambdaQueryWrapper.apply(CrmebUtil.getFindInSetSql("cate_id", (ArrayList<Integer>) request.getCateId()));
        }

        if(StringUtils.isNotBlank(request.getKeywords())){
            if(CrmebUtil.isString2Num(request.getKeywords())){
                Integer productId = Integer.valueOf(request.getKeywords());
                lambdaQueryWrapper.like(StoreProduct::getId, productId);
            }else{
                lambdaQueryWrapper
                        .like(StoreProduct::getStoreName, request.getKeywords())
                        .or().like(StoreProduct::getStoreInfo, request.getKeywords())
                        .or().like(StoreProduct::getBarCode, request.getKeywords());
            }
        }

        if (StrUtil.isNotBlank(request.getSalesOrder())) {
            if(request.getSalesOrder().equals(Constants.SORT_DESC)){
                lambdaQueryWrapper.last(" order by (sales + ficti) desc, sort desc, id desc");
            }else{
                lambdaQueryWrapper.last(" order by (sales + ficti) asc, sort asc, id asc");
            }
        } else {
            if(!StringUtils.isBlank(request.getPriceOrder())){
                if(request.getPriceOrder().equals(Constants.SORT_DESC)){
                    lambdaQueryWrapper.orderByDesc(StoreProduct::getPrice);
                }else{
                    lambdaQueryWrapper.orderByAsc(StoreProduct::getPrice);
                }
            }

            lambdaQueryWrapper.orderByDesc(StoreProduct::getSort);
            lambdaQueryWrapper.orderByDesc(StoreProduct::getId);
        }
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据商品tabs获取对应类型的产品数量
     * @return
     */
    @Override
    public List<StoreProductTabsHeader> getTabsHeader() {
        List<StoreProductTabsHeader> headers = new ArrayList<>();
        StoreProductTabsHeader header1 = new StoreProductTabsHeader(0,"出售中商品",1);
        StoreProductTabsHeader header2 = new StoreProductTabsHeader(0,"仓库中商品",2);
        StoreProductTabsHeader header3 = new StoreProductTabsHeader(0,"已经售馨商品",3);
        StoreProductTabsHeader header4 = new StoreProductTabsHeader(0,"警戒库存",4);
        StoreProductTabsHeader header5 = new StoreProductTabsHeader(0,"商品回收站",5);
        headers.add(header1);
        headers.add(header2);
        headers.add(header3);
        headers.add(header4);
        headers.add(header5);
        for (StoreProductTabsHeader h : headers){
            LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            switch (h.getType()){
                case 1:
                    //出售中（已上架）
                    lambdaQueryWrapper.eq(StoreProduct::getIsShow, true);
                    lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                    break;
                case 2:
                    //仓库中（未上架）
                    lambdaQueryWrapper.eq(StoreProduct::getIsShow, false);
                    lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                    break;
                case 3:
                    //已售罄
                    lambdaQueryWrapper.le(StoreProduct::getStock, 0);
                    lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                    break;
                case 4:
                    //警戒库存
                    Integer stock = Integer.parseInt(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_FORM_KEY_store_stock));
                    lambdaQueryWrapper.le(StoreProduct::getStock, stock);
                    lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                    break;
                case 5:
                    //回收站
                    lambdaQueryWrapper.or().eq(StoreProduct::getIsDel, true);
                    break;
                default:
                    break;
            }
            List<StoreProduct> storeProducts = dao.selectList(lambdaQueryWrapper);
            h.setCount(storeProducts.size());
        }
        return headers;
    }

    /**
     * 库存变动写入redis队列
     * @param request StoreProductStockRequest 参数对象
     * @author Mr.Zhang
     * @since 2020-05-06
     * @return int
     */
    @Override
    public boolean stockAddRedis(StoreProductStockRequest request) {
        String _productString = JSON.toJSONString(request);
        redisUtil.lPush(Constants.PRODUCT_STOCK_UPDATE, _productString);
        return true;
    }

    /**
     * 后台任务批量操作库存
     */
    @Override
    public void consumeProductStock() {
        String redisKey = Constants.PRODUCT_STOCK_UPDATE;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("StoreProductServiceImpl.doProductStock | size:" + size);
        if(size < 1){
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if(null == data){
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
     * 扣减库存添加销量
     * @param productId 产品id
     * @param num 商品数量
     * @param type 是否限购
     * @return 扣减结果
     */
    @Override
    public boolean decProductStock(Integer productId, Integer num, Integer attrValueId, Integer type) {
        // 因为attrvalue表中unique使用Id代替，更新前先查询此表是否存在
        // 不存在=但属性 存在则是多属性
        StoreProductAttrValue productsInAttrValue = storeProductAttrValueService.getById(attrValueId);
        StoreProduct storeProduct = getById(productId);
        boolean result = storeProductAttrValueService.decProductAttrStock(productId,attrValueId,num,type);
        if (!result) return result;
        LambdaUpdateWrapper<StoreProduct> lqwuper = new LambdaUpdateWrapper<>();
        lqwuper.set(StoreProduct::getStock, storeProduct.getStock()-num);
        lqwuper.set(StoreProduct::getSales, storeProduct.getSales()+num);
        lqwuper.eq(StoreProduct::getId, productId);
        lqwuper.apply(StrUtil.format(" (stock - {} >= 0) ", num));
        result = update(lqwuper);
        if(result){ //判断库存警戒值
            Integer alterNumI=0;
            String alterNum = systemConfigService.getValueByKey("store_stock");
            if(StringUtils.isNotBlank(alterNum)) alterNumI = Integer.parseInt(alterNum);
            if(alterNumI >= productsInAttrValue.getStock()){
                // todo socket 发送库存警告
            }
        }
        return result;
    }

    /**
     * 根据商品id取出二级分类
     * @param productIdStr String 商品分类
     * @return List<Integer>
     */
    @Override
    public List<Integer> getSecondaryCategoryByProductId(String productIdStr) {
        List<Integer> idList = new ArrayList<>();

        if(StringUtils.isBlank(productIdStr)){
            return idList;
        }
        List<Integer> productIdList = CrmebUtil.stringToArray(productIdStr);
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(StoreProduct::getId, productIdList);
        List<StoreProduct> productList = dao.selectList(lambdaQueryWrapper);
        if(productIdList.size() < 1){
            return idList;
        }

        //把所有的分类id写入集合
        for (StoreProduct storeProduct : productList) {
            List<Integer> categoryIdList = CrmebUtil.stringToArray(storeProduct.getCateId());
            idList.addAll(categoryIdList);
        }

        //去重
        List<Integer> cateIdList = idList.stream().distinct().collect(Collectors.toList());
        if(cateIdList.size() < 1){
            return idList;
        }

        //取出所有的二级分类
        List<Category> categoryList = categoryService.getByIds(cateIdList);
        if(categoryList.size() < 1){
            return idList;
        }

        for (Category category: categoryList) {
            List<Integer> parentIdList = CrmebUtil.stringToArrayByRegex(category.getPath(), "/");
            if(parentIdList.size() > 2){
                Integer secondaryCategoryId = parentIdList.get(2);
                if(secondaryCategoryId > 0){
                    idList.add(secondaryCategoryId);
                }
            }

        }

        return idList;
    }

    /**
     * 根据其他平台url导入产品信息
     * @param url 待导入平台url
     * @param tag 1=淘宝，2=京东，3=苏宁，4=拼多多， 5=天猫
     * @return
     */
    @Override
    public StoreProductRequest importProductFromUrl(String url, int tag) {
        StoreProductRequest productRequest = null;
        try {
            switch (tag){
                case 1:
                    productRequest = productUtils.getTaobaoProductInfo(url,tag);
                    break;
                case 2:
                    productRequest = productUtils.getJDProductInfo(url,tag);
                    break;
                case 3:
                    productRequest = productUtils.getSuningProductInfo(url,tag);
                    break;
                case 4:
                    productRequest = productUtils.getPddProductInfo(url,tag);
                    break;
                case 5:
                    productRequest = productUtils.getTmallProductInfo(url,tag);
                    break;
            }
        }catch (Exception e){
            throw new CrmebException("确认URL和平台是否正确，以及平台费用是否足额"+e.getMessage());
        }
        return productRequest;
    }


    /**
     * 推荐商品列表
     * @param limit 最大数据量
     * @return 推荐商品列表集
     */
    @Override
    public List<StoreProduct> getRecommendStoreProduct(Integer limit) {
        if(limit <0 || limit > 20) throw new CrmebException("获取推荐商品数量不合法 limit > 0 || limit < 20");
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<StoreProduct>();
        lambdaQueryWrapper.eq(StoreProduct::getIsGood,1);
        lambdaQueryWrapper.eq(StoreProduct::getIsShow,1);
        lambdaQueryWrapper.eq(StoreProduct::getIsDel,false);

        lambdaQueryWrapper.orderByDesc(StoreProduct::getSort).orderByDesc(StoreProduct::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    @Override
    public boolean deleteProduct(Integer productId, String type) {
        StoreProduct product = getById(productId);
        if (ObjectUtil.isNull(product)) {
            throw new CrmebException("商品不存在");
        }
        if (StrUtil.isNotBlank(type) && "recycle".equals(type) && product.getIsDel()) {
            throw new CrmebException("商品已存在回收站");
        }

        //定义修改对象
        LambdaUpdateWrapper<StoreProduct> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        if (StrUtil.isNotBlank(type) && "delete".equals(type)) {
            // 判断商品活动状态(秒杀、砍价、拼团)
            isExistActivity(productId);
            lambdaUpdateWrapper.eq(StoreProduct::getId, productId);
            int delete = dao.delete(lambdaUpdateWrapper);
            return delete > 0;
        }
        lambdaUpdateWrapper.eq(StoreProduct::getId, productId);
        lambdaUpdateWrapper.set(StoreProduct::getIsDel, true);
        return update(lambdaUpdateWrapper);
    }

    /**
     * 判断商品活动状态(秒杀、砍价、拼团)
     * @param productId
     */
    private void isExistActivity(Integer productId) {
        Boolean existActivity = false;
        // 秒杀活动判断
        existActivity = storeSeckillService.isExistActivity(productId);
        if (existActivity) {
            throw new CrmebException("有商品关联的秒杀商品活动开启中，不能删除");
        }
        // 砍价活动判断
        existActivity = storeBargainService.isExistActivity(productId);
        if (existActivity) {
            throw new CrmebException("有商品关联的砍价商品活动开启中，不能删除");
        }
        // 拼团活动判断
        existActivity = storeCombinationService.isExistActivity(productId);
        if (existActivity) {
            throw new CrmebException("有商品关联的拼团商品活动开启中，不能删除");
        }
    }

    /**
     * 恢复已删除的商品
     * @param productId 商品id
     * @return 恢复结果
     */
    @Override
    public boolean reStoreProduct(Integer productId) {
        LambdaUpdateWrapper<StoreProduct> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(StoreProduct::getId, productId);
        lambdaUpdateWrapper.set(StoreProduct::getIsDel, false);
        return update(lambdaUpdateWrapper);
    }

    ///////////////////////////////////////////自定义方法

    /**
     * 扣减库存任务操作
     * @param storeProductStockRequest 扣减库存参数
     * @return 执行结果
     */
    @Override
    public boolean doProductStock(StoreProductStockRequest storeProductStockRequest){
        // 获取商品本身信息
        StoreProduct existProduct = getById(storeProductStockRequest.getProductId());
        List<StoreProductAttrValue> existAttr =
                storeProductAttrValueService.getListByProductIdAndAttrId(
                        storeProductStockRequest.getProductId(),
                        storeProductStockRequest.getAttrId().toString(),
                        storeProductStockRequest.getType());
        if(null == existProduct || null == existAttr){ // 未找到商品
            logger.info("库存修改任务未获取到商品信息"+JSON.toJSONString(storeProductStockRequest));
            return true;
        }

        // 回滚商品库存/销量 并更新
        boolean isPlus = storeProductStockRequest.getOperationType().equals("add");
        int productStock = isPlus ? existProduct.getStock() + storeProductStockRequest.getNum() : existProduct.getStock() - storeProductStockRequest.getNum();
        existProduct.setStock(productStock);
        existProduct.setSales(existProduct.getSales() - storeProductStockRequest.getNum());
        updateById(existProduct);

        // 回滚sku库存
        for (StoreProductAttrValue attrValue : existAttr) {
            int productAttrStock = isPlus ? attrValue.getStock() + storeProductStockRequest.getNum() : attrValue.getStock() - storeProductStockRequest.getNum();
            attrValue.setStock(productAttrStock);
            attrValue.setSales(attrValue.getSales()-storeProductStockRequest.getNum());
            storeProductAttrValueService.updateById(attrValue);
        }
        return true;
    }

    /**
     * 获取复制商品配置
     * @return copyType 复制类型：1：一号通
     *         copyNum 复制条数(一号通类型下有值)
     */
    @Override
    public MyRecord copyConfig() {
        String copyType = systemConfigService.getValueByKey("system_product_copy_type");
        if (StrUtil.isBlank(copyType)) {
            throw new CrmebException("请先进行采集商品配置");
        }
        int copyNum = 0;
        if (copyType.equals("1")) {// 一号通
            JSONObject info = onePassService.info();
            copyNum = Optional.ofNullable(info.getJSONObject("copy").getInteger("num")).orElse(0);
        }
        MyRecord record = new MyRecord();
        record.set("copyType", copyType);
        record.set("copyNum", copyNum);
        return record;
    }

    /**
     * 复制平台商品
     * @param url 商品链接
     * @return MyRecord
     */
    @Override
    public MyRecord copyProduct(String url) {
        JSONObject jsonObject = onePassService.copyGoods(url);
        StoreProductRequest storeProductRequest = ProductUtils.onePassCopyTransition(jsonObject);
        MyRecord record = new MyRecord();
        return record.set("info", storeProductRequest);
    }

    /**
     * 添加/扣减库存
     * @param id 商品id
     * @param num 数量
     * @param type 类型：add—添加，sub—扣减
     */
    @Override
    public Boolean operationStock(Integer id, Integer num, String type) {
        UpdateWrapper<StoreProduct> updateWrapper = new UpdateWrapper<>();
        if (type.equals("add")) {
            updateWrapper.setSql(StrUtil.format("stock = stock + {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales - {}", num));
        }
        if (type.equals("sub")) {
            updateWrapper.setSql(StrUtil.format("stock = stock - {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales + {}", num));
            // 扣减时加乐观锁保证库存不为负
            updateWrapper.last(StrUtil.format(" and (stock - {} >= 0)", num));
        }
        updateWrapper.eq("id", id);
        boolean update = update(updateWrapper);
        if (!update) {
            throw new CrmebException("更新普通商品库存失败,商品id = " + id);
        }
        return update;
    }

    /**
     * 下架
     * @param id 商品id
     */
    @Override
    public Boolean offShelf(Integer id) {
        StoreProduct storeProduct = getById(id);
        if (ObjectUtil.isNull(storeProduct)) {
            throw new CrmebException("商品不存在");
        }
        if (!storeProduct.getIsShow()) {
            return true;
        }

        storeProduct.setIsShow(false);
        Boolean execute = transactionTemplate.execute(e -> {
            dao.updateById(storeProduct);
            storeCartService.productStatusNotEnable(id);
            return Boolean.TRUE;
        });

        return execute;
    }

    /**
     * 上架
     * @param id 商品id
     * @return Boolean
     */
    @Override
    public Boolean putOnShelf(Integer id) {
        StoreProduct storeProduct = getById(id);
        if (ObjectUtil.isNull(storeProduct)) {
            throw new CrmebException("商品不存在");
        }
        if (storeProduct.getIsShow()) {
            return true;
        }

        // 获取商品skuid
        StoreProductAttrValue tempSku = new StoreProductAttrValue();
        tempSku.setProductId(id);
        tempSku.setType(Constants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttrValue> skuList = storeProductAttrValueService.getByEntity(tempSku);
        List<Integer> skuIdList = skuList.stream().map(StoreProductAttrValue::getId).collect(Collectors.toList());

        storeProduct.setIsShow(true);
        Boolean execute = transactionTemplate.execute(e -> {
            dao.updateById(storeProduct);
            storeCartService.productStatusNoEnable(skuIdList);
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 首页商品列表
     * @param type 类型 【1 精品推荐 2 热门榜单 3首发新品 4促销单品】
     * @param pageParamRequest 分页参数
     * @return CommonPage
     */
    @Override
    public List<StoreProduct> getIndexProduct(Integer type, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.select(StoreProduct::getId, StoreProduct::getImage, StoreProduct::getStoreName,
                StoreProduct::getQuotaControl,StoreProduct::getGiveIntegral,
                StoreProduct::getPrice,StoreProduct::getUnitName, StoreProduct::getOtPrice, StoreProduct::getActivity);
        switch (type){
            case Constants.INDEX_RECOMMEND_BANNER: //精品推荐
                lambdaQueryWrapper.eq(StoreProduct::getIsBest, true);
                break;
            case Constants.INDEX_HOT_BANNER: //热门榜单
                lambdaQueryWrapper.eq(StoreProduct::getIsHot, true);
                break;
            case Constants.INDEX_NEW_BANNER: //首发新品
                lambdaQueryWrapper.eq(StoreProduct::getIsNew, true);
                break;
            case Constants.INDEX_BENEFIT_BANNER: //促销单品
                lambdaQueryWrapper.eq(StoreProduct::getIsBenefit, true);
                break;
            case Constants.INDEX_GOOD_BANNER: // 优选推荐
                lambdaQueryWrapper.eq(StoreProduct::getIsGood, true);
                break;
        }

        lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
        lambdaQueryWrapper.gt(StoreProduct::getStock, 0);
        lambdaQueryWrapper.eq(StoreProduct::getIsShow, true);

        lambdaQueryWrapper.orderByDesc(StoreProduct::getSort);
        lambdaQueryWrapper.orderByDesc(StoreProduct::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 获取商品移动端列表-h5
     * @param request 筛选参数
     * @param pageRequest 分页参数
     * @return List
     */
    @Override
    public List<StoreProduct> findH5List(ProductRequest request, PageParamRequest pageRequest) {
        //分页对象以及查询对象
        LambdaQueryWrapper<StoreProduct> lqw = Wrappers.lambdaQuery();

        // id、名称、图片、价格、销量、活动
        lqw.select(StoreProduct::getId, StoreProduct::getStoreName, StoreProduct::getImage,
                StoreProduct::getPrice,StoreProduct::getQuotaControl,StoreProduct::getGiveIntegral,
                StoreProduct::getActivity, StoreProduct::getSales, StoreProduct::getFicti, StoreProduct::getUnitName);

        //条件-是否删除、商户Id、库存、状态
        lqw.eq(StoreProduct::getIsDel, false);
        lqw.ge(StoreProduct::getStock, 10);//大于或等于
        lqw.eq(StoreProduct::getIsShow, true);

        //条件-品牌id
        if(ObjectUtil.isNotNull(request.getBrandId())&&request.getBrandId()>0){
            lqw.eq(StoreProduct::getBrandId, request.getBrandId());
        }

        //条件-分类
        if (ObjectUtil.isNotNull(request.getCid()) && request.getCid() > 0) {
            //查找当前类下的所有子类
            List<Category> childVoListByPid = categoryService.getChildVoListByPid(request.getCid());
            List<Integer> categoryIdList = childVoListByPid.stream().map(Category::getId).collect(Collectors.toList());
            categoryIdList.add(request.getCid());
            lqw.apply(CrmebUtil.getFindInSetSql("cate_id", (ArrayList<Integer>) categoryIdList));
        }

        //条件-关键字
        if(StrUtil.isNotBlank(request.getKeyword())){
            if(CrmebUtil.isString2Num(request.getKeyword())){
                Integer productId = Integer.valueOf(request.getKeyword());
                lqw.like(StoreProduct::getId, productId);
            }else{
                lqw.like(StoreProduct::getStoreName, request.getKeyword());
            }
        }

        // 排序-销量
        if (StrUtil.isNotBlank(request.getSalesOrder())) {
            //升序或降序
            if(request.getSalesOrder().equals(Constants.SORT_DESC)){
                lqw.last(" order by (sales + ficti) desc, sort desc, id desc");
            }else{
                lqw.last(" order by (sales + ficti) asc, sort asc, id asc");
            }
        } else {
            //排序-价格
            if(StrUtil.isNotBlank(request.getPriceOrder())){
                //升序或降序
                if(request.getPriceOrder().equals(Constants.SORT_DESC)){
                    lqw.orderByDesc(StoreProduct::getPrice);
                }else{
                    lqw.orderByAsc(StoreProduct::getPrice);
                }
            }

            //排序-sort、id
            lqw.orderByDesc(StoreProduct::getSort);
            lqw.orderByDesc(StoreProduct::getId);
        }

        //返回数据
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        return dao.selectList(lqw);
    }

    /**
     * 获取移动端商品详情
     * @param id 商品id
     * @return StoreProduct
     */
    @Override
    public StoreProduct getH5Detail(Integer id) {
        LambdaQueryWrapper<StoreProduct> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreProduct::getId, StoreProduct::getImage, StoreProduct::getStoreName, StoreProduct::getSliderImage,
                StoreProduct::getVideoLink,StoreProduct::getBrandId,
                StoreProduct::getOtPrice,StoreProduct::getVipPrice, StoreProduct::getStock, StoreProduct::getSales,
                StoreProduct::getPrice, StoreProduct::getActivity,
                StoreProduct::getFicti, StoreProduct::getIsSub, StoreProduct::getStoreInfo, StoreProduct::getBrowse, StoreProduct::getUnitName);
        lqw.eq(StoreProduct::getId, id);
        lqw.eq(StoreProduct::getIsDel, false);
        lqw.eq(StoreProduct::getIsShow, true);
        StoreProduct storeProduct = dao.selectOne(lqw);
        if (ObjectUtil.isNull(storeProduct)) {
            throw new CrmebException(StrUtil.format("未找到编号为{}的商品", id));
        }

        StoreProductDescription sd = storeProductDescriptionService.getOne(
                new LambdaQueryWrapper<StoreProductDescription>()
                        .eq(StoreProductDescription::getProductId, storeProduct.getId())
                        .eq(StoreProductDescription::getType, Constants.PRODUCT_TYPE_NORMAL));
        if(ObjectUtil.isNotNull(sd)) {
            storeProduct.setContent(StrUtil.isBlank(sd.getDescription()) ? "" : sd.getDescription());
        }
        return storeProduct;
    }

    /**
     * 获取购物车商品信息
     * @param productId 商品编号
     * @return StoreProduct
     */
    @Override
    public StoreProduct getCartByProId(Integer productId) {
        LambdaQueryWrapper<StoreProduct> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreProduct::getId, StoreProduct::getImage, StoreProduct::getStoreName);
        lqw.eq(StoreProduct::getId, productId);
        return dao.selectOne(lqw);
    }

    /**
     * 根据商品ids获取对应的列表
     * @param productIdList 商品id列表
     * @return List<StoreProduct>
     */
    @Override
    public List<StoreProduct> findH5ListByProIds(List<Integer> productIdList) {
        LambdaQueryWrapper<StoreProduct> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreProduct::getId, StoreProduct::getImage, StoreProduct::getStoreName, StoreProduct::getPrice);
        lqw.in(StoreProduct::getId, productIdList);
        return dao.selectList(lqw);
    }

    /**
     * 导入导出模板-mian测试
     * 2021-7-29(已应用)
     * 1、分离了属性值sku，动态导入导出。
     * 2、属性值-与属性-对应关系-动态生成，模板内不用再填！
     * 2021-8-03(已应用)
     * 通过路径生成写入商品导入导出excel文件
     *
     * @param args
     * @throws IllegalAccessException
     */
    public static void main(String[] args) throws IllegalAccessException, IOException {
        //setExcel(null);
    }

    @Override
    public void importProductExcel(String path) {
        try {
            //验证路径是否为空
            if(path==null||"".equals(path)) {
                path="D:\\lingfe\\other\\商品信息1001.xlsx";
            }

            //hutool
            ExcelReader reader = getExcelReader(path,null,StoreProductExcel.class,0,null);

            //得到数据
            List<StoreProductExcel> all = reader.readAll(StoreProductExcel.class);
            for (StoreProductExcel sp:all) {
                //实例化-商品信息-对象
                StoreProduct storeProduct = new StoreProduct();
                BeanUtils.copyProperties(sp, storeProduct);//转换

                //处理-商品-参数
                storeProduct.setBarCode(sp.getBarCode()==null?"没有":sp.getBarCode());

                //根据商品名称查-验证商品是否已存在
                LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(StoreProduct::getStoreName,storeProduct.getStoreName());
                lambdaQueryWrapper.orderByDesc(StoreProduct::getAddTime);
                StoreProduct  selectOne= dao.selectOne(lambdaQueryWrapper);
                if(selectOne!=null){
                    //商品已存在，跳过
                    continue;
                }else{
                    //定义查询条件-查询分类
                    LambdaQueryWrapper<Category> lambdaQueryWrapperCategory = new LambdaQueryWrapper<>();
                    lambdaQueryWrapperCategory.eq(Category::getName, storeProduct.getCateId());
                    lambdaQueryWrapperCategory.eq(Category::getType, CategoryConstants.CATEGORY_TYPE_PRODUCT);
                    Category category  =categoryService.getOne(lambdaQueryWrapperCategory);
                    //检测是否存在
                    if(category == null){
                        //不存在-新增分类
                        category = new Category();
                        category.setPid(614);           //默认-上级
                        category.setPath("/0/614/");    //默认-path
                        category.setExtra("crmebimage/store/2020/08/13/1826c7f20771444e888501d91332f129wik43qfgmw.png");//默认-分类图标
                        category.setType(CategoryConstants.CATEGORY_TYPE_PRODUCT);//类型-1=产品分类
                        category.setName(sp.getCateName());
                        category.setStatus(true);
                        //执行保存
                        categoryService.save(category);
                        System.out.println("此分类不存在="+storeProduct.getCateId());
                    }
                    //得到-分类id-设置商品分类
                    storeProduct.setCateId(category.getId().toString());

                    //验证品牌名称-是否已经存在
                    LambdaQueryWrapper<StoreBrands> lambdaQueryWrapperStoreBrands = new LambdaQueryWrapper<>();
                    lambdaQueryWrapperStoreBrands.eq(StoreBrands::getBrandName,sp.getBrandName());
                    lambdaQueryWrapperStoreBrands.orderByDesc(StoreBrands::getCreateTime);
                    StoreBrands storeBrands = storeBrandsService.getOne(lambdaQueryWrapperStoreBrands);
                    //验证品牌信息-是否存在
                    if(storeBrands == null){
                        //不存在-新增
                        storeBrands = new StoreBrands();
                        storeBrands.setBrandImg("crmebimage/brand/2021/06/24/f6b73a624f454696ad8975dceb5217792yu7qi8fkr.png");//默认-品牌图标
                        storeBrands.setIsDisplay(true);
                        storeBrands.setCreateTime(DateUtil.nowDateTime());
                        storeBrands.setBrandDesc("产品导入-未设置品牌介绍");
                        storeBrands.setBrandName(sp.getBrandName());
                        storeBrands.setCateId(category.getId().toString());
                        storeBrands.setIsDel(false);
                        storeBrands.setSort(0);
                        //执行保存-品牌信息
                        storeBrandsService.save(storeBrands);
                    }
                    //得到-品牌ID-设置商品品牌
                    storeProduct.setBrandId(storeBrands.getId());

                    //执行保存-商品信息
                    this.save(storeProduct);
                }

                //保存商品之后-第二次循环-处理属性
                for (StoreProductExcel storp:all) {
                    //验证是否为同一个商品
                    if(sp.getStoreName().equals(storp.getStoreName())){
                        //定义变量
                        List<String> kuanhaoList=new ArrayList<>(); //款号-值
                        List<String> colourList=new ArrayList<>();  //颜色-值
                        List<String> chimaList=new ArrayList<>();   //尺码-值

                        //第三次循环-得到属性值
                        for (StoreProductExcel spAttrValue:all){
                            //验证是否在当前与第一个循环-为同一个商品
//                            if(sp.getStoreName().equals(spAttrValue.getStoreName())){
//                                //属性值-款号
//                                kuanhaoList.add(spAttrValue.getKuanhao());
//                                //属性值-颜色
//                                colourList.add(spAttrValue.getColour());
//                                //属性值-尺码
//                                chimaList.add(spAttrValue.getChima());
//                            }
                        }

                        //得到-属性值-去重复
                        kuanhaoList=kuanhaoList.stream().distinct().collect(Collectors.toList());
                        colourList=colourList.stream().distinct().collect(Collectors.toList());
                        chimaList=chimaList.stream().distinct().collect(Collectors.toList());

                        //设置-商品属性
                        StoreProductAttr storeProductAttr=new StoreProductAttr();
                        storeProductAttr.setProductId(storeProduct.getId());                   //商品ID
                        storeProductAttr.setType(0);
                        storeProductAttr.setCreateTime(DateUtil.nowDateTime());

                        //执行保存-该商品-属性-款号
//                        storeProductAttr.setAttrName(storp.getKuanhaoAttName());
//                        storeProductAttr.setAttrValues(String.join(",", kuanhaoList));
//                        attrService.save(storeProductAttr);
//
//                        //执行保存-该商品-属性-颜色
//                        storeProductAttr.setAttrName(storp.getColourAttName());
//                        storeProductAttr.setAttrValues(String.join(",",colourList ));
//                        attrService.save(storeProductAttr);
//
//                        //执行保存-该商品-属性-尺码
//                        storeProductAttr.setAttrName(storp.getChimaAttName());
//                        storeProductAttr.setAttrValues(String.join(",", chimaList));
//                        attrService.save(storeProductAttr);

                        //定义-商品属性-属性值-list集合
                        List<StoreProductAttrValue> storeProductAttrValueList = new ArrayList<>();
                        //定义-商品属性-详情-list集合
                        List<StoreProductAttrValueRequest> stringList = new ArrayList<>();

                        //循环处理
                        for(String kuaihao:kuanhaoList){    //款号
                            for(String colour:colourList){      //颜色
                                for(String chima:chimaList){        //尺码
                                    //实例化-商品-属性值-请求对象
                                    StoreProductAttrValueRequest attValue = new StoreProductAttrValueRequest();
                                    attValue.setProductId(storeProduct.getId());
                                    attValue.setSuk(new StringBuffer()
                                            .append(kuaihao).append(",")
                                            .append(colour).append(",")
                                            .append(chima).toString());
                                    attValue.setStock(storeProduct.getStock());
                                    attValue.setSales(0);
                                    attValue.setPrice(storeProduct.getPrice());
                                    attValue.setImage(storeProduct.getImage());
                                    attValue.setCost(storeProduct.getCost());
                                    attValue.setBarCode(storeProduct.getBarCode());
                                    attValue.setOtPrice(storeProduct.getOtPrice());
                                    attValue.setWeight(BigDecimal.ZERO);
                                    attValue.setVolume(BigDecimal.ZERO);
                                    attValue.setBrokerage(BigDecimal.ZERO);
                                    attValue.setBrokerageTwo(BigDecimal.ZERO);

                                    //追加-商品属性-与属性值-对应关系-Map集合
                                    LinkedHashMap<String,String>  mapAttrValue= new LinkedHashMap<>();
//                                    mapAttrValue.put(storp.getKuanhaoAttName(),kuaihao);
//                                    mapAttrValue.put(storp.getColourAttName(),colour);
//                                    mapAttrValue.put(storp.getChimaAttName(),chima);
                                    attValue.setAttrValue(mapAttrValue);//设置商品属性-与属性值-对应关系-Map集合

                                    //实例化-商品属性-属性值-实体对象
                                    StoreProductAttrValue storeProductAttrValue=new StoreProductAttrValue();
                                    BeanUtils.copyProperties(attValue, storeProductAttrValue);//转换
                                    storeProductAttrValue.setAttrValue(JSON.toJSONString(mapAttrValue));

                                    //添加到-商品属性值-集合
                                    storeProductAttrValueList.add(storeProductAttrValue);

                                    //添加到-商品属性-详情集合
                                    stringList.add(attValue);
                                }
                            }
                        }

                        //实例化-商品属性-详情对象
                        StoreProductAttrResult storeProductAttrResult=new StoreProductAttrResult();
                        storeProductAttrResult.setResult(systemAttachmentService.clearPrefix(JSON.toJSONString(stringList)));
                        storeProductAttrResult.setId(0);
                        storeProductAttrResult.setProductId(storeProduct.getId());
                        storeProductAttrResult.setChangeTime(DateUtil.getNowTime());
                        storeProductAttrResult.setType(Constants.PRODUCT_TYPE_NORMAL);

                        // 保存属性
                        if(storeProductAttrValueList.size() > 0){
                            //商品属性值
                            boolean saveOrUpdateResult = storeProductAttrValueService.saveOrUpdateBatch(storeProductAttrValueList);
                            if(!saveOrUpdateResult) throw new CrmebException("新增属性值失败!");

                            //商品属性详情
                            boolean saveSPAttrResult= storeProductAttrResultService.save(storeProductAttrResult);
                            if(!saveSPAttrResult) throw new CrmebException("新增属性值详情失败!");
                        }

                        //保存商品详情
                        StoreProductDescription spd = new StoreProductDescription();
                        spd.setProductId(storeProduct.getId());
                        spd.setType(Constants.PRODUCT_TYPE_NORMAL);
                        spd.setDescription("导入产品-默认商品详情！");

                        //先删再添加
                        storeProductDescriptionService.deleteByProductId(spd.getProductId(),Constants.PRODUCT_TYPE_NORMAL);
                        storeProductDescriptionService.save(spd);

                        //跳出循环，执行一次
                        break;
                    }

                    //跳过
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public StoreProductExcelResponse importSave(List<StoreProductExcel> listProductExcel, List<StoreProductAttrExcel>  listAttrExcel,List<StoreProductAttrValueExcel> listAttrValueExcel) {
        //实例化-商品信息-导入导出-响应对象
        StoreProductExcelResponse storeProductExcelResponse=new StoreProductExcelResponse();

        //定义变量
        Integer ok=0;         //成功数量
        Integer lose=0;       //失败数量
        Integer reader=0;     //重复数量
        String dayDate=DateUtil.nowDate(Constants.DATE_FORMAT_DATE);//今日日期

        //第四步,定义集合(分别是:属性、属性值、属性详情)
        List<StoreProductAttr>  listAttr = new ArrayList<>();
        List<StoreProductAttrValue> listAttrValue = new ArrayList<>();
        List<StoreProductAttrValueRequest> listAttrResult = new ArrayList<>();

        //第5步-循环处理-商品信息
        for (StoreProductExcel storeProductExcel:listProductExcel) {
            //转换-商品信息
            StoreProduct storeProduct = new StoreProduct();
            BeanUtils.copyProperties(storeProductExcel, storeProduct);

            //拼接-图片存放路径
            StringBuffer sbImagesPath=new StringBuffer("product/");     //图片存放目录
            sbImagesPath.append(dayDate).append("/");                   //今日日期作为目录
            this.splicingImages(sbImagesPath,storeProduct);

            //验证并保存商品信息
            Boolean bool=this.validateProductOnImport(storeProductExcel,storeProduct);
            if(!bool){
                reader++;
                continue;//跳过
            }

            //清空
            listAttr.clear();
            listAttrValue.clear();
            listAttrResult.clear();

            //第6步-处理-属性
            for (StoreProductAttrExcel storeProductAttrExcel : listAttrExcel) {
                //验证-商品ID与属性中的商品ID-是否相等
                if(!storeProductExcel.getId().equals(storeProductAttrExcel.getProductId())){
                    continue;//不相等，跳过
                }

                //转换-属性
                StoreProductAttr storeProductAttr=new StoreProductAttr();
                BeanUtils.copyProperties(storeProductAttrExcel, storeProductAttr);
                storeProductAttr.setCreateTime(DateUtil.nowDateTime());
                storeProductAttr.setProductId(storeProduct.getId());
                storeProductAttr.setType(0);

                //添加到-商品-属性-集合
                listAttr.add(storeProductAttr);
            }

            //第7步-处理-属性值
            //提取属性list-某个字段
            //List<String> newList = listAttr.stream().map(StoreProductAttr::getAttrName).collect(Collectors.toList());
            for (StoreProductAttrValueExcel storeProductAttrValueExcel : listAttrValueExcel) {
                //验证-商品ID与属性值中的商品ID-是否相等
                if(!storeProductExcel.getId().equals(storeProductAttrValueExcel.getProductId())){
                    continue;//不相等，跳过
                }

                //转换-属性值
                StoreProductAttrValue storeProductAttrValue=new StoreProductAttrValue();
                BeanUtils.copyProperties(storeProductAttrValueExcel, storeProductAttrValue);
                storeProductAttrValue.setProductId(storeProduct.getId());

                //如果属性库存为空，则取商品库存
                if(storeProductAttrValue.getStock()==null||storeProductAttrValue.getStock() == 0){
                    storeProductAttrValue.setStock(storeProduct.getStock());
                }

                //验证-属性图
                String attrValueImage=null;
                if(storeProductAttrValue.getImage() ==null && "".equals(storeProductAttrValue.getAttrValue())){
                    attrValueImage=storeProduct.getImage();
                }else{
                    attrValueImage=new StringBuffer(sbImagesPath).append("attrImg/").append(storeProductAttrValue.getImage()).toString();
                }
                storeProductAttrValue.setImage(attrValueImage);

                //处理sku-得到属性值与属性-对应关系
                LinkedHashMap<String,String> mapAttrValue=new LinkedHashMap<>();
                if(listAttr.size()>0){
                    String[] suk= storeProductAttrValueExcel.getSuk().split(",");
                    for (int i=0;i<suk.length;i++){
                        //验证sku
                        if(i>=listAttr.size()){
                            //当-有属性值-没有对应属性名称时：设置默认名称
                            StoreProductAttr storeProductAttr=new StoreProductAttr();
                            storeProductAttr.setAttrName("未知属性名称-"+i+1);
                            storeProductAttr.setProductId(storeProduct.getId());
                            storeProductAttr.setAttrValues(suk[i]);
                            storeProductAttr.setType(0);
                            listAttr.add(storeProductAttr);//添加到-属性集合

                            //新属性名称
                            mapAttrValue.put(storeProductAttr.getAttrName(),suk[i]);
                        }else{
                            //取出属性值
                            StoreProductAttr storeProductAttr= listAttr.get(i);
                            String attvalues=storeProductAttr.getAttrValues();

                            //定义变量
                            List<String> arr=new ArrayList<>();
                            boolean bl=true;

                            //当-有sku里有值，没有对应属性值时：在属性值里增加新值。
                            if(attvalues != null){
                                //匹配是否已经存在属性值，不存在则添加,存在不添加。
                                arr = Arrays.stream(attvalues.split(",")).collect(Collectors.toList());
                                for (String str:arr) {
                                    if(str.equals(suk[i])){
                                        bl=false;
                                        break;
                                    }
                                }
                            }

                            //验证是否执行添加属性值
                            if(bl){
                                arr.add(suk[i]);
                                attvalues=String.join(",",arr);
                            }

                            //重新赋值
                            storeProductAttr.setAttrValues(attvalues);
                            //正常suk
                            mapAttrValue.put(listAttr.get(i).getAttrName(),suk[i]);
                        }
                    }

                    //将sukMap-转成字符串-重新赋值到属性值与属性-对应关系字段
                    String json= JSON.toJSONString(mapAttrValue);
                    //设置商品属性-与属性值-对应关系-Map集合
                    storeProductAttrValue.setAttrValue(json);
                }

                //实例化-商品-属性值-请求对象
                StoreProductAttrValueRequest attValue = new StoreProductAttrValueRequest();
                BeanUtils.copyProperties(storeProductAttrValue, attValue);//转换
                attValue.setAttrValue(mapAttrValue);
                attValue.setStock(storeProductAttrValue.getStock());

                //添加到-商品-属性值-集合
                listAttrValue.add(storeProductAttrValue);
                //添加到-商品-属性详情-请求集合
                listAttrResult.add(attValue);
            }

            //解决-当有属性名称-没有属性值时：删掉属性值为空的属性
            List<StoreProductAttr> finalListAttr=listAttr.stream().filter(p -> p.getAttrValues() != null).collect(Collectors.toList());

            //第8步-生成-商品属性-属性详情
            StoreProductAttrResult storeProductAttrResult=new StoreProductAttrResult();//实例化-商品属性-详情对象
            String str = JSON.toJSONString(listAttrResult);
            String result= systemAttachmentService.clearPrefix(str);
            storeProductAttrResult.setResult(StringEscapeUtils.unescapeJava(result));//去除转易
            storeProductAttrResult.setId(0);
            storeProductAttrResult.setProductId(storeProduct.getId());
            storeProductAttrResult.setChangeTime(DateUtil.getNowTime());
            storeProductAttrResult.setType(Constants.PRODUCT_TYPE_NORMAL);

            //第9步-创建-商品-默认详情
            String content = storeProduct.getContent();
            StringBuffer stringBuffer=new StringBuffer();
            if(StringUtils.isNotBlank(content)){
                String[] strings = content.split(",");
                for (String imgPath:strings) {
                    stringBuffer.append("<img src='").append(imgPath).append("' />");
                }
            }else{
                stringBuffer.append("导入产品-默认商品详情！");
            }
            StoreProductDescription spd = new StoreProductDescription();
            spd.setProductId(storeProduct.getId());
            spd.setType(Constants.PRODUCT_TYPE_NORMAL);
            spd.setDescription(stringBuffer.toString());

            //最后一步-执行保存
            Boolean execute = transactionTemplate.execute(e -> {
                boolean saveAttr=               attrService.saveBatch(finalListAttr);
                boolean saveAttrValue=          storeProductAttrValueService.saveBatch(listAttrValue);
                boolean saveAttrResult=         storeProductAttrResultService.save(storeProductAttrResult);
                boolean saveSPDescription=      storeProductDescriptionService.save(spd);
                System.out.println("保存-商品-属性->"+saveAttr);
                System.out.println("保存-商品-属性-值->"+saveAttrValue);
                System.out.println("保存-商品-属性-详情->"+saveAttrResult);
                System.out.println("保存-商品-详情->"+saveSPDescription);
                return Boolean.TRUE;
            });

            //验证结果
            if (!execute) {
                System.out.println("商品->"+storeProduct.getStoreName());
                System.out.print("导入失败!");
                lose++;
                continue;
            }

            //控制台-提示
            System.out.println("商品->"+storeProduct.getStoreName());
            System.out.print("导入成功!");
            ok++;
        }

        //设置响应结果
        storeProductExcelResponse.setOkNum(ok);
        storeProductExcelResponse.setLoseNum(lose);
        storeProductExcelResponse.setReaderNum(reader);

        //完
        return storeProductExcelResponse;
    }

    @Override
    public void exportProductExcel(StoreProductSearchRequest request,HttpServletResponse response) {
        try {
            //得到-要导出的数据
            Map<String,Object> map= getExportData(request);

            //取出数据
            List<StoreProductExcel> listProduct= (List<StoreProductExcel>) map.get("list");
            List<StoreProductAttrExcel> listAttrs= (List<StoreProductAttrExcel>) map.get("storeProductAttrList");
            List<StoreProductAttrValueExcel> listAttrValue= (List<StoreProductAttrValueExcel>) map.get("storeProductAttrValueList");

            //写-通过工具类创建writer
            ExcelWriter writer= null;

            //写入-商品信息
            writer=getExcelWriter(new StoreProductExcel(),null,null);
            writer.renameSheet("商品信息");
            writer.setSheet(0);
            writer.write(listProduct, true);

            //写入-属性
            writer= getExcelWriter(new StoreProductAttrExcel(),null,null);
            writer.renameSheet("属性");
            writer.setSheet(1);
            writer.write(listAttrs, true);

            //写入-属性值
            writer= getExcelWriter(new StoreProductAttrValueExcel(),null,null);
            writer.renameSheet("属性值");
            writer.setSheet(2);
            writer.write(listAttrValue, true);

            //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            String name = "产品导入-模版";

            //out为OutputStream，需要写出到的目标流
            ServletOutputStream out= null;

            //设置响应头,下载文件的默认名称，.xls 是2003版本，excel2003、2007、2010都可以打开，兼容性最好
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(name, "utf-8") + ".xls");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");

            //输出文件
            out = response.getOutputStream();
            writer.flush(out, true);

            // 关闭writer，释放内存
            writer.close();

            //此处记得关闭输出Servlet流
            IoUtil.close(out);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public StoreProductExcelResponse importProductExcelUpgrade(String path){
        //读-hutool
        ExcelReader reader = null;

        //第一步，取出商品信息
        reader = getExcelReader(path,null,StoreProductExcel.class,0,null);//取出第0个sheet=商品信息
        List<StoreProductExcel> listProductExcel = reader.readAll(StoreProductExcel.class);
        reader.close();

        //第二部-取出属性
        reader = getExcelReader(path,null,StoreProductAttrExcel.class,1,null);//取出第1个sheet=属性
        List<StoreProductAttrExcel>  listAttrExcel = reader.readAll(StoreProductAttrExcel.class);
        reader.close();

        //第三步-取出属性值
        reader = getExcelReader(path,null,StoreProductAttrValueExcel.class,2,null);//取出第2个sheet=属性值
        List<StoreProductAttrValueExcel> listAttrValueExcel = reader.readAll(StoreProductAttrValueExcel.class);
        reader.close();

        //执行保存
        return this.importSave(listProductExcel,listAttrExcel,listAttrValueExcel);
    }

    @Override
    public StoreProductExcelResponse importUploadFileExcel(MultipartFile multipartFile) throws IOException {
        //文件验证
        String fileName = FilesUtils.verificationFileMultipartFile(multipartFile,".xlsx");

        //得到文件流
        InputStream inputStream=multipartFile.getInputStream();

        //保存文件
        this.saveFile(multipartFile, fileName,"excel");

        //返回
        return this.duquExcelInputStream(inputStream);
    }

    @Override
    public StoreProductExcelResponse importUploadFileExcelZip(MultipartFile multipartFile) {
        //实例化-响应对象
        StoreProductExcelResponse storeProductExcelResponse = new StoreProductExcelResponse();

        try {
            //文件验证
            String fileName = FilesUtils.verificationFileMultipartFile(multipartFile,".zip");

            //保存文件
            String path = this.saveFile(multipartFile, fileName,"zip");//文件存储路径
            String pathRoot=new StringBuffer(path).append("/").append(fileName).toString();//上传的zip压缩包文件路径
            String saveAsFileZip = new StringBuffer(path).append("/").append(fileName.substring(0,fileName.indexOf("."))).toString(); //解压文件另存为路径

            // 获得zip信息
            ZipFile zipFile = new ZipFile(pathRoot, Charset.forName("GBK"));
            Enumeration<ZipEntry> enu = (Enumeration<ZipEntry>) zipFile.entries();//转换
            Boolean isgeshiFiles=false;
            while (enu.hasMoreElements()) {
                //转换-文件流
                ZipEntry zipElement = (ZipEntry) enu.nextElement();
                InputStream read = zipFile.getInputStream(zipElement);

                //文件名称-并验证是否为文件
                fileName = zipElement.getName();
                if (fileName != null && fileName.indexOf(".") != -1) {
                    //是，执行另存为
                    FilesUtils.unZipFile(fileName, read, saveAsFileZip);

                    //验证是否为excel
                    if(zipElement.getName().contains(".xlsx")){
                        //执行导入商品信息
                        storeProductExcelResponse = this.duquExcelInputStream(read);
                        isgeshiFiles=true;
                    }
                }
            }

            //关闭
            zipFile.close();

            //验证-是否为模版格式
            if(!isgeshiFiles){
                throw new CrmebException("该zip压缩包格式不正确！");
            }
        }catch (Exception e){
            throw new CrmebException("上传zip，发生错误:"+e.getMessage());
        }

        //返回
        return storeProductExcelResponse;
    }

    @Override
    public String saveFile(MultipartFile multipartFile, String fileName,String folder) throws IOException {
        //得到存储文件地址
        String uploadPath = systemConfigService.getValueByKey(Constants.UPLOAD_ROOT_PATH_CONFIG_KEY);
        String path = new StringBuffer(uploadPath).append("/")
                .append(folder).append("/")
                .append(DateUtil.nowDate(Constants.DATE_FORMAT_DATE).replace("-", "/")).toString();// 拼接文件路径
        File file = UploadUtil.createFile(new StringBuffer(path).append("/").append(fileName).toString());// 创建文件
        multipartFile.transferTo(file);//保存文件
        return path;
    }

    @Override
    public void downloadProductExcelImportTemplate(HttpServletResponse response) throws Exception {
        //得到-商品信息
        StoreProductExcel storeProductExcel=new StoreProductExcel();//实例化-商品信息Excel导入导出-对象
        List<StoreProductExcel> rows = Lists.newArrayList();
        rows.add(storeProductExcel);

        //得到-属性
        List<StoreProductAttrExcel> listAttr=new ArrayList<>();
        StoreProductAttrExcel storeProductAttr=null;
        //属性1
        storeProductAttr=new StoreProductAttrExcel();
        listAttr.add(storeProductAttr);
        //属性2
        storeProductAttr=new StoreProductAttrExcel();
        storeProductAttr.setAttrName("说明");
        //storeProductAttr.setAttrValues("属性sku,sku");
        listAttr.add(storeProductAttr);
        //属性3
        storeProductAttr=new StoreProductAttrExcel();
        storeProductAttr.setAttrName("这是");
        //storeProductAttr.setAttrValues("这是测试,测试,test");
        listAttr.add(storeProductAttr);
        //属性4
        storeProductAttr=new StoreProductAttrExcel();
        storeProductAttr.setAttrName("规则");
        //storeProductAttr.setAttrValues("多个用逗号隔开,逗号隔开,用英文逗号隔开");
        listAttr.add(storeProductAttr);

        //得到-属性值
        List<StoreProductAttrValueExcel> listAttrValue=new ArrayList<>();
        StoreProductAttrValueExcel storeProductAttrValue=new StoreProductAttrValueExcel();
        listAttrValue.add(storeProductAttrValue);
        storeProductAttrValue=new StoreProductAttrValueExcel();
        storeProductAttrValue.setId(1);
        storeProductAttrValue.setSuk("(选填),suk,test,逗号隔开");
        listAttrValue.add(storeProductAttrValue);

        //初始化写入-商品信息-头设置
        ExcelWriter writer=getExcelWriter(storeProductExcel,null,null);
        writer.setSheet(0);
        writer.renameSheet("商品信息");
        writer.write(rows, true);

        //属性-头-设置
        writer.clearHeaderAlias();
        writer=getExcelWriter(storeProductAttr,null,writer);
        writer.setSheet(1);
        writer.renameSheet("属性");
        writer.write(listAttr, true);

        //属性值-头-设置
        writer.clearHeaderAlias();
        writer=getExcelWriter(storeProductAttrValue,null,writer);
        writer.setSheet(2);
        writer.renameSheet("属性值");
        writer.write(listAttrValue, true);

        //文件名称
        String uuid=UUID.randomUUID().toString().replace("-","");
        StringBuffer fileName=new StringBuffer("产品导入模版").append(uuid).append(".xlsx");

        //通知通知客服端文件的MIME类型
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(fileName.toString(), StandardCharsets.UTF_8.name()));
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ServletOutputStream out= response.getOutputStream();
        writer.flush(out,true);

        //关闭writer，释放内存
        writer.close();
        IoUtil.close(out);
    }

    @Override
    public void downloadProductExcelImportTemplateZip(HttpServletResponse response) throws IOException {
        //定义商品路径
        String uploadPath = systemConfigService.getValueByKey(Constants.UPLOAD_ROOT_PATH_CONFIG_KEY);
        String fileName=DateUtil.nowDateTime(Constants.DATE_TIME_FORMAT_NUM);
        String path=new StringBuffer(uploadPath)
                .append("/zip/")
                .append(DateUtil.nowDate(Constants.DATE_FORMAT_DATE).replace("-", "/"))
                .append("/")
                .append(fileName).toString();
        String excelName="info.xlsx";

        //生成-商品信息excel-模板文件
        this.setExcel(new StringBuffer(path).append("/").append(excelName).toString());

        //生成-商品-对应的资源文件夹及文件
        String[] productIdArr={"0","1"};
        File file=null;
        for (String id:productIdArr) {
            //不存在就创建
            file=new File(new StringBuilder(path).append("/").append(id).toString());
            if(!file.exists()){
                file.mkdirs();

                //创建文件
                String imgurl="https://bing.ioliu.cn/v1/rand";
                FileResultVo vo=ImageHttpGet.getImages(imgurl,id,path,null);
                System.out.println(vo);
            }
        }

        //响应客户端
        // 浏览器处理乱码问题
        //String userAgent = request.getHeader("User-Agent");
        // filename.getBytes("UTF-8")处理safari的乱码问题
        //byte[] bytes = userAgent.contains("MSIE") ? fileName.getBytes() : fileName.getBytes("UTF-8");
        // 各浏览器基本都支持ISO编码
        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
        // 文件名外的双引号处理firefox的空格截断问题
        response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", fileName));
        response.setContentType("application/x-msdownload");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        OutputStream out = response.getOutputStream();

        //压缩目录
        this.zipFilesResponse(path,out);
    }

    /**
     * 将压缩包响应给客户端
     * @param zipPath 压缩包路径
     * @param outputStream 输出文件流
     */
    public static void zipFilesResponse(String zipPath,OutputStream outputStream) {
        File file = new File( zipPath );
        ZipOutputStream zipOutputStream = null;
        try {
            zipOutputStream = new ZipOutputStream( outputStream );
            if (file.isDirectory()) {
                FilesUtils.directory( zipOutputStream, file, "" );
            } else {
                FilesUtils.zipFile( zipOutputStream, file, "" );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //删除压缩包
                FilesUtils.delFilePath(zipPath);

                //释放内存·
                zipOutputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过路径写入-商品导入导出-excel模板
     * @param path
     */
    public static void setExcel(String path){
        //文件路径
        if(path == null){
            path=new StringBuffer("d:/").append(UUID.randomUUID()).append(".xlsx").toString();
        }

        //写-通过工具类创建writer
        ExcelWriter writer= null;

        // 合并单元格后的标题行，使用默认标题样式
        //writer.merge(declaredFields.length-1, "商品信息");
        //只导出设置别名字段
        //writer.setOnlyAlias(true);
        // 一次性写出内容，使用默认样式，强制输出标题
        // 关闭writer，释放内存

        try {
            //得到-商品信息
            StoreProductExcel storeProductExcel=new StoreProductExcel();//实例化-商品信息Excel导入导出-对象
            List<StoreProductExcel> rows = Lists.newArrayList();
            rows.add(storeProductExcel);

            //得到-属性
            List<StoreProductAttrExcel> listAttr=new ArrayList<>();
            StoreProductAttrExcel storeProductAttr=null;
            //属性1
            storeProductAttr=new StoreProductAttrExcel();
            listAttr.add(storeProductAttr);
            //属性2
            storeProductAttr=new StoreProductAttrExcel();
            storeProductAttr.setAttrName("说明");
            //storeProductAttr.setAttrValues("属性sku,sku");
            listAttr.add(storeProductAttr);
            //属性3
            storeProductAttr=new StoreProductAttrExcel();
            storeProductAttr.setAttrName("这是");
            //storeProductAttr.setAttrValues("这是测试,测试,test");
            listAttr.add(storeProductAttr);
            //属性4
            storeProductAttr=new StoreProductAttrExcel();
            storeProductAttr.setAttrName("规则");
            //setAttrValues("多个用逗号隔开,逗号隔开,用英文逗号隔开");
            listAttr.add(storeProductAttr);

            //得到-属性值
            List<StoreProductAttrValueExcel> listAttrValue=new ArrayList<>();
            StoreProductAttrValueExcel storeProductAttrValue=new StoreProductAttrValueExcel();
            listAttrValue.add(storeProductAttrValue);

            //得到-详情图
            List<StoreProductInfoExcel> listInfo=new ArrayList<>();
            StoreProductInfoExcel storeProductInfoExcel=new StoreProductInfoExcel();
            listInfo.add(storeProductInfoExcel);
            listInfo.add(storeProductInfoExcel);

            //初始化写入-商品信息-头设置
            writer=getExcelWriter(storeProductExcel,path,null);
            writer.setSheet(0);
            writer.renameSheet("商品信息");
            writer.write(rows, true);
            //释放内存
            writer.close();

            //属性-头-设置
            writer.clearHeaderAlias();
            writer=getExcelWriter(storeProductAttr,path,null);
            writer.setSheet(1);
            writer.renameSheet("属性");
            writer.write(listAttr, true);
            //释放内存
            writer.close();

            //属性值-头-设置
            writer.clearHeaderAlias();
            writer=getExcelWriter(storeProductAttrValue,path,null);
            writer.setSheet(2);
            writer.renameSheet("属性值");
            writer.write(listAttrValue, true);
            //释放内存
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
            throw new CrmebException(e.getMessage());
        }
    }

    /**
     * 读取excel文件流
     * @param inputStream 文件流
     * @return
     */
    public StoreProductExcelResponse duquExcelInputStream(InputStream inputStream){
        try{
            //读
            ExcelReader reader=null;

            //得到-商品-基本信息
            reader = getExcelReader(null,inputStream,StoreProductExcel.class,0,null);//取出第0个sheet=商品信息
            List<StoreProductExcel> listProductExcel=reader.readAll(StoreProductExcel.class);

            //得到-商品-属性
            reader = getExcelReader(null,null,StoreProductAttrExcel.class,1,reader);//取出第1个sheet=属性
            List<StoreProductAttrExcel>  listAttrExcel = reader.readAll(StoreProductAttrExcel.class);

            //得到-商品-属性值
            reader = getExcelReader(null,null,StoreProductAttrValueExcel.class,2,reader);//取出第2个sheet=属性值
            List<StoreProductAttrValueExcel> listAttrValueExcel = reader.readAll(StoreProductAttrValueExcel.class);

            //释放内存
            reader.close();
            inputStream.close();

            //执行保存
            return this.importSave(listProductExcel,listAttrExcel,listAttrValueExcel);
        }catch (Exception e){
            e.printStackTrace();
            throw new CrmebException("导入失败,格式错误:"+e.getMessage());
        }
    }

    /**
     * 查询商品是否存在
     * @param storeProduct 商品信息实体对象
     * @return
     */
    public StoreProduct getStoreProduct(StoreProduct storeProduct){
        //根据商品名称查-验证商品是否已存在
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreProduct::getStoreName,storeProduct.getStoreName());
        lambdaQueryWrapper.eq(StoreProduct::getUnitName,storeProduct.getUnitName());
        lambdaQueryWrapper.eq(StoreProduct::getKeyword,storeProduct.getKeyword());
        lambdaQueryWrapper.eq(StoreProduct::getPrice,storeProduct.getPrice());
        lambdaQueryWrapper.eq(StoreProduct::getBarCode,storeProduct.getBarCode());
        lambdaQueryWrapper.orderByDesc(StoreProduct::getAddTime);

        //到商品信息并返回
        List<StoreProduct> productList= dao.selectList(lambdaQueryWrapper);
        if(productList == null || productList.size()<=0){
            return null;
        }else{
            return productList.get(0);
        }
    }

    /**
     * 根据产品分类名称-得到分类信息
     * 不存在就创建
     * @param cateName  分类名称
     * @return
     */
    public Category getCategor(String cateName){
        //定义查询条件-查询分类
        LambdaQueryWrapper<Category> lambdaQueryWrapperCategory = new LambdaQueryWrapper<>();
        lambdaQueryWrapperCategory.eq(Category::getName, cateName);
        lambdaQueryWrapperCategory.eq(Category::getType, CategoryConstants.CATEGORY_TYPE_PRODUCT);
        Category category  = categoryService.getOne(lambdaQueryWrapperCategory);
        //检测是否存在
        if(category == null){
            //不存在-新增分类
            category = new Category();
            category.setPid(614);           //默认-上级
            category.setPath("/0/614/");    //默认-path
            category.setExtra("crmebimage/store/2020/08/13/1826c7f20771444e888501d91332f129wik43qfgmw.png");//默认-分类图标
            category.setType(CategoryConstants.CATEGORY_TYPE_PRODUCT);//类型-1=产品分类
            category.setName(cateName);
            category.setStatus(true);

            //执行保存
            categoryService.save(category);
            System.out.println("此分类名称不存在,已创建->"+cateName);
        }
        return category;
    }

    /**
     * 根据品牌名称查询品牌信息，
     * 如果不存在，则创建
     * @param brandName 品牌名称
     * @param cateId    分类ID
     * @return
     */
    public StoreBrands getStoreBrands(String brandName,Integer cateId){
        //验证品牌名称-是否已经存在
        LambdaQueryWrapper<StoreBrands> lambdaQueryWrapperStoreBrands = new LambdaQueryWrapper<>();
        lambdaQueryWrapperStoreBrands.eq(StoreBrands::getBrandName,brandName);
        lambdaQueryWrapperStoreBrands.orderByDesc(StoreBrands::getCreateTime);
        StoreBrands storeBrands = storeBrandsService.getOne(lambdaQueryWrapperStoreBrands);
        //验证品牌信息-是否存在
        if(storeBrands == null){
            //不存在-新增
            storeBrands = new StoreBrands();
            storeBrands.setBrandImg("crmebimage/brand/2021/06/24/f6b73a624f454696ad8975dceb5217792yu7qi8fkr.png");//默认-品牌图标
            storeBrands.setIsDisplay(true);
            storeBrands.setCreateTime(DateUtil.nowDateTime());
            storeBrands.setBrandDesc("产品导入-未设置品牌介绍");
            storeBrands.setBrandName(brandName);
            storeBrands.setCateId(cateId.toString());
            storeBrands.setIsDel(false);
            storeBrands.setSort(0);
            //执行保存-品牌信息
            storeBrandsService.save(storeBrands);
        }
        return storeBrands;
    }

    /**
     * 拼接商品图片存放目录
     * @param sbImagesPath
     * @param storeProduct  商品信息实体
     */
    public void splicingImages(StringBuffer sbImagesPath,StoreProduct storeProduct){
        //商品图片
        String img=storeProduct.getImage();//主图
        String images=storeProduct.getSliderImage();//轮播图
        String contenImages=storeProduct.getContent();//详情图
        sbImagesPath.append(storeProduct.getId()).append("/");//ID目录

        //拼接-主图
        img=new StringBuffer(sbImagesPath).append(img).toString();

        //拼接-轮播图
        List<String> imagesList= new ArrayList<>();
        if(images!=null&&!"".equals(images)){
            String[] imagesArr= images.split(",");
            for (int i=0;i<imagesArr.length;i++) {
                String IMG=new StringBuffer(sbImagesPath).append(imagesArr[i]).toString();
                imagesList.add(IMG);
            }
            images=String.join(",",imagesArr);
        }

        //拼接-详情图
        if(contenImages!=null&&!"".equals(contenImages)){
            String[] contenImagesList= contenImages.split(",");
            for (int i=0;i<contenImagesList.length;i++) {
                contenImagesList[i]=new StringBuffer(sbImagesPath).append("info/").append(contenImagesList[i]).toString();
            }
            contenImages=String.join(",",contenImagesList);
        }

        //拼接-导购视频
        String videoLink=null;
        if(storeProduct.getVideoLink()!=null&&!"".equals(storeProduct.getVideoLink())){
            videoLink=new StringBuffer(sbImagesPath).append(storeProduct.getVideoLink()).toString();
        }

        //重新赋值
        storeProduct.setImage(img);
        storeProduct.setSliderImage(JSON.toJSONString(imagesList));
        storeProduct.setContent(contenImages);
        storeProduct.setVideoLink(videoLink);
    }

    /**
     * 导入时验证或处理产品信息
     * @param storeProductExcel    实体对象-商品信息导入导出类
     * @param storeProduct         实体对象-商品信息
     * @return 是否继续
     */
    public boolean validateProductOnImport(StoreProductExcel storeProductExcel,StoreProduct storeProduct){
        //验证-商品ID-是否为空
        if(storeProduct.getId() == null || storeProduct.getId()<0){
            return false;
        }

        //处理-商品-参数
        storeProduct.setBarCode(storeProduct.getBarCode()==null?"没有":storeProduct.getBarCode());
        storeProduct.setAddTime(DateUtil.getNowTime());
        storeProduct.setActivity("0");
        storeProduct.setSpecType(Boolean.TRUE);
        storeProduct.setIsShow(Boolean.TRUE);

        //验证商品是否已经存在
        StoreProduct product = getStoreProduct(storeProduct);
        if(product!=null){
            return false;
        }

        //验证商品库存
        if(storeProduct.getStock() == null || storeProduct.getStock() ==0){
            storeProduct.setStock(999);
        }

        //验证商品详情
        if(storeProduct.getStoreInfo() == null || "".equals(storeProduct.getStoreInfo())){
            storeProduct.setStoreInfo("导入商品-默认简介");
        }

        //得到-分类id-设置商品分类
        Category category = getCategor(storeProductExcel.getCateName());
        storeProduct.setCateId(category.getId().toString());

        //得到-品牌ID-设置商品品牌
        StoreBrands storeBrands=getStoreBrands(storeProductExcel.getBrandName(),category.getId());
        storeProduct.setBrandId(storeBrands.getId());

        //执行保存-商品信息
        return this.save(storeProduct);
    }

    /**
     * 得到-导出商品信息
     * @param request
     * @return
     */
    Map<String,Object> getExportData(StoreProductSearchRequest request){
        //实例化对象
        Map<String,Object> map=new HashMap<>();
        List<StoreProductExcel> list=new ArrayList<>();
        List<StoreProductAttrExcel> storeProductAttrList=new ArrayList<>();
        List<StoreProductAttrValueExcel> storeProductAttrValueList=new ArrayList<>();

        //得到-商品基本数据
        List<StoreProduct> storeProductList = this.getStoreProductList(request);
        for (StoreProduct storeProduct:storeProductList) {
            //转换
            StoreProductExcel storeProductExcel = new StoreProductExcel();
            BeanUtils.copyProperties(storeProduct, storeProductExcel);
            list.add(storeProductExcel);

            //得到-商品-属性
            StoreProductAttr storeProductAttr = new StoreProductAttr();
            storeProductAttr.setProductId(storeProduct.getId()).setType(Constants.PRODUCT_TYPE_NORMAL);
            List<StoreProductAttr> attrs = attrService.getByEntity(storeProductAttr);
            for (StoreProductAttr attr:attrs) {
                //转换
                StoreProductAttrExcel storeProductAttrExcel = new StoreProductAttrExcel();
                BeanUtils.copyProperties(attr, storeProductAttrExcel);
                storeProductAttrList.add(storeProductAttrExcel);
            }

            //得到-商品-属性值
            StoreProductAttrValue storeProductAttrValue = new StoreProductAttrValue();
            storeProductAttrValue.setProductId(storeProduct.getId()).setType(Constants.PRODUCT_TYPE_NORMAL);
            List<StoreProductAttrValue> attrValues = storeProductAttrValueService.getByEntity(storeProductAttrValue);
            for (StoreProductAttrValue attrValue:attrValues) {
                //转换
                StoreProductAttrValueExcel storeProductAttrValueExcel = new StoreProductAttrValueExcel();
                BeanUtils.copyProperties(attrValue, storeProductAttrValueExcel);
                storeProductAttrValueList.add(storeProductAttrValueExcel);
            }
        }

        //放入map
        map.put("list",list);
        map.put("storeProductAttrList",storeProductAttrList);
        map.put("storeProductAttrValueList",storeProductAttrValueList);

        //返回map
        return map;
    }

    /**
     * 得到-ExcelReader.addHeaderAlias
     * @param path      文件路径
     * @param inputStream 文件流
     * @param clazz     实体类
     * @param index  第几个sheet，默认为0
     *
     * @return ExcelReader
     */
    public static ExcelReader getExcelReader(String path, InputStream inputStream, Class clazz, int index,ExcelReader reader1){
        //得到-ExcelReader
        ExcelReader reader = null;
        if(path !=null){
            reader=ExcelUtil.getReader(path, index);
        }else if(inputStream!=null){
            reader=ExcelUtil.getReader(inputStream, index);
        }else if(reader1 != null){
            reader=reader1;
            //清除之前的title
            reader.setHeaderAlias(new HashMap<>());
            reader.setSheet(index);
        }

        // 1.根据类路径获取类
        Class<?> c = clazz;
        // 2.获取类的属性
        Field[] declaredFields = c.getDeclaredFields();
        // 3.遍历属性，获取属性上ApiModelProperty的值，属性的名，存入Properties
        if (declaredFields.length != 0) {
            for (Field field : declaredFields) {
                if (field.getAnnotation(ApiModelProperty.class) != null) {
                    // key和value可根据需求存
                    // 这存的key为注解的值，value为类属性名
                    reader.addHeaderAlias(field.getAnnotation(ApiModelProperty.class).value(),field.getName());
                }
            }
        }
        return reader;
    }

    /**
     * 得到-ExcelWriter.addHeaderAlias
     * @param clazz     实体类
     * @param path      文件路径
     * @return ExcelReader
     */
    public static ExcelWriter getExcelWriter(Object clazz,String path,ExcelWriter writer) throws IllegalAccessException {
        //得到-ExcelReader
        if(path != null){
            writer = ExcelUtil.getWriter(path);
        }else if(writer == null ){
            writer = ExcelUtil.getWriter();
        }

        // 1.根据类路径获取类
        //Class<?> c = clazz;
        // 2.获取类的属性
        Field[] declaredFields = clazz.getClass().getDeclaredFields();
        // 3.遍历属性，获取属性上ApiModelProperty的值，属性的名，存入Properties
        int n=0;
        if (declaredFields.length != 0) {
            for (Field field : declaredFields) {
                if (field.getAnnotation(ApiModelProperty.class) != null) {
                    // key和value可根据需求存
                    // 这存的key为注解的值，value为类属性名
                    //自定义标题别名
                    String apiValue=  field.getAnnotation(ApiModelProperty.class).value();
                    String name=field.getName();
                    field.setAccessible(true);                          //允许访问私有字段
                    Object fieldValue =  field.get(clazz);              //获得私有字段值

                    //设置-标题别名，name=标题，apiValue=别名
                    writer.addHeaderAlias(name,apiValue);
                    //计算列宽度
                    writer.setColumnWidth(n, (fieldValue==null?"":fieldValue.toString()).length()+10); //第n列40px宽
                    n++;
                }
            }
        }
        return writer;
    }

}

