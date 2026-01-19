package com.stannapav.emailsystem.db.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    @NotBlank(message = "Username can't be empty")
    private String username;

    @NotBlank(message = "Email can't be empty")
    @Email(message = "Invalid email format")
    private String email;
}
