package com.stannapav.emailsystem.controllers;

import com.stannapav.emailsystem.db.dtos.CronDTO;
import com.stannapav.emailsystem.db.entities.CronJob;
import com.stannapav.emailsystem.db.services.CronService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/cron-jobs")
public class CronController {
    private final CronService cronService;

    @PostMapping("/addCronJob")
    public ResponseEntity<CronJob> createCronJob(@Valid @RequestBody CronDTO cronDTO) {
        CronJob cronJob = cronService.createCronJob(cronDTO);
        URI location = URI.create("/api/cron-jobs/" + cronJob.getId());

        return ResponseEntity
                .created(location)
                .body(cronJob);
    }

    @PutMapping("/{cronId}")
    public ResponseEntity<CronJob> updateCronJob(
            @PathVariable @Min(1) Integer cronId,
            @Valid @RequestBody CronDTO cronDTO) {
        return ResponseEntity.ok(cronService.updateCronJob(cronId, cronDTO));
    }

    @GetMapping("/{cronId}")
    public ResponseEntity<CronJob> getCronJobById(@PathVariable @Min(1) Integer cronId) {
        return ResponseEntity.ok(cronService.getCronJobById(cronId));
    }

    @GetMapping
    public Page<CronJob> getCronJobs(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(0) int size) {
        return cronService.getAllCronJobsPageable(page, size);
    }

    @DeleteMapping("/{cronId}")
    public ResponseEntity<Void> deleteCronJob(@PathVariable @Min(1) Integer cronId) {
        cronService.deleteCronJob(cronId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllCronJobs() {
        cronService.deleteAllCronJobs();
        return ResponseEntity.noContent().build();
    }
}
