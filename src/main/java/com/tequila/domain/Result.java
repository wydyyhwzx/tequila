package com.tequila.domain;

/**
 * Created by wangyudong on 2018/1/9.
 */
public class Result<T>{
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

    private T result;

    public static <T> Result<T> success(T model) {
        Result result = new Result(0);
        result.setResult(model);
        return result;
    }

    public static <T> Result<T> success() {
        Result result = new Result(0);
        result.setResult(null);
        return result;
    }

    public static <T> Result<T> fail(int code, String message, String description) {
        Result result = new Result(code);
        result.setMessage(message);
        result.setDescription(description);
        return result;
    }

    public Result(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
