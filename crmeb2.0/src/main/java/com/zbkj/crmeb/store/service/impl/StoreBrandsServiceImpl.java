package com.zbkj.crmeb.store.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.category.model.Category;
import com.zbkj.crmeb.category.service.CategoryService;
import com.zbkj.crmeb.store.dao.StoreBrandsDao;
import com.zbkj.crmeb.store.model.StoreBrands;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.StoreProductReply;
import com.zbkj.crmeb.store.request.StoreBrandsSearchRequest;
import com.zbkj.crmeb.store.response.StoreBrandsPreferredRsponse;
import com.zbkj.crmeb.store.service.StoreBrandsService;
import com.zbkj.crmeb.store.service.StoreProductService;
import com.zbkj.crmeb.store.vo.StoreBrandsVo;
import com.zbkj.crmeb.system.service.SystemGroupDataService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: crmeb
 * @description: 品牌表接口实现类
 * @author: 零风
 * @create: 2021-06-23 11:15
 **/
@Service
public class StoreBrandsServiceImpl extends ServiceImpl<StoreBrandsDao, StoreBrands>  implements StoreBrandsService {

    @Resource
    private StoreBrandsDao dao;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SystemGroupDataService systemGroupDataService;

    @Autowired
    private StoreProductService storeProductService;

    @Override
    public List<StoreBrands> getCateIdList(StoreBrandsSearchRequest request) {
        //得到lambda对象，用作筛选条件
        LambdaQueryWrapper<StoreBrands> lambdaQueryWrapper = Wrappers.lambdaQuery();

        //条件-分类ID
        if(StringUtils.isNotBlank(request.getCateId())){
            lambdaQueryWrapper.apply(CrmebUtil.getFindInSetSql("cate_id", request.getCateId()));
        }

        //条件-只查询显示的
        lambdaQueryWrapper.eq(StoreBrands::getIsDisplay, true);

        //条件-只查未删除的
        lambdaQueryWrapper.eq(StoreBrands::getIsDel,false);

        //排序条件，根据sort以及创建时间排序
        lambdaQueryWrapper.orderByDesc(StoreBrands::getSort).orderByDesc(StoreBrands::getCreateTime);
        List<StoreBrands> sbList = dao.selectList(lambdaQueryWrapper);

        //返回
        return sbList;
    }

    @Override
    public StoreBrandsVo getInfoId(Integer id) {
        //得到品牌信息
        StoreBrands sb = getById(id);

        //得到分类
        String[] cateIdArr = sb.getCateId().split(",");
        List<Category> cateList=new ArrayList<>();
        Category category=null;
        for (String str: cateIdArr) {
            category=categoryService.getById(str);
            cateList.add(category);
        }

        //储存
        StoreBrandsVo sbVo=new StoreBrandsVo();
        sbVo.setSbrandsInfo(sb);
        sbVo.setCateList(cateList);

        //返回
        return sbVo;
    }

    @Override
    public boolean updateIsDisplay(Integer id) {
        StoreBrands sb = getById(id);
        sb.setIsDisplay(!sb.getIsDisplay());
        return updateById(sb);
    }

    @Override
    public PageInfo<StoreBrands> getAdminList(StoreBrandsSearchRequest request, PageParamRequest pageParamRequest) {
        //得到分页对象
        Page<StoreBrands> sbPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //得到lambda对象，用作筛选条件
        LambdaQueryWrapper<StoreBrands> lambdaQueryWrapper = Wrappers.lambdaQuery();

        //条件-是否显示
        if(request.getIsDisplay() > 0){
            boolean isDisplay=false;
            if(request.getIsDisplay()==1) isDisplay=true;
            lambdaQueryWrapper.eq(StoreBrands::getIsDisplay, isDisplay);
        }

        //条件-关键字
        if(StringUtils.isNotBlank(request.getKeywords())){
            lambdaQueryWrapper.like(StoreBrands::getBrandName, request.getKeywords());
        }

        //排序条件，根据sort以及创建时间排序
        lambdaQueryWrapper.orderByDesc(StoreBrands::getSort).orderByDesc(StoreBrands::getCreateTime);
        List<StoreBrands> sbList = dao.selectList(lambdaQueryWrapper);

        //返回数据
        return CommonPage.copyPageInfo(sbPage, sbList);
    }

    @Override
    public StoreBrandsPreferredRsponse getBrandsPreferredInfo(StoreBrandsSearchRequest request, PageParamRequest pageParamRequest) {
        //得到分页对象
        Page<StoreProduct> storeProductPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //实例化-品牌优选-响应对象
        StoreBrandsPreferredRsponse storeBrandsPreferredRsponse=new StoreBrandsPreferredRsponse();

        //得到-品牌优选-详情-广告banner
        storeBrandsPreferredRsponse.setInfoGuangaoBanner(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_BRANDS_INFO_BANNER));
        //得到-品牌优选-详情-推荐分类
        storeBrandsPreferredRsponse.setInfoType(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_BRANDS_INFO_TYPE));

        //得到lambda对象，用作筛选条件
        LambdaQueryWrapper<StoreBrands> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(StoreBrands::getIsPreferred,true);
        lambdaQueryWrapper.orderByDesc(StoreBrands::getSort).orderByDesc(StoreBrands::getCreateTime);
        List<StoreBrands> storeBrandsList=dao.selectList(lambdaQueryWrapper);
        if(storeBrandsList!=null&&storeBrandsList.size()>0){
            //得到-品牌优选-list
            storeBrandsPreferredRsponse.setBrandsPreferredList(storeBrandsList);

            //得到-品牌优选-推荐商品-分页list
            List<Integer> brandsIdList=storeBrandsList.stream().map(StoreBrands::getId).collect(Collectors.toList());
            LambdaQueryWrapper<StoreProduct>  lambdaQueryWrapperStoreProduct=new LambdaQueryWrapper<>();
            lambdaQueryWrapperStoreProduct.eq(StoreProduct::getIsGood,true);

            //条件-分类ID
            if(request.getCateId() !=null && request.getCateId().split(",").length<=1){
                lambdaQueryWrapperStoreProduct.like(StoreProduct::getCateId, request.getCateId());
            }else{
                lambdaQueryWrapperStoreProduct.in(StoreProduct::getCateId, request.getCateId());
            }

            //条件-品牌ID
            if(request.getBrandId()==null||request.getBrandId()<=0){
                lambdaQueryWrapperStoreProduct.in(StoreProduct::getBrandId, brandsIdList);
            }else{
                lambdaQueryWrapperStoreProduct.in(StoreProduct::getBrandId, request.getBrandId());
            }

            //得到-商品数据
            List<StoreProduct>  storeProductList= storeProductService.list(lambdaQueryWrapperStoreProduct);
            PageInfo<StoreProduct> pageStoreProductList= CommonPage.copyPageInfo(storeProductPage, storeProductList);

            //设置到响应对象
            storeBrandsPreferredRsponse.setPageStoreProductList(pageStoreProductList);
        }

        //返回
        return  storeBrandsPreferredRsponse;
    }
}
