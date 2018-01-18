package com.tequila.service;

import com.tequila.model.UserDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Created by wangyudong on 2018/1/18.
 */
@Component
public class UserService {
    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    public boolean sendRegisterMail(UserDO user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(from);
        message.setSubject(user.getName() + "欢迎注册，请激活账号");
        message.setText("http://localhost:8088/user/test");

        try {
            mailSender.send(message);
            logger.info("简单邮件已经发送。");
            return true;
        } catch (Exception e) {
            logger.error("发送简单邮件时发生异常！", e);
        }

        return false;
    }
}
