package com.victorto.auth.service;

// package com.example.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    
    @Value("${app.base-url}")
    private String baseUrl;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        String resetUrl = baseUrl + "/api/v1/password/reset?token=" + token;
        String subject = "Password Reset Request";
        String text = "To reset your password, click the link below:\n" + resetUrl + 
                     "\n\nIf you didn't request this, please ignore this email.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        
        mailSender.send(message);
    }
}
