package com.stannapav.emailsystem.controllers;

import com.stannapav.emailsystem.db.dtos.CountDTO;
import com.stannapav.emailsystem.db.dtos.PageResponse;
import com.stannapav.emailsystem.db.dtos.UserStatDTO;
import com.stannapav.emailsystem.db.services.LogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LogController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class LogControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LogService logService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Test
    void LogController_GetAllLogs_ReturnOk() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        UserStatDTO userStat = new UserStatDTO();
        userStat.setUsername("user");
        userStat.setEmail("user@gmail.com");
        userStat.setCount(new CountDTO(0L, 1L));
        userStat.setFirst(now);
        userStat.setLast(now);

        PageResponse<UserStatDTO> page =
                new PageResponse<>(List.of(userStat), 0, 1, 1, 1);

        when(logService.getUserStats(0, 1)).thenReturn(page);

        ResultActions response = mockMvc.perform(get("/api/logs")
                .param("page", "0")
                .param("size", "1"));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value(userStat.getUsername()))
                .andExpect(jsonPath("$.content[0].email").value(userStat.getEmail()))
                .andExpect(jsonPath("$.content[0].count.rest").value(userStat.getCount().getRest()))
                .andExpect(jsonPath("$.content[0].count.cron").value(userStat.getCount().getCron()))
                .andExpect(jsonPath("$.content[0].first").value(userStat.getFirst().format(formatter)))
                .andExpect(jsonPath("$.content[0].last").value(userStat.getLast().format(formatter)));
    }
}
