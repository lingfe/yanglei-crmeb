package com.zbkj.crmeb.export.utils;

import java.lang.annotation.*;

/**
 * <p>
 * 自定义注解导出Excel操作
 * <span>@Target 表示此注解可以被添加到实体类属性上</span>
 * <span>@Retention 表示在程序运行时, 可以通过反射读取此注解中的信息</span>
 * <span>@Documented 表示在生成JavaDoc时, 可以将此注解显示出来</span>
 * </p>
 *
 * @author bai
 * @date 2021/1/18 17:50
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelProperty {
    /**
     * <p>标记要导出的excel的列名称</p>
     *
     * @return 默认为 null
     */
    String name() default "";

    /**
     * <p>标记导出excel列的日期格式</p>
     *
     * @return 日期格式
     */
    String dateFormat() default "";

    /**
     * 替换类型
     * <p>
     * 使用说明: <span>如果你的实体类对象sex属性或者status属性实际获取到的值并不是0/1,或者ON/OFF那么就不会进行替换</span>
     * </p>
     *
     * <p>
     * 根据下划线分隔,翻译过来的意思就是将0替换成女,将1替换成男
     * replace = {"0_女", "1_男"}
     * private int sex;
     * 根据下划线分隔,翻译过来的意思就是将ON替换成开启,将OFF替换成关闭
     * replace = {"ON_开启", "OFF_关闭"}
     * private String status;
     * </p>
     *
     * @return 替换后的中文
     */
    String[] replace() default {};
}
