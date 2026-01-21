package com.stannapav.emailsystem.controllers;

import com.stannapav.emailsystem.db.entities.User;
import com.stannapav.emailsystem.db.enums.LogType;
import com.stannapav.emailsystem.db.services.MailService;
import com.stannapav.emailsystem.db.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/mails")
@Tag(name = "Mail Management", description = "API for sending mail to user")
public class MailController {
    private final MailService mailService;
    private final UserService userService;

    @Operation(summary = "Sends mail to a user", description = "Asynchronously sends mail to a user from system by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Mail is sending",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "503", description = "Mail service is temporally unavailable",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/send/{userId}")
    public ResponseEntity<Void> sendMailToUser(@PathVariable Integer userId) {
        User user = userService.getUserById(userId);
        mailService.sendUserMailAsync(user, LogType.REST);

        return ResponseEntity.accepted().build();
    }
}
