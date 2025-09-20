package com.example.library.dto;

import com.example.library.model.CopyStatus;
import lombok.Data;

@Data
public class BookCopyDto {
    private Long id;
    private Long bookId;
    private Integer copyNumber;
    private String barcode;
    private CopyStatus status;
}
