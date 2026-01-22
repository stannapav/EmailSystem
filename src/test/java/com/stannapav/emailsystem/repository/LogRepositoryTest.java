package com.stannapav.emailsystem.repository;

import com.stannapav.emailsystem.db.entities.Log;
import com.stannapav.emailsystem.db.entities.User;
import com.stannapav.emailsystem.db.enums.LogType;
import com.stannapav.emailsystem.db.repositories.LogRepository;
import com.stannapav.emailsystem.db.repositories.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

@DataJpaTest
public class LogRepositoryTest {
    @Autowired
    private LogRepository logRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void LogRepository_Save_ReturnSavedLog(){
        User user = new User();
        user.setUsername("user");
        user.setEmail("user@gmail.com");
        userRepository.save(user);

        Log log = new Log();
        log.setUser(user);
        log.setType(LogType.REST);
        Log savedLog = logRepository.save(log);

        Assertions.assertThat(savedLog).isNotNull();
        Assertions.assertThat(savedLog).isEqualTo(log);
    }

    @Test
    public void LogRepository_FindAll_ReturnAllLogsList(){
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@gmail.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@gmail.com");
        userRepository.save(user2);

        Log log1 = new Log();
        log1.setUser(user1);
        log1.setType(LogType.REST);
        Log savedLog1 = logRepository.save(log1);

        Log log2 = new Log();
        log2.setUser(user2);
        log2.setType(LogType.CRON);
        Log savedLog2 = logRepository.save(log2);

        List<Log> logs = logRepository.findAll();

        Assertions.assertThat(logs).isNotNull();
        Assertions.assertThat(logs).isEqualTo(List.of(savedLog1, savedLog2));
    }
}
