package com.example.library.controller;

import com.example.library.dto.ApiResponse;
import com.example.library.dto.BorrowResponseDto;
import com.example.library.model.BorrowRecord;
import com.example.library.service.BorrowService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    private final BorrowService borrowService;
    private final ModelMapper mapper;

    public BorrowController(BorrowService borrowService, ModelMapper mapper) {
        this.borrowService = borrowService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasAnyRole('MEMBER','LIBRARIAN','ADMIN')")
    @PostMapping("/book/{bookId}/user/{userId}")
    public ResponseEntity<?> borrowBook(@PathVariable Long bookId, @PathVariable Long userId) {
        BorrowRecord br = borrowService.borrowBook(userId, bookId);
        return ResponseEntity.ok(mapper.map(br, BorrowResponseDto.class));
    }

    @PreAuthorize("hasAnyRole('MEMBER','LIBRARIAN','ADMIN')")
    @PostMapping("/barcode/{barcode}/user/{userId}")
    public ResponseEntity<?> borrowByBarcode(@PathVariable String barcode, @PathVariable Long userId) {
        BorrowRecord br = borrowService.borrowByBarcode(userId, barcode);
        return ResponseEntity.ok(mapper.map(br, BorrowResponseDto.class));
    }

    @PreAuthorize("hasAnyRole('MEMBER','LIBRARIAN','ADMIN')")
    @PostMapping("/return/{borrowId}")
    public ResponseEntity<?> returnBook(@PathVariable Long borrowId) {
        BorrowRecord br = borrowService.returnBook(borrowId);
        return ResponseEntity.ok(mapper.map(br, BorrowResponseDto.class));
    }

    @PreAuthorize("hasAnyRole('MEMBER','LIBRARIAN','ADMIN')")
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<?> activeForUser(@PathVariable Long userId) {
        List<BorrowRecord> list = borrowService.getActiveBorrowsForUser(userId);
        return ResponseEntity.ok(list.stream().map(b -> mapper.map(b, BorrowResponseDto.class)).collect(Collectors.toList()));
    }
}
