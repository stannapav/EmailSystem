package com.stannapav.emailsystem.db.repositories;

import com.stannapav.emailsystem.db.entities.CronJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CronRepository extends JpaRepository<CronJob, Integer> {

    Optional<CronJob> findByExpression(String expression);
}
