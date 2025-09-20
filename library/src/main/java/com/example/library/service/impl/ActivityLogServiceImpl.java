package com.example.library.service.impl;

import com.example.library.model.ActivityLog;
import com.example.library.model.User;
import com.example.library.repository.ActivityLogRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.ActivityLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository repository;
    private final UserRepository userRepository;

    public ActivityLogServiceImpl(ActivityLogRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public ActivityLog log(Long userId, String action, String details, String ip) {
        ActivityLog log = ActivityLog.builder()
                .action(action)
                .details(details)
                .ip(ip)
                .build();
        if (userId != null) {
            User u = userRepository.findById(userId).orElse(null);
            log.setUser(u);
        }
        return repository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityLog> getRecentLogs(int limit) {
        List<ActivityLog> recent = repository.findTop100ByOrderByCreatedAtDesc();
        if (limit <= 0 || limit >= recent.size()) return recent;
        return recent.subList(0, limit);
    }
}
