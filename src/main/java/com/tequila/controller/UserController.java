package com.tequila.controller;

import com.tequila.common.Constants;
import com.tequila.common.Md5Util;
import com.tequila.common.StatusCode;
import com.tequila.domain.Result;
import com.tequila.mapper.UserMapper;
import com.tequila.model.UserDO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by wangyudong on 2018/1/9.
 */
@RestController
@RequestMapping("/user")
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserMapper userMapper;
    @Value("${host}")
    private String host;

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
        user.setTokenExpire(new Date(System.currentTimeMillis() + Constants.expire));
        try {
            userMapper.insert(user);
        }catch (Exception e) {
            logger.error("[UserController] register err", e);
            return Result.fail(StatusCode.SYSTEM_ERROR);
        }

        setCookie(response, Constants.uid, String.valueOf(user.getId()));
        setCookie(response, Constants.loginToken, user.getToken());

        return Result.success();
    }

    @RequestMapping("/test")
    @ResponseBody
    public Result<String> test(){
        return Result.success("成功");
    }

    private void setCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        if (StringUtils.isNotBlank(host) && !host.equals("localhost"))
            cookie.setDomain(host);
        cookie.setPath("/");
        cookie.setMaxAge(Constants.expireSecond);
        cookie.setVersion(cookie.getVersion());
        response.addCookie(cookie);
    }
}
