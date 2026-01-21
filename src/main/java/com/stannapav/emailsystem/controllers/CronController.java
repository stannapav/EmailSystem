package com.stannapav.emailsystem.controllers;

import com.stannapav.emailsystem.db.dtos.CronDTO;
import com.stannapav.emailsystem.db.entities.CronJob;
import com.stannapav.emailsystem.db.services.CronService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "CronJob Management", description = "APIs for managing cronjobs")
public class CronController {
    private final CronService cronService;

    @Operation(summary = "Creates cronjob", description = "Creates cronjob and schedules it to send mail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cronjob created successfully",
                    content = @Content(schema = @Schema(implementation = CronJob.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/addCronJob")
    public ResponseEntity<CronJob> createCronJob(@Valid @RequestBody CronDTO cronDTO) {
        CronJob cronJob = cronService.createCronJob(cronDTO);
        URI location = URI.create("/api/cron-jobs/" + cronJob.getId());

        return ResponseEntity
                .created(location)
                .body(cronJob);
    }

    @Operation(summary = "Updates cronjob", description = "Updated cronjob from system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cronjob updated successfully",
                    content = @Content(schema = @Schema(implementation = CronJob.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Cronjob not found",
                    content = @Content(schema = @Schema()))
    })
    @PutMapping("/{cronId}")
    public ResponseEntity<CronJob> updateCronJob(
            @PathVariable @Min(1) Integer cronId,
            @Valid @RequestBody CronDTO cronDTO) {
        return ResponseEntity.ok(cronService.updateCronJob(cronId, cronDTO));
    }

    @Operation(summary = "Gets cronjob", description = "Gets cronjob from system by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cronjob retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CronJob.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Cronjob not found",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/{cronId}")
    public ResponseEntity<CronJob> getCronJobById(@PathVariable @Min(1) Integer cronId) {
        return ResponseEntity.ok(cronService.getCronJobById(cronId));
    }

    @Operation(summary = "Gets all cronjob", description = "Gets all cronjob from system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All cronjob retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CronJob.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping
    public Page<CronJob> getCronJobs(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(0) int size) {
        return cronService.getAllCronJobsPageable(page, size);
    }

    @Operation(summary = "Deletes cronjob", description = "Deletes cronjob from system by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cronjob deleted successfully",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Cronjob not found",
                    content = @Content(schema = @Schema()))
    })
    @DeleteMapping("/{cronId}")
    public ResponseEntity<Void> deleteCronJob(@PathVariable @Min(1) Integer cronId) {
        cronService.deleteCronJob(cronId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletes all cronjob", description = "Deletes all cronjob from system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All cronjob deleted successfully",
                    content = @Content(schema = @Schema()))
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteAllCronJobs() {
        cronService.deleteAllCronJobs();
        return ResponseEntity.noContent().build();
    }
}
