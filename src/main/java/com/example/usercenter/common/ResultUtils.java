package com.example.usercenter.common;

/**
 * 定义返回成功或者失败的返回类
 */
public class ResultUtils {
    /**
     * 业务执行成功返回响应
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败返回响应
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    public static BaseResponse error(ErrorCode errorCode, String description) {
        return new BaseResponse<>(errorCode, description);
    }

    public static BaseResponse error(ErrorCode errorCode, String message, String description) {
        return new BaseResponse<>(errorCode, message, description);
    }

    public static BaseResponse error(int errorCode, String message, String description) {
        return new BaseResponse<>(errorCode,null, message, description);
    }
}
