package com.videoguard.service;

import com.videoguard.dto.LoginRequest;
import com.videoguard.dto.RegisterRequest;
import com.videoguard.dto.UserResponse;
import com.videoguard.dto.UserRoleUpdateRequest;
import com.videoguard.entity.User;
import com.videoguard.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

    private static final String DEMO_PASSWORD_HASH = "CHANGE_ME_HASH";

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
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
        return withToken(user);
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String password = request.getPassword() == null ? "" : request.getPassword().trim();
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new IllegalArgumentException("Username and password are required.");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(password);
        user.setRole(WorkflowConstants.ROLE_USER);
        return withToken(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse me(Long userId) {
        User user = userRepository.findById(userId == null ? 1L : userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    @Transactional
    public UserResponse updateRole(Long userId, UserRoleUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        String role = WorkflowConstants.normalizeRole(request.getRole());
        if (!WorkflowConstants.ROLES.contains(role)) {
            throw new IllegalArgumentException("Role must be 一般用户, 审核员, or 管理员.");
        }
        user.setRole(role);
        return UserResponse.from(userRepository.save(user));
    }

    private UserResponse withToken(User user) {
        UserResponse response = UserResponse.from(user);
        response.setToken(jwtService.generateToken(user.getId(), response.getRole()));
        return response;
    }
}
