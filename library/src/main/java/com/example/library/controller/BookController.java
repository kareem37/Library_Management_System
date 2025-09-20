package com.example.library.controller;

import com.example.library.dto.*;
import com.example.library.model.Book;
import com.example.library.model.BookCopy;
import com.example.library.model.Author;
import com.example.library.model.Language;
import com.example.library.model.Category;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.LanguageRepository;
import com.example.library.repository.CategoryRepository;
import com.example.library.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final ModelMapper mapper;
    private final AuthorRepository authorRepo;
    private final LanguageRepository languageRepo;
    private final CategoryRepository categoryRepo;

    public BookController(BookService bookService, ModelMapper mapper, AuthorRepository authorRepo, LanguageRepository languageRepo, CategoryRepository categoryRepo) {
        this.bookService = bookService;
        this.mapper = mapper;
        this.authorRepo = authorRepo;
        this.languageRepo = languageRepo;
        this.categoryRepo = categoryRepo;
    }

    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PostMapping
    public ResponseEntity<?> createBook(@Valid @RequestBody BookDto dto) {
        Book b = new Book();
        b.setTitle(dto.getTitle());
        b.setIsbn(dto.getIsbn());
        b.setSummary(dto.getSummary());
        b.setPublisher(dto.getPublisher());

        if (dto.getLanguageId() != null) languageRepo.findById(dto.getLanguageId()).ifPresent(b::setLanguage);
        if (dto.getCategoryId() != null) categoryRepo.findById(dto.getCategoryId()).ifPresent(b::setCategory);

        if (dto.getAuthorIds() != null) {
            Set<Author> authors = dto.getAuthorIds().stream()
                    .map(id -> authorRepo.findById(id).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            b.setAuthors(authors);
        }
        Book saved = bookService.createBook(b);
        BookDto out = mapper.map(saved, BookDto.class);
        return ResponseEntity.ok(out);
    }

    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PostMapping("/{bookId}/copies")
    public ResponseEntity<?> addCopies(@PathVariable Long bookId, @RequestParam int count) {
        List<BookCopy> copies = bookService.addCopies(bookId, count);
        List<BookCopyDto> dto = copies.stream().map(c -> mapper.map(c, BookCopyDto.class)).collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @DeleteMapping("/{bookId}/copies/{copyNumber}")
    public ResponseEntity<?> removeCopy(@PathVariable Long bookId, @PathVariable int copyNumber) {
        bookService.removeCopy(bookId, copyNumber);
        return ResponseEntity.ok(new ApiResponse(true, "Copy removed"));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<?> getBook(@PathVariable Long bookId) {
        Book b = bookService.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        BookDto dto = mapper.map(b, BookDto.class);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String q, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        List<Book> res = bookService.searchByTitle(q, page, size);
        List<BookDto> dto = res.stream().map(b -> mapper.map(b, BookDto.class)).collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }
}
