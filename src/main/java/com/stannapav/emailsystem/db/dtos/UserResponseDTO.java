package com.stannapav.emailsystem.db.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponseDTO {
    @NotBlank(message = "Username can't be empty")
    private String username;

    @NotBlank(message = "Email can't be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "CreatedOn can't be empty")
    private LocalDateTime createdOn;
}
