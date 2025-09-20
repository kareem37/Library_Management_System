package com.example.library.controller;

import com.example.library.dto.*;
import com.example.library.model.Role;
import com.example.library.model.User;
import com.example.library.repository.RoleRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.ActivityLogService;
import com.example.library.service.UserService;
import com.example.library.security.JwtUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final ModelMapper mapper;
    private final RoleRepository roleRepository;
    private final ActivityLogService logService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils,
                          UserService userService,
                          ModelMapper mapper,
                          RoleRepository roleRepository,
                          ActivityLogService logService,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.mapper = mapper;
        this.roleRepository = roleRepository;
        this.logService = logService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.generateToken(authentication);
        // log login
        userRepository.findByUsername(req.getUsername()).ifPresent(u -> logService.log(u.getId(), "LOGIN", "User logged in", null));
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setFullName(req.getFullName());
        User saved = userService.createUser(u, req.getPassword());

        // assign MEMBER role by default
        Role role = roleRepository.findByName("ROLE_MEMBER").orElse(null);
        if (role != null) {
            userService.assignRoleToUser(saved.getId(), role.getId());
        }

        logService.log(saved.getId(), "REGISTER", "New user registered", null);
        return ResponseEntity.ok(new ApiResponse(true, "Registered successfully"));
    }
}
