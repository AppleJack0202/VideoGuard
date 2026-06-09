package com.videoguard.controller;

import com.videoguard.dto.UserResponse;
import com.videoguard.dto.UserRoleUpdateRequest;
import com.videoguard.service.AuthService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public List<UserResponse> list() {
        return authService.listUsers();
    }

    @PutMapping("/{id}/role")
    public UserResponse updateRole(@PathVariable Long id, @Valid @RequestBody UserRoleUpdateRequest request) {
        return authService.updateRole(id, request);
    }
}
