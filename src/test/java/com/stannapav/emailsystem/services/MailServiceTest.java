package com.stannapav.emailsystem.services;

import com.stannapav.emailsystem.db.entities.User;
import com.stannapav.emailsystem.db.enums.LogType;
import com.stannapav.emailsystem.db.services.LogService;
import com.stannapav.emailsystem.db.services.MailService;
import com.stannapav.emailsystem.exceptions.MailSendingException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {
    @Mock
    private JavaMailSender mailSender;

    @Mock
    private LogService logService;

    @InjectMocks
    private MailService mailService;

    @Test
    public void MailService_sendUserMailAsync_Success(){
        User user = new User();
        user.setUsername("test");
        user.setEmail("test@gmail.com");
        user.setCreatedOn(LocalDateTime.now());

        LogType logType = LogType.CRON;

        ArgumentCaptor<SimpleMailMessage> mailCaptor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        mailService.sendUserMailAsync(user, logType);

        verify(mailSender).send(mailCaptor.capture());
        verify(logService).createLog(user, logType);

        SimpleMailMessage sentMessage = mailCaptor.getValue();

        Assertions.assertThat(sentMessage).isNotNull();
        Assertions.assertThat(sentMessage.getTo()).containsExactly(user.getEmail());
        Assertions.assertThat(sentMessage.getSubject()).isEqualTo("Вітання!");
        Assertions.assertThat(sentMessage.getText())
                .contains(user.getUsername())
                .contains(user.getCreatedOn().toString());
    }

    @Test
    public void MailService_sendUserMailAsync_ThrowException(){
        User user = new User();
        user.setEmail("fail@gmail.com");

        doThrow(new MailSendingException())
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        Assertions.assertThatThrownBy( () -> mailService.sendUserMailAsync(user, LogType.CRON))
                .isInstanceOf(MailSendingException.class)
                .hasMessageContaining("Failed to send email to " + user.getEmail());

        verify(logService, never()).createLog(any(), any());
    }
}
