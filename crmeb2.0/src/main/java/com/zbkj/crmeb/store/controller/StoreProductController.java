package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.zbkj.crmeb.store.model.StoreMakeAnAppointment;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.request.*;
import com.zbkj.crmeb.store.response.StoreProductExcelResponse;
import com.zbkj.crmeb.store.response.StoreProductRAResponse;
import com.zbkj.crmeb.store.response.StoreProductResponse;
import com.zbkj.crmeb.store.response.StoreProductTabsHeader;
import com.zbkj.crmeb.store.service.StoreCartService;
import com.zbkj.crmeb.store.service.StoreMakeAnAppointmentService;
import com.zbkj.crmeb.store.service.StoreProductRAService;
import com.zbkj.crmeb.store.service.StoreProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * 商品表-后端端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/store/product")
@Api(tags = "商品") //配合swagger使用
public class StoreProductController {

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreCartService storeCartService;

    @Autowired
    private StoreProductRAService storeProductRAService;

    @Autowired
    private StoreMakeAnAppointmentService storeMakeAnAppointmentService;

    @ApiOperation(value = "商品-绑定供应商")
    @RequestMapping(value = "/setMerId", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productIds", value = "商品ID，多个用逗号隔开"),
            @ApiImplicitParam(name = "merId", value = "供应商id标识")
    })
    public CommonResult<Boolean> setMerId(
            @RequestParam("productIds") String productIds,
            @RequestParam("merId") Integer merId) {
        return CommonResult.success(storeProductService.setMerId(productIds,merId));
    }

    @ApiOperation(value = "查询该产品-产品代理列表")
    @RequestMapping(value = "/ra/getInfoList", method = RequestMethod.GET)
    @ApiImplicitParam(name = "productId",value = "产品ID标识")
    public CommonResult<List<StoreProductRAResponse>> getInfoList(@RequestParam("productId") Integer productId) {
        return CommonResult.success(storeProductRAService.getInfoList(productId));
    }

    @ApiOperation(value = "删除-产品代理")
    @RequestMapping(value = "/ra/deleteWhereId", method = RequestMethod.GET)
    @ApiImplicitParam(name = "spraId",value = "产品代理表ID标识")
    public CommonResult<String> raDeleteWhereId(@RequestParam("spraId") Integer spraId) {
        if (storeProductRAService.delete(spraId)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "新增-产品代理")
    @RequestMapping(value = "/ra/add", method = RequestMethod.POST)
    public CommonResult<String> raAdd(@RequestBody @Validated StoreProductRARequest request) {
        if (storeProductRAService.add(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreProductResponse>> getList(
            @Validated StoreProductSearchRequest request,
            @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(storeProductService.getList(request, pageParamRequest, Boolean.FALSE)));
    }

    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated StoreProductRequest storeProductRequest) {
        if (storeProductService.save(storeProductRequest)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 删除商品表
     *
     * @param id Integer
     * @author 李杰
     * @since 2021-6-27
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestBody @PathVariable Integer id, @RequestParam(value = "type", required = false, defaultValue = "recycle") String type) {
        if (storeProductService.deleteProduct(id, type)) {
            if (type.equals("recycle")) {
                storeCartService.productStatusNotEnable(id);
            } else {
                storeCartService.productDelete(id);
            }
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 恢复已删除商品表
     *
     * @param id Integer
     * @author Stivepeim
     * @since 2021-08-28
     */
    @ApiOperation(value = "恢复商品")
    @RequestMapping(value = "/restore/{id}", method = RequestMethod.GET)
    public CommonResult<String> restore(@RequestBody @PathVariable Integer id) {
        if (storeProductService.reStoreProduct(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 修改商品表
     * @param storeProductRequest 修改参数
     * @author 李杰
     * @since 2021-6-27
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated StoreProductRequest storeProductRequest) {
        if (storeProductService.update(storeProductRequest)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 查询商品表信息-详细信息
     * @param id Integer
     * @author 李杰
     * @since 2021-6-27
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<StoreProductResponse> info(@PathVariable Integer id) {
        StoreProductResponse storeProductResponse = storeProductService.getByProductId(id);
        return CommonResult.success(storeProductResponse);
    }

    @ApiOperation(value = "商品表头数量")
    @RequestMapping(value = "/tabs/headers", method = RequestMethod.GET)
    public CommonResult<List<StoreProductTabsHeader>> getTabsHeader() {
        return CommonResult.success(storeProductService.getTabsHeader());
    }

    /**
     * 上架
     */
    @ApiOperation(value = "上架")
    @RequestMapping(value = "/putOnShell/{id}", method = RequestMethod.GET)
    public CommonResult<String> putOn(@PathVariable Integer id) {
        if (storeProductService.putOnShelf(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 上架-批量
     */
    @ApiOperation(value = "上架-批量")
    @RequestMapping(value = "/putOnShell/batch", method = RequestMethod.POST)
    @ApiImplicitParam(name = "idstr", value = "商品id字符串，多个用逗号隔开", dataType = "String", required = true)
    public CommonResult<String> putOnBatch(@RequestParam("idstr") String idstr) {
        //验证非空，转换
        if(idstr == null || "".equals(idstr))throw new CrmebException("商品id字符串,不能为空！");
        String[] idArr=idstr.split(",");
        for (String id:idArr){
            if(!storeProductService.putOnShelf(Integer.valueOf(id))){
                return CommonResult.failed("失败！id:"+id);
            }
        }
        return CommonResult.success();
    }

    /**
     * 下架
     */
    @ApiOperation(value = "下架")
    @RequestMapping(value = "/offShell/{id}", method = RequestMethod.GET)
    public CommonResult<String> offShell(@PathVariable Integer id) {
        if (storeProductService.offShelf(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 虚拟销量
     *
     * @param id integer id
     * @author 李杰
     * @since 2021-6-15
     */
    @ApiOperation(value = "虚拟销量")
    @RequestMapping(value = "/ficti/{id}/{num}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "int", required = true),
            @ApiImplicitParam(name = "num", value = "数值", dataType = "int", required = true),
    })
    public CommonResult<String> sale(@PathVariable Integer id, @PathVariable Integer num) {
        StoreProduct storeProduct = storeProductService.getById(id);
        storeProduct.setFicti(num);
        if (storeProductService.updateById(storeProduct)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 库存变动
     *
     * @param request StoreProductStockRequest 参数
     * @author 李杰
     * @since 2021-05-19
     */
    @ApiOperation(value = "库存变动")
    @RequestMapping(value = "/stock", method = RequestMethod.GET)
    public CommonResult<String> stock(@Validated StoreProductStockRequest request) {
        if (storeProductService.stockAddRedis(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "导入99Api商品")
    @RequestMapping(value = "/importProduct", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "form", value = "导入平台1=淘宝，2=京东，3=苏宁，4=拼多多, 5=天猫", dataType = "int", required = true),
            @ApiImplicitParam(name = "url", value = "URL", dataType = "String", required = true),
    })
    public CommonResult<StoreProductRequest> importProduct(
            @RequestParam @Valid int form,
            @RequestParam @Valid String url) throws IOException, JSONException {
        StoreProductRequest productRequest = storeProductService.importProductFromUrl(url, form);
        return CommonResult.success(productRequest);
    }

    /**
     * 获取复制商品配置
     */
    @ApiOperation(value = "获取复制商品配置")
    @RequestMapping(value = "/copy/config", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> copyConfig() {
        return CommonResult.success(storeProductService.copyConfig());
    }

    @ApiOperation(value = "复制平台商品")
    @RequestMapping(value = "/copy/product", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> copyProduct(@RequestBody @Valid StoreCopyProductRequest request) {
        return CommonResult.success(storeProductService.copyProduct(request.getUrl()));
    }

    @ApiOperation(value = "导入-商品信息(Excel文件路径方式)")
    @RequestMapping(value = "/add/importProductExcel", method = RequestMethod.POST)
    @ApiImplicitParam(name = "path", value = "文件路径", dataType = "String", required = false)
    public CommonResult<StoreProductExcelResponse> importProductExcel(@RequestParam("path") String path) {
        return CommonResult.success(storeProductService.importProductExcelUpgrade(path));
    }

    @ApiOperation(value = "导入-商品信息(上传Excel文件方式)")
    @RequestMapping(value = "/add/importUploadFileExcel", method = RequestMethod.POST)
    public CommonResult<StoreProductExcelResponse> importUploadFileExcel(MultipartFile file) throws IOException {
        return CommonResult.success(storeProductService.importUploadFileExcel(file));
    }

    @ApiOperation(value = "导入-商品信息(上传ZIP压缩包的方式)")
    @RequestMapping(value = "/add/importUploadFileExcelZip", method = RequestMethod.POST)
    public CommonResult<StoreProductExcelResponse> importUploadFileExcelZip(MultipartFile file) {
        return CommonResult.success(storeProductService.importUploadFileExcelZip(file));
    }

    @ApiOperation(value = "下载-商品信息模板(Excel文件格式）")
    @RequestMapping(value = "/noF/downloadPEIT", method = RequestMethod.POST)
    public void downloadPEIT(HttpServletResponse response) throws Exception {
        storeProductService.downloadProductExcelImportTemplate(response);
    }

    @ApiOperation(value = "下载-商品信息模版(zip压缩包格式)")
    @RequestMapping(value = "/noF/downloadZip", method = RequestMethod.POST)
    public void downloadZip(HttpServletResponse response) throws IOException {
        storeProductService.downloadProductExcelImportTemplateZip(response);
    }

    @ApiOperation(value = "导出-商品信息(Excel文件格式）") //配合swagger使用
    @RequestMapping(value = "/noF/excel/exportProductExcel", method = RequestMethod.GET)
    public void exportProductExcel(
            @Validated StoreProductSearchRequest request,
            HttpServletResponse response,
            @Validated PageParamRequest pageParamRequest) {
        storeProductService.exportProductExcel(request, response);
    }

    @ApiOperation(value = "商品预约-预约记录分页列表",notes = Constants.SELECT)
    @RequestMapping(value = "/maa/getPageList", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreMakeAnAppointment>> maaGetPageList(
            @Validated StoreMakeAnAppointmentSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(storeMakeAnAppointmentService.getPageList(request, pageParamRequest,Boolean.TRUE)));
    }

}

