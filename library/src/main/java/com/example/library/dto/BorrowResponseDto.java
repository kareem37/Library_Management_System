package com.example.library.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class BorrowResponseDto {
    private Long id;
    private Long userId;
    private Long bookCopyId;
    private LocalDateTime borrowedAt;
    private LocalDateTime dueAt;
    private LocalDateTime returnedAt;
    private String status;
    private BigDecimal fineAmount;
}
