package com.stannapav.emailsystem.services;

import com.stannapav.emailsystem.db.dtos.PageResponse;
import com.stannapav.emailsystem.db.dtos.UserStatDTO;
import com.stannapav.emailsystem.db.dtos.UserStatProjection;
import com.stannapav.emailsystem.db.repositories.LogRepository;
import com.stannapav.emailsystem.db.services.LogService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LogServiceTest {
    @Mock
    private LogRepository logRepository;

    @InjectMocks
    private LogService logService;

    @Test
    public void LogService_GetUserStat_ReturnResponsePage() {
        LocalDateTime now = LocalDateTime.now();

        UserStatProjection projection1 = mock(UserStatProjection.class);
        when(projection1.getUsername()).thenReturn("user1");
        when(projection1.getEmail()).thenReturn("user1@test.com");
        when(projection1.getRest()).thenReturn(0L);
        when(projection1.getCron()).thenReturn(1L);
        when(projection1.getFirst()).thenReturn(now);
        when(projection1.getLast()).thenReturn(now);

        UserStatProjection projection2 = mock(UserStatProjection.class);
        when(projection2.getUsername()).thenReturn("user2");
        when(projection2.getEmail()).thenReturn("user2@test.com");
        when(projection2.getRest()).thenReturn(1L);
        when(projection2.getCron()).thenReturn(0L);
        when(projection2.getFirst()).thenReturn(now);
        when(projection2.getLast()).thenReturn(now);

        Page<UserStatProjection> statsPage = new PageImpl<>(
                List.of(projection1, projection2),
                PageRequest.of(0,2),
                2
        );

        when(logRepository.getUserStats(any(Pageable.class)))
                .thenReturn(statsPage);

        PageResponse<UserStatDTO> response = logService.getUserStats(0,2);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getContent()).hasSize(2);

        Assertions.assertThat(response.getContent().get(0).getCount().getCron()).isEqualTo(1);
        Assertions.assertThat(response.getContent().get(1).getCount().getRest()).isEqualTo(1);

        verify(logRepository).getUserStats(any(Pageable.class));
    }
}
