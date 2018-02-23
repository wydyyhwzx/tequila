package com.tequila.controller;

import com.tequila.common.*;
import com.tequila.domain.Result;
import com.tequila.model.UserDO;
import com.tequila.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by wangyudong on 2018/1/9.
 */
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Value("${host}")
    private String host;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Result<UserDO> register(@RequestParam String name, @RequestParam String phone, @RequestParam String mail,
                           @RequestParam String password, @RequestParam String confirm, @RequestParam String verifyCode,
                           HttpServletRequest request,HttpServletResponse response) throws Exception{
        Result result = ValidatorUtil.isUserName(name);
        if (null != result) {
            return result;
        }
        result = ValidatorUtil.isMobile(phone);
        if (null != result) {
            return result;
        }
        result = ValidatorUtil.isEmail(mail);
        if (null != result) {
            return result;
        }
        result = ValidatorUtil.isPassword(password, "密码");
        if (null != result) {
            return result;
        }
        if (StringUtils.isBlank(confirm)) {
            result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("密码确认不能为空");
            return result;
        }
        if (!password.equals(confirm)) {
            result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("密码输入不一致，请重新输入密码");
            return result;
        }
        result = VerifyUtil.verifyCodeCheck(request, CookieEnum.REGISTER_VERIFY, verifyCode);
        if (null != result) {
            return result;
        }

        Result<UserDO> userDOResult = userService.register(name, phone, mail, password);
        if (userDOResult.getCode() != 0) {
            result = Result.fail(userDOResult.getCode(), userDOResult.getMessage(), userDOResult.getDescription());
            return result;
        }

        CookieUtil.setCookie(response, CookieEnum.UID, String.valueOf(userDOResult.getResult().getId()), host, "/");
        CookieUtil.setCookie(response, CookieEnum.LOGIN_TOKEN, userDOResult.getResult().getToken(), host, "/");
        CookieUtil.deleteCookie(response, CookieEnum.REGISTER_VERIFY, host, "/");

        return Result.success(userDOResult.getResult());
    }

    /*@RequestMapping(value = "/activate/{mail}/{activateCode}", method = RequestMethod.GET)
    @ResponseBody
    public String activate(@PathVariable String mail, @PathVariable String activateCode) throws Exception{
        Result result = ValidatorUtil.isEmail(mail);
        if (null != result) {
            return "激活链接不正确，请确认！";
        }
        if (StringUtils.isBlank(activateCode)) {
            return "激活链接不正确，请确认！";
        }

        return userService.activate(mail, activateCode);
    }*/


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Result<UserDO> login(@RequestParam String phone, @RequestParam String password, @RequestParam String verifyCode,
                           HttpServletRequest request,HttpServletResponse response) throws Exception {
        Result result = ValidatorUtil.isMobile(phone);
        if (null != result) {
            return result;
        }
        result = ValidatorUtil.isPassword(password, "密码");
        if (null != result) {
            return result;
        }
        result = VerifyUtil.verifyCodeCheck(request, CookieEnum.LOGIN_VERIFY, verifyCode);
        if (null != result) {
            return result;
        }

        Result<UserDO> userDOResult = userService.login(phone, password);
        if (userDOResult.getCode() != 0) {
            result = Result.fail(userDOResult.getCode(), userDOResult.getMessage(), userDOResult.getDescription());
            return result;
        }

        CookieUtil.setCookie(response, CookieEnum.UID, String.valueOf(userDOResult.getResult().getId()), host, "/");
        CookieUtil.setCookie(response, CookieEnum.LOGIN_TOKEN, userDOResult.getResult().getToken(), host, "/");
        CookieUtil.deleteCookie(response, CookieEnum.LOGIN_VERIFY, host, "/");

        return Result.success(userDOResult.getResult());
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    public Result logout(HttpServletResponse response) throws Exception {
        CookieUtil.deleteCookie(response, CookieEnum.UID, host, "/");
        CookieUtil.deleteCookie(response, CookieEnum.LOGIN_TOKEN, host, "/");

        return Result.success();
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    @ResponseBody
    public Result resetPassword(@RequestParam String password, @RequestParam String newPassword,
                                @RequestParam String confirm, @RequestParam String verifyCode,
                           HttpServletRequest request,HttpServletResponse response) throws Exception{
        Result result = ValidatorUtil.isPassword(password, "原密码");
        if (null != result) {
            return result;
        }
        result = ValidatorUtil.isPassword(newPassword, "新密码");
        if (null != result) {
            return result;
        }
        if (StringUtils.isBlank(confirm)) {
            result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("密码确认不能为空");
            return result;
        }
        if (!newPassword.equals(confirm)) {
            result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("密码输入不一致，请重新输入密码");
            return result;
        }
        result = VerifyUtil.verifyCodeCheck(request, CookieEnum.RESET_PASSWORD_VERIFY, verifyCode);
        if (null != result) {
            return result;
        }

        Result resetResult = userService.resetPassword(UserUtil.getUser().getPhone(), password, newPassword);
        if (resetResult.getCode() != 0) {
            result = Result.fail(resetResult.getCode(), resetResult.getMessage(), resetResult.getDescription());
            return result;
        }

        CookieUtil.deleteCookie(response, CookieEnum.RESET_PASSWORD_VERIFY, host, "/");

        return Result.success();
    }

    @RequestMapping(value = "/findPassword", method = RequestMethod.POST)
    @ResponseBody
    public Result findPassword(@RequestParam String mail, @RequestParam String verifyCode, HttpServletRequest request,HttpServletResponse response) throws Exception{
        Result result = ValidatorUtil.isEmail(mail);
        if (null != result) {
            return result;
        }
        result = VerifyUtil.verifyCodeCheck(request, CookieEnum.FIND_PASSWORD_VERIFY, verifyCode);
        if (null != result) {
            return result;
        }

        Result resetResult = userService.findPassword(mail);
        if (resetResult.getCode() != 0) {
            result = Result.fail(resetResult.getCode(), resetResult.getMessage(), resetResult.getDescription());
            return result;
        }

        CookieUtil.deleteCookie(response, CookieEnum.FIND_PASSWORD_VERIFY, host, "/");

        return Result.success();
    }

    @RequestMapping(value ="/getUserInfo", method = RequestMethod.GET)
    @ResponseBody
    public Result<UserDO> getUserInfo() throws Exception{
        return Result.success(UserUtil.getUser());
    }

    @RequestMapping(value = "/getVerifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result getVerifyCode(@RequestParam int type, @RequestParam(required = false, defaultValue = "160") int width, @RequestParam(required = false, defaultValue = "40") int height,
                                HttpServletResponse response) throws Exception{
        for (VerifyCodeType codeType : VerifyCodeType.values()) {
            if (type == codeType.getCode()) {
                Map<String,String> codeMap = VerifyUtil.createVerifyCode();
                CookieUtil.setCookie(response, codeType.getCookieEnum(), codeMap.get(Constants.rerifyUUIDKey), host, "/");
                // 设置响应的类型格式为图片格式
                response.setContentType("image/jpeg");
                //禁止图像缓存。
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expires", 0);
                VerifyUtil.writeCode(response.getOutputStream(), codeMap.get(Constants.rerifyCodeKey), width, height);
                return null;
            }
        }

        Result result = Result.fail(StatusCode.PARAM_ERROR);
        result.setDescription("验证码类型不对");
        return result;
    }
}
