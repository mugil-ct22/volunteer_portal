package com.volunteer.portal.repository;

import com.volunteer.portal.entity.Registration;
import com.volunteer.portal.entity.User;
import com.volunteer.portal.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByUser(User user);

    @Query("SELECT r FROM Registration r WHERE r.user = :user ORDER BY r.registeredAt DESC")
    List<Registration> findByUserOrderByRegisteredAtDesc(@Param("user") User user);
    List<Registration> findByEvent(Event event);
    
    Optional<Registration> findByUserAndEvent(User user, Event event);
    
    @Query("SELECT r FROM Registration r WHERE r.user.id = :userId AND r.status = :status")
    List<Registration> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Registration.RegistrationStatus status);
    
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.user.id = :userId AND r.status = 'APPLIED'")
    Long countAppliedByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.user.id = :userId AND r.status = 'COMPLETED'")
    Long countCompletedByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.user.id = :userId AND r.status = 'REJECTED'")
    Long countRejectedByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) FROM Registration r")
    Long countAllRegistrations();

    @Query("SELECT MONTH(r.registeredAt), YEAR(r.registeredAt), COUNT(r) FROM Registration r " +
           "WHERE r.registeredAt IS NOT NULL GROUP BY YEAR(r.registeredAt), MONTH(r.registeredAt) " +
           "ORDER BY YEAR(r.registeredAt) DESC, MONTH(r.registeredAt) DESC")
    List<Object[]> findMonthlyRegistrationStats();
}
