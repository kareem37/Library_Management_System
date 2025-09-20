package com.example.library.service;

import com.example.library.model.ActivityLog;

import java.util.List;

public interface ActivityLogService {
    ActivityLog log(Long userId, String action, String details, String ip);
    List<ActivityLog> getRecentLogs(int limit);
}
