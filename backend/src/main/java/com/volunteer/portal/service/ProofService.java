package com.volunteer.portal.service;

import com.volunteer.portal.dto.ProofDto;
import com.volunteer.portal.entity.AdminUser;
import com.volunteer.portal.entity.Certificate;
import com.volunteer.portal.entity.Event;
import com.volunteer.portal.entity.Proof;
import com.volunteer.portal.entity.Registration;
import com.volunteer.portal.entity.User;
import com.volunteer.portal.repository.AdminUserRepository;
import com.volunteer.portal.repository.CertificateRepository;
import com.volunteer.portal.repository.EventRepository;
import com.volunteer.portal.repository.ProofRepository;
import com.volunteer.portal.repository.RegistrationRepository;
import com.volunteer.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProofService {

    /* =======================
       REPOSITORIES
    ======================= */
    @Autowired
    private ProofRepository proofRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private CertificateService certificateService;

    private static final String UPLOAD_DIR = "uploads/";

    /* =========================================================
       UPLOAD PROOF
    ========================================================= */
    public ProofDto uploadProof(Long eventId, String userEmail, MultipartFile file) throws IOException {

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

        registrationRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new RuntimeException("User not registered for this event"));

        // Handle existing proof safely
        proofRepository.findByUserAndEvent(user, event)
                .ifPresent(existing -> {
                    Proof.ProofStatus status = existing.getStatus();

                    if (status == Proof.ProofStatus.APPROVED) {
                        throw new RuntimeException("Proof already approved for this event");
                    }
                    if (status == Proof.ProofStatus.PENDING) {
                        throw new RuntimeException("Proof is pending review for this event");
                    }
                    proofRepository.delete(existing);
                });

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, fileName);

        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());

        String fullUrl = "https://volunteer-portal-skeu.onrender.com/uploads/" + fileName;
        Proof proof = new Proof(user, event, fullUrl);
        return convertToDto(proofRepository.save(proof));
    }

    /* =========================================================
       DELETE PROOF
    ========================================================= */
    public void deleteProof(Long proofId, String userEmail) {

        if (proofId == null) {
            throw new IllegalArgumentException("Proof ID cannot be null");
        }

        Proof proof = proofRepository.findById(proofId)
                .orElseThrow(() -> new RuntimeException("Proof not found"));

        if (!proof.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("You can only delete your own proofs");
        }

        if (proof.getStatus() != Proof.ProofStatus.PENDING) {
            throw new RuntimeException("Only pending proofs can be deleted");
        }

        try {
            if (proof.getProofUrl() != null) {
                // Extract filename from full URL
                String filename = proof.getProofUrl().substring(proof.getProofUrl().lastIndexOf('/') + 1);
                Files.deleteIfExists(Paths.get(UPLOAD_DIR, filename));
            }
        } catch (Exception ignored) {}

        proofRepository.delete(proof);
    }

    /* =========================================================
       GET USER PROOFS
    ========================================================= */
    public List<ProofDto> getUserProofs(String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return proofRepository.findByUserOrderBySubmittedAtDesc(user)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /* =========================================================
       GET ALL PROOFS
    ========================================================= */
    public List<ProofDto> getAllProofs() {
        return proofRepository.findAllOrderedBySubmittedAt()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /* =========================================================
       GET PROOFS BY ADMIN
    ========================================================= */
    public List<ProofDto> getProofsByAdmin(String adminEmail) {

        AdminUser admin = adminUserRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        List<Event> adminEvents = eventRepository.findByCreator(admin);

        return proofRepository.findByEventIn(adminEvents)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /* =========================================================
       APPROVE PROOF + CERTIFICATE
    ========================================================= */
    public ProofDto approveProof(Long proofId) {

        Long id = proofId;
if (id == null) {
    throw new IllegalArgumentException("Proof ID cannot be null");
}

Proof proof = proofRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Proof not found"));


        if (proof.getStatus() != Proof.ProofStatus.PENDING) {
            throw new RuntimeException("Proof is not pending");
        }

        proof.setStatus(Proof.ProofStatus.APPROVED);
        proof.setReviewedAt(LocalDateTime.now());
        proof.setPointsAwarded(proof.getEvent().getPoints());

        User user = proof.getUser();
        user.setTotalPoints(user.getTotalPoints() + proof.getPointsAwarded());
        userRepository.save(user);

        registrationRepository.findByUserAndEvent(user, proof.getEvent())
                .ifPresent(reg -> {
                    reg.setStatus(Registration.RegistrationStatus.COMPLETED);
                    registrationRepository.save(reg);
                });

        Proof savedProof = proofRepository.save(proof);

        try {
            certificateService.generateCertificate(user, proof.getEvent(), savedProof);
        } catch (Exception e) {
            System.err.println("Certificate generation failed: " + e.getMessage());
        }

        return convertToDto(savedProof);
    }

    /* =========================================================
       REJECT PROOF
    ========================================================= */
    public ProofDto rejectProof(Long proofId, String reason) {

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Rejection reason cannot be empty");
        }

        Long id = proofId;
if (id == null) {
    throw new IllegalArgumentException("Proof ID cannot be null");
}

Proof proof = proofRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Proof not found"));


        if (proof.getStatus() != Proof.ProofStatus.PENDING) {
            throw new RuntimeException("Proof is not pending");
        }

        proof.setStatus(Proof.ProofStatus.REJECTED);
        proof.setReviewedAt(LocalDateTime.now());
        proof.setRejectionReason(reason);

        registrationRepository.findByUserAndEvent(proof.getUser(), proof.getEvent())
                .ifPresent(reg -> {
                    reg.setStatus(Registration.RegistrationStatus.REJECTED);
                    registrationRepository.save(reg);
                });

        return convertToDto(proofRepository.save(proof));
    }

    /* =========================================================
       REGENERATE CERTIFICATE (ADMIN)
    ========================================================= */
    public void regenerateCertificate(Long proofId, String adminEmail) {

        Long id = proofId;
if (id == null) {
    throw new IllegalArgumentException("Proof ID cannot be null");
}

Proof proof = proofRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Proof not found"));

        boolean ownsProof = getProofsByAdmin(adminEmail)
                .stream()
                .anyMatch(p -> proofId.equals(p.getId()));

        if (!ownsProof) {
            throw new RuntimeException("Unauthorized regeneration attempt");
        }

        certificateRepository.findByProofId(proofId)
                .ifPresent(certificateRepository::delete);

        try {
            certificateService.generateCertificate(
                    proof.getUser(),
                    proof.getEvent(),
                    proof
            );
        } catch (Exception e) {
            throw new RuntimeException("Certificate regeneration failed");
        }
    }

    /* =========================================================
       DTO CONVERSION
    ========================================================= */
    private ProofDto convertToDto(Proof proof) {

        ProofDto dto = new ProofDto();
        dto.setId(proof.getId());
        dto.setUserId(proof.getUser().getId());
        dto.setUserName(proof.getUser().getName());
        dto.setEventId(proof.getEvent().getId());
        dto.setEventTitle(proof.getEvent().getTitle());
        dto.setEventCategory(proof.getEvent().getCategory());
        dto.setEventCoordinatorName(proof.getEvent().getCreator().getName());
        dto.setProofUrl(proof.getProofUrl());
        dto.setStatus(proof.getStatus().name());
        dto.setSubmittedAt(proof.getSubmittedAt());
        dto.setReviewedAt(proof.getReviewedAt());
        dto.setPointsAwarded(proof.getPointsAwarded());
        dto.setRejectionReason(proof.getRejectionReason());

        Long proofId = proof.getId();
        if (proofId != null) {
            Certificate cert = certificateService.getCertificateByProofId(proofId);
            if (cert != null) {
                dto.setCertificateId(cert.getCertificateId());
            }
        }

        return dto;
    }
}
