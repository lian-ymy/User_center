package com.example.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类，通过对成功完成某个功能的业务设置一个状态码，使得前端能够对该项业务进行良好的判断
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    private String description;

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse() {
    }

    public BaseResponse(int code, T data, String message) {
        this(code, data, message, "");
    }

    public BaseResponse(int code, T data) {
        this(code, data, "","");
    }

    /**
     * 定义异常返回响应
     * @param errorCode
     */
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }

    public BaseResponse(ErrorCode errorCode,String description) {
        this(errorCode.getCode(), null, errorCode.getMessage(), description);
    }

    public BaseResponse(ErrorCode errorCode,String message,String description) {
        this(errorCode.getCode(), null, message, description);
    }
}
