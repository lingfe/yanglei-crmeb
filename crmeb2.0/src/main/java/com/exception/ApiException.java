package com.exception;

/**
 * Api异常类
 * @author: 零风
 * @CreateDate: 2022/1/10 10:13
 */
public class ApiException extends RuntimeException{

    private ExceptionHandler exceptionHandler;

    public ApiException(ExceptionHandler exceptionHandler) {
        super(exceptionHandler.getMessage());
        this.exceptionHandler = exceptionHandler;
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExceptionHandler getErrorCode() {
        return exceptionHandler;
    }
}
