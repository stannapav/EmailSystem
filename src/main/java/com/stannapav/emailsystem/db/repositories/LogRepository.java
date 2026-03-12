package com.stannapav.emailsystem.db.repositories;

import com.stannapav.emailsystem.db.dtos.UserStatProjection;
import com.stannapav.emailsystem.db.entities.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LogRepository extends JpaRepository<Log, Integer> {

    @Query("""
        SELECT 
            u.username as username,
            u.email as email,
            SUM(CASE WHEN l.type = 'REST' THEN 1 ELSE 0 END) as rest,
            SUM(CASE WHEN l.type = 'CRON' THEN 1 ELSE 0 END) as cron,
            MIN(l.createdOn) as first,
            MAX(l.createdOn) as last
        FROM Log l
        JOIN l.user u
        GROUP BY u.id, u.username, u.email
        ORDER BY (SUM(CASE WHEN l.type = 'REST' THEN 1 ELSE 0 END) +
                  SUM(CASE WHEN l.type = 'CRON' THEN 1 ELSE 0 END)) DESC
    """)
    Page<UserStatProjection> getUserStats(Pageable pageable);
}
