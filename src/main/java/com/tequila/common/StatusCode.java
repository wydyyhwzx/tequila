package com.tequila.common;

/**
 * Created by wangyudong on 2018/1/10.
 */
public enum StatusCode {
    OK(0, "OK", "OK"),
    SYSTEM_ERROR(1, "SYSTEM_ERROR", "系统错误，请稍后重试"),
    LOGIN_ERROR(2, "LOGIN ERROR", "未登录，请先登录"),
    REGISTER_ERROR(3, "REGISTER PARAM ERROR", "REGISTER PARAM ERROR"),
    ;

    /**
     * 返回的状态码,0为成功，其他为失败
     */
    int code;
    /**
     * 状态消息,开发人员使用
     */
    String message;
    /**
     * 描述信息, 展示给外部用户
     */
    String description;

    StatusCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
