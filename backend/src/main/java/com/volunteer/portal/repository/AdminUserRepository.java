package com.volunteer.portal.repository;

import com.volunteer.portal.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByEmail(String email);
    Optional<AdminUser> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}