package com.volunteer.portal.service;

import com.volunteer.portal.dto.LeaderboardDto;
import com.volunteer.portal.entity.Proof;
import com.volunteer.portal.entity.User;
import com.volunteer.portal.repository.ProofRepository;
import com.volunteer.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class LeaderboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProofRepository proofRepository;

    // ✅ ORDINAL RANKING (1,2,3,4...)
    public List<LeaderboardDto> getLeaderboard() {

        // MUST be ordered by points DESC
        List<User> users = userRepository.findLeaderboard();

        List<LeaderboardDto> leaderboard = new ArrayList<>();

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);

            leaderboard.add(new LeaderboardDto(
                    i + 1,                       // Ordinal rank: 1,2,3,4... (unique for each user)
                    user.getName(),
                    user.getTotalPoints()
            ));
        }

        return leaderboard;
    }

    // ✅ RECALCULATE POINTS
    @Transactional
    public void recalculateAllUserPoints() {

        List<User> allUsers = userRepository.findAllUsers();

        for (User user : allUsers) {
            List<Proof> userProofs = proofRepository.findByUser(user);

            int totalPoints = userProofs.stream()
                    .filter(p -> p.getStatus() == Proof.ProofStatus.APPROVED)
                    .mapToInt(p -> p.getPointsAwarded() != null ? p.getPointsAwarded() : 0)
                    .sum();

            user.setTotalPoints(totalPoints);
            userRepository.save(user);
        }
    }
}
