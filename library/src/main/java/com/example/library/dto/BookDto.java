package com.example.library.dto;

import lombok.Data;

import java.util.Set;

@Data
public class BookDto {
    private Long id;
    private String title;
    private String isbn;
    private String summary;
    private String publisher;
    private Long languageId;
    private Long categoryId;
    private Integer totalCopies;
    private Set<Long> authorIds;
}
