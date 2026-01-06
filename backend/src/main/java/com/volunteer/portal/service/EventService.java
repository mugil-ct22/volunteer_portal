package com.volunteer.portal.service;

import com.volunteer.portal.dto.EventDto;
import com.volunteer.portal.entity.AdminUser;
import com.volunteer.portal.entity.Event;
import com.volunteer.portal.entity.Registration;
import com.volunteer.portal.entity.User;
import com.volunteer.portal.repository.AdminUserRepository;
import com.volunteer.portal.repository.EventRepository;
import com.volunteer.portal.repository.ProofRepository;
import com.volunteer.portal.repository.RegistrationRepository;
import com.volunteer.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private ProofRepository proofRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    public List<EventDto> getAllEvents() {
        List<Event> events = eventRepository.findAllOrderByCreatedAtDesc();
        return events.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public EventDto getEventById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        return convertToDto(event);
    }

    public EventDto createEvent(EventDto eventDto, String adminEmail) {
        validateCategory(eventDto.getCategory());

        AdminUser creator = adminUserRepository.findByEmail(adminEmail)
            .orElseThrow(() -> new RuntimeException("Admin not found"));

        Event event = new Event();
        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        event.setEventDate(eventDto.getEventDate());
        event.setPoints(eventDto.getPoints());
        event.setMaxVolunteers(eventDto.getMaxVolunteers());
        event.setCategory(eventDto.getCategory());
        event.setCreator(creator);

        Event savedEvent = eventRepository.save(event);
        return convertToDto(savedEvent);
    }

    public EventDto updateEvent(Long id, EventDto eventDto) {
        if (id == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        validateCategory(eventDto.getCategory());

        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        event.setEventDate(eventDto.getEventDate());
        event.setPoints(eventDto.getPoints());
        event.setMaxVolunteers(eventDto.getMaxVolunteers());
        event.setCategory(eventDto.getCategory());

        Event updatedEvent = eventRepository.save(event);
        return convertToDto(updatedEvent);
    }

    public void deleteEvent(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Event not found");
        }
        eventRepository.deleteById(id);
    }

    public void registerForEvent(Long eventId, String userEmail) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("User email cannot be null or empty");
        }
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        boolean alreadyRegistered = registrationRepository.findByUserAndEvent(user, event).isPresent();
        if (alreadyRegistered) {
            throw new RuntimeException("Already registered for this event");
        }

        Registration registration = new Registration(user, event);
        registrationRepository.save(registration);
    }

    public void unregisterFromEvent(Long eventId, String userEmail) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("User email cannot be null or empty");
        }

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        Registration registration = registrationRepository.findByUserAndEvent(user, event)
            .orElseThrow(() -> new RuntimeException("Not registered for this event"));

        if (registration == null) {
            throw new RuntimeException("Not registered for this event");
        }

        // Check if user has any approved proofs for this event
        boolean hasApprovedProofs = proofRepository.findByUser(user)
            .stream()
            .anyMatch(proof -> proof.getEvent().getId().equals(eventId) &&
                              proof.getStatus() == com.volunteer.portal.entity.Proof.ProofStatus.APPROVED);

        if (hasApprovedProofs) {
            throw new RuntimeException("Cannot unregister from event with approved proofs");
        }

        // Check if user has any pending proofs for this event
        boolean hasPendingProofs = proofRepository.findByUser(user)
            .stream()
            .anyMatch(proof -> proof.getEvent().getId().equals(eventId) &&
                              proof.getStatus() == com.volunteer.portal.entity.Proof.ProofStatus.PENDING);

        if (hasPendingProofs) {
            throw new RuntimeException("Cannot unregister from event with pending proofs. Please delete your pending proof first.");
        }

        registrationRepository.delete(registration);
    }

    public List<EventDto> getRegisteredEvents(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<Registration> registrations = registrationRepository.findByUserOrderByRegisteredAtDesc(user);
        return registrations.stream()
            .map(reg -> convertToDto(reg.getEvent()))
            .collect(Collectors.toList());
    }

    public List<EventDto> getEventsByCreator(String adminEmail) {
        AdminUser creator = adminUserRepository.findByEmail(adminEmail)
            .orElseThrow(() -> new RuntimeException("Admin not found"));

        List<Event> events = eventRepository.findByCreator(creator);
        return events.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<EventDto> getEventsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }

        List<Event> events = eventRepository.findByCategory(category);
        return events.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<String> getAllCategories() {
        return Arrays.asList(
            "Community Service",
            "Environmental",
            "Health & Awareness",
            "Education & Teaching",
            "Blood Donation",
            "Disaster Relief",
            "Others"
        );
    }

    private void validateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category is required");
        }

        List<String> validCategories = getAllCategories();
        if (!validCategories.contains(category)) {
            throw new IllegalArgumentException("Invalid category: " + category +
                ". Valid categories are: " + String.join(", ", validCategories));
        }
    }

    private EventDto convertToDto(Event event) {
        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setPoints(event.getPoints());
        dto.setMaxVolunteers(event.getMaxVolunteers());
        dto.setCategory(event.getCategory() != null ? event.getCategory() : "Others");
        dto.setCreatedAt(event.getCreatedAt());

        long registeredCount = registrationRepository.findByEvent(event).size();
        dto.setRegisteredVolunteers((int) registeredCount);

        return dto;
    }
}
