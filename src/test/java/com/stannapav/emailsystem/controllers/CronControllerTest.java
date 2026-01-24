package com.stannapav.emailsystem.controllers;

import com.stannapav.emailsystem.db.dtos.CronDTO;
import com.stannapav.emailsystem.db.entities.CronJob;
import com.stannapav.emailsystem.db.services.CronService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CronController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class CronControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CronService cronService;

    @Autowired
    private ObjectMapper objectMapper;

    private CronJob cronJob;

    private CronDTO cronDTO;

    @BeforeEach
    public void init() {
        cronJob = new CronJob();
        cronJob.setId(1);
        cronJob.setExpression("0 2 * * * *");

        cronDTO = new CronDTO();
        cronDTO.setExpression("0 2 * * * *");
    }

    @Test
    public void CronController_CreateCron_ReturnCreated() throws Exception {
        when(cronService.createCronJob(any(CronDTO.class))).thenReturn(cronJob);

        ResultActions response = mockMvc.perform(post("/api/cron-jobs/addCronJob")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cronDTO)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.expression").value(cronDTO.getExpression()));
    }

    @Test
    public void CronController_UpdateCron_ReturnOk() throws Exception {
        when(cronService.updateCronJob(eq(1),any(CronDTO.class))).thenReturn(cronJob);

        ResultActions response = mockMvc.perform(put("/api/cron-jobs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cronDTO)));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.expression").value(cronDTO.getExpression()));
    }

    @Test
    public void CronController_GetCronById_ReturnOk() throws Exception {
        when(cronService.getCronJobById(any(Integer.class))).thenReturn(cronJob);

        ResultActions response = mockMvc.perform(get("/api/cron-jobs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .param("expression", cronDTO.getExpression()));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.expression").value(cronDTO.getExpression()));
    }

    @Test
    void CronController_GetAllCronJobs_ReturnOk() throws Exception {
        Page<CronJob> page =
                new PageImpl<>(List.of(cronJob), PageRequest.of(0, 20), 1);

        when(cronService.getAllCronJobsPageable(0, 20)).thenReturn(page);

        ResultActions response = mockMvc.perform(get("/api/cron-jobs")
                .param("page", "0")
                .param("size", "20"));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].expression").value(cronJob.getExpression()));
    }

    @Test
    void CronController_DeleteCronById_ReturnNoContent() throws Exception {
        doNothing().when(cronService).deleteCronJob(1);

        mockMvc.perform(delete("/api/cron-jobs/1"))
                .andExpect(status().isNoContent());

        verify(cronService).deleteCronJob(1);
    }
}
