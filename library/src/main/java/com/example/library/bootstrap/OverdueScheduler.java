package com.example.library.bootstrap;

import com.example.library.service.BorrowService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OverdueScheduler {

    private final BorrowService borrowService;

    public OverdueScheduler(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    // run daily at 2:00 AM
    @Scheduled(cron = "0 0 2 * * *")
    public void processOverdue() {
        borrowService.processOverdueFines();
    }
}
