package com.tequila.common;

/**
 * Created by wangyudong on 2018/1/22.
 */
public enum CookieEnum {
    UID("uid", 30 * 24 * 60 * 60, 2592000000L),
    LOGIN_TOKEN("login_token", 30 * 24 * 60 * 60, 2592000000L),
    REGISTER_VERIFY("register_code", 5 * 60, 300L);

    private String key;
    private int expire;
    private long expireLong;

    private CookieEnum(String key, int expire, long expireLong) {
        this.key = key;
        this.expire = expire;
        this.expireLong = expireLong;
    }

    public String getKey() {
        return key;
    }

    public int getExpire() {
        return expire;
    }

    public long getExpireLong() {
        return expireLong;
    }
}
