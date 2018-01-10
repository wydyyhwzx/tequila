package com.tequila.controller;

import com.tequila.common.Md5Util;
import com.tequila.common.StatusCode;
import com.tequila.domain.Result;
import com.tequila.mapper.UserMapper;
import com.tequila.model.UserDO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by wangyudong on 2018/1/9.
 */
@RestController
@RequestMapping("/user")
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);
    private static String uid = "uid";
    private static String loginToken = "login_token";
    private static long expire = 2592000000l;
    private static int expireSecond = 30 * 24 * 60 * 60;

    @Resource
    private UserMapper userMapper;

    @RequestMapping("/register")
    @ResponseBody
    public Result register(@RequestParam String name, @RequestParam String phone, @RequestParam String mail, HttpServletResponse response) {
        if (StringUtils.isBlank(name)) {
            Result result = Result.fail(StatusCode.REGISTER_ERROR);
            result.setDescription("用户名不能为空");
        }
        if (StringUtils.isBlank(phone)) {
            Result result = Result.fail(StatusCode.REGISTER_ERROR);
            result.setDescription("手机号不能为空");
        }
        if (StringUtils.isBlank(mail)) {
            Result result = Result.fail(StatusCode.REGISTER_ERROR);
            result.setDescription("邮箱不能为空");
        }

        UserDO user = new UserDO();
        user.setName(name);
        user.setPhone(phone);
        user.setMail(mail);
        user.setToken(Md5Util.encryptMD5(name, phone));
        user.setTokenExpire(new Date(System.currentTimeMillis() + expire));
        try {
            userMapper.insert(user);
        }catch (Exception e) {
            logger.error("[UserController] register err", e);
            return Result.fail(StatusCode.SYSTEM_ERROR);
        }

        setCookie(response, uid, String.valueOf(user.getId()));
        setCookie(response, loginToken, user.getToken());

        return Result.success();
    }

    @RequestMapping("/validated")
    @ResponseBody
    public Result<String> validated(HttpServletRequest request) {
        int id = 0;
        String token = null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(uid))
                id = Integer.parseInt(cookie.getValue());
            if (cookie.getName().equals(loginToken))
                token = cookie.getValue();
        }

        if (id < 1 || StringUtils.isBlank(token))
            return Result.fail(StatusCode.LOGIN_ERROR);

        try {
            UserDO user = userMapper.findById(id);
            if (null == user || !token.equals(user.getToken()) || new Date().after(user.getTokenExpire()))
                return Result.fail(StatusCode.LOGIN_ERROR);

            return Result.success();
        }catch (Exception e) {
            logger.error("[UserController] validated err", e);
            return Result.fail(StatusCode.SYSTEM_ERROR);
        }
    }

    private void setCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
     //   cookie.setDomain("tequila.com"); todo
        cookie.setPath("/");
        cookie.setMaxAge(expireSecond);
        cookie.setVersion(cookie.getVersion());
        response.addCookie(cookie);
    }
}
