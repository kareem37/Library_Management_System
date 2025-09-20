package com.example.library.service.impl;

import com.example.library.config.AppConstants;
import com.example.library.exception.CopyNotAvailableException;
import com.example.library.exception.MaxBorrowLimitReachedException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.model.*;
import com.example.library.repository.BookCopyRepository;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.ActivityLogService;
import com.example.library.service.BorrowService;
import com.example.library.service.SettingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookCopyRepository bookCopyRepository;
    private final UserRepository userRepository;
    private final SettingService settingService;
    private final ActivityLogService activityLogService;

    public BorrowServiceImpl(BorrowRecordRepository borrowRecordRepository,
                             BookCopyRepository bookCopyRepository,
                             UserRepository userRepository,
                             SettingService settingService,
                             ActivityLogService activityLogService) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.userRepository = userRepository;
        this.settingService = settingService;
        this.activityLogService = activityLogService;
    }

    @Override
    public BorrowRecord borrowBook(Long userId, Long bookId) {
        // 1. verify user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // 2. check user active borrows
        long activeBorrowCount = borrowRecordRepository.countActiveBorrowsByUserId(userId);
        int userLimit = user.getMaxBorrowLimit() != null ? user.getMaxBorrowLimit()
                : settingService.getSettingAsInt("default.max_borrow_limit", AppConstants.DEFAULT_BORROW_PERIOD_DAYS);
        if (activeBorrowCount >= userLimit) {
            throw new MaxBorrowLimitReachedException("User reached max borrow limit (" + userLimit + ")");
        }

        // 3. find available copy
        List<BookCopy> available = bookCopyRepository.findAvailableCopiesForBook(bookId);
        if (available == null || available.isEmpty()) {
            throw new CopyNotAvailableException("No available copies for book id " + bookId);
        }
        BookCopy chosen = available.get(0);

        // 4. reserve/mark as BORROWED and create borrow record
        chosen.setStatus(CopyStatus.BORROWED);
        bookCopyRepository.save(chosen);

        int borrowDays = settingService.getSettingAsInt("borrow.period.days", AppConstants.DEFAULT_BORROW_PERIOD_DAYS);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime due = now.plusDays(borrowDays);

        BorrowRecord br = BorrowRecord.builder()
                .user(user)
                .bookCopy(chosen)
                .borrowedAt(now)
                .dueAt(due)
                .status(BorrowStatus.BORROWED)
                .fineAmount(BigDecimal.ZERO)
                .build();

        BorrowRecord saved = borrowRecordRepository.save(br);

        // log activity
        activityLogService.log(userId, "BORROW_BOOK", "Borrowed copy id " + chosen.getId() + " of book " + chosen.getBook().getTitle(), null);

        return saved;
    }

    @Override
    public BorrowRecord borrowByBarcode(Long userId, String barcode) {
        // find copy by barcode, ensure available
        BookCopy copy = bookCopyRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("BookCopy", "barcode", barcode));
        if (copy.getStatus() != CopyStatus.AVAILABLE) {
            throw new CopyNotAvailableException("Copy is not available");
        }

        // use the same logic: check limits then mark borrowed
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        long activeBorrowCount = borrowRecordRepository.countActiveBorrowsByUserId(userId);
        int userLimit = user.getMaxBorrowLimit() != null ? user.getMaxBorrowLimit()
                : settingService.getSettingAsInt("default.max_borrow_limit", AppConstants.DEFAULT_BORROW_PERIOD_DAYS);

        if (activeBorrowCount >= userLimit) {
            throw new MaxBorrowLimitReachedException("User reached max borrow limit (" + userLimit + ")");
        }

        copy.setStatus(CopyStatus.BORROWED);
        bookCopyRepository.save(copy);

        int borrowDays = settingService.getSettingAsInt("borrow.period.days", AppConstants.DEFAULT_BORROW_PERIOD_DAYS);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime due = now.plusDays(borrowDays);

        BorrowRecord br = BorrowRecord.builder()
                .user(user)
                .bookCopy(copy)
                .borrowedAt(now)
                .dueAt(due)
                .status(BorrowStatus.BORROWED)
                .fineAmount(BigDecimal.ZERO)
                .build();

        BorrowRecord saved = borrowRecordRepository.save(br);
        activityLogService.log(userId, "BORROW_BOOK_BY_BARCODE", "Borrowed barcode " + barcode, null);
        return saved;
    }

    @Override
    public BorrowRecord returnBook(Long borrowRecordId) {
        BorrowRecord br = borrowRecordRepository.findById(borrowRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("BorrowRecord", "id", borrowRecordId));
        if (br.getStatus() != BorrowStatus.BORROWED && br.getStatus() != BorrowStatus.OVERDUE) {
            throw new IllegalStateException("Borrow record is not in borrowed/overdue state");
        }

        LocalDateTime now = LocalDateTime.now();
        br.setReturnedAt(now);
        // determine overdue days
        if (br.getDueAt() != null && now.isAfter(br.getDueAt())) {
            long daysOverdue = Duration.between(br.getDueAt(), now).toDays();
            double finePerDay = settingService.getSettingAsDouble("fine.per.day", AppConstants.DEFAULT_FINE_PER_DAY);
            BigDecimal fine = BigDecimal.valueOf(daysOverdue * finePerDay);
            br.setFineAmount(fine);
            br.setStatus(BorrowStatus.OVERDUE);
        } else {
            br.setStatus(BorrowStatus.RETURNED);
            br.setFineAmount(BigDecimal.ZERO);
        }

        // update copy status back to AVAILABLE (unless flagged lost)
        BookCopy copy = br.getBookCopy();
        copy.setStatus(CopyStatus.AVAILABLE);
        bookCopyRepository.save(copy);

        BorrowRecord saved = borrowRecordRepository.save(br);
        activityLogService.log(br.getUser().getId(), "RETURN_BOOK", "Returned borrow id " + br.getId(), null);
        return saved;
    }

    @Override
    public BorrowRecord returnByCopyBarcode(String barcode, Long userId) {
        BookCopy copy = bookCopyRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("BookCopy", "barcode", barcode));

        // find the active borrow for this copy
        List<BorrowRecord> active = borrowRecordRepository.findByBookCopyIdAndStatus(copy.getId(), BorrowStatus.BORROWED);
        if (active.isEmpty()) {
            throw new IllegalStateException("No active borrow for this copy");
        }
        BorrowRecord br = active.get(0);

        if (!br.getUser().getId().equals(userId)) {
            // Could be allowed for librarians/admins, but we'll enforce match for member self-return
            throw new IllegalStateException("This borrow belongs to another user");
        }

        return returnBook(br.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecord> getActiveBorrowsForUser(Long userId) {
        return borrowRecordRepository.findActiveBorrowRecordsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecord> findOverdueRecords() {
        return borrowRecordRepository.findOverdueRecords(LocalDateTime.now());
    }

    @Override
    public void processOverdueFines() {
        List<BorrowRecord> overdue = findOverdueRecords();
        double finePerDay = settingService.getSettingAsDouble("fine.per.day", AppConstants.DEFAULT_FINE_PER_DAY);
        LocalDateTime now = LocalDateTime.now();
        for (BorrowRecord br : overdue) {
            if (br.getDueAt() == null) continue;
            long daysOver = Duration.between(br.getDueAt(), now).toDays();
            if (daysOver <= 0) continue;
            BigDecimal fine = BigDecimal.valueOf(daysOver * finePerDay);
            br.setFineAmount(fine);
            br.setStatus(BorrowStatus.OVERDUE);
            borrowRecordRepository.save(br);
            activityLogService.log(br.getUser().getId(), "OVERDUE_FINE_UPDATED", "Borrow id " + br.getId() + " fine updated to " + fine, null);
        }
    }
}
