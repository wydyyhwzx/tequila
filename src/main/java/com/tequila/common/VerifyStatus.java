package com.tequila.common;

/**
 * Created by wangyudong on 2018/1/23.
 */
public enum VerifyStatus {
    init(0, "无认证"),
    base(1, "基础信息已认证"),
    idCard(2, "身份信息已认证"),
    pay(3, "缴费已认证")
            ;

    private int code;
    private String desc;

    private VerifyStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
