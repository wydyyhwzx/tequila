package com.tequila.common;

/**
 * Created by wangyudong on 2018/2/8.
 */
public class TequilaException extends RuntimeException{
    /**
     * 返回的错误码
     */
    private int code;
    /**
     * 返回的错误信息, 描述错误的原因
     */
    private String message;
    /**
     * 返回的错误描述, 友好展示给外部用户
     */
    private String description;

    public TequilaException(StatusCode statusCode) {
        this(statusCode.getCode(), statusCode.getMessage(), statusCode.getDescription());
    }

    public TequilaException(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
