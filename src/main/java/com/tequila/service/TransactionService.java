package com.tequila.service;

import com.tequila.mapper.UserMapper;
import com.tequila.model.UserDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by wangyudong on 2018/1/23.
 */
@Component
public class TransactionService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailService mailService;

    @Transactional(timeout = 30, rollbackFor = Throwable.class)
    public void registerUser(UserDO user/*, String subject, String html*/) {
        userMapper.insert(user);

        /*boolean success = mailService.sendHtmlMail(user.getMail(), subject, html);
        if (!success) {
            throw new RuntimeException("send registerMail error");
        }*/
    }

    @Transactional(timeout = 30, rollbackFor = Throwable.class)
    public void findPassword(UserDO user, String mail, String subject, String text) {
        userMapper.update(user);

        boolean success = mailService.sendTextMail(mail, subject, text);
        if (!success) {
            throw new RuntimeException("send findPassword error");
        }
    }
}
