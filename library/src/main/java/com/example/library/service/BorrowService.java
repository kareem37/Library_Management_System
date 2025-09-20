package com.example.library.service;

import com.example.library.model.BorrowRecord;

import java.util.List;

public interface BorrowService {
    BorrowRecord borrowBook(Long userId, Long bookId);
    BorrowRecord borrowByBarcode(Long userId, String barcode);
    BorrowRecord returnBook(Long borrowRecordId);
    BorrowRecord returnByCopyBarcode(String barcode, Long userId);
    List<BorrowRecord> getActiveBorrowsForUser(Long userId);
    List<BorrowRecord> findOverdueRecords();
    void processOverdueFines(); // recalculates fines for overdue records
}
