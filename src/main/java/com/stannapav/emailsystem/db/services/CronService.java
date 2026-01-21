package com.stannapav.emailsystem.db.services;

import com.stannapav.emailsystem.db.dtos.CronDTO;
import com.stannapav.emailsystem.db.entities.CronJob;
import com.stannapav.emailsystem.db.repositories.CronRepository;
import com.stannapav.emailsystem.db.repositories.UserRepository;
import com.stannapav.emailsystem.validation.CronValidator;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Service
public class CronService {
    @Autowired
    private CronRepository cronRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CronValidator cronValidator;

    @Autowired
    private MailService mailService;

    @Autowired
    private TaskScheduler scheduler;

    @Autowired
    private ModelMapper mapper;

    private final Map<Integer, ScheduledFuture<?>> cronJobs = new HashMap<>();

    public void schedule(CronJob cronJob) {
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            userRepository.findAll().forEach(mailService::sendUserMailAsync);
        }, new CronTrigger(cronJob.getExpression()));

        cronJobs.put(cronJob.getId(), future);
    }

    public void cancel(Integer cronJobId) {
        ScheduledFuture<?> future = cronJobs.get(cronJobId);

        if (future != null)
            future.cancel(false);
    }

    public CronJob createCronJob(CronDTO cronDTO){
        cronValidator.validate(cronDTO.getExpression());

        CronJob cronJob = mapper.map(cronDTO, CronJob.class);
        cronRepository.save(cronJob);
        schedule(cronJob);

        return cronJob;
    }

    public CronJob updateCronJob(Integer id, CronDTO cronDTO) {
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

        return updateCronJob;
    }

    public void deleteCronJob(Integer id) {
        CronJob deleteCronJob = cronRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CronJob not found"));

        cancel(id);

        cronRepository.delete(deleteCronJob);
    }

    public void deleteAllCronJobs() {
        cronRepository.deleteAll();
    }

    public CronJob getCronJobById(Integer id) {
        return cronRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CronJob not found"));
    }

    public Page<CronJob> getAllCronJobsPageable(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cronRepository.findAll(pageable);
    }
}
