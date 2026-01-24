package com.stannapav.emailsystem.controllers;

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

    @Test
    void LogController_GetAllLogs_ReturnOk() throws Exception {
        UserStatDTO userStat = new UserStatDTO();
        userStat.setUsername("name");

        PageResponse<UserStatDTO> page =
                new PageResponse<>(List.of(userStat), 0, 1, 1, 1);

        when(logService.getUserStats(0, 1)).thenReturn(page);

        ResultActions response = mockMvc.perform(get("/api/logs")
                .param("page", "0")
                .param("size", "1"));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value(userStat.getUsername()));
    }
}
