package com.stannapav.emailsystem.services;

import com.stannapav.emailsystem.db.dtos.PageResponse;
import com.stannapav.emailsystem.db.dtos.UserStatDTO;
import com.stannapav.emailsystem.db.entities.Log;
import com.stannapav.emailsystem.db.entities.User;
import com.stannapav.emailsystem.db.enums.LogType;
import com.stannapav.emailsystem.db.repositories.LogRepository;
import com.stannapav.emailsystem.db.services.LogService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LogServiceTest {
    @Mock
    private LogRepository logRepository;

    @InjectMocks
    private LogService logService;

    @Test
    public void LogService_GetUserStat_ReturnResponsePage() {
        User user1 = new User();
        user1.setId(1);

        User user2 = new User();
        user2.setId(2);

        Log log1 = new Log();
        log1.setUser(user1);
        log1.setType(LogType.CRON);
        log1.setCreatedOn(LocalDateTime.now());

        Log log2 = new Log();
        log2.setUser(user2);
        log2.setType(LogType.REST);
        log2.setCreatedOn(LocalDateTime.now());

        List<Log> logs = List.of(log1, log2);

        List<UserStatDTO> userStats = new ArrayList<>();
        userStats.add(new UserStatDTO(null, null, 0, 1, log1.getCreatedOn(), log1.getCreatedOn()));
        userStats.add(new UserStatDTO(null, null, 1, 0, log2.getCreatedOn(), log2.getCreatedOn()));

        when(logRepository.findAll()).thenReturn(logs);

        PageResponse<UserStatDTO> response = logService.getUserStats(0, 2);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getContent().get(0).getCronCount()).isEqualTo(userStats.get(0).getCronCount());
        Assertions.assertThat(response.getContent().get(1).getRestCount()).isEqualTo(userStats.get(1).getRestCount());

        verify(logRepository).findAll();
    }
}
