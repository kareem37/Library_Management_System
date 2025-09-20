package com.example.library.exception;

public class MaxBorrowLimitReachedException extends RuntimeException {
    public MaxBorrowLimitReachedException(String message) {
        super(message);
    }
}
