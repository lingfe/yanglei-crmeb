package com.zbkj.crmeb.cloudAccount.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class Response<T> {

    /**
     * 返回码
     **/
    private String code;

    /**
     * 返回描述
     **/
    private String message;

    /**
     * 返回码
     **/
    private String request_id;

    /**
     * 返回交易数据
     **/
    private T data;
}
