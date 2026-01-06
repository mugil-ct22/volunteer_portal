package com.volunteer.portal.service;

import com.volunteer.portal.dto.AuthResponse;
import com.volunteer.portal.dto.LoginRequest;
import com.volunteer.portal.dto.RegisterRequest;
import com.volunteer.portal.entity.AdminUser;
import com.volunteer.portal.entity.User;
import com.volunteer.portal.repository.AdminUserRepository;
import com.volunteer.portal.repository.UserRepository;
import com.volunteer.portal.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest registerRequest) {
        // Check if email exists in either table
        if (userRepository.existsByEmail(registerRequest.getEmail()) || adminUserRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByUsername(registerRequest.getUsername()) || adminUserRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Set role from request or default to USER
        String role = registerRequest.getRole() != null ? registerRequest.getRole() : "USER";

        if ("ADMIN".equals(role)) {
            AdminUser adminUser = new AdminUser();
            adminUser.setName(registerRequest.getName());
            adminUser.setUsername(registerRequest.getUsername());
            adminUser.setEmail(registerRequest.getEmail());
            adminUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

            AdminUser savedAdminUser = adminUserRepository.save(adminUser);

            String token = jwtUtil.generateToken(savedAdminUser.getEmail(), "ADMIN");

            return new AuthResponse(token, "ADMIN", savedAdminUser.getId(),
                                    savedAdminUser.getName(), savedAdminUser.getEmail());
        } else {
            User user = new User();
            user.setName(registerRequest.getName());
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

            User savedUser = userRepository.save(user);

            String token = jwtUtil.generateToken(savedUser.getEmail(), "USER");

            return new AuthResponse(token, "USER", savedUser.getId(),
                                    savedUser.getName(), savedUser.getEmail());
        }
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String loginIdentifier = loginRequest.getUsername();
        String loginIdentifierLower = loginIdentifier.toLowerCase();

        // Check User table first - try username, then email (case insensitive for email)
        User user = null;
        if (userRepository.existsByUsername(loginIdentifier)) {
            user = userRepository.findByUsername(loginIdentifier)
                .orElseThrow(() -> new RuntimeException("User not found"));
        } else if (userRepository.existsByEmail(loginIdentifierLower)) {
            user = userRepository.findByEmail(loginIdentifierLower)
                .orElseThrow(() -> new RuntimeException("User not found"));
        }

        if (user != null) {
            String token = jwtUtil.generateToken(user.getEmail(), "USER");
            return new AuthResponse(token, "USER", user.getId(),
                                    user.getName(), user.getEmail());
        }

        // Check AdminUser table - try username, then email (case insensitive for email)
        AdminUser adminUser = null;
        if (adminUserRepository.existsByUsername(loginIdentifier)) {
            adminUser = adminUserRepository.findByUsername(loginIdentifier)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));
        } else if (adminUserRepository.existsByEmail(loginIdentifierLower)) {
            adminUser = adminUserRepository.findByEmail(loginIdentifierLower)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));
        }

        if (adminUser != null) {
            String token = jwtUtil.generateToken(adminUser.getEmail(), "ADMIN");
            return new AuthResponse(token, "ADMIN", adminUser.getId(),
                                    adminUser.getName(), adminUser.getEmail());
        }

        throw new RuntimeException("Invalid credentials");
    }
}
