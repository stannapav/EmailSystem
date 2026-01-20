package com.stannapav.emailsystem.db.services;

import com.stannapav.emailsystem.db.entities.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendUserMail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Вітання!");
        message.setText(
                "Ім'я користувача: " + user.getUsername() + "\n" +
                "Дата та час створення: " + user.getCreatedOn()
        );

        mailSender.send(message);
    }
}
