package com.example.library.repository;

import com.example.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    // Search by title or ISBN
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Book> findByIsbnContaining(String isbn, Pageable pageable);

    // Search by category (categoryId) or language
    Page<Book> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Book> findByLanguageId(Long languageId, Pageable pageable);

    // Basic list by author will be covered by custom queries in services using book.authors
    List<Book> findByTitleIgnoreCase(String title);
}
