package com.volunteer.portal.repository;

import com.volunteer.portal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    
    @Query("SELECT u FROM User u")
    List<User> findAllUsers();

    @Query("SELECT u FROM User u ORDER BY u.totalPoints DESC, u.id ASC")
    List<User> findLeaderboard();

    @Query("SELECT COUNT(u) FROM User u")
    Long countUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.totalPoints > 0")
    Long countUsersWithPoints();

    @Query("SELECT u.name, u.email, COUNT(p), COALESCE(SUM(p.pointsAwarded), 0) " +
           "FROM User u LEFT JOIN Proof p ON p.user.id = u.id WHERE p.status = 'APPROVED' " +
           "GROUP BY u.id, u.name, u.email ORDER BY COUNT(p) DESC, COALESCE(SUM(p.pointsAwarded), 0) DESC")
    List<Object[]> findTopActiveUsers();
}
