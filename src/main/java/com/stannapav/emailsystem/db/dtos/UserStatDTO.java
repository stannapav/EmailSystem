package com.stannapav.emailsystem.db.dtos;

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

    private long restCount;
    private long cronCount;

    private LocalDateTime first;
    private  LocalDateTime last;
}
