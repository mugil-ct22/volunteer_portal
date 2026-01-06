package com.volunteer.portal.controller;

import com.volunteer.portal.entity.Certificate;
import com.volunteer.portal.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/certificates")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @GetMapping("/download/{certificateId}")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable String certificateId) {
        try {
            byte[] certificateFile = certificateService.getCertificateFile(certificateId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", certificateId + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(certificateFile);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/verify/{certificateId}")
    public ResponseEntity<?> verifyCertificate(@PathVariable String certificateId) {
        try {
            Certificate certificate = certificateService.getCertificateById(certificateId);

            // Return certificate verification details
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return ResponseEntity.ok(new CertificateVerificationResponse(
                certificate.getCertificateId(),
                certificate.getUser().getName(),
                certificate.getEvent().getTitle(),
                certificate.getIssuedAt().format(formatter),
                certificate.getProof().getPointsAwarded(),
                true // Valid certificate
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new CertificateVerificationResponse(
                certificateId,
                null,
                null,
                null,
                null,
                false // Invalid certificate
            ));
        }
    }

    // DTO for certificate verification response
    public static class CertificateVerificationResponse {
        private String certificateId;
        private String volunteerName;
        private String eventTitle;
        private String issuedDate;
        private Integer pointsAwarded;
        private boolean isValid;

        public CertificateVerificationResponse(String certificateId, String volunteerName,
                                             String eventTitle, String issuedDate,
                                             Integer pointsAwarded, boolean isValid) {
            this.certificateId = certificateId;
            this.volunteerName = volunteerName;
            this.eventTitle = eventTitle;
            this.issuedDate = issuedDate != null ? issuedDate.toString() : null;
            this.pointsAwarded = pointsAwarded;
            this.isValid = isValid;
        }

        // Getters
        public String getCertificateId() { return certificateId; }
        public String getVolunteerName() { return volunteerName; }
        public String getEventTitle() { return eventTitle; }
        public String getIssuedDate() { return issuedDate; }
        public Integer getPointsAwarded() { return pointsAwarded; }
        public boolean isValid() { return isValid; }
    }
}
