package com.volunteer.portal.controller;

import com.volunteer.portal.dto.EventDto;
import com.volunteer.portal.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")

public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventDto>> getAllEvents() {
        List<EventDto> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable Long id) {
        EventDto event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @PostMapping("/register/{eventId}")
    public ResponseEntity<Void> registerForEvent(@PathVariable Long eventId, Authentication authentication) {
        String userEmail = authentication.getName();
        eventService.registerForEvent(eventId, userEmail);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unregister/{eventId}")
    public ResponseEntity<Void> unregisterFromEvent(@PathVariable Long eventId, Authentication authentication) {
        String userEmail = authentication.getName();
        eventService.unregisterFromEvent(eventId, userEmail);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/registered")
    public ResponseEntity<List<EventDto>> getRegisteredEvents(Authentication authentication) {
        String userEmail = authentication.getName();
        List<EventDto> events = eventService.getRegisteredEvents(userEmail);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<EventDto>> getEventsByCategory(@PathVariable String category) {
        List<EventDto> events = eventService.getEventsByCategory(category);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = eventService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}
