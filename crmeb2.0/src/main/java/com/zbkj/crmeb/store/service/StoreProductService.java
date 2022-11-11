package com.zbkj.crmeb.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.front.request.IndexStoreProductSearchRequest;
import com.zbkj.crmeb.front.request.ProductRequest;
import com.zbkj.crmeb.front.response.ProductAttrResponse;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.StoreProductAttr;
import com.zbkj.crmeb.store.request.StoreProductRequest;
import com.zbkj.crmeb.store.request.StoreProductSearchRequest;
import com.zbkj.crmeb.store.request.StoreProductStockRequest;
import com.zbkj.crmeb.store.response.StoreProductExcelResponse;
import com.zbkj.crmeb.store.response.StoreProductResponse;
import com.zbkj.crmeb.store.response.StoreProductTabsHeader;
import com.zbkj.crmeb.store.vo.StoreProductAttrExcel;
import com.zbkj.crmeb.store.vo.StoreProductAttrValueExcel;
import com.zbkj.crmeb.store.vo.StoreProductExcel;
import org.json.JSONException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 商品表-IService层接口
 * @author: 零风
 * @CreateDate: 2022/3/4 16:51
 */
public interface StoreProductService extends IService<StoreProduct> {

    /**
     * 将商品属性-转换为-商品属性响应对象（公共转换）
     * @param attrList 商品属性list列表
     * @Author 零风
     * @Date  2022/3/4
     * @return 商品属性响应list集合
     */
    List<ProductAttrResponse> getSkuAttr(List<StoreProductAttr> attrList);

    /**
     * 商品-绑定供应商ID/商户id
     * @param productId 商品id标识，多个用逗号隔开
     * @param merId     商户id标识/供应商id标识
     * @Author 零风
     * @Date  2021/12/28
     * @return 结果
     */
    Boolean setMerId(String productId,Integer merId);

    /**
     * 获取产品列表Admin(后台)
     * @param request           请求参数
     * @param pageParamRequest  分页对象
     * @param isYw              是否处理业务逻辑
     * @Author 零风
     * @Date  2022/3/4
     * @return 分页商品响应信息
     */
    PageInfo<StoreProductResponse> getList(StoreProductSearchRequest request, PageParamRequest pageParamRequest,Boolean isYw);

    /**
     * 根据产品ID集合-搜索分页产品列表
     * @param request           请求参数
     * @param pageParamRequest  分页对象
     * @param productIdList     产品ID集合
     * @Author 零风
     * @Date  2022/3/4
     * @return 产品list集合
     */
    List<StoreProduct> getList(StoreProductSearchRequest request, PageParamRequest pageParamRequest, List<Integer> productIdList);

