package com.zbkj.crmeb.export.utils;

/**
 * <p>
 * 自定义Excel中常用常量类
 * </p>
 *
 * @author bai
 * @date 2021/1/18 17:50
 */
public interface ExcelConstants {
    /**
     * <span>常用日期格式</span>
     */
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_CHINESE = "yyyy年MM月dd日";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String YYYY_MM_DD_HH_MM_CHINESE = "yyyy年MM月dd日 HH时mm分";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SS__CHINESE = "yyyy年MM月dd日 HH时mm分ss秒";
    public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String YYYY_MM_DD_HH_MM_SS_SSS__CHINESE = "yyyy年MM月dd日 HH时mm分ss秒SSS毫秒";
    public static final String YYYY_MM_DD_HH_MM_NO_SEPARATOR = "yyyyMMddHHmm";
    public static final String YYYY_MM_DD_HH_MM_SS_NO_SEPARATOR = "yyyyMMddHHmmss";

    /**
     * <span>getter/setter方法</span>
     */
    public static final String METHOD_GET = "get";
    public static final String METHOD_SET = "set";

    /**
     * <span>excel导出的版本后缀 03 xls / 07 xlsx</span>
     */
    public static final String XLS = ".xls";
    public static final String XLSX = ".xlsx";

    /**
     * <span>通过网络请求 http 导出 excel 时 response 响应信息的一些设置规则</span>
     */
    public static final String RESPONSE_CONTENT_TYPE = "application/octet-stream";
    public static final String RESPONSE_CHARACTER_ENCODING_UTF8 = "utf-8";
    public static final String RESPONSE_HEADER_NAME = "Content-disposition";
    public static final String RESPONSE_HEADER_VALUE = "attachment; filename=";

    /**
     * <span>通过网络请求 http 导出 excel 时 request 请求信息的一些设置规则</span>
     */
    public static final String REQUEST_HEADER = "User-Agent";
    public static final String REQUEST_MSIE = "MSIE";
    public static final String REQUEST_TRIDENT = "Trident";
    public static final String REQUEST_EDGE = "Edge";
}
