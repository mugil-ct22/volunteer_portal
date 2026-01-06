package com.volunteer.portal.controller;

import com.volunteer.portal.dto.DashboardDto;
import com.volunteer.portal.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class UserController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> getUserDashboard(Authentication authentication) {
        String userEmail = authentication.getName();
        DashboardDto dashboard = dashboardService.getUserDashboard(userEmail);
        return ResponseEntity.ok(dashboard);
    }
}