    /**
     * 获取产品列表H5(客户端)
     * @param request           请求参数
     * @param pageParamRequest  分页对象
     * @Author 零风
     * @Date  2022/3/4
     * @return 商品列表
     */
    List<StoreProduct> getList(IndexStoreProductSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 根据产品ID集合-获取商品信息
     * @param productIds id集合
     * @Author 零风
     * @Date  2022/3/4
     * @return 商品集合
     */
    List<StoreProduct> getListInIds(List<Integer> productIds);

    /**
     * 根据产品参数-查询商品
     * @param storeProduct 产品参数
     * @Author 零风
     * @Date  2022/3/4
     * @return 产品信息
     */
    StoreProduct getByEntity(StoreProduct storeProduct);

    /**
     * 保存产品信息
     * @param storeProductRequest 请求参数
     * @Author 零风
     * @Date  2022/3/4
     * @return 结果
     */
    boolean save(StoreProductRequest storeProductRequest);

    /**
     * 更新产品信息
     * @param storeProductRequest 请求参数
     * @Author 零风
     * @Date  2022/3/4
     * @return 结果
     */
    boolean update(StoreProductRequest storeProductRequest);

    /**
     * 产品详情
     * @param id   产品ID标识
     * @Author 零风
     * @Date  2022/3/4
     * @return 结果
     */
    StoreProductResponse getByProductId(int id);

    /**
     * 获取tabsHeader对应数量
     * @return
     */
    List<StoreProductTabsHeader> getTabsHeader();

    /**
     * 添加库存
     * @param request
     * @return
     */
    boolean stockAddRedis(StoreProductStockRequest request);

    /**
     * 根据其他平台url导入产品信息
     * @param url 待倒入平台的url
     * @param tag 待导入平台标识
     * @return 待导入的商品信息
     */
    StoreProductRequest importProductFromUrl(String url, int tag) throws IOException, JSONException;

    /**
     * 获取推荐商品
     * @param limit 最大数据量
     * @return 推荐商品集合
     */
    List<StoreProduct> getRecommendStoreProduct(Integer limit);

    /**
     * 扣减库存加销量
     * @param productId 产品id
     * @param num 商品数量
     * @param type 是否限购 0=不限购
     * @return 扣减结果
     */
    boolean decProductStock(Integer productId, Integer num, Integer attrValueId, Integer type);

    /**
     * 根据商品id取出二级分类
     * @param productId
     * @return List<Integer>
     */
    List<Integer> getSecondaryCategoryByProductId(String productId);

    /**
     * 删除商品
     * @param productId 商品id
     * @param type      类型：recycle——回收站,delete——彻底删除
     * @return 删除结果
     */
    boolean deleteProduct(Integer productId, String type);

    /**
     * 恢复已删除商品
     * @param productId 商品id
     * @return 恢复结果
     */
    boolean reStoreProduct(Integer productId);

    /**
     * 后台任务批量操作库存
     */
    void consumeProductStock();

    /**
     * 扣减库存任务操作
     * @param storeProductStockRequest 扣减库存参数
     * @return 执行结果
     */
    boolean doProductStock(StoreProductStockRequest storeProductStockRequest);

    /**
     * 获取复制商品配置
     */
    MyRecord copyConfig();

    /**
     * 复制平台商品
     * @param url 商品链接
     * @return
     */
    MyRecord copyProduct(String url);

    /**
     * 添加/扣减库存
     * @param id 商品id
     * @param num 数量
     * @param type 类型：add—添加，sub—扣减
     */
    Boolean operationStock(Integer id, Integer num, String type);

    /**
     * 下架
     * @param id 商品id
     */
    Boolean offShelf(Integer id);

    /**
     * 上架
     * @param id 商品id
     * @return Boolean
     */
    Boolean putOnShelf(Integer id);

    /**
     * 首页商品列表
     * @param type 类型 【1 精品推荐 2 热门榜单 3首发新品 4促销单品】
     * @param pageParamRequest 分页参数
     * @return CommonPage
     */
    List<StoreProduct> getIndexProduct(Integer type, PageParamRequest pageParamRequest);

    /**
     * 获取商品移动端列表
     * @param request 筛选参数
     * @param pageRequest 分页参数
     * @return List
     */
    List<StoreProduct> findH5List(ProductRequest request, PageParamRequest pageRequest);

    /**
     * 获取移动端商品详情
     * @param id 商品id
     * @return StoreProduct
     */
    StoreProduct getH5Detail(Integer id);

    /**
     * 获取购物车商品信息
     * @param productId 商品编号
     * @return StoreProduct
     */
    StoreProduct getCartByProId(Integer productId);

    /**
     * 根据商品ids获取对应的列表
     * @param productIdList 商品id列表
     * @return List<StoreProduct>
     */
    List<StoreProduct> findH5ListByProIds(List<Integer> productIdList);

    /**
     * 导入-商品-excel文件(路径方式)
     * (已废弃)
     * @param path 文件路径
     */
    @Deprecated
    void importProductExcel(String path);

    /**
     * 导入-商品-excel文件(路径方式)(升级版)
     * @return
     */
    StoreProductExcelResponse importProductExcelUpgrade(String path);

    /**
     * 下载-商品信息(Excel文件格式）
     * @param request       导出条件
     * @param response      响应对象
     */
    void exportProductExcel(StoreProductSearchRequest request,HttpServletResponse response);

    /**
     * 导入-商品信息(上传Excel的方式)
     * @param file excel文件
     */
    StoreProductExcelResponse importUploadFileExcel(MultipartFile file) throws IOException;

    /**
     * 导入-商品信息(上传压缩包的方式)
     * @param file
     * @return
     */
    StoreProductExcelResponse importUploadFileExcelZip(MultipartFile file);

    /**
     * 执行-商品-导入保存
     * @param listProductExcel      商品list
     * @param listAttrExcel         商品属性list
     * @param listAttrValueExcel    商品属性值list
     * @Author 零风
     * @Date  2022/3/7
     * @return 执行结果
     */
    StoreProductExcelResponse importSave(List<StoreProductExcel> listProductExcel, List<StoreProductAttrExcel>  listAttrExcel, List<StoreProductAttrValueExcel> listAttrValueExcel);

    /**
     * 下载-商品信息模板(Excel文件）
     * @param response http服务响应
     * @Author 零风
     * @Date  2022/3/7
     */
    void downloadProductExcelImportTemplate(HttpServletResponse response) throws Exception;

    /**
     * 下载-商品信息模版(zip压缩包格式)
     * @param response http服务响应
     * @Author 零风
     * @Date  2022/3/7
     */
    void downloadProductExcelImportTemplateZip(HttpServletResponse response) throws IOException;

    /**
     * 保存文件(公共接口)
     * @Author 零风
     * @Date  2022/3/4
     * @return 文件存储路径
     */
    String saveFile(MultipartFile multipartFile, String fileName,String folder) throws IOException;

}
