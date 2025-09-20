package com.example.library.service.impl;

import com.example.library.exception.ResourceNotFoundException;
import com.example.library.model.Book;
import com.example.library.model.BookCopy;
import com.example.library.model.CopyStatus;
import com.example.library.repository.BookCopyRepository;
import com.example.library.repository.BookRepository;
import com.example.library.service.BookService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;

    public BookServiceImpl(BookRepository bookRepository,
                           BookCopyRepository bookCopyRepository) {
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
    }

    @Override
    public Book createBook(Book book) {
        Book saved = bookRepository.save(book);
        // attempt to sync copies - but copies are added separately
        syncTotalCopies(saved.getId());
        return saved;
    }

    @Override
    public Book updateBook(Book book) {
        if (book.getId() == null) throw new IllegalArgumentException("Book id is required for update");
        Book existing = bookRepository.findById(book.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", book.getId()));
        existing.setTitle(book.getTitle());
        existing.setIsbn(book.getIsbn());
        existing.setSummary(book.getSummary());
        existing.setPublisher(book.getPublisher());
        existing.setCategory(book.getCategory());
        existing.setLanguage(book.getLanguage());
        existing.setAuthors(book.getAuthors());
        Book saved = bookRepository.save(existing);
        syncTotalCopies(saved.getId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<BookCopy> addCopies(Long bookId, int copiesToAdd) {
        if (copiesToAdd <= 0) throw new IllegalArgumentException("copiesToAdd must be > 0");
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));
        // find current highest copy number
        List<BookCopy> existingCopies = bookCopyRepository.findByBookId(bookId);
        int highest = existingCopies.stream().map(c -> c.getCopyNumber() == null ? 0 : c.getCopyNumber())
                .max(Integer::compareTo).orElse(0);
        List<BookCopy> created = new ArrayList<>();
        for (int i = 1; i <= copiesToAdd; i++) {
            BookCopy copy = BookCopy.builder()
                    .book(book)
                    .copyNumber(highest + i)
                    .barcode(generateBarcode(book.getId(), highest + i))
                    .status(CopyStatus.AVAILABLE)
                    .build();
            created.add(bookCopyRepository.save(copy));
        }
        syncTotalCopies(bookId);
        return created;
    }

    @Override
    public void removeCopy(Long bookId, int copyNumber) {
        List<BookCopy> copies = bookCopyRepository.findByBookId(bookId);
        BookCopy target = copies.stream()
                .filter(c -> c.getCopyNumber() != null && c.getCopyNumber() == copyNumber)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("BookCopy", "bookId+copyNumber", bookId + ":" + copyNumber));
        if (target.getStatus() != null && target.getStatus() == CopyStatus.BORROWED) {
            throw new IllegalStateException("Cannot remove a borrowed copy");
        }
        bookCopyRepository.delete(target);
        syncTotalCopies(bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> searchByTitle(String title, int page, int size) {
        return bookRepository.findByTitleContainingIgnoreCase(title, PageRequest.of(page, size))
                .getContent();
    }

    @Override
    public void syncTotalCopies(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));
        int count = bookCopyRepository.findByBookId(bookId).size();
        book.setTotalCopies(count);
        bookRepository.save(book);
    }

    private String generateBarcode(Long bookId, int copyNumber) {
        // Simple barcode: BOOK-<bookId>-COPY-<copyNumber>-<timestamp>
        return String.format("BOOK-%d-COPY-%d-%d", bookId, copyNumber, System.currentTimeMillis());
    }
}
