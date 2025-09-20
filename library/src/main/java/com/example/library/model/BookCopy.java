package com.example.library.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book_copies", uniqueConstraints = {@UniqueConstraint(columnNames = {"book_id","copy_number"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    private Integer copyNumber;

    @Column(unique = true)
    private String barcode;

    @Enumerated(EnumType.STRING)
    private CopyStatus status = CopyStatus.AVAILABLE;
}
