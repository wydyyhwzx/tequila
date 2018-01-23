package com.tequila.controller;

import com.tequila.common.*;
import com.tequila.domain.Result;
import com.tequila.model.UserDO;
import com.tequila.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangyudong on 2018/1/9.
 */
@RestController
@RequestMapping("/user")
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserService userService;
    @Value("${host}")
    private String host;

    @RequestMapping(value = "/register"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public Result register(@RequestParam String name, @RequestParam String phone, @RequestParam String mail,
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
        result = ValidatorUtil.isPassword(password);
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
        result = ValidatorUtil.isVerifyCode(verifyCode);
        if (null != result) {
            return result;
        }
        String verifyCookie = CookieUtil.getValue(request, CookieEnum.REGISTER_VERIFY);
        if (StringUtils.isBlank(verifyCookie)) {
            result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("验证码已过期，请刷新验证码");
            return result;
        }
        if (!VerifyUtil.verifyCodeCheck(verifyCookie, verifyCode)) {
            result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("验证码不正确，请重新输入");
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

        return Result.success();
    }

    @RequestMapping(value = "/getVerifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map<String,String>> getVerifyCode(@RequestParam int type, HttpServletResponse response) throws Exception{
        Map<String,String> codeMap = VerifyUtil.createVerifyCode();
        if (type == Constants.verifyRegisterType) {
            CookieUtil.setCookie(response, CookieEnum.REGISTER_VERIFY, codeMap.get(Constants.rerifyUUIDKey), host, "/");
            Map<String,String> result = new HashMap<>();
            result.put(Constants.rerifyCodeKey, codeMap.get(Constants.rerifyCodeKey));
            return Result.success(result);
        }

        Result result = Result.fail(StatusCode.PARAM_ERROR);
        result.setDescription("验证码类型不对");
        return result;
    }
}
