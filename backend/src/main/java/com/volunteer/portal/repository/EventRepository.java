package com.volunteer.portal.repository;

import com.volunteer.portal.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e ORDER BY e.createdAt DESC")
    java.util.List<Event> findAllOrderByCreatedAtDesc();

    @Query("SELECT COUNT(e) FROM Event e")
    Long countEvents();

    @Query("SELECT e.title, COUNT(r), e.points FROM Event e LEFT JOIN Registration r ON r.event.id = e.id " +
           "GROUP BY e.id, e.title, e.points ORDER BY COUNT(r) DESC")
    java.util.List<Object[]> findEventPopularity();

    java.util.List<Event> findByCategory(String category);

    java.util.List<Event> findByCreator(com.volunteer.portal.entity.AdminUser creator);
}
