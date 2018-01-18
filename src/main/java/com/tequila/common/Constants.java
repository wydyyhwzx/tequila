package com.tequila.common;

/**
 * Created by wangyudong on 2018/1/18.
 */
public class Constants {
    public static String uid = "uid";  //uid cookie名称
    public static String loginToken = "login_token"; //loginToken cookie名称
    public static long expire = 2592000000l;         // cookie过期时间，ms单位
    public static int expireSecond = 30 * 24 * 60 * 60; // cookie过期时间，s单位
}
