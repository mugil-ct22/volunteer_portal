package com.volunteer.portal.controller;

import com.volunteer.portal.dto.ProofDto;
import com.volunteer.portal.service.ProofService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/proof")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class ProofController {

    @Autowired
    private ProofService proofService;

    @GetMapping("/user")
    public ResponseEntity<List<ProofDto>> getUserProofs(Authentication authentication) {
        String userEmail = authentication.getName();
        List<ProofDto> proofs = proofService.getUserProofs(userEmail);
        return ResponseEntity.ok(proofs);
    }

    @PostMapping(value = "/upload/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProofDto> uploadProof(@PathVariable Long eventId,
                                                @RequestParam("file") MultipartFile file,
                                                Authentication authentication) throws IOException {
        String userEmail = authentication.getName();
        ProofDto proof = proofService.uploadProof(eventId, userEmail, file);
        return ResponseEntity.ok(proof);
    }

    @DeleteMapping("/{proofId}")
    public ResponseEntity<Void> deleteProof(@PathVariable Long proofId, Authentication authentication) {
        String userEmail = authentication.getName();
        proofService.deleteProof(proofId, userEmail);
        return ResponseEntity.ok().build();
    }
}
