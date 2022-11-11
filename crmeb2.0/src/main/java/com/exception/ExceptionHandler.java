package com.exception;

/**
 * Exception-接口定义
 * @author: 零风
 * @CreateDate: 2022/1/10 10:14
 */
public interface ExceptionHandler {

    /**
     * 获取-状态码
     * @return
     */
    long getCode();

    /**
     * 获取-信息通知
     * @return
     */
    String getMessage();
}
