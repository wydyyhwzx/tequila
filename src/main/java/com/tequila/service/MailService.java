package com.tequila.service;

import com.tequila.model.UserDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Created by wangyudong on 2018/1/23.
 */
@Component
public class MailService {
    private static Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    public boolean sendTextMail(String mail, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(mail);
        message.setSubject(subject);
        message.setText(text);

        try {
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            logger.error("发送text邮件时发生异常,subject:" + subject, e);
        }

        return false;
    }

    public boolean sendHtmlMail(String mail, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(mail);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            logger.error("发送html邮件时发生异常,subject:" + subject, e);
        }

        return false;
    }
}
