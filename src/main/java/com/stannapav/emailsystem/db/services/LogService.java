package com.stannapav.emailsystem.db.services;

import com.stannapav.emailsystem.db.dtos.CountDTO;
import com.stannapav.emailsystem.db.dtos.PageResponse;
import com.stannapav.emailsystem.db.dtos.UserStatDTO;
import com.stannapav.emailsystem.db.dtos.UserStatProjection;
import com.stannapav.emailsystem.db.entities.Log;
import com.stannapav.emailsystem.db.entities.User;
import com.stannapav.emailsystem.db.enums.LogType;
import com.stannapav.emailsystem.db.repositories.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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
        Pageable pageable = PageRequest.of(page, size);

        Page<UserStatProjection> statsPage = logRepository.getUserStats(pageable);

        List<UserStatDTO> content = statsPage.getContent()
                .stream()
                .map(p -> new UserStatDTO(
                        p.getUsername(),
                        p.getEmail(),
                        new CountDTO(p.getRest(), p.getCron()),
                        p.getFirst(),
                        p.getLast()
                ))
                .toList();

        return new PageResponse<>(
                content,
                statsPage.getNumber(),
                statsPage.getSize(),
                statsPage.getTotalElements(),
                statsPage.getTotalPages()
        );
    }
}
