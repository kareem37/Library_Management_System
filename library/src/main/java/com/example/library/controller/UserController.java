package com.example.library.controller;

import com.example.library.dto.ApiResponse;
import com.example.library.dto.UserDto;
import com.example.library.model.User;
import com.example.library.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper mapper;

    public UserController(UserService userService, ModelMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<?> assignRole(@PathVariable Long userId, @PathVariable Long roleId) {
        User u = userService.assignRoleToUser(userId, roleId);
        UserDto dto = mapper.map(u, UserDto.class);
        dto.setRoles(u.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()));
        return ResponseEntity.ok(dto);
    }

    // minimal endpoint to get profile
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','MEMBER')")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        User u = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        UserDto dto = mapper.map(u, UserDto.class);
        dto.setRoles(u.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()));
        return ResponseEntity.ok(dto);
    }
}
