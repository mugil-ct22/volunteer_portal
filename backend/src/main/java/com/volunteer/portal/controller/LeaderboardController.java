package com.volunteer.portal.controller;

import com.volunteer.portal.dto.LeaderboardDto;
import com.volunteer.portal.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<List<LeaderboardDto>> getLeaderboard() {
        List<LeaderboardDto> leaderboard = leaderboardService.getLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }

    @PostMapping("/recalculate")
    public ResponseEntity<String> recalculatePoints() {
        leaderboardService.recalculateAllUserPoints();
        return ResponseEntity.ok("User points recalculated successfully");
    }
}
