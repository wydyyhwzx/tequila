package com.tequila.common;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wangyudong on 2018/1/22.
 */
public class CookieUtil {
    public static void setCookie(HttpServletResponse response, CookieEnum cookieEnum, String value, String host, String path) {
        Cookie cookie = new Cookie(cookieEnum.getKey(), value);
        cookie.setHttpOnly(true);
        if (StringUtils.isNotBlank(host) && !host.equals("localhost"))
            cookie.setDomain(host);
        cookie.setPath(path);
        cookie.setMaxAge(cookieEnum.getExpire());
        cookie.setVersion(cookie.getVersion());
        response.addCookie(cookie);
    }

    public static String getValue(HttpServletRequest request, CookieEnum cookieEnum) {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(cookieEnum.getKey()))
                return cookie.getValue();
        }

        return null;
    }
}
