package com.zbkj.crmeb.pub.controller;

import com.zbkj.crmeb.export.controller.Student;
import com.zbkj.crmeb.export.utils.ExcelUtil;
import com.zbkj.crmeb.store.service.StoreProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @program: crmeb
 * @description: Excel-控制类
 * @author: 零风
 * @create: 2021-07-27 08:45
 **/
@Slf4j
@RestController("ExcelControllers")
@RequestMapping("api/public/excel")
@Api(tags = "文件操作-Excel")
public class ExcelController {

    @Autowired
    private StoreProductService storeProductService;

    private static final List<Student> STUDENTS;

    static {
        STUDENTS = Arrays.asList(
                new Student(1, "张三", 25, 1, new Date(), "上海朱家嘴"),
                new Student(2, "李四", 33, 0, new Date(), "上海青浦"),
                new Student(3, "王五", 48, 1, new Date(), "上海陆家班"),
                new Student(4, "赵六", 19, 3, new Date(), "上海足球场")
        );
    }

    /**
     * 导出测试
     * @param request
     * @param response
     */
    @RequestMapping(value = "/noF/exportStudentExcel")
    public void exportStudentExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
            ExcelUtil.exportFile(request, response, STUDENTS, Student.class, "学生信息表");
            System.out.println("excel导出成功~");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("excel导出失败~");
        }
    }


    /**
     * 下载-商品信息-Excel文件-导入模版
     * @param response  响应对象
     */
    @ApiOperation(value = "下载-商品信息模板(Excel文件）")
    @RequestMapping(value="/noF/downloadPEIT",method= RequestMethod.GET)
    public void downloadPEIT(HttpServletResponse response) throws Exception {
        //写入-商品信息
        storeProductService.downloadProductExcelImportTemplate(response);
    }

}
