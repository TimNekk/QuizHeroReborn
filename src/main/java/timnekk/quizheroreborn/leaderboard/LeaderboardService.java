package timnekk.quizheroreborn.leaderboard;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import timnekk.quizheroreborn.user.UserRepository;
import timnekk.quizheroreborn.user.User;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    private final UserRepository userRepository;

    public LeaderboardResponse getLeaderboard(LeaderboardRequest request) {
        Sort sort = Sort.by(Sort.Direction.DESC, User.getPointsColumnName());
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);

        Page<LeaderboardEntry> leaderboard = userRepository.findAll(pageable)
                .map(user -> LeaderboardEntry.builder()
                        .username(user.getUsername())
                        .points(user.getPoints())
                        .build());

        return LeaderboardResponse.builder()
                .leaderboard(leaderboard)
                .build();
    }
}
