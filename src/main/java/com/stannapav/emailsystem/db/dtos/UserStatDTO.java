package com.stannapav.emailsystem.db.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatDTO {
    private String username;
    private String email;
    private CountDTO count;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime first;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private  LocalDateTime last;
}
