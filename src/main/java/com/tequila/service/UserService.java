package com.tequila.service;

import com.tequila.common.*;
import com.tequila.domain.Result;
import com.tequila.mapper.UserMapper;
import com.tequila.model.UserDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by wangyudong on 2018/1/18.
 */
@Component
public class UserService {
    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TransactionService transactionService;

    public Result<UserDO> register(String name, String phone, String mail, String password) {
        List<UserDO> userDOS = userMapper.listByNameOrPhoneOrMail(name, phone, mail);
        if (userDOS != null && userDOS.size() > 0) {
            for (UserDO userDO : userDOS) {
                if (userDO.getName().equals(name)) {
                    Result result = Result.fail(StatusCode.PARAM_ERROR);
                    result.setDescription("用户名已存在");
                    return result;
                }
                if (userDO.getPhone().equals(phone)) {
                    Result result = Result.fail(StatusCode.PARAM_ERROR);
                    result.setDescription("手机号已存在");
                    return result;
                }
                if (userDO.getMail().equals(mail)) {
                    Result result = Result.fail(StatusCode.PARAM_ERROR);
                    result.setDescription("邮箱已存在");
                    return result;
                }
            }
        }

        UserDO user = new UserDO();
        user.setName(name);
        user.setPhone(phone);
        user.setMail(mail);
        user.setPassword(password);
        user.setToken(Md5Util.encryptMD5(mail, phone));
        user.setTokenExpire(new Date(System.currentTimeMillis() + CookieEnum.LOGIN_TOKEN.getExpireLong()));
        user.setVerifyStatus(VerifyStatus.init.getCode());
        user.setMemberType(MemberType.normal.getCode());
        transactionService.registerUser(user, user.getName() + "，欢迎注册，请激活账号", "http://localhost:8088/user/test");

        return Result.success(user);
    }

    public Result<UserDO> login(String mail, String password) {
        UserDO user = userMapper.findByMailAndPassWord(mail, password);
        if (user == null){
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("密码不正确，请重新输入");
            return result;
        }

        UserDO update = new UserDO();
        update.setId(user.getId());
        update.setToken(Md5Util.encryptMD5(mail, user.getPhone()));
        update.setTokenExpire(new Date(System.currentTimeMillis() + CookieEnum.LOGIN_TOKEN.getExpireLong()));
        userMapper.update(update);

        return Result.success(user);
    }

    public Result resetPassword(String mail, String password, String newPassword) {
        UserDO user = userMapper.findByMailAndPassWord(mail, password);
        if (user == null){
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("原密码不正确，请重新输入");
            return result;
        }

        UserDO update = new UserDO();
        update.setId(user.getId());
        update.setPassword(newPassword);
        userMapper.update(update);

        return Result.success(user);
    }

    public Result findPassword(UserDO user) {
        String newPassword = VerifyUtil.getRandomString(8);
        UserDO update = new UserDO();
        update.setId(user.getId());
        update.setPassword(newPassword);
        transactionService.findPassword(update, user.getMail(), user.getName() + "，密码找回", "您的新密码为：" + newPassword + "，请尽快修改，以免密码泄漏");
        return Result.success();
    }
}
