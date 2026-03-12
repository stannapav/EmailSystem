package com.stannapav.emailsystem.db.services;

import com.stannapav.emailsystem.db.dtos.CronDTO;
import com.stannapav.emailsystem.db.dtos.ResponseCronDTO;
import com.stannapav.emailsystem.db.entities.CronJob;
import com.stannapav.emailsystem.db.enums.LogType;
import com.stannapav.emailsystem.db.repositories.CronRepository;
import com.stannapav.emailsystem.db.repositories.UserRepository;
import com.stannapav.emailsystem.validation.CronValidator;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class CronService {
    private final CronRepository cronRepository;
    private final UserRepository userRepository;
    private final CronValidator cronValidator;
    private final MailService mailService;
    private final TaskScheduler scheduler;
    private final ModelMapper mapper;

    private final Map<Integer, ScheduledFuture<?>> cronJobs = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        List<CronJob> cronJobsFromDb = cronRepository.findAll();

        for (CronJob cronJob : cronJobsFromDb) {
            schedule(cronJob);
        }
    }

    public void schedule(CronJob cronJob) {
        if (cronJobs.containsKey(cronJob.getId())) {
            return;
        }

        ScheduledFuture<?> future = scheduler.schedule(
                () -> userRepository.findAll()
                        .forEach(user -> mailService.sendUserMailAsync(user, LogType.CRON)),
                new CronTrigger(cronJob.getExpression())
        );

        cronJobs.put(cronJob.getId(), future);
    }

    public void cancel(Integer cronJobId) {
        ScheduledFuture<?> future = cronJobs.remove(cronJobId);

        if (future != null) {
            future.cancel(false);
        }
    }

    public ResponseCronDTO createCronJob(CronDTO cronDTO){
        cronValidator.validate(cronDTO.getExpression());

        CronJob cronJob = mapper.map(cronDTO, CronJob.class);
        cronRepository.save(cronJob);
        schedule(cronJob);

        return mapper.map(cronJob, ResponseCronDTO.class);
    }

    public ResponseCronDTO updateCronJob(Integer id, CronDTO cronDTO) {
        cronValidator.validate(cronDTO.getExpression());

        CronJob updateCronJob = cronRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CronJob not found"));

        Optional<CronJob> optionalCronJob = cronRepository.findByExpression(cronDTO.getExpression());

        if(optionalCronJob.isEmpty()) {
            updateCronJob.setExpression(cronDTO.getExpression());
        } else {
            throw new IllegalArgumentException("This CronJob already exists");
        }

        cronRepository.save(updateCronJob);
        cancel(updateCronJob.getId());
        schedule(updateCronJob);

        return mapper.map(updateCronJob, ResponseCronDTO.class);

    }

    public void deleteCronJob(Integer id) {
        CronJob deleteCronJob = cronRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CronJob not found"));

        cancel(id);

        cronRepository.delete(deleteCronJob);
    }

    public void deleteAllCronJobs() {
        for (ScheduledFuture<?> future : cronJobs.values()) {
            if (future != null) {
                future.cancel(false);
            }
        }

        cronJobs.clear();

        cronRepository.deleteAll();
    }

    public ResponseCronDTO getCronJobById(Integer id) {
        CronJob findCronJob = cronRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CronJob not found"));

        return mapper.map(findCronJob, ResponseCronDTO.class);
    }

    public Page<ResponseCronDTO> getAllCronJobsPageable(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CronJob> cronPage = cronRepository.findAll(pageable);

        return cronPage.map(cron -> mapper.map(cron, ResponseCronDTO.class));
    }
}
