package com.stannapav.emailsystem.controllers;

import com.stannapav.emailsystem.db.dtos.PageResponse;
import com.stannapav.emailsystem.db.dtos.UserStatDTO;
import com.stannapav.emailsystem.db.services.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/logs")
@Tag(name = "Logs Management", description = "API for managing logs and getting statistic")
public class LogController {
    private final LogService logService;

    @Operation(summary = "Gets static of mails send to users", description = "Gets a page of static of mails " +
            "send to every user with details of what type of mail, count and first/last mail sent when")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserStatDTO.class)))
    })
    @GetMapping
    public PageResponse<UserStatDTO> getUserStats(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(0) int size ) {
        return logService.getUserStats(page, size);
    }
}
