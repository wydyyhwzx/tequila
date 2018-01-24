package com.tequila.common;

import com.tequila.domain.Result;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangyudong on 2018/1/22.
 */
public class ValidatorUtil {
    /**
     * 正则表达式：验证用户名
     */
    private static final Pattern userNamePattern = Pattern.compile("^[a-zA-Z]\\w{5,14}$");
    /**
     * 正则表达式：验证手机号
     */
    private static final Pattern mobilePattern = Pattern.compile("^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,1,5-9]))\\d{8}$");
    /**
     * 正则表达式：验证邮箱
     */
    private static final Pattern emailPattern = Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
    /**
     * 正则表达式：验证密码
     */
    private static final Pattern passwordPattern = Pattern.compile("^[a-zA-Z0-9]{6,15}$");
    /**
     * 正则表达式：验证验证码
     */
    private static final Pattern verifyCodePattern = Pattern.compile("^[a-zA-Z0-9]{" + Constants.verifySize + "}$");
    /**
     * 正则表达式：验证身份证
     */
    private static final Pattern idCardPattern = Pattern.compile("(^\\d{18}$)|(^\\d{15}$)");
    /**
     * 正则表达式：验证汉字
     */
    private static final Pattern chinesePattern = Pattern.compile("^[\u4e00-\u9fa5]{1,}$");
    /**
     * 正则表达式：验证URL
     */
    private static final Pattern urlPattern = Pattern.compile("http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?");

    public static Result isUserName(String userName) {
        if (StringUtils.isBlank(userName)) {
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("用户名不能为空");
            return result;
        }
        Matcher m = userNamePattern.matcher(userName);
        if (m.matches())
            return null;

        Result result = Result.fail(StatusCode.PARAM_ERROR);
        result.setDescription("用户名为6-15个字母／数字，字母开头，请重新输入");
        return result;
    }

    public static Result isMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("手机号不能为空");
            return result;
        }
        Matcher m = mobilePattern.matcher(mobile);
        if (m.matches())
            return null;

        Result result = Result.fail(StatusCode.PARAM_ERROR);
        result.setDescription("手机号格式不正确，请重新输入");
        return result;
    }

    public static Result isEmail(String email) {
        if (StringUtils.isBlank(email)) {
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("邮箱不能为空");
            return result;
        }
        Matcher m = emailPattern.matcher(email);
        if (m.matches())
            return null;

        Result result = Result.fail(StatusCode.PARAM_ERROR);
        result.setDescription("邮箱格式不正确，请重新输入");
        return result;
    }

    public static Result isPassword(String password, String name) {
        if (StringUtils.isBlank(password)) {
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription(name + "不能为空");
            return result;
        }
        Matcher m = passwordPattern.matcher(password);
        if (m.matches())
            return null;

        Result result = Result.fail(StatusCode.PARAM_ERROR);
        result.setDescription(name + "为6-15个字母／数字组合，请重新输入");
        return result;
    }

    public static Result isVerifyCode(String verifyCode) {
        if (StringUtils.isBlank(verifyCode)) {
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("验证码不能为空");
            return result;
        }
        Matcher m = verifyCodePattern.matcher(verifyCode);
        if (m.matches())
            return null;

        Result result = Result.fail(StatusCode.PARAM_ERROR);
        result.setDescription("验证码为6位字母／数字组合，请重新输入");
        return result;
    }

    public static Result isIdCard(String idCard) {
        if (StringUtils.isBlank(idCard)) {
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("身份证不能为空");
            return result;
        }
        Matcher m = idCardPattern.matcher(idCard);
        if (m.matches())
            return null;

        Result result = Result.fail(StatusCode.PARAM_ERROR);
        result.setDescription("身份证格式不正确，请重新输入");
        return result;
    }

    public static Result isChinese(String chinese, String name) {
        if (StringUtils.isBlank(chinese)) {
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription(name + "不能为空");
            return result;
        }
        Matcher m = chinesePattern.matcher(chinese);
        if (m.matches())
            return null;

        Result result = Result.fail(StatusCode.PARAM_ERROR);
        result.setDescription(name + "不是汉字，请重新输入");
        return result;
    }

    public static Result isUrl(String url, String name) {
        if (StringUtils.isBlank(url)) {
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription(name + "不能为空");
            return result;
        }
        Matcher m = urlPattern.matcher(url);
        if (m.matches())
            return null;

        Result result = Result.fail(StatusCode.PARAM_ERROR);
        result.setDescription(name + "不是url，请重新输入");
        return result;
    }
}
