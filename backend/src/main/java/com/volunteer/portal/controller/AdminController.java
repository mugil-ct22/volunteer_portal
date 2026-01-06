package com.volunteer.portal.controller;

import com.volunteer.portal.dto.DashboardDto;
import com.volunteer.portal.dto.EventDto;
import com.volunteer.portal.dto.ProofDto;
import com.volunteer.portal.dto.UserDto;
import com.volunteer.portal.service.DashboardService;
import com.volunteer.portal.service.EventService;
import com.volunteer.portal.service.ProofService;
import com.volunteer.portal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")

@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private EventService eventService;

    @Autowired
    private ProofService proofService;

    @Autowired
    private UserService userService;

    /* ==============================
       DASHBOARD
    ============================== */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> getAdminDashboard(Authentication authentication) {
        return ResponseEntity.ok(
                dashboardService.getAdminDashboard(authentication.getName())
        );
    }

    /* ==============================
       EVENTS
    ============================== */
    @GetMapping("/events")
    public ResponseEntity<List<EventDto>> getMyEvents(Authentication authentication) {
        return ResponseEntity.ok(
                eventService.getEventsByCreator(authentication.getName())
        );
    }

    @PostMapping("/events")
    public ResponseEntity<EventDto> createEvent(
            @Valid @RequestBody EventDto eventDto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                eventService.createEvent(eventDto, authentication.getName())
        );
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<EventDto> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventDto eventDto
    ) {
        return ResponseEntity.ok(
                eventService.updateEvent(id, eventDto)
        );
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok().build();
    }

    /* ==============================
       PROOFS
    ============================== */
    @GetMapping("/proofs")
    public ResponseEntity<List<ProofDto>> getMyProofs(Authentication authentication) {
        return ResponseEntity.ok(
                proofService.getProofsByAdmin(authentication.getName())
        );
    }

    @PutMapping("/proofs/{proofId}/approve")
    public ResponseEntity<ProofDto> approveProof(
            @PathVariable Long proofId,
            Authentication authentication
    ) {

        String adminEmail = authentication.getName();

        boolean ownsProof = proofService.getProofsByAdmin(adminEmail)
                .stream()
                .anyMatch(p -> p.getId().equals(proofId));

        if (!ownsProof) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                proofService.approveProof(proofId)
        );
    }

    /* ==============================
       REGENERATE CERTIFICATE ✅
    ============================== */
    @PutMapping("/proofs/{proofId}/regenerate-certificate")
    public ResponseEntity<String> regenerateCertificate(
            @PathVariable Long proofId,
            Authentication authentication
    ) {
        proofService.regenerateCertificate(proofId, authentication.getName());
        return ResponseEntity.ok("Certificate regenerated successfully");
    }

    @PutMapping("/proofs/{proofId}/reject")
    public ResponseEntity<ProofDto> rejectProof(
            @PathVariable Long proofId,
            @RequestParam("reason") String reason,
            Authentication authentication
    ) {

        String adminEmail = authentication.getName();

        boolean ownsProof = proofService.getProofsByAdmin(adminEmail)
                .stream()
                .anyMatch(p -> p.getId().equals(proofId));

        if (!ownsProof) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                proofService.rejectProof(proofId, reason)
        );
    }

    /* ==============================
       USERS
    ============================== */
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }
}
