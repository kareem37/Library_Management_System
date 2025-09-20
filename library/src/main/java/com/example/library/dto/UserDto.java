package com.example.library.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Boolean enabled;
    private Set<String> roles;
}
