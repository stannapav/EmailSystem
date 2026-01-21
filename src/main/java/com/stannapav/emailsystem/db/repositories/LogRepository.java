package com.stannapav.emailsystem.db.repositories;

import com.stannapav.emailsystem.db.entities.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Integer> {
}
