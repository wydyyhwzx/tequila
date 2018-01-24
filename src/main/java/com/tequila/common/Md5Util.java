package com.tequila.common;

import java.security.MessageDigest;
import java.util.Random;

/**
 * Created by wangyudong on 2018/1/10.
 */
public class Md5Util {
    private static String utfEncoding = "UTF-8";
    private static Random random = new Random();

    public static String encryptMD5(String data, String secret) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(data.getBytes(utfEncoding));

            final byte[] dataDigest = md5.digest();

            md5.reset();
            md5.update((byte2hex(dataDigest) + secret + random.nextInt(1000)).getBytes(utfEncoding));

            final byte[] allDigest = md5.digest();
            return byte2hex(allDigest);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            char[] dataChares = data.toCharArray();
            char[] secretChares = secret.toCharArray();
            int length = dataChares.length > secretChares.length ? secretChares.length : dataChares.length;
            boolean turns = false;
            for (int i = 0; i < length; i++) {
                turns = !turns;
                if (turns)
                    sb.append(dataChares[i]).append(secretChares[i]);
                else
                    sb.append(secretChares[i]).append(dataChares[i]);
            }
            return sb.toString().toUpperCase();
        }
    }

    public static String byte2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sb.append("0");
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }
}
