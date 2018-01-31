package com.tequila.common;

/**
 * Created by wangyudong on 2018/1/23.
 */
public enum MemberType {
    free(0, "注册会员"),
    normal(1, "普通会员"),
    verify(2, "vip会员"),
    ;

    private int code;
    private String desc;

    private MemberType(int code, String desc) {
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
