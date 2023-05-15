package timnekk.quizheroreborn.leaderboard.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardEntry {
    private String username;
    private int points;
}