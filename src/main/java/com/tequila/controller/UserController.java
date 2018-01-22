package com.tequila.controller;

import com.tequila.common.Constants;
import com.tequila.common.Md5Util;
import com.tequila.common.StatusCode;
import com.tequila.common.VerifyUtil;
import com.tequila.domain.Result;
import com.tequila.mapper.UserMapper;
import com.tequila.model.UserDO;
import com.tequila.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

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

    @Resource
    private UserService userService;
    @Resource
    private UserMapper userMapper;
    @Value("${host}")
    private String host;

    /*
    * 注册接口:/user/register
    * POST方式
    *
    * 参数
    * name:用户名
    * phone:手机号
    * mail:邮箱
    * password:密码
    * confirm:密码重复确认
    * verifyCode:验证码
    *
    * 返回值只包含状态码和错误提示，其中错误码：
    * 0：成功
    * 1：系统错误
    * 2：参数错误，提示信息里有具体原因
    * */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Result register(@RequestParam String name, @RequestParam String phone, @RequestParam String mail,
                           @RequestParam String password, @RequestParam String confirm, @RequestParam String verifyCode,
                           HttpServletRequest request,HttpServletResponse response) throws Exception{
        if (StringUtils.isBlank(name)) {
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("用户名不能为空");
        }
        if (StringUtils.isBlank(phone)) {
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("手机号不能为空");
        }
        if (StringUtils.isBlank(mail)) {
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("邮箱不能为空");
        }
        if (StringUtils.isBlank(password)) {
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("密码不能为空");
        }
        if (StringUtils.isBlank(confirm)) {
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("密码确认不能为空");
        }
        if (!password.equals(confirm)) {
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("密码输入不一致，请重新输入密码");
        }
        if (StringUtils.isBlank(verifyCode)) {
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("验证码不能为空");
        }
        String uuid = null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(Constants.registerVerifyCookie))
                uuid = cookie.getValue();
        }
        if (StringUtils.isBlank(uuid)) {
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("验证码已过期，请刷新验证码");
        }
        if (!VerifyUtil.verifyCodeCheck(uuid, verifyCode)) {
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("验证码不正确，请重新输入");
        }

        UserDO user = new UserDO();
        user.setName(name);
        user.setPhone(phone);
        user.setMail(mail);
        user.setToken(Md5Util.encryptMD5(name, phone));
        user.setTokenExpire(new Date(System.currentTimeMillis() + Constants.loginTokenExpire));
        try {
            userMapper.insert(user);
        }catch (Exception e) {
            logger.error("[UserController] register err", e);
            return Result.fail(StatusCode.SYSTEM_ERROR);
        }

        userService.sendRegisterMail(user);
        setCookie(response, Constants.uidCookie, String.valueOf(user.getId()));
        setCookie(response, Constants.loginTokenCookie, user.getToken());

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
        cookie.setMaxAge(Constants.loginTokenExpireSecond);
        cookie.setVersion(cookie.getVersion());
        response.addCookie(cookie);
    }
}
