package com.uoh.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class EmailManager {

    public enum CodeType { REGISTER, RESET }

    @Value("${spring.mail.username}")
    private String senderEmail;

    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;

    private static final String REGISTER_PREFIX = "email:register:";
    private static final String RESET_PREFIX = "email:reset:";

    public EmailManager(StringRedisTemplate redisTemplate, JavaMailSender mailSender) {
        this.redisTemplate = redisTemplate;
        this.mailSender = mailSender;
    }

    public void sendCode(String email, CodeType type) {
        String code = String.valueOf((int)((Math.random() * 9 + 1) * 100000));
        String key = getKey(email, type);
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
        log.info("发送{}验证码邮箱: {}, 验证码: {}", type, email, code);
        sendEmail(email, code, type);
    }

    public boolean checkCode(String email, String code, CodeType type) {
        String cacheCode = redisTemplate.opsForValue().get(getKey(email, type));
        return cacheCode != null && cacheCode.equals(code);
    }

    public void deleteCode(String email, CodeType type) {
        redisTemplate.delete(getKey(email, type));
        log.info("{}验证码已删除，邮箱: {}", type, email);
    }

    private String getKey(String email, CodeType type) {
        return type == CodeType.REGISTER ? REGISTER_PREFIX + email : RESET_PREFIX + email;
    }

    private void sendEmail(String to, String code, CodeType type) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(to);
        String subject = type == CodeType.REGISTER ? "注册验证码" : "找回密码验证码";
        message.setSubject(subject);
        message.setText("您的验证码是：" + code + "，5分钟内有效");
        mailSender.send(message);
    }
}
