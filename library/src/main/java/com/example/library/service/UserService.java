package com.example.library.service;

import com.example.library.model.User;

import java.util.Optional;

public interface UserService {
    User createUser(User user, String rawPassword);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    User assignRoleToUser(Long userId, Long roleId);
}
