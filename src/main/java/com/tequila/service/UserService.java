package com.tequila.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tequila.common.*;
import com.tequila.domain.Result;
import com.tequila.mapper.UserMapper;
import com.tequila.model.UserDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${host}")
    private String host;
    @Value("${server.port}")
    private String port;

    public Result<UserDO> register(String name, String phone, String mail, String password) throws Exception{
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
        user.setMemberType(MemberType.free.getCode());
        /*String activateUUID = Md5Util.encryptMD5(name, mail + "activate");
        StringBuilder sb = new StringBuilder("{\"");
        sb.append(Constants.extendActivateCode).append("\":\"").append(activateUUID).append("\"}");*/
        user.setExtend("{}");
        /*StringBuilder html = new StringBuilder("<h1>");
        html.append(user.getName()).append(" 特此谨致问候").append("</h1>");
        html.append("<div><br></div>").append("<div>感谢您的注册，您现在可以激活账号，开始使用。</div>").append("<div><br></div>")
                .append("<div><a href=\"http://").append(host);
        if (port != null && !port.equals("80"))
            html.append(":").append(port);
        html.append("/user/activate/").append(mail).append("/").append(activateUUID)
                .append("\" target=\"_blank\" style=\"color: #007eb9; text-decoration: none; outline: none;\">")
                .append("激活账号 »</a></div>");*/
        transactionService.registerUser(user/*, "Tequila 注册确认", html.toString()*/);

        return Result.success(user);
    }

    /*public String activate(String mail, String activateCode) throws Exception{
        List<UserDO> userDOS = userMapper.listByNameOrPhoneOrMail(null, null, mail);
        if (userDOS == null || userDOS.size() != 1) {
            return "激活链接不正确，请确认！";
        }
        UserDO user = userDOS.get(0);
        ObjectNode extendJson = UserUtil.getExtendJson(user);
        if (!extendJson.has(Constants.extendActivateCode)) {
            return "账号已经被激活过，请在app中登录使用！";
        }
        if (!activateCode.equals(extendJson.get(Constants.extendActivateCode).asText())) {
            return "激活链接不正确，请确认！";
        }

        extendJson.remove(Constants.extendActivateCode);
        UserDO update = new UserDO();
        update.setId(user.getId());
        update.setExtend(extendJson.toString());
        userMapper.update(update);

        return "激活成功，请在app中登录使用！";
    }*/

    public Result<UserDO> login(String mail, String password) throws Exception{
        List<UserDO> userDOS = userMapper.listByNameOrPhoneOrMail(null, null, mail);
        if (userDOS == null || userDOS.size() != 1) {
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("邮箱不存在，请重新输入");
            return result;
        }
        /*UserDO user = userDOS.get(0);
        ObjectNode extendJson = UserUtil.getExtendJson(user);
        if (extendJson.has(Constants.extendActivateCode)) {
            return Result.fail(StatusCode.NO_ACTIVATE_ERROR);
        }*/

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

        user.setToken(update.getToken());
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

    public Result findPassword(String mail) {
        List<UserDO> userDOS = userMapper.listByNameOrPhoneOrMail(null, null, mail);
        if (userDOS == null || userDOS.size() != 1) {
            Result result = Result.fail(StatusCode.PARAM_ERROR);
            result.setDescription("邮箱不存在，请检查后重新输入");
            return result;
        }
        UserDO user = userDOS.get(0);
        String newPassword = VerifyUtil.getRandomString(8);
        UserDO update = new UserDO();
        update.setId(user.getId());
        update.setPassword(newPassword);
        transactionService.findPassword(update, mail, "Tequila 密码找回", "您的新密码为：" + newPassword + "，请尽快修改，以免密码泄漏");
        return Result.success();
    }
}
