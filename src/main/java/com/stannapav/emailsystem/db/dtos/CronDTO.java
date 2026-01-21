package com.stannapav.emailsystem.db.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CronDTO {
    @NotBlank(message = "Expression cant be empty")
    private String expression;

}
