package com.stannapav.emailsystem.controllers;

import com.stannapav.emailsystem.db.entities.User;
import com.stannapav.emailsystem.db.services.MailService;
import com.stannapav.emailsystem.db.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/mails")
public class MailController {
    private final MailService mailService;
    private final UserService userService;

    @PostMapping("/send/{userId}")
    public ResponseEntity<Void> sendMailToUser(@PathVariable Integer userId) {
        User user = userService.getUserById(userId);
        mailService.sendUserMailAsync(user);

        return ResponseEntity.accepted().build();
    }
}
