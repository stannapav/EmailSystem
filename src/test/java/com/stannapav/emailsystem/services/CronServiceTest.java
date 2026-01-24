package com.stannapav.emailsystem.services;

import com.stannapav.emailsystem.db.dtos.CronDTO;
import com.stannapav.emailsystem.db.entities.CronJob;
import com.stannapav.emailsystem.db.repositories.CronRepository;
import com.stannapav.emailsystem.db.services.CronService;
import com.stannapav.emailsystem.validation.CronValidator;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CronServiceTest {
    @Mock
    private CronRepository cronRepository;

    @Mock
    private CronValidator cronValidator;

    @Mock
    private TaskScheduler scheduler;

    @Mock
    private ModelMapper mapper;

    @Mock
    private ScheduledFuture<?> scheduledFuture;

    @InjectMocks
    private CronService cronService;

    @Test
    public void CronService_CreateCronJob_ReturnCronJob() {
        CronDTO cronDTO = new CronDTO();
        cronDTO.setExpression("0 2 * * * *");

        CronJob cronJob = new CronJob();
        cronJob.setExpression(cronDTO.getExpression());

        when(mapper.map(cronDTO, CronJob.class)).thenReturn(cronJob);
        doReturn(scheduledFuture)
                .when(scheduler)
                .schedule(any(Runnable.class), any(CronTrigger.class));

        CronJob createdCron = cronService.createCronJob(cronDTO);

        Assertions.assertThat(createdCron).isNotNull();
        Assertions.assertThat(createdCron.getExpression()).isEqualTo(cronDTO.getExpression());

        verify(cronValidator).validate(cronDTO.getExpression());
        verify(cronRepository).save(cronJob);
        verify(scheduler).schedule(any(Runnable.class), any(CronTrigger.class));
    }

    @Test
    public void CronService_CreateCronJobFailValidation_ThrowException() {
        String expression = "Invalid";

        CronDTO cronDTO = new CronDTO();
        cronDTO.setExpression(expression);

        doThrow(new IllegalArgumentException("Invalid cron expression"))
                .when(cronValidator).validate(expression);

        Assertions.assertThatThrownBy(() -> cronService.createCronJob(cronDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid cron expression");

        verify(cronRepository, never()).save(any());
        verify(scheduler, never()).schedule(any(Runnable.class), any(CronTrigger.class));
    }

    @Test
    public void CronService_UpdateCronJob_ReturnCronJob() {
        Integer id = 1;

        CronDTO cronDTO = new CronDTO();
        cronDTO.setExpression("0 2 * * * *");

        CronJob existingCron = new CronJob();
        existingCron.setId(id);

        when(cronRepository.findById(id)).thenReturn(Optional.of(existingCron));
        when(cronRepository.findByExpression(cronDTO.getExpression())).thenReturn(Optional.empty());
        doReturn(scheduledFuture)
                .when(scheduler)
                .schedule(any(Runnable.class), any(CronTrigger.class));

        CronJob updatedCron = cronService.updateCronJob(id, cronDTO);

        Assertions.assertThat(updatedCron).isNotNull();
        Assertions.assertThat(updatedCron.getExpression()).isEqualTo(cronDTO.getExpression());

        verify(cronValidator).validate(cronDTO.getExpression());
        verify(cronRepository).save(existingCron);
        verify(scheduledFuture, never()).cancel(true);
        verify(scheduler).schedule(any(Runnable.class), any(CronTrigger.class));
    }

    @Test
    public void CronService_UpdateCronJobFailValidation_ThrowException() {
        String expression = "Invalid";

        CronDTO cronDTO = new CronDTO();
        cronDTO.setExpression(expression);

        doThrow(new IllegalArgumentException("Invalid cron expression"))
                .when(cronValidator).validate(expression);

        Assertions.assertThatThrownBy(() -> cronService.updateCronJob(1, cronDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid cron expression");

        verify(cronRepository, never()).save(any());
        verify(scheduledFuture, never()).cancel(true);
        verify(scheduler, never()).schedule(any(Runnable.class), any(CronTrigger.class));
    }

    @Test
    public void CronService_UpdateCronJobNotFound_ThrowException() {
        Integer id = 1;

        CronDTO cronDTO = new CronDTO();
        cronDTO.setExpression("0 2 * * * *");

        when(cronRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> cronService.updateCronJob(id, cronDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("CronJob not found");

        verify(cronValidator).validate(cronDTO.getExpression());
        verify(cronRepository, never()).save(any());
        verify(scheduledFuture, never()).cancel(true);
        verify(scheduler, never()).schedule(any(Runnable.class), any(CronTrigger.class));
    }

    @Test
    public void CronService_UpdateCronJobAlreadyExists_ThrowException() {
        Integer id = 1;
        String expression = "0 2 * * * *";

        CronDTO cronDTO = new CronDTO();
        cronDTO.setExpression(expression);

        CronJob existingCron = new CronJob();
        existingCron.setId(id);

        when(cronRepository.findById(id)).thenReturn(Optional.of(existingCron));
        when(cronRepository.findByExpression(expression)).thenReturn(Optional.of(new CronJob()));

        Assertions.assertThatThrownBy(() -> cronService.updateCronJob(id, cronDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("This CronJob already exists");

        verify(cronValidator).validate(expression);
        verify(cronRepository, never()).save(any());
        verify(scheduledFuture, never()).cancel(true);
        verify(scheduler, never()).schedule(any(Runnable.class), any(CronTrigger.class));
    }

    @Test
    public void CronService_DeleteCronJob_Void() {
        Integer id = 1;

        CronJob existCron = new CronJob();
        existCron.setId(id);

        when(cronRepository.findById(id)).thenReturn(Optional.of(existCron));

        cronService.deleteCronJob(id);

        verify(cronRepository).delete(existCron);
    }

    @Test
    public void CronService_DeleteCronJobNotFound_ThrowException() {
        Integer id = 1;

        when(cronRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> cronService.deleteCronJob(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("CronJob not found");

        verify(scheduledFuture, never()).cancel(true);
        verify(cronRepository, never()).deleteById(id);
    }

    @Test
    public void CronService_GetCronJobById_ReturnCronJob() {
        Integer id = 1;

        CronJob existCron = new CronJob();
        existCron.setId(id);

        when(cronRepository.findById(id)).thenReturn(Optional.of(existCron));

        CronJob cronJob = cronService.getCronJobById(id);

        Assertions.assertThat(cronJob).isNotNull();
        Assertions.assertThat(cronJob.getId()).isEqualTo(id);
    }

    @Test
    public void CronService_GetCronJobByIdNotFound_ThrowException() {
        Integer id = 1;

        when(cronRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> cronService.getCronJobById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("CronJob not found");
    }
}
