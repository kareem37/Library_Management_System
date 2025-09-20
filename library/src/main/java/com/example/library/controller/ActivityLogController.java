package com.example.library.controller;

import com.example.library.model.ActivityLog;
import com.example.library.service.ActivityLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class ActivityLogController {

    private final ActivityLogService logService;

    public ActivityLogController(ActivityLogService logService) {
        this.logService = logService;
    }

    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @GetMapping("/recent")
    public ResponseEntity<?> recent(@RequestParam(defaultValue = "50") int limit) {
        List<ActivityLog> logs = logService.getRecentLogs(limit);
        return ResponseEntity.ok(logs);
    }
}
