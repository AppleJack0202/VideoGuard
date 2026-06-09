package com.videoguard.controller;

import com.videoguard.dto.LoginRequest;
import com.videoguard.dto.RegisterRequest;
import com.videoguard.dto.UserResponse;
import com.videoguard.service.AuthService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @GetMapping("/me")
    public UserResponse me(HttpServletRequest request, @RequestParam(value = "userId", required = false) Long userId) {
        Object currentUserId = request.getAttribute("currentUserId");
        if (currentUserId instanceof Long id) {
            return authService.me(id);
        }
        return authService.me(userId);
    }
}
