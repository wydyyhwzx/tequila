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
        transactionService.registerUser(user);

        return Result.success(user);
    }
}
