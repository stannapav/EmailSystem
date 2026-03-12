package com.stannapav.emailsystem.db.repositories;

import com.stannapav.emailsystem.db.entities.CronJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CronRepository extends JpaRepository<CronJob, Integer> {

    Optional<CronJob> findByExpression(String expression);
}
