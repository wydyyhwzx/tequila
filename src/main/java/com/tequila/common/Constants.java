package com.tequila.common;

/**
 * Created by wangyudong on 2018/1/18.
 */
public class Constants {
    public static String uidCookie = "uid";  //uid cookie名称
    public static String loginTokenCookie = "login_token"; //loginToken cookie名称
    public static long loginTokenExpire = 2592000000l;         // cookie过期时间，ms单位
    public static int loginTokenExpireSecond = 30 * 24 * 60 * 60; // cookie过期时间，s单位

    public static String registerVerifyCookie = "register_uuid"; //注册验证码对应的uuid在cookie中的名称
    public static int verifyExpire = 5 * 60; //验证码对应的uuid在cookie中的过期时间,s单位
    public static int verifySize = 6; //验证码长度
}
