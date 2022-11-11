package com.zbkj.crmeb.export.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 自定义Excel操作工具类
 * </p>
 *
 * @author bai
 * @date 2021/1/18 17:50
 */
public class ExcelUtil {
    /**
     * <p>
     * 自定义导出excel文件方法
     * </p>
     *
     * @param request   http请求
     * @param response  http响应
     * @param data      需要导出的excel数据 list 集合
     * @param clazz     list集合中的泛型类模板
     * @param excelName 导出的 excel 表名称
     */
    public static void exportFile(HttpServletRequest request, HttpServletResponse response, List<?> data, Class<?> clazz, String excelName) throws Exception {
        // 创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 调用导出excel的方法
        ExcelUtil.export(workbook, data, clazz, excelName);
        // 生成文件名
        String fileName = DateTimeFormatter.ofPattern(ExcelConstants.YYYY_MM_DD_HH_MM_SS_NO_SEPARATOR).format(LocalDateTime.now())
                + excelName + ExcelConstants.XLSX;
        // 设置导出响应流规则
        response.setContentType(ExcelConstants.RESPONSE_CONTENT_TYPE);
        response.setHeader("Content-Type",ExcelConstants.RESPONSE_CONTENT_TYPE);
        response.setCharacterEncoding(ExcelConstants.RESPONSE_CHARACTER_ENCODING_UTF8);
        response.addHeader(ExcelConstants.RESPONSE_HEADER_NAME, ExcelConstants.RESPONSE_HEADER_VALUE
                + ExcelUtil.makeDownloadFileName(request, fileName));
        // 将文件输出
        try (OutputStream stream = response.getOutputStream()) {
            workbook.write(stream);
            stream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * 导出excel实例方法
     * </p>
     *
     * @param list      导出的list集合
     * @param clazz     导出的实体类模板class
     * @param sheetName excel工作表标题名称
     */
    private static void export(XSSFWorkbook workbook, List<?> list, Class<?> clazz, String sheetName) throws Exception {
        Sheet sheet = workbook.createSheet(sheetName); // 创建工作表 sheet
        Row titleRow = sheet.createRow(0); // 创建标题行 titleRow
        Field[] fields = clazz.getDeclaredFields(); // 通过反射获取类模板中包含public,protected,private等属性
        Map<String, Method> methods = ExcelUtil.getMethod(clazz, ExcelConstants.METHOD_GET); // 获取类模板中所有属性的 getter 方法

        // 创建标题行
        for (int i = 0; i < fields.length; i++) {
            ExcelProperty annotation = fields[i].getAnnotation(ExcelProperty.class); // 获取每一个属性上的 ExcelProperty 注解
            ExcelUtil.setCellValues(titleRow, i, annotation.name()); // annotation.name() 就是实体类中属性上 ExcelProperty 注解中的 name 属性
        }

        // 创建普通行
        for (int i = 0; i < list.size(); i++) {
            Row sheetRow = sheet.createRow(i + 1);
            Object targetObj = list.get(i); // 获取到集合中的一个对象
            int currentIndex = 0;
            for (Field field : fields) {
                Method method = methods.get(field.getName()); // 根据属性名称获取到属性 get 方法
                Object value = method.invoke(targetObj); // 通过 Method.invoke(类对象) 来获取到 get 方法返回的 value 值
                ExcelProperty annotation = field.getAnnotation(ExcelProperty.class); // 获取每一个属性上的 ExcelProperty 注解

                // ExcelProperty 中 dateFormat 属性不为空表示此属性需要日期格式化
                if (StringUtils.hasLength(annotation.dateFormat())) {
                    SimpleDateFormat sdf = ExcelUtil.switchDateFormatter(annotation.dateFormat());
                    ExcelUtil.setCellValues(sheetRow, currentIndex, sdf.format(value));
                }
                // ExcelProperty 中 replace 属性不为空表示此属性需要格式转换
                else if (annotation.replace().length > 0) {
                    for (String replaceText : annotation.replace()) {
                        String[] keys = replaceText.split("_"); // 注解自定义规则就是根据下划线进行分隔对象
                        if (keys.length > 2) throw new RuntimeException("replace关键字规格不正确");
                        if (keys[0].equals(value.toString())) {
                            ExcelUtil.setCellValues(sheetRow, currentIndex, keys[1]);
                        }
                    }
                }
                // 默认情况下的 ExcelProperty 只有 name,直接进行普通 excel 导出即可
                else {
                    ExcelUtil.setCellValues(sheetRow, currentIndex, value);
                }
                currentIndex += 1;
            }
        }
    }

    /**
     * <p>
     * 判断属性需要格式化的日期规则
     * </p>
     *
     * @param format 日期格式化公式
     * @return 格式化对象 SimpleDateFormat
     */
    private static SimpleDateFormat switchDateFormatter(String format) {
        SimpleDateFormat sdf;
        switch (format) {
            case ExcelConstants.YYYY_MM_DD:
                sdf = new SimpleDateFormat(ExcelConstants.YYYY_MM_DD);
                break;
            case ExcelConstants.YYYY_MM_DD_HH_MM_SS_SSS:
                sdf = new SimpleDateFormat(ExcelConstants.YYYY_MM_DD_HH_MM_SS_SSS);
                break;
            default:
                sdf = new SimpleDateFormat(ExcelConstants.YYYY_MM_DD_HH_MM_SS);
                break;
        }
        return sdf;
    }

    /**
     * <p>
     * 向指定位置的单元格中设置值
     * </p>
     *
     * @param row          第几行
     * @param currentIndex 第几个单元格
     * @param value        添加进单元格的内容
     */
    private static void setCellValues(Row row, Integer currentIndex, Object value) {
        Cell cell = row.createCell(currentIndex);
        // 通过反射判断传入的 value 类型
        switch (value.getClass().getSimpleName()) {
            case "String":
                cell.setCellValue(value.toString());
                break;
            case "Integer":
                cell.setCellValue(Integer.parseInt(value.toString()));
                break;
            case "Float":
                cell.setCellValue(Float.parseFloat(value.toString()));
                break;
            case "Double":
                cell.setCellValue(Double.parseDouble(value.toString()));
                break;
            case "Long":
                cell.setCellValue(Long.parseLong(value.toString()));
                break;
            case "Boolean":
                cell.setCellValue(Boolean.parseBoolean(value.toString()));
                break;
            default:
                cell.setCellValue("");
                break;
        }
    }

    /**
     * <p>
     * 根据 methodKeyWord 来获取指定类模板下的 getter/setter方法
     * 用法例如: Map<String, Method> map = this.getMethod(Student.class, "get");
     * 那么返回值 map 中的值就是 Student 中所有属性的 get方法
     * </p>
     *
     * @param clazz         类模板
     * @param methodKeyWord get就是获取getter方法/set就是获取setter方法
     */
    private static Map<String, Method> getMethod(Class<?> clazz, String methodKeyWord) {
        Map<String, Method> methodMap = new HashMap<String, Method>(16);
        /* 获取类模板所有属性 */
        Field[] fields = clazz.getDeclaredFields();
        for (Field value : fields) {
            /* 获取属性名并组装方法名称 */
            String fieldName = value.getName();
            /*
             * methodKeyWord = 'get'
             * fieldName = 'name'
             * fieldName.substring(0, 1).toUpperCase() = 'N'
             * fieldName.substring(1) = 'ame()'
             * getMethodName = 'getName()'
             * */
            String getMethodName = methodKeyWord +
                    fieldName.substring(0, 1).toUpperCase() +
                    fieldName.substring(1);
            try {
                Method method = clazz.getMethod(getMethodName);
                /*
                 * 存储内容为: id,getId();
                 * name,getName();
                 * */
                methodMap.put(fieldName, method);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                System.err.println("无法获取字段的方法:" + value.getName());
            }
        }
        return methodMap;
    }

    /**
     * <p>
     * 生成下载文件
     * </p>
     * @param request  http请求
     * @param fileName 文件名称
     */
    private static String makeDownloadFileName(HttpServletRequest request, String fileName) {
        String agent = request.getHeader(ExcelConstants.REQUEST_HEADER);
        byte[] bytes = fileName.getBytes(StandardCharsets.UTF_8);
        if (agent.contains(ExcelConstants.REQUEST_MSIE) || agent.contains(ExcelConstants.REQUEST_TRIDENT) || agent.contains(ExcelConstants.REQUEST_EDGE)) {
            return new String(bytes, StandardCharsets.UTF_8);
        } else {
            return new String(bytes, StandardCharsets.UTF_8);
            //return new String(bytes, StandardCharsets.ISO_8859_1);
        }
    }
}
