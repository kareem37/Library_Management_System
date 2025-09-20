package com.example.library.repository;

import com.example.library.model.BookCopy;
import com.example.library.model.CopyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    // Find all copies of a book
    List<BookCopy> findByBookId(Long bookId);

    // Find available copies for a given book (ordered by copy number)
    List<BookCopy> findByBookIdAndStatusOrderByCopyNumberAsc(Long bookId, CopyStatus status);

    // Find a single available copy (helper)
    @Query("SELECT c FROM BookCopy c WHERE c.book.id = :bookId AND c.status = 'AVAILABLE' ORDER BY c.copyNumber ASC")
    List<BookCopy> findAvailableCopiesForBook(@Param("bookId") Long bookId);

    Optional<BookCopy> findByBarcode(String barcode);
}
