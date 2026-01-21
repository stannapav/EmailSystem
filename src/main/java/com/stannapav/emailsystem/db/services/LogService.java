package com.stannapav.emailsystem.db.services;

import com.stannapav.emailsystem.db.dtos.PageResponse;
import com.stannapav.emailsystem.db.dtos.UserStatDTO;
import com.stannapav.emailsystem.db.entities.Log;
import com.stannapav.emailsystem.db.entities.User;
import com.stannapav.emailsystem.db.enums.LogType;
import com.stannapav.emailsystem.db.repositories.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogRepository logRepository;

    public void createLog(User user, LogType logType) {
        Log log = new Log();
        log.setUser(user);
        log.setType(logType);

        logRepository.save(log);
    }

    public PageResponse<UserStatDTO> getUserStats(int page, int size) {
        List<Log> logs = logRepository.findAll();
        Map<User, List<Log>> logsByUser = logs.stream()
                .collect(Collectors.groupingBy(Log::getUser));

        List<UserStatDTO> userStats = new ArrayList<>();

        for (Map.Entry<User, List<Log>> entry : logsByUser.entrySet()) {
            User user = entry.getKey();
            List<Log> userLogs = entry.getValue();

            long restCount = userLogs.stream().filter(log -> log.getType() == LogType.REST).count();
            long cronCount = userLogs.stream().filter(log -> log.getType() == LogType.CRON).count();
            LocalDateTime first = userLogs.stream().map(Log::getCreatedOn).min(LocalDateTime::compareTo).orElse(null);
            LocalDateTime last = userLogs.stream().map(Log::getCreatedOn).max(LocalDateTime::compareTo).orElse(null);

            UserStatDTO userStat = new UserStatDTO(user.getUsername(), user.getEmail(), restCount, cronCount, first, last);
            userStats.add(userStat);
        }

        userStats.sort(Comparator.comparingLong(s -> -(s.getRestCount() + s.getCronCount())));

        int totalPages = (int) Math.ceil((double) userStats.size() / size);
        return new PageResponse<>(
                userStats,
                page,
                size,
                userStats.size(),
                totalPages);
    }
}
