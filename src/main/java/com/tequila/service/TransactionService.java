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
    public void registerUser(UserDO user) {
        userMapper.insert(user);

        boolean sucess = mailService.sendRegisterMail(user);
        if (!sucess) {
            throw new RuntimeException("sendRegisterMail error");
        }
    }
}
