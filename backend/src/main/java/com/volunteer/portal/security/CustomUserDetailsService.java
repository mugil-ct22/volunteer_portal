package com.volunteer.portal.security;

import com.volunteer.portal.entity.AdminUser;
import com.volunteer.portal.entity.User;
import com.volunteer.portal.repository.AdminUserRepository;
import com.volunteer.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        String usernameOrEmailLower = usernameOrEmail.toLowerCase();

        // First check in User table by username
        if (userRepository.existsByUsername(usernameOrEmail)) {
            User user = userRepository.findByUsername(usernameOrEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + usernameOrEmail));
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                    .build();
        }
        // Check in User table by email (case insensitive)
        else if (userRepository.existsByEmail(usernameOrEmailLower)) {
            User user = userRepository.findByEmail(usernameOrEmailLower)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + usernameOrEmail));
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                    .build();
        }
        // Then check in AdminUser table by username
        else if (adminUserRepository.existsByUsername(usernameOrEmail)) {
            AdminUser adminUser = adminUserRepository.findByUsername(usernameOrEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("Admin user not found with username: " + usernameOrEmail));
            return org.springframework.security.core.userdetails.User.builder()
                    .username(adminUser.getEmail())
                    .password(adminUser.getPassword())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .build();
        }
        // Check in AdminUser table by email (case insensitive)
        else if (adminUserRepository.existsByEmail(usernameOrEmailLower)) {
            AdminUser adminUser = adminUserRepository.findByEmail(usernameOrEmailLower)
                    .orElseThrow(() -> new UsernameNotFoundException("Admin user not found with email: " + usernameOrEmail));
            return org.springframework.security.core.userdetails.User.builder()
                    .username(adminUser.getEmail())
                    .password(adminUser.getPassword())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .build();
        }
        else {
            throw new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
        }
    }
}
