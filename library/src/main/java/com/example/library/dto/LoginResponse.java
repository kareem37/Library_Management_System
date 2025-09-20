package com.example.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight response for login.
 * Contains the JWT token and token type (default "Bearer").
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";

    // convenience constructor that accepts only token and uses default tokenType
    public LoginResponse(String token) {
        this.token = token;
        this.tokenType = "Bearer";
    }
}
