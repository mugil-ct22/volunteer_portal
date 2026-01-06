package com.volunteer.portal.service;

import com.volunteer.portal.dto.DashboardDto;
import com.volunteer.portal.entity.AdminUser;
import com.volunteer.portal.entity.User;
import com.volunteer.portal.repository.AdminUserRepository;
import com.volunteer.portal.repository.EventRepository;
import com.volunteer.portal.repository.ProofRepository;
import com.volunteer.portal.repository.RegistrationRepository;
import com.volunteer.portal.repository.UserRepository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private ProofRepository proofRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    public DashboardDto getUserDashboard(String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();

        Long totalEvents = eventRepository.countEvents();
        Long appliedEvents = registrationRepository.countAppliedByUserId(userId);
        Long completedEvents = proofRepository.countApprovedByUserId(userId);
        Long rejectedEvents = proofRepository.countRejectedByUserId(userId);
        Integer totalPoints = proofRepository.sumPointsByUserId(userId);

        return new DashboardDto(
                totalEvents,
                appliedEvents,
                completedEvents,
                rejectedEvents,
                totalPoints,
                null,
                null
        );
    }

    public DashboardDto getAdminDashboard(String adminEmail) {

        AdminUser admin = adminUserRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // Count events created by this admin
        Long totalEvents = (long) eventRepository.findByCreator(admin).size();

        // Count pending proofs for events owned by this admin
        List<com.volunteer.portal.entity.Event> adminEvents = eventRepository.findByCreator(admin);
        Long pendingApprovals = proofRepository.findByEventIn(adminEvents)
                .stream()
                .filter(proof -> proof.getStatus() == com.volunteer.portal.entity.Proof.ProofStatus.PENDING)
                .count();

        // Total volunteers is global (not admin-specific)
        Long totalVolunteers = userRepository.countUsers();

        return new DashboardDto(
                totalEvents,
                null,
                null,
                null,
                null,
                totalVolunteers,
                pendingApprovals
        );
    }
}
