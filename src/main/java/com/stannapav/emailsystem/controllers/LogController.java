package com.stannapav.emailsystem.controllers;

import com.stannapav.emailsystem.db.dtos.UserStatDTO;
import com.stannapav.emailsystem.db.services.LogService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/logs")
public class LogController {
    private final LogService logService;

    @GetMapping
    public Page<UserStatDTO> getUserStats(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(0) int size ) {
        return logService.getUserStats(page, size);
    }
}
