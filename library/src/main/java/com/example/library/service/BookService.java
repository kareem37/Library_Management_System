package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.BookCopy;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Book createBook(Book book);
    Book updateBook(Book book);
    Optional<Book> findById(Long id);
    List<BookCopy> addCopies(Long bookId, int copiesToAdd);
    void removeCopy(Long bookId, int copyNumber);
    List<Book> searchByTitle(String title, int page, int size);
    void syncTotalCopies(Long bookId);
}
