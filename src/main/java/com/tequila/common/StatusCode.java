package com.tequila.common;

/**
 * Created by wangyudong on 2018/1/10.
 */
public enum StatusCode {
    OK(0, "OK", "OK"),
    SYSTEM_ERROR(1, "SYSTEM ERROR", "系统错误，请稍后重试"),
    PARAM_ERROR(2, "PARAM ERROR", "参数错误"),
    LOGIN_ERROR(3, "LOGIN ERROR", "未登录，请先登录"),
    NO_ACCESS_ERROR(4, "NO_ACCESS_ERROR", "没有访问权限"),
    ;

    /**
     *
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
