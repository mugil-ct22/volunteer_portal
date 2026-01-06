package com.volunteer.portal.repository;

import com.volunteer.portal.entity.Proof;
import com.volunteer.portal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProofRepository extends JpaRepository<Proof, Long> {
    List<Proof> findByUser(User user);

    @Query("SELECT p FROM Proof p WHERE p.user = :user ORDER BY p.submittedAt DESC")
    List<Proof> findByUserOrderBySubmittedAtDesc(@Param("user") User user);
    List<Proof> findByStatus(Proof.ProofStatus status);

    Optional<Proof> findByUserAndEvent(User user, com.volunteer.portal.entity.Event event);

    List<Proof> findByEventIn(List<com.volunteer.portal.entity.Event> events);
    
    @Query("SELECT COUNT(p) FROM Proof p WHERE p.status = 'PENDING'")
    Long countPendingProofs();
    
    @Query("SELECT p FROM Proof p ORDER BY p.submittedAt DESC")
    List<Proof> findAllOrderedBySubmittedAt();
    
    @Query("SELECT COUNT(p) FROM Proof p WHERE p.user.id = :userId AND p.status = 'APPROVED'")
    Long countApprovedByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(p) FROM Proof p WHERE p.user.id = :userId AND p.status = 'REJECTED'")
    Long countRejectedByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COALESCE(SUM(p.pointsAwarded), 0) FROM Proof p WHERE p.user.id = :userId AND p.status = 'APPROVED'")
    Integer sumPointsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM Proof p")
    Long countAllProofs();

    @Query("SELECT COUNT(p) FROM Proof p WHERE p.status = 'APPROVED'")
    Long countApprovedProofs();

    @Query("SELECT COUNT(p) FROM Proof p WHERE p.status = 'REJECTED'")
    Long countRejectedProofs();

    @Query("SELECT COALESCE(SUM(p.pointsAwarded), 0) FROM Proof p WHERE p.status = 'APPROVED'")
    Integer sumAllPoints();

    @Query("SELECT " +
           "CASE " +
           "WHEN p.pointsAwarded BETWEEN 0 AND 25 THEN '0-25' " +
           "WHEN p.pointsAwarded BETWEEN 26 AND 50 THEN '26-50' " +
           "WHEN p.pointsAwarded BETWEEN 51 AND 75 THEN '51-75' " +
           "WHEN p.pointsAwarded BETWEEN 76 AND 100 THEN '76-100' " +
           "ELSE '100+' END as range, COUNT(p) " +
           "FROM Proof p WHERE p.status = 'APPROVED' AND p.pointsAwarded IS NOT NULL " +
           "GROUP BY CASE " +
           "WHEN p.pointsAwarded BETWEEN 0 AND 25 THEN '0-25' " +
           "WHEN p.pointsAwarded BETWEEN 26 AND 50 THEN '26-50' " +
           "WHEN p.pointsAwarded BETWEEN 51 AND 75 THEN '51-75' " +
           "WHEN p.pointsAwarded BETWEEN 76 AND 100 THEN '76-100' " +
           "ELSE '100+' END ORDER BY range")
    List<Object[]> findPointsDistribution();
}
