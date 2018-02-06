package com.tequila.common;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyudong on 2018/1/18.
 */
public class Constants {
    public static final int verifySize = 6; //验证码长度
    public static final String rerifyUUIDKey = "uuid"; //uuid 在verify map 中的key
    public static final String rerifyCodeKey = "code"; //code 在verify map 中的key
    public static final String extendActivateCode = "activateCode"; //extend 中激活码key

    public static final String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";
    public static final Charset UTF8 = Charset.forName("UTF-8");

    public static final List<String> MANAGER_PHONES = new ArrayList<>();

    static {
        MANAGER_PHONES.add("15210362506");
    }
}
