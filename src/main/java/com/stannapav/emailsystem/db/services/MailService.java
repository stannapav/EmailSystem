package com.stannapav.emailsystem.db.services;

import com.stannapav.emailsystem.db.entities.User;
import com.stannapav.emailsystem.db.enums.LogType;
import com.stannapav.emailsystem.exceptions.MailSendingException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final LogService logService;

    @Async("mailExecutor")
    public void sendUserMailAsync(User user, LogType logType) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Вітання!");
            message.setText(
                    "Ім'я користувача: " + user.getUsername() + "\n" +
                            "Дата та час створення: " + user.getCreatedOn()
            );

            mailSender.send(message);
            logService.createLog(user, logType);
        } catch (MailSendingException e) {
            throw new MailSendingException("Failed to send email to " + user.getEmail(), e);
        }
    }
}
