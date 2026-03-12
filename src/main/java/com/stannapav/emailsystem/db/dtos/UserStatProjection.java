package com.stannapav.emailsystem.db.dtos;

import java.time.LocalDateTime;

public interface UserStatProjection {
    String getUsername();
    String getEmail();

    long getRest();
    long getCron();

    LocalDateTime getFirst();
    LocalDateTime getLast();
}