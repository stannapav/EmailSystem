package com.stannapav.emailsystem.db.entities;

import com.stannapav.emailsystem.db.enums.LogType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "logs")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private LogType type;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @PrePersist
    protected void onCreate() {
        this.createdOn = LocalDateTime.now();
    }
}
