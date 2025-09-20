package com.example.library.repository;

import com.example.library.model.BorrowRecord;
import com.example.library.model.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    // Active borrows for a user (BORROWED or OVERDUE)
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.user.id = :userId AND br.status IN ('BORROWED','OVERDUE')")
    long countActiveBorrowsByUserId(Long userId);

    // All active borrow records for user
    @Query("SELECT br FROM BorrowRecord br WHERE br.user.id = :userId AND br.status IN ('BORROWED','OVERDUE')")
    List<BorrowRecord> findActiveBorrowRecordsByUserId(Long userId);

    // Find overdue borrow records (dueAt < now and status BORROWED)
    @Query("SELECT br FROM BorrowRecord br WHERE br.dueAt < :now AND br.status = 'BORROWED'")
    List<BorrowRecord> findOverdueRecords(LocalDateTime now);

    // Find by book copy id where status is BORROWED (useful to check if copy is currently borrowed)
    List<BorrowRecord> findByBookCopyIdAndStatus(Long bookCopyId, BorrowStatus status);
}
