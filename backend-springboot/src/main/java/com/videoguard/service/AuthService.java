package com.videoguard.service;

import com.videoguard.dto.LoginRequest;
import com.videoguard.dto.UserResponse;
import com.videoguard.entity.User;
import com.videoguard.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

    private static final String DEMO_PASSWORD_HASH = "CHANGE_ME_HASH";

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest request) {
        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String password = request.getPassword() == null ? "" : request.getPassword().trim();
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new IllegalArgumentException("Username and password are required.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        if (!DEMO_PASSWORD_HASH.equals(user.getPasswordHash()) && !user.getPasswordHash().equals(password)) {
            throw new IllegalArgumentException("Invalid password.");
        }
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserResponse me(Long userId) {
        User user = userRepository.findById(userId == null ? 1L : userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return UserResponse.from(user);
    }
}
