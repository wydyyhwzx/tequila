package com.tequila.common;

/**
 * Created by wangyudong on 2018/1/23.
 */
public enum VerifyCodeType {
    register(0, CookieEnum.REGISTER_VERIFY, "注册验证码类型"),
    login(1, CookieEnum.LOGIN_VERIFY, "登录验证码类型"),
    resetPassword(2, CookieEnum.RESET_PASSWORD_VERIFY, "密码重置验证码类型"),
    findPassword(3, CookieEnum.FIND_PASSWORD_VERIFY, "密码找回验证码类型"),
    ;

    private int code;
    private String desc;
    private CookieEnum cookieEnum;

    private VerifyCodeType(int code, CookieEnum cookieEnum, String desc) {
        this.code = code;
        this.cookieEnum = cookieEnum;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public CookieEnum getCookieEnum() {
        return cookieEnum;
    }

    public String getDesc() {
        return desc;
    }
}
