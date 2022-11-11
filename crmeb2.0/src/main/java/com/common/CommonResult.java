package com.common;

import com.exception.ExceptionCodeEnum;
import com.exception.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 公共返回封装类
 * @author: 零风
 * @CreateDate: 2021/12/24 14:35
 */
public class CommonResult<T> {

    private long code;          //状态码
    private String message;     //消息通知
    private T data;             //数据

    /** 构造函数 */
    protected CommonResult() {}
    protected CommonResult(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果，返回异常通知
     */
    public static <T> CommonResult<T> success() {
        return new CommonResult<T>(ExceptionCodeEnum.SUCCESS.getCode(), ExceptionCodeEnum.SUCCESS.getMessage(), null);
    }

    /**
     * 成功返回结果，返回手动通知
     */
    public static <T> CommonResult<T> success(String message) {
        return new CommonResult<T>(ExceptionCodeEnum.SUCCESS.getCode(), message, null);
    }

    /**
     * 成功返回结果，返回数据
     * @param data 获取的数据
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<T>(ExceptionCodeEnum.SUCCESS.getCode(), ExceptionCodeEnum.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回结果，返回数据Map类型
     * @param record 获取的数据
     */
    public static CommonResult<Map<String, Object>> success(MyRecord record) {
        return new CommonResult<>(ExceptionCodeEnum.SUCCESS.getCode(), ExceptionCodeEnum.SUCCESS.getMessage(), record.getColumns());
    }

    /**
     * 成功返回结果，返回数据List<Map>集合类型
     * @param recordList 获取的数据
     */
    public static CommonResult<List<Map<String, Object>>> success(List<MyRecord> recordList) {
        List<Map<String, Object>> list = new ArrayList<>();
        recordList.forEach(i -> {
             list.add(i.getColumns());
        });
        return new CommonResult<>(ExceptionCodeEnum.SUCCESS.getCode(), ExceptionCodeEnum.SUCCESS.getMessage(), list);
    }

    /**
     * 成功返回结果,返回数据和手动消息通知
     * @param data 获取的数据
     * @param  message 提示信息
     */
    public static <T> CommonResult<T> success(T data, String message) {
        return new CommonResult<T>(ExceptionCodeEnum.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回结果，返回自定义异常通知
     * @param errorCode 错误码
     */
    public static <T> CommonResult<T> failed(ExceptionHandler errorCode) {
        System.out.println("errorCode1:" + errorCode);
        return new CommonResult<T>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 失败返回结果,返回自定义异常状态码和自定义消息通知
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public static <T> CommonResult<T> failed(ExceptionHandler errorCode,String message) {
        System.out.println("errorCode2:" + errorCode);
        return new CommonResult<T>(errorCode.getCode(), message, null);
    }

    /**
     * 失败返回结果，返回自定义信息通知
     * @param message 提示信息
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<T>(ExceptionCodeEnum.FAILED.getCode(), message, null);
    }

    /**
     * 失败返回结果，返回自定义失败异常
     */
    public static <T> CommonResult<T> failed() {
        return failed(ExceptionCodeEnum.FAILED);
    }

    /**
     * 参数验证失败返回结果，返回自定义参数校验异常
     */
    public static <T> CommonResult<T> validateFailed() {
        return failed(ExceptionCodeEnum.VALIDATE_FAILED);
    }

    /**
     * 参数验证失败返回结果
     * @param message 提示信息
     */
    public static <T> CommonResult<T> validateFailed(String message) {
        return new CommonResult<T>(ExceptionCodeEnum.VALIDATE_FAILED.getCode(), message, null);
    }

    /**
     * 未登录返回结果
     */
    public static <T> CommonResult<T> unauthorized(T data) {
        return new CommonResult<T>(ExceptionCodeEnum.UNAUTHORIZED.getCode(), ExceptionCodeEnum.UNAUTHORIZED.getMessage(), data);
    }

    /**
     * 未登录返回结果
     */
    public static <T> CommonResult<T> unauthorized() {
        return new CommonResult<T>(ExceptionCodeEnum.UNAUTHORIZED.getCode(), ExceptionCodeEnum.UNAUTHORIZED.getMessage(), null);
    }

    /**
     * 没有权限查看
     */
    public static <T> CommonResult<T> forbidden() {
        return new CommonResult<T>(ExceptionCodeEnum.FORBIDDEN.getCode(), ExceptionCodeEnum.FORBIDDEN.getMessage(), null);
    }

    /**
     * 未授权返回结果
     */
    public static <T> CommonResult<T> forbidden(T data) {
        return new CommonResult<T>(ExceptionCodeEnum.FORBIDDEN.getCode(), ExceptionCodeEnum.FORBIDDEN.getMessage(), data);
    }

    public long getCode() {
        return code;
    }
    public void setCode(long code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
}
